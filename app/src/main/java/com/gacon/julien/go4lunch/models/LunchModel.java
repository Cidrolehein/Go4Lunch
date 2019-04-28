package com.gacon.julien.go4lunch.models;

import android.graphics.Bitmap;

import com.google.android.libraries.places.api.model.DayOfWeek;
import com.google.android.libraries.places.api.model.Period;
import com.google.android.libraries.places.api.model.Place;
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

    public LunchModel(String title, String address, List<Period> periods, String type) {
        this.title = title;
        this.address = address;
        this.mPeriods = periods;
        this.place_type = type;
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
}
