package com.gacon.julien.go4lunch.controller.activities.api;

import com.gacon.julien.go4lunch.models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserHelper {

    private static final String COLLECTION_NAME = "users";

    // --- COLLECTION REFERENCE ---

    public static CollectionReference getUsersCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // --- CREATE ---

    public static Task<Void> createUser(String uid, String username,
                                        String urlPicture, String placeSelectedId, String placeName,
                                        String placeAddress) {
        // - Create User object
        User userToCreate = new User(uid, username, urlPicture, placeSelectedId, placeName, placeAddress);
        // - Add a new User Document to Firestore
        return UserHelper.getUsersCollection()
                .document(uid) // Setting uID for Document
                .set(userToCreate); // Setting object for Document
    }

    public static Task<Void> updatePlaceSelectedId(String placeSelectedId, String uid) {
        return UserHelper.getUsersCollection().document(uid).update("placeSelectedId", placeSelectedId);
    }

    public static Task<Void> updatePlaceName(String placeName, String uid) {
        return UserHelper.getUsersCollection().document(uid).update("placeName", placeName);
    }

    public static Task<Void> updatePlaceSelectedAddress(String placeSelectedAddress, String uid) {
        return UserHelper.getUsersCollection().document(uid).update("placeSelectedAddress", placeSelectedAddress);
    }

}
