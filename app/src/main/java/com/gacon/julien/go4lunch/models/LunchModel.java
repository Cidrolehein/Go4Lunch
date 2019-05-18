package com.gacon.julien.go4lunch.models;

import android.graphics.Bitmap;
import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.DayOfWeek;
import com.google.android.libraries.places.api.model.Period;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class LunchModel implements Serializable {

    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("address")
    @Expose
    private String address;
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
    @SerializedName("Distance in Meters")
    @Expose
    private float mDisanceInMeters;
    @SerializedName("Phone")
    @Expose
    private String mPhoneNumber;


    public LunchModel(String title, String address, List<Period> periods, String type, Double rating,
                      List<PhotoMetadata> photoMetadatas, Uri websiteUri, List<Place.Field> placesFields,
                      String placeId, Place place, PlacesClient client, float distanceInMeters, String phone) {
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
        this.mDisanceInMeters = distanceInMeters;
        this.mPhoneNumber = phone;
    }

    public String getTitle() {
        return title;
    }

    public String getAddress() {
        return address;
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

    public List<Period> getPeriods() {
        return mPeriods;
    }

    public float getDisanceInMeters() {
        return mDisanceInMeters;
    }

    public Uri getWebsiteUriPlace() {
        return websiteUriPlace;
    }

    public String getPhoneNumber() {
        return mPhoneNumber;
    }
}
