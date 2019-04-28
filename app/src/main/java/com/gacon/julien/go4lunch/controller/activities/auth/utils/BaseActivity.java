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
import com.google.android.libraries.places.api.model.DayOfWeek;
import com.google.android.libraries.places.api.model.Period;
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
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

/**
 * firebase users checking
 */
public class BaseActivity extends AppCompatActivity {

    protected static final int RC_SIGN_IN = 123;
    private static final String TAG = "PLACES_API";
    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 123;
    private ArrayList arrayListPlaceId;
    //FOR DATA
    // - Identifier for Sign-In Activity
    protected List<Place.Field> placeFields;
    protected List<Place.Field> placesFields;
    protected ArrayList<LunchModel> placesNameList, placesAddressList;
    protected LunchAdapter mAdapter;
    // Places API
    PlacesClient mPlacesClient;
    PlacesClient mPlacesDetails;
    // Define a Place ID.
    private String placeId = "INSERT_PLACE_ID_HERE";

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
        mPlacesDetails = Places.createClient(this);
    }

    protected void getCurrentPlaces() {
        // Use fields to define the data types to return.
        placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS);
        placesFields = Arrays.asList(Place.Field.ID,
                Place.Field.NAME,
                Place.Field.ADDRESS,
                Place.Field.OPENING_HOURS,
                Place.Field.TYPES);
        placesNameList = new ArrayList<>();

// Use the builder to create a FindCurrentPlaceRequest.
        FindCurrentPlaceRequest request =
                FindCurrentPlaceRequest.builder(placeFields).build();

// Call findCurrentPlace and handle the response (first check that the user has granted permission).
        arrayListPlaceId = new ArrayList();
        if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Task<FindCurrentPlaceResponse> placeResponse = mPlacesClient.findCurrentPlace(request);
            placeResponse.addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    FindCurrentPlaceResponse response = task.getResult();
                    for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
                        String attributions = "";
                        Log.i(TAG, String.format("Place '%s' with Id",
                                placeLikelihood.getPlace().getId()));
                                placeLikelihood.getPlace().getName();
                        // Get place detail for current hour
                        placeId = placeLikelihood.getPlace().getId();
                        arrayListPlaceId.add(placeId);
                        String placeName = placeLikelihood.getPlace().getName();
                        String placeAddress = placeLikelihood.getPlace().getAddress();
                    }
                } else {
                        Exception exception = task.getException();
                        if (exception instanceof ApiException) {
                            ApiException apiException = (ApiException) exception;
                            Log.e(TAG, "Place not found: " + apiException.getStatusCode());
                        }
                }
                for (int i = 0; i < arrayListPlaceId.size(); i++){
                    placeId = (String) arrayListPlaceId.get(i);
                    if (placeId != null) {
                        FetchPlaceRequest requestById = FetchPlaceRequest.builder(placeId, placesFields).build();
                        // Construct a request object, passing the place ID and fields array.
                        mPlacesDetails.fetchPlace(requestById).addOnSuccessListener((responseId) -> {
                            Place place = responseId.getPlace();
                                Log.i(TAG, "Place found: " + place.getName());
                                List<Period> periodList = new ArrayList<>();
                                if (place.getOpeningHours() != null) {
                                    periodList = place.getOpeningHours().getPeriods();
                                } else periodList = null;
                                ArrayList<LunchModel> model;
                                model = new ArrayList<>();
                                model.add(new LunchModel(place.getName(),
                                        place.getAddress(),
                                        periodList,
                                        place.getTypes().get(0).toString()));
                                updateUi(model);
                        });
                    }
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
