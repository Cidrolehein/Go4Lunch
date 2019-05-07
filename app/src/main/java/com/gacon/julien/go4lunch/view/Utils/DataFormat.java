package com.gacon.julien.go4lunch.view.Utils;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;

import com.gacon.julien.go4lunch.models.LunchModel;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

/**
 * Format the datas from Lunch Model to Lunch View Holder
 */
public class DataFormat {

    /**
     * Format meters
     * @param newLunch New Lunch model
     * @return Meters
     */
    public String formatMeters(LunchModel newLunch) {
        int metersInteger = convertMeters(newLunch.getDisanceInMeters());
        String metersToString = Integer.toString(metersInteger);
        return metersToString + "m";
    }

    /**
     * Convert meters to int
     * @param m Float meters from lunch model
     * @return  Meters to int
     */
    private int convertMeters(float m) {
        int meters;
        meters = Math.round(m);
        return meters;
    }

    /**
     * Get Rating Stars
     * @param lunch Lunch model
     * @param mStarRating1 ImageView One rating
     * @param mStarRating2 ImageView Two rating
     * @param mStarRating3 ImageView Three rating
     * @param mStarRating4 ImagesView Four rating
     */
    public void getRatingStar(LunchModel lunch,
                              ImageView mStarRating1,
                              ImageView mStarRating2,
                              ImageView mStarRating3,
                              ImageView mStarRating4) {
        if (lunch.getPlace_rating() == 1 && lunch.getPlace_rating() < 2) {
            mStarRating1.setVisibility(View.GONE);
            mStarRating2.setVisibility(View.GONE);
            mStarRating3.setVisibility(View.GONE);
            mStarRating4.setVisibility(View.GONE);
        } else if (lunch.getPlace_rating() >= 2 && lunch.getPlace_rating() < 3) {
            mStarRating1.setVisibility(View.VISIBLE);
            mStarRating2.setVisibility(View.GONE);
            mStarRating3.setVisibility(View.GONE);
            mStarRating4.setVisibility(View.GONE);
        } else if (lunch.getPlace_rating() >= 3 && lunch.getPlace_rating() < 4) {
            mStarRating1.setVisibility(View.VISIBLE);
            mStarRating2.setVisibility(View.VISIBLE);
            mStarRating3.setVisibility(View.GONE);
            mStarRating4.setVisibility(View.GONE);
        } else if (lunch.getPlace_rating() >= 4 && lunch.getPlace_rating() < 5) {
            mStarRating1.setVisibility(View.VISIBLE);
            mStarRating2.setVisibility(View.VISIBLE);
            mStarRating3.setVisibility(View.VISIBLE);
            mStarRating4.setVisibility(View.GONE);
        } else {
            mStarRating1.setVisibility(View.VISIBLE);
            mStarRating2.setVisibility(View.VISIBLE);
            mStarRating3.setVisibility(View.VISIBLE);
            mStarRating4.setVisibility(View.VISIBLE);
        }
    }

    /**
     * PlaceImage
     * @param place Get the Place Details
     * @param placesClient Get the current place
     * @param imageView ImageView for image
     */
    public void addImages(Place place, PlacesClient placesClient, ImageView imageView) {
        PhotoMetadata photoMetadata = Objects.requireNonNull(place.getPhotoMetadatas()).get(0);
        // Create a FetchPhotoRequest.
        FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                .setMaxWidth(500) // Optional.
                .setMaxHeight(500) // Optional.
                .build();
        placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
            Bitmap bitmap = fetchPhotoResponse.getBitmap();
            imageView.setImageBitmap(bitmap);
        });
    }

    /**
     * Set Place Day and Time
     * @param lunch Lunch Model
     * @return A string to put in textView
     */
    public String getDayOpen(LunchModel lunch) {
        String isItOpen = "";
        int openPlaceHour, closePlaceHour;
        // Get the current day & hour
        Calendar calendar = Calendar.getInstance();
        String dayOfWeek = getCurrentDay(calendar);
        int currentHour = getCurrentHour(calendar);
        // Get openPlaceDay, openPlaceHour, closePlaceHour
        ArrayList daysOpen = getArrayOfDays(lunch); // open's days
        for (int i = 0; i < lunch.getPeriods().size(); i++) {
            if (lunch.getPeriods().get(i).getOpen() != null && lunch.getPeriods().get(i).getClose() != null) {
                openPlaceHour = Objects.requireNonNull(lunch.getPeriods().get(i).getOpen()).getTime().getHours();
                closePlaceHour = Objects.requireNonNull(lunch.getPeriods().get(i).getClose()).getTime().getHours();
                // Compare and  get isItOpen
                if (daysOpen.contains(dayOfWeek) && currentHour >= openPlaceHour && currentHour <= closePlaceHour) {
                    isItOpen = "Open until " + closePlaceHour + "h";
                } else if (!daysOpen.contains(dayOfWeek)) {
                    isItOpen = "Close today";
                } else if (daysOpen.contains(dayOfWeek) && currentHour == closePlaceHour - 1){
                    isItOpen = "Closing soon !";
                } else isItOpen = "Close until " + openPlaceHour + "h";
            }
        }
        return isItOpen;
    }

    /**
     * Get the current day
     * @param calendar Calendar
     * @return The current day
     */
    private String getCurrentDay(Calendar calendar) {
        String day = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
        day = day.toUpperCase(); // change currentDay to upper case
        return day;
    }

    /**
     * Get the current hour
     * @param calendar Initialize Calendar
     * @return Hour
     */
    private int getCurrentHour(Calendar calendar) {
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * All the open days
     * @param lunch Model lunch
     * @return Array of days
     */
    private ArrayList getArrayOfDays(LunchModel lunch) {
        ArrayList<String> arrayListOfDays = new ArrayList<>();
        String openPlaceDay;
        for (int i = 0; i < lunch.getPeriods().size(); i++) {
            openPlaceDay = Objects.requireNonNull(lunch.getPeriods().get(i).getOpen()).getDay().toString();
            arrayListOfDays.add(openPlaceDay);
        }
        return arrayListOfDays;
    }
}
