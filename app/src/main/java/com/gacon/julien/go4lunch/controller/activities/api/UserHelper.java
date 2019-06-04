package com.gacon.julien.go4lunch.controller.activities.api;

import com.gacon.julien.go4lunch.models.PlaceRating;
import com.gacon.julien.go4lunch.models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class UserHelper {

    private static final String COLLECTION_NAME = "users";
    private static final String SUB_COLLECTION_NAME = "PlaceRating";

    // --- COLLECTION REFERENCE ---

    public static CollectionReference getUsersCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // --- SUB-COLLECTION ---

    private static CollectionReference getPlaceRatingCollection(String uid){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME).document(uid).collection(SUB_COLLECTION_NAME);
    }

    // --- CREATE ---

    public static Task<Void> createUser(String uid, String username,
                                        String urlPicture, String placeSelectedId, String placeName) {
        // - Create User object
        User userToCreate = new User(uid, username, urlPicture, placeSelectedId, placeName);
        // - Add a new User Document to Firestore
        return UserHelper.getUsersCollection()
                .document(uid) // Setting uID for Document
                .set(userToCreate); // Setting object for Document
    }

    // create subcollection placeRating for rate each place by user
    public static Task<Void> createPlaceRating(String placeId, String rating, String uid){
        PlaceRating placeRatingToCreate = new PlaceRating(rating);
        return UserHelper.getPlaceRatingCollection(uid)
                .document(placeId)
                .set(placeRatingToCreate);
    }

    // --- GET ---

    public static Task<DocumentSnapshot> getUser(String uid){
        return UserHelper.getUsersCollection().document(uid).get();
    }

    public static Task<DocumentSnapshot> getRating(String placeId, String uid){
        return UserHelper.getPlaceRatingCollection(uid).document(placeId).get();
    }

    public static Query getAllRating(String uid){
        return UserHelper.getPlaceRatingCollection(uid)
                .document()
                .collection(SUB_COLLECTION_NAME)
                .limit(3);
    }

    // --- UPDATE ---

    public static Task<Void> updateUsername(String username, String uid) {
        return UserHelper.getUsersCollection().document(uid).update("username", username);
    }

    public static Task<Void> updatePlaceSelectedId(String placeSelectedId, String uid) {
        return UserHelper.getUsersCollection().document(uid).update("placeSelectedId", placeSelectedId);
    }

    public static Task<Void> updatePlaceName(String placeName, String uid) {
        return UserHelper.getUsersCollection().document(uid).update("placeName", placeName);
    }

    // --- DELETE ---

    public static Task<Void> deleteUser(String uid) {
        return UserHelper.getUsersCollection().document(uid).delete();
    }

}
