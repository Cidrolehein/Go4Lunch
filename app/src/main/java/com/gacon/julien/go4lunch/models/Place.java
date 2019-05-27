package com.gacon.julien.go4lunch.models;

import androidx.annotation.Nullable;

public class Place {

    private String uid;
    private String placename;
    @Nullable
    private String urlPicture;

    public Place() { }

    public Place(String uid, String placename) {
        this.uid = uid;
        this.placename = placename;
    }

    // --- GETTERS ---
    public String getUid() { return uid; }
    public String getPlacename() { return placename; }
    public String getUrlPicture() { return urlPicture; }

    // --- SETTERS ---
    public void setPlacename(String username) { this.placename = placename; }
    public void setUid(String uid) { this.uid = uid; }
    public void setUrlPicture(String urlPicture) { this.urlPicture = urlPicture; }
}
