package com.gacon.julien.go4lunch.controller.activities.api;

import com.gacon.julien.go4lunch.models.PlaceRating;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class PlaceRatingHelper {

    private static final String COLLECTION_NAME = "PlaceRating";

    public static CollectionReference getPlaceRatingCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // create collection placeRating for rate each place by user
    public static Task<Void> createPlaceRating(String rating, String placeId){
        PlaceRating placeRatingToCreate = new PlaceRating(rating, placeId);
        return PlaceRatingHelper.getPlaceRatingCollection()
                .document(placeId)
                .set(placeRatingToCreate);
    }

    public static Task<DocumentSnapshot> getRating(String placeId){
        return PlaceRatingHelper.getPlaceRatingCollection().document(placeId).get();
    }
}
