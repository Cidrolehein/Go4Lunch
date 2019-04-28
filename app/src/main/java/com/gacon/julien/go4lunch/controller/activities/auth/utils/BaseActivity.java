package com.gacon.julien.go4lunch.controller.activities.auth.utils;

import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

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
import com.google.android.libraries.places.api.model.Period;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
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

    //FOR DATA
    // - Identifier for Sign-In Activity
    protected static final int RC_SIGN_IN = 123;
    private static final String TAG = "PLACES_API";
    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 123;
    protected List<Place.Field> placeFields;
    protected List<Place.Field> placesFields;
    protected ArrayList<LunchModel> placesNameList;
    protected LunchAdapter mAdapter;
    // Places API
    PlacesClient mPlacesClient;
    PlacesClient mPlacesDetails;
    private ArrayList<String> arrayListPlaceId;
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

    /**
     * Call findCurrentPlace and handle the response (first check that the user has granted permission).
     * Call FetchPlaceRequest to get place details
     */
    protected void getCurrentPlaces() {
        // Use fields to define the data types to return.
        placeFields = Collections.singletonList(Place.Field.ID);
        // Places fields to Place Details
        placesFields = Arrays.asList(Place.Field.ID,
                Place.Field.NAME,
                Place.Field.ADDRESS,
                Place.Field.OPENING_HOURS,
                Place.Field.TYPES,
                Place.Field.RATING,
                Place.Field.PHOTO_METADATAS,
                Place.Field.WEBSITE_URI);
        placesNameList = new ArrayList<>();

// Use the builder to create a FindCurrentPlaceRequest.
        FindCurrentPlaceRequest request =
                FindCurrentPlaceRequest.builder(placeFields).build();

        if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Ad place Id of current places in arraylist
            arrayListPlaceId = new ArrayList<>();
            Task<FindCurrentPlaceResponse> placeResponse = mPlacesClient.findCurrentPlace(request);
            placeResponse.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FindCurrentPlaceResponse response = task.getResult();
                    assert response != null;
                    for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
                        Log.i(TAG, String.format("Place '%s' with Id",
                                placeLikelihood.getPlace().getId()));
                        placeLikelihood.getPlace().getName();
                        // Get place detail for current hour
                        placeId = placeLikelihood.getPlace().getId();
                        arrayListPlaceId.add(placeId);
                    }
                } else {
                    Exception exception = task.getException();
                    if (exception instanceof ApiException) {
                        ApiException apiException = (ApiException) exception;
                        Log.e(TAG, "Place not found: " + apiException.getStatusCode());
                    }
                }

                // Get place details from current places id
                for (int i = 0; i < arrayListPlaceId.size(); i++) {
                    placeId = arrayListPlaceId.get(i);
                    if (placeId != null) {
                        FetchPlaceRequest requestById = FetchPlaceRequest.builder(placeId, placesFields).build();
                        // Construct a request object, passing the place ID and fields array.
                        mPlacesDetails.fetchPlace(requestById).addOnSuccessListener((responseId) -> {
                            Place place = responseId.getPlace();
                            Log.i(TAG, "Place found: " + place.getName());
                            // Add period of hours
                            List<Period> periodList;
                            if (place.getOpeningHours() != null) {
                                periodList = place.getOpeningHours().getPeriods();
                            } else periodList = null;
                            // Add rating
                            double rating = 1;
                            if (place.getRating() != null) {
                                rating = place.getRating();
                            }
                            ArrayList<LunchModel> model;
                            model = new ArrayList<>();
                            model.add(new LunchModel(place.getName(),
                                    place.getAddress(),
                                    periodList,
                                    Objects.requireNonNull(place.getTypes()).get(0).toString(),
                                    rating,
                                    place.getPhotoMetadatas(),
                                    place.getWebsiteUri()));
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
                Toast toast = Toast.makeText(this, "Please accept your localisation", Toast.LENGTH_SHORT);
                toast.show();
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
            Toast toast = Toast.makeText(this, "Your permission has already been granted", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    protected void updateUi(List<LunchModel> lunchPlaces) {
        placesNameList.addAll(lunchPlaces);
        mAdapter.notifyDataSetChanged();
    }

}
