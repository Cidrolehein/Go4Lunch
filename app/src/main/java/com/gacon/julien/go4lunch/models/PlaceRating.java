package com.gacon.julien.go4lunch.models;

public class PlaceRating {

    private String placeRating;
    private String placeId;

    public PlaceRating() {
        // Need empty constructor
    }


    public PlaceRating(String placeRating, String placeId) {
        this.placeRating = placeRating;
        this.placeId = placeId;
    }

    public String getPlaceRating() {
        return placeRating;
    }

    public void setPlaceRating(String placeRating) {
        this.placeRating = placeRating;
    }
}
