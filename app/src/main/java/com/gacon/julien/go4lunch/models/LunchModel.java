package com.gacon.julien.go4lunch.models;

import android.graphics.Bitmap;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

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

    public LunchModel(String title, String address, String image) {
        this.title = title;
        this.address = address;
        this.placeImage = image;
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
}
