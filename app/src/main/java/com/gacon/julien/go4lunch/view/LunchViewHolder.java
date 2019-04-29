package com.gacon.julien.go4lunch.view;

import android.graphics.Bitmap;
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
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
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
        if(newLunch.getPeriods() != null){
            this.mTextViewIsItOpen.setText(getDayOpen(newLunch));
        }
        if (newLunch.getPhotoMetadatasOfPlace() != null) {
            addImages(newLunch, newLunch.getPlaceId(), newLunch.getPlace(), newLunch.getPlacesClient());
        }

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

    private void addImages(LunchModel lunchModelPhotoMetaData, String id, Place place, PlacesClient placesClient) {
        List<Place.Field> fields = Collections.singletonList(lunchModelPhotoMetaData.getFieldList().get(6));
        FetchPlaceRequest placeRequest = FetchPlaceRequest.builder(id, fields).build();
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
        // Get the current day
        Calendar calendar = Calendar.getInstance();
        String weekDay = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        weekDay = weekDay.toUpperCase(); // Convert day to uppercase
        Log.i("Current day", "Current day is :" + weekDay);
        //Get the days open
        ArrayList<String> openDays = new ArrayList<String>();
        ArrayList<Integer> openHours = new ArrayList<>();
        int openHour;
        int closeHour;
        if (lunch.getPeriods() != null) {
            for (int i = 0; i < lunch.getPeriods().size(); i++) {
                openDays.add(Objects.requireNonNull(lunch.getPeriods().get(i).getOpen()).getDay().toString());
                if(lunch.getPeriods().get(i).getOpen() != null && lunch.getPeriods().get(i).getClose() != null){
                    // Get hour
                    openHour = Objects.requireNonNull(lunch.getPeriods().get(i).getOpen()).getTime().getHours();
                    closeHour = Objects.requireNonNull(lunch.getPeriods().get(i).getClose()).getTime().getHours();
                    // Change dateFormat to compare
                    SimpleDateFormat dateFormat = new SimpleDateFormat("hh", Locale.getDefault());
                    String dateCloseString = Integer.toString(closeHour);
                    String dateOpenString = Integer.toString(openHour);
                    String dateCurrentString = dateFormat.format(hour);
                    try {
                        Date dateClose = dateFormat.parse(dateCloseString);
                        Date dateOpen = dateFormat.parse(dateOpenString);
                        Date dateCurrent = dateFormat.parse(dateCurrentString);
                        // Compare the current day with open day
                        if (openDays.contains(weekDay) && dateOpen.before(dateCurrent)) {
                            isItOpen = "Open until " + closeHour + "h";
                        } else isItOpen = "Close until " + openHour + "h";

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                }
        }
        return isItOpen;
    }
}
