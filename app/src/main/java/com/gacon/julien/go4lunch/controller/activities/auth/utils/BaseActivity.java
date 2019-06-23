package com.gacon.julien.go4lunch.controller.activities.auth.utils;

import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.firebase.ui.auth.AuthUI;
import com.gacon.julien.go4lunch.R;
import com.gacon.julien.go4lunch.controller.fragments.ListView.DetailsListViewFragment;
import com.gacon.julien.go4lunch.controller.fragments.MapViewFragment;
import com.gacon.julien.go4lunch.models.LunchModel;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
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
import java.util.List;
import java.util.Objects;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;


/**
 * firebase users checking
 */
public class BaseActivity extends AppCompatActivity {

    // - Place API
    public static final String TAG = "PLACES_API";
    //FOR DATA
    // Firebase
    // - Identifier for Sign-In Activity
    protected static final int RC_SIGN_IN = 123;
    protected static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 123;
    public ArrayList<LunchModel> placesNameList;
    public List<Place.Field> placeDetailFields;
    // - Location
    protected Location currentLocation;
    protected double currentLatitude;
    protected double currentLongitude;
    protected PlacesClient mPlacesClient;
    protected PlacesClient mPlacesDetails;
    protected List<Place.Field> placeFields;
    protected List<Place.Field> placeAutoCompleteFields;
    protected FindCurrentPlaceRequest request;
    protected String placeId = "INSERT_PLACE_ID_HERE";
    protected ArrayList<String> arrayListPlaceId;
    protected Task<FindCurrentPlaceResponse> placeResponse;
    protected ArrayList<LatLng> latLngArrayList;
    protected ArrayList<LunchModel> model;
    protected Fragment mMapViewFragment, mListViewFragment, mWormatesFragment;

// --------------------
    // UTILS
    // --------------------

    // FIREBASE CONNECTION

    /**
     * User on firebase
     *
     * @return user connected
     */
    @Nullable
    public FirebaseUser getCurrentUser() {
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
                                        new AuthUI.IdpConfig.FacebookBuilder().build(),
                                        new AuthUI.IdpConfig.TwitterBuilder().build()))
                        .setIsSmartLockEnabled(false, true)
                        .build(),
                RC_SIGN_IN);
    }

    // USER LOCATION

    /**
     * Get the device location when the task is completed
     */
    public void getDeviceLocation() {
        // For location
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            Task<Location> location = fusedLocationProviderClient.getLastLocation();
            location.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Got last known location. In some rare situations this can be null.
                    // (ex. when Google Map is not allow)
                    currentLocation = task.getResult();
                    if (currentLocation != null) {
                        currentLatitude = currentLocation.getLatitude();
                        currentLongitude = currentLocation.getLongitude();
                    }
                }
            });
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation : SecurityException: " + e.getMessage());
        }
    }

    /**
     * Get the permission for location
     */
    public void getLocationPermission() {
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

    /**
     * Check the location permission
     *
     * @return true if the location is access
     */
    public boolean checkLocationPermission() {
        boolean checkPermission;
        checkPermission = ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        return checkPermission;
    }

    // GOOGLE PLACE

    /**
     * Initialize Google Places Client and Details
     */
    protected void initPlaces() {
        // Initialize Places with Google Api Key.
        String google_api_key = getString(R.string.google_maps_key);
        Places.initialize(this, google_api_key);
        // Create a new Places client instance.
        mPlacesClient = Places.createClient(this);
        mPlacesDetails = Places.createClient(this);
    }

    /**
     * Initialize current places and add to a Fields List
     */
    protected void initializeCurrentPlace() {
        // Use fields to define the data types to return.
        placeFields = Arrays.asList(Place.Field.ID,
                Place.Field.LAT_LNG,
                Place.Field.NAME,
                Place.Field.TYPES);
    }

    /**
     * Build a current place request
     */
    protected void findCurrentPlaceRequest() {
        // Use the builder to create a FindCurrentPlaceRequest.
        request = FindCurrentPlaceRequest.builder(placeFields).build();
    }

    /**
     * Initialize place details and add to a Fields List
     */
    protected void initializePlaceDetails() {
        // Places fields to Place Details
        placeDetailFields = Arrays.asList(Place.Field.ID,
                Place.Field.NAME,
                Place.Field.ADDRESS,
                Place.Field.OPENING_HOURS,
                Place.Field.TYPES,
                Place.Field.RATING,
                Place.Field.PHOTO_METADATAS,
                Place.Field.WEBSITE_URI,
                Place.Field.LAT_LNG,
                Place.Field.PHONE_NUMBER);
        placesNameList = new ArrayList<>();
    }

    /**
     * Get the current place id and put in a arraylist
     *
     * @param task Find current place response
     */
    protected void getCurrentPlaceId(Task<FindCurrentPlaceResponse> task) {
        if (task.isSuccessful()) {
            FindCurrentPlaceResponse response = task.getResult();
            assert response != null;
                for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
                    if (typeOfInterestForDetails(placeLikelihood)){
                        // Get place detail for current hour
                        placeId = placeLikelihood.getPlace().getId();
                        arrayListPlaceId.add(placeId);
                    }
            }
        } else {
            this.apiException(task);
        }
    }

    /**
     * Task find current place response
     */
    protected void taskFindCurrentPlaceResponse() {
        placeResponse = null;
        if (this.checkLocationPermission()) {
            placeResponse = mPlacesClient.findCurrentPlace(request);
        } else {
            // A local method to request required permissions;
            // See https://developer.android.com/training/permissions/requesting
            getLocationPermission();
        }

    }

    /**
     * Interests of type of places
     *
     * @param placeLikelihood Places
     * @return Type of interest
     */
    protected boolean typeOfInterestForDetails(PlaceLikelihood placeLikelihood) {
        boolean mTypeOfInterest;
        mTypeOfInterest = Objects.requireNonNull(placeLikelihood.getPlace().getTypes()).toString().contains("RESTAURANT");
        return mTypeOfInterest;
    }

    /**
     * Get the place details
     */
    protected void getPlaceDetails() {
        model = new ArrayList<>();
        latLngArrayList = new ArrayList<>();
        // Get place details from current places id
        for (int i = 0; i < arrayListPlaceId.size(); i++) {
            placeId = arrayListPlaceId.get(i);
            if (placeId != null) {
                FetchPlaceRequest requestById = FetchPlaceRequest.builder(placeId, placeDetailFields).build();
                // Construct a request object, passing the place ID and fields array.
                mPlacesDetails.fetchPlace(requestById).addOnSuccessListener((responseId) -> {
                    Place place = responseId.getPlace();
                    Log.i(TAG, "Place found: " + place.getName());
                    // Add period of hours
                    List<Period> periodList = null;
                    if (place.getOpeningHours() != null) {
                        periodList = new ArrayList<>(place.getOpeningHours().getPeriods());
                    }
                    // Add rating
                    double rating = 1;
                    if (place.getRating() != null) {
                        rating = place.getRating();
                    }
                    // Phone number
                    String phoneNumber = place.getPhoneNumber();
                    String detailPlaceId = place.getId();
                    // Create a new model
                        model.add(new LunchModel(place.getName(),
                                place.getAddress(),
                                periodList,
                                Objects.requireNonNull(place.getTypes()).get(0).toString(),
                                rating,
                                place.getPhotoMetadatas(),
                                place.getWebsiteUri(),
                                placeDetailFields,
                                detailPlaceId,
                                place,
                                mPlacesDetails,
                                distanceInMeter(place.getLatLng()), // Distance between place in meters
                                phoneNumber));
                        // For Google Maps
                        latLngArrayList.add(place.getLatLng());
                        // Open map fragment when all data completed
                        if (arrayListPlaceId.size() == model.size()){
                            getMapViewFragment();
                        }
                });
            }
        }
    }

    /**
     * Create a MapView Fragment
     */
    protected void getMapViewFragment(){
        if (mMapViewFragment == null) {
            mMapViewFragment = new MapViewFragment();
        }
        getFragment(mMapViewFragment);
    }

    /**
     * Get data and calculate the distance between in meters
     * @param placeLatLng Google Place Details
     * @return Distance in meters
     */
    public float distanceInMeter(LatLng placeLatLng){
        float distanceInMeters = 0;
        if (placeLatLng != null && currentLocation != null) {
            Location placeLocation = new Location("");
            placeLocation.setLatitude(placeLatLng.latitude);
            placeLocation.setLongitude(placeLatLng.longitude);
            distanceInMeters = currentLocation.distanceTo(placeLocation);
        }
        return distanceInMeters;
    }

    /**
     * Get Fragment
     *
     * @param selectedFragment Fragment selected
     */
    protected void getFragment(Fragment selectedFragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.activity_main_frame_layout,
                selectedFragment).commitAllowingStateLoss();
    }

    /**
     * Call findCurrentPlace and handle the response (first check that the user has granted permission).
     * Call FetchPlaceRequest to get place details
     */
    protected void getPlacesComplete() {
        arrayListPlaceId = new ArrayList<>();
        if (this.checkLocationPermission()) {
            getDeviceLocation();
            findCurrentPlaceRequest();
            taskFindCurrentPlaceResponse();
            placeResponse.addOnCompleteListener(task -> {
                getCurrentPlaceId(task);
                getPlaceDetails();

            });
        } else {
            // A local method to request required permissions;
            // See https://developer.android.com/training/permissions/requesting
            getLocationPermission();
            getDeviceLocation(); // get the location again when user allow location permission
        }
    }

    /**
     * Main method to get places
     */
    protected void getCurrentPlaces() {
        initializeCurrentPlace();
        initializePlaceDetails();
        findCurrentPlaceRequest();
        // Ad place Id of current places in arraylist
        getPlacesComplete();
    }

    // UTILS

    /**
     * Api exception
     *
     * @param task Current task
     */
    public void apiException(Task task) {
        Exception exception = task.getException();
        if (exception instanceof ApiException) {
            ApiException apiException = (ApiException) exception;
            Log.e(TAG, "Place not found: " + apiException.getStatusCode());
        }
    }

    // --------------------
    // FRAGMENTS
    // --------------------

    /**
     * Go to detail fragment when we click on description map marker
     */
    public void createDetailFragment() {
        Fragment detailsListView = new DetailsListViewFragment();
        this.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_main_frame_layout, detailsListView)
                .commit();
    }


    // --------------------
    // AUTOCOMPLETE
    // --------------------

    /**
     * New lunch model for autocomplete details
     *
     * @param place            lunch place
     * @param type             Type of the place (not necessary)
     * @param distanceinmetter distance between the place and the new place (not necessary in that case)
     * @return new lunch model
     */
    public LunchModel autoCompleteNewLunchModel(Place place, String type, float distanceinmetter) {
        return new LunchModel(place.getName(), place.getAddress(),
                Objects.requireNonNull(place.getOpeningHours()).getPeriods(),
                type, place.getRating(), place.getPhotoMetadatas(), place.getWebsiteUri(),
                getPlaceAutoCompleteFields(), place.getId(), place,
                getPlacesDetails(), distanceinmetter, place.getPhoneNumber());
    }

    // --------------------
    // ERROR HANDLER
    // --------------------

    public OnFailureListener onFailureListener() {
        return e -> Toast.makeText(getApplicationContext(),
                getString(R.string.error_unknown_error),
                Toast.LENGTH_LONG).show();
    }

    // GETTERS

    public ArrayList<LatLng> getLatLngArrayList() {
        return latLngArrayList;
    }

    public List<Place.Field> getPlaceAutoCompleteFields() {
        return placeAutoCompleteFields;
    }

    public ArrayList<LunchModel> getModel() {
        return model;
    }

    public double getCurrentLatitude() {
        return currentLatitude;
    }

    public double getCurrentLongitude() {
        return currentLongitude;
    }

    public PlacesClient getPlacesDetails() {
        return mPlacesDetails;
    }

}
