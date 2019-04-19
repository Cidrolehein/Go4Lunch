package com.gacon.julien.go4lunch.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LunchModel {

    @SerializedName("title")
    @Expose
    private String title;

    public String getTitle() {
        return title;
    }
}
