package com.gacon.julien.go4lunch.models;

import androidx.annotation.Nullable;

public class User {

    private String uid;
    private String username;
    @Nullable
    private String urlPicture;
    private String placeSelectedId;
    private String placeName;

    public User() {
    }

    public User(String uid, String username, String urlPicture, String placeSelectedId, String placeName) {
        this.uid = uid;
        this.username = username;
        this.urlPicture = urlPicture;
        this.placeSelectedId = placeSelectedId;
        this.placeName = placeName;
    }

    // --- GETTERS ---
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    // --- SETTERS ---
    public void setUsername(String username) {
        this.username = username;
    }

    public String getUrlPicture() {
        return urlPicture;
    }

    public void setUrlPicture(String urlPicture) {
        this.urlPicture = urlPicture;
    }


}
