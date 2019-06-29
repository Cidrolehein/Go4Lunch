package com.gacon.julien.go4lunch.models;

import androidx.annotation.Nullable;

public class User {

    private String uid;
    private String username;
    @Nullable
    private String urlPicture;
    private String placeSelectedId;
    private String placeName;
    private String placeSelectedAddress;

    public User() {
    }

    public User(String uid, String username, String urlPicture, String placeSelectedId, String placeName,
                String placeSelectedAddress) {
        this.uid = uid;
        this.username = username;
        this.urlPicture = urlPicture;
        this.placeSelectedId = placeSelectedId;
        this.placeName = placeName;
        this.placeSelectedAddress = placeSelectedAddress;
    }

    public String getUsername() {
        return username;
    }

    public String getPlaceSelectedId() {
        return placeSelectedId;
    }

    public String getPlaceName() {
        return placeName;
    }

    public String getUrlPicture() {
        return urlPicture;
    }

    public String getPlaceSelectedAddress() {
        return placeSelectedAddress;
    }
}
