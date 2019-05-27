package com.gacon.julien.go4lunch.controller.activities.api;

import com.gacon.julien.go4lunch.models.Place;
import com.gacon.julien.go4lunch.models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class PlacesHelper {

    private static final String COLLECTION_NAME = "places";

    // --- COLLECTION REFERENCE ---

    public static CollectionReference getPlacesCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // --- CREATE ---

    public static Task<Void> createPlace(String uid, String placename) {
        Place userToCreate = new Place(uid, placename);
        return PlacesHelper.getPlacesCollection().document(uid).set(userToCreate);
    }

    // --- GET ---

    public static Task<DocumentSnapshot> getPlace(String uid){
        return PlacesHelper.getPlacesCollection().document(uid).get();
    }

    // --- UPDATE ---

    public static Task<Void> updatePlacename(String placename, String uid) {
        return PlacesHelper.getPlacesCollection().document(uid).update("placename", placename);
    }

    // --- DELETE ---

    public static Task<Void> deletePlace(String uid) {
        return PlacesHelper.getPlacesCollection().document(uid).delete();
    }
}
