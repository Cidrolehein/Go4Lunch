package com.gacon.julien.go4lunch.view;

import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.gacon.julien.go4lunch.R;
import com.gacon.julien.go4lunch.models.LunchModel;
import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;

class LunchViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.title)
    TextView mTextViewTitle;
    @BindView(R.id.address)
    TextView mTextViewAddress;
    @BindView(R.id.place_image)
    ImageView imageView;
    @BindView(R.id.type)
    TextView mTextViewType;
    @BindView(R.id.Date)
    TextView mTextViewIsItOpen;
    @BindView(R.id.distance)
    TextView mTextViewDistance;
    @BindView(R.id.star_rating_1)
    ImageView mStarRating1;
    @BindView(R.id.star_rating_2)
    ImageView mStarRating2;
    @BindView(R.id.star_rating_3)
    ImageView mStarRating3;
    @BindView(R.id.star_rating_4)
    ImageView mStarRating4;


    LunchViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    void updateWithLunch(LunchModel newLunch, RequestManager glide) {
        this.mTextViewTitle.setText(newLunch.getTitle());
        this.mTextViewAddress.setText(newLunch.getAddress());
        this.mTextViewType.setText(newLunch.getPlace_type());
        this.getRatingStar(newLunch);
        this.getImage(newLunch, glide);
        if (newLunch.getPeriods() != null) {
            this.mTextViewIsItOpen.setText(getDayOpen(newLunch));
        }
        if (newLunch.getPhotoMetadatasOfPlace() != null) {
            addImages(newLunch.getPlace(), newLunch.getPlacesClient());
        }
        this.mTextViewDistance.setText(convertMeters(newLunch.getDisanceInMeters()) + "M");
    }

    private void getImage(LunchModel placeImage, RequestManager glide) {
        glide.load(placeImage.getPlaceImage()).apply(new RequestOptions()).into(imageView);
    }

    private void getRatingStar(LunchModel lunch) {
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

    private void addImages(Place place, PlacesClient placesClient) {
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

    private String getDayOpen(LunchModel lunch) {
        String isItOpen = "";
        int openPlaceHour, closePlaceHour;
        // Get the current day & hour
        Calendar calendar = Calendar.getInstance();
        String dayOfWeek = getCurrentDay(calendar);
        int currentHour = getCurrentHour(calendar);
        // Get openPlaceDay, openPlaceHour, closePlaceHour
        if (lunch.getPeriods() != null) {
            ArrayList daysOpen = getArrayOfDays(lunch); // open's days
            for (int i = 0; i < lunch.getPeriods().size(); i++) {
                if (lunch.getPeriods().get(i).getOpen() != null && lunch.getPeriods().get(i).getClose() != null) {
                    openPlaceHour = Objects.requireNonNull(lunch.getPeriods().get(i).getOpen()).getTime().getHours();
                    closePlaceHour = Objects.requireNonNull(lunch.getPeriods().get(i).getClose()).getTime().getHours();
                    // Compare and  get isItOpen
                    if (daysOpen.contains(dayOfWeek) && currentHour >= openPlaceHour && currentHour <= closePlaceHour) {
                        isItOpen = "Open until " + closePlaceHour + "h";
                    } else if (daysOpen.contains(dayOfWeek)) {
                        isItOpen = "Close today";
                    } else if (daysOpen.contains(dayOfWeek) && currentHour < openPlaceHour && currentHour > closePlaceHour) {
                        isItOpen = "Open until " + openPlaceHour + "h";
                    }
                }
            }
        }
        return isItOpen;
    }

    private String getCurrentDay(Calendar calendar) {
        String day = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
        day = day.toUpperCase(); // change currentDay to upper case
        return day;
    }

    private int getCurrentHour(Calendar calendar) {
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    private ArrayList getArrayOfDays(LunchModel lunch){
        ArrayList<String> arrayListOfDays = new ArrayList<>();
        String openPlaceDay;
        for (int i = 0; i < lunch.getPeriods().size(); i++) {
            openPlaceDay = Objects.requireNonNull(lunch.getPeriods().get(i).getOpen()).getDay().toString();
            arrayListOfDays.add(openPlaceDay);
        }
        return arrayListOfDays;
    }

    private int convertMeters(float m) {
        int meters;
        meters = Math.round(m);
        return meters;
    }
}
