package com.gacon.julien.go4lunch.controller.activities.auth.utils;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.firebase.ui.auth.AuthUI;
import com.gacon.julien.go4lunch.R;
import com.gacon.julien.go4lunch.models.LunchModel;
import com.gacon.julien.go4lunch.view.LunchAdapter;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

/**
 * firebase users checking
 */
public class BaseActivity extends AppCompatActivity {

    protected static final int RC_SIGN_IN = 123;
    private static final String TAG = "PLACES_API";
    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 123;
    // Define a Place ID.
    private String placeId = "INSERT_PLACE_ID_HERE";
    //FOR DATA
    // - Identifier for Sign-In Activity
    protected List<Place.Field> placeFields;
    protected ArrayList<LunchModel> placesNameList, placesAddressList;
    protected LunchAdapter mAdapter;
    // Places API
    PlacesClient mPlacesClient;

// --------------------
    // UTILS
    // --------------------

    /**
     * User on firebase
     *
     * @return user connected
     */
    @Nullable
    protected FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    /**
     * Check if a user is connected
     *
     * @return true or false
     */
    protected Boolean isCurrentUserLogged() {
        return (this.getCurrentUser() != null);
    }

    /**
     * Launch Login Sign-In Activity
     */
    protected void startLoginSignInActivity() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setTheme(R.style.FirebaseConnection)
                        .setAvailableProviders(
                                Arrays.asList(
                                        new AuthUI.IdpConfig.GoogleBuilder().build(),
                                        new AuthUI.IdpConfig.FacebookBuilder().build()))
                        .setIsSmartLockEnabled(false, true)
                        .build(),
                RC_SIGN_IN);
    }

    protected void initPlaces() {
        // Initialize Places. TODO : secure api key
        Places.initialize(getApplicationContext(), "AIzaSyD95VyLsXgPACvDl1lAhl85lI3U2v6vNHs");
        // Create a new Places client instance.
        mPlacesClient = Places.createClient(this);
    }

    protected void getCurrentPlaces() {
        // Use fields to define the data types to return.
        placeFields = Arrays.asList(Place.Field.NAME,
                Place.Field.ID,
                Place.Field.ADDRESS,
                Place.Field.PHOTO_METADATAS,
                Place.Field.TYPES);
        placesNameList = new ArrayList<>();

// Use the builder to create a FindCurrentPlaceRequest.
        FindCurrentPlaceRequest request =
                FindCurrentPlaceRequest.builder(placeFields).build();

// Call findCurrentPlace and handle the response (first check that the user has granted permission).
        if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mPlacesClient.findCurrentPlace(request).addOnSuccessListener(((response) -> {
                for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
                    String attributions = "";
                    Log.i(TAG, String.format("Place '%s' with Id '%s' with address '%s' with photo metadata '%s' and type '%s' has likelihood: %f",
                            placeLikelihood.getPlace().getName(),
                            placeLikelihood.getPlace().getId(),
                            placeLikelihood.getPlace().getAddress(),
                            placeLikelihood.getPlace().getPhotoMetadatas(),
                            placeLikelihood.getPlace().getTypes(),
                            placeLikelihood.getLikelihood()));
// get place detail for current hour
                    if (placeLikelihood.getPlace().getId() != null){
                        placeId = placeLikelihood.getPlace().getId();
                        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.OPENING_HOURS);
                        // Construct a request object, passing the place ID and fields array.
                        FetchPlaceRequest requestById = FetchPlaceRequest.builder(placeId, placeFields).build();
                        mPlacesClient.fetchPlace(requestById).addOnSuccessListener((responseId) -> {
                            Place place = responseId.getPlace();
                            Log.i(TAG, "Place found: " + place.getName() + "with opening hour: " + place.getOpeningHours());
                        });
                    }

                    if (placeLikelihood.getPlace().getPhotoMetadatas() != null) {
                        // Get the photo metadata.
                        PhotoMetadata photoMetadata = placeLikelihood.getPlace().getPhotoMetadatas().get(0);

                        // Get the attribution text.
                        attributions = photoMetadata.getAttributions();

                        // Create a FetchPhotoRequest.
                        FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                                .setMaxWidth(100) // Optional.
                                .setMaxHeight(100) // Optional.
                                .build();
                        mPlacesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
                            Bitmap bitmap = fetchPhotoResponse.getBitmap();
                            Log.i(TAG, "Les images bitmap sont " + bitmap.toString());
                        }).addOnFailureListener((exception) -> {
                            if (exception instanceof ApiException) {
                                ApiException apiException = (ApiException) exception;
                                int statusCode = apiException.getStatusCode();
                                // Handle error with given status code.
                                Log.e(TAG, "Place not found: " + exception.getMessage());
                            }
                        });
                    }

                    placesNameList.add(new LunchModel(placeLikelihood.getPlace().getName(),
                            placeLikelihood.getPlace().getAddress(), attributions));
                }
                updateUi(placesNameList);
            })).addOnFailureListener((exception) -> {
                if (exception instanceof ApiException) {
                    ApiException apiException = (ApiException) exception;
                    Log.e(TAG, "Place not found: " + apiException.getStatusCode());
                }
            });
        } else {
            // A local method to request required permissions;
            // See https://developer.android.com/training/permissions/requesting
            getLocationPermission();
        }
    }

    protected void getLocationPermission() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_FINE_LOCATION);

                // MY_PERMISSIONS_REQUEST_FINE_LOCATION is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }
    }

    public ArrayList getPlacesNameList() {
        return placesNameList;
    }

    protected void updateUi(List<LunchModel> lunchPlaces) {
        placesNameList.addAll(lunchPlaces);
        mAdapter.notifyDataSetChanged();
    }

}
