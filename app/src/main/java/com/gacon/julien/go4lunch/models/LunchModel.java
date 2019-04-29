package com.gacon.julien.go4lunch.models;

import android.graphics.Bitmap;
import android.net.Uri;

import com.google.android.libraries.places.api.model.DayOfWeek;
import com.google.android.libraries.places.api.model.Period;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class LunchModel {

    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("place_image")
    @Expose
    private String placeImage;
    @SerializedName("Opening_hours")
    @Expose
    private List<Period> mPeriods;
    @SerializedName("Place Type")
    @Expose
    private String place_type;
    @SerializedName("Rating")
    @Expose
    private double place_rating;
    @SerializedName("Photo")
    @Expose
    private List<PhotoMetadata> photoMetadatasOfPlace;
    @SerializedName("WebSite")
    @Expose
    private Uri websiteUriPlace;
    @SerializedName("ListField")
    @Expose
    private List<Place.Field> mFieldList;
    @SerializedName("Place id")
    @Expose
    private String mPlaceId;
    @SerializedName("Place")
    @Expose
    private Place mPlace;
    @SerializedName("Place client")
    @Expose
    private PlacesClient mPlacesClient;

    public LunchModel(String title, String address, List<Period> periods, String type, Double rating,
                      List<PhotoMetadata> photoMetadatas, Uri websiteUri, List<Place.Field> placesFields,
                      String placeId, Place place, PlacesClient client) {
        this.title = title;
        this.address = address;
        this.mPeriods = periods;
        this.place_type = type;
        this.place_rating = rating;
        this.photoMetadatasOfPlace = photoMetadatas;
        this.websiteUriPlace = websiteUri;
        this.mFieldList = placesFields;
        this.mPlaceId = placeId;
        this.mPlace = place;
        this.mPlacesClient = client;
    }

    public String getTitle() {
        return title;
    }

    public String getAddress() {
        return address;
    }

    public String getPlaceImage() {
        return placeImage;
    }

    public String getPlace_type() {
        return place_type;
    }

    public double getPlace_rating() {
        return place_rating;
    }

    public List<PhotoMetadata> getPhotoMetadatasOfPlace() {
        return photoMetadatasOfPlace;
    }

    public List<Place.Field> getFieldList() {
        return mFieldList;
    }

    public String getPlaceId() {
        return mPlaceId;
    }

    public Place getPlace() {
        return mPlace;
    }

    public PlacesClient getPlacesClient() {
        return mPlacesClient;
    }
}
