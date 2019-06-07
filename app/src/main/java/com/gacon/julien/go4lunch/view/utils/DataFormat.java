package com.gacon.julien.go4lunch.view.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;

import com.gacon.julien.go4lunch.models.LunchModel;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.Objects;

/**
 * Format the datas from Lunch Model to Lunch View Holder
 */
public class DataFormat {

    /**
     * Format meters
     *
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
     *
     * @param m Float meters from lunch model
     * @return Meters to int
     */
    private int convertMeters(float m) {
        int meters;
        meters = Math.round(m);
        return meters;
    }

    /**
     * Get Rating Stars
     *
     * @param rating       Rating double of Place (0 - 3)
     * @param mStarRating1 ImageView One rating
     * @param mStarRating2 ImageView Two rating
     * @param mStarRating3 ImageView Three rating
     */
    public void getRatingStar(float rating,
                              ImageView mStarRating1,
                              ImageView mStarRating2,
                              ImageView mStarRating3) {
        if (rating == 0) {
            mStarRating1.setVisibility(View.GONE);
            mStarRating2.setVisibility(View.GONE);
            mStarRating3.setVisibility(View.GONE);
        } else if (rating == 1.0) {
            mStarRating1.setVisibility(View.VISIBLE);
            mStarRating2.setVisibility(View.GONE);
            mStarRating3.setVisibility(View.GONE);
        } else if (rating == 2.0) {
            mStarRating1.setVisibility(View.VISIBLE);
            mStarRating2.setVisibility(View.VISIBLE);
            mStarRating3.setVisibility(View.GONE);
        } else {
            mStarRating1.setVisibility(View.VISIBLE);
            mStarRating2.setVisibility(View.VISIBLE);
            mStarRating3.setVisibility(View.VISIBLE);
        }
    }

    /**
     * PlaceImage
     *
     * @param place        Get the Place Details
     * @param placesClient Get the current place
     * @param imageView    ImageView for image
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

    public String changeColorToHex(int resource, Context context) {
        return "#" + Integer.toHexString(ContextCompat
                .getColor(Objects.requireNonNull(context),
                        resource));
    }

    /**
     * Pass the old rate of string to float
     *
     * @param string      string of old rate
     * @return new float rate
     */
    public float passStringToFloat(String string) {
        float floatToPass = 0;
        if (string != null) {
            try {
                floatToPass = Float.valueOf(string);
            } catch (NumberFormatException ex) {
                ex.printStackTrace();
            }
        }
        return floatToPass;
    }

}
