package com.gacon.julien.go4lunch.controller.fragments;

import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.gacon.julien.go4lunch.R;
import com.gacon.julien.go4lunch.models.LunchModel;
import com.gacon.julien.go4lunch.view.LunchAdapter;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Response;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Period;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public abstract class BaseFragment extends Fragment {

    protected static final String TAG = "PLACES_API";
    protected static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 123;
    protected LunchAdapter mAdapter;
    protected Location currentLocation;
    // Places API
    protected FindCurrentPlaceRequest request;
    protected List<Place.Field> placeFields;
    protected List<Place.Field> placeDetailFields;
    protected ArrayList<LunchModel> placesNameList;
    protected PlacesClient mPlacesClient;
    protected PlacesClient mPlacesDetails;
    protected ArrayList<String> arrayListPlaceId;
    protected Task<FindCurrentPlaceResponse> placeResponse;
    protected double currentLatitude;
    protected double currentLongitude;
    // Google Map
    protected GoogleMap googleMap;
    // Define a Place ID.
    protected String placeId = "INSERT_PLACE_ID_HERE";
    ArrayList<LatLng> latLngArrayList;

    protected void getDeviceLocation() {
        // For location
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(Objects.requireNonNull(getActivity()));
        try {
            Task location = fusedLocationProviderClient.getLastLocation();
            location.addOnCompleteListener(getActivity(), task -> {
                if (task.isSuccessful()) {
                    currentLocation = (Location) task.getResult();
                    currentLatitude = currentLocation.getLatitude();
                    currentLongitude = currentLocation.getLongitude();
                    assert currentLocation != null;
                }
            });
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation : SecurityException: " + e.getMessage());
        }
    }

    protected void getLocationPermission() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getActivity()),
                ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                Toast toast = Toast.makeText(getActivity(), "Please accept your localisation", Toast.LENGTH_SHORT);
                toast.show();
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_FINE_LOCATION);

                // MY_PERMISSIONS_REQUEST_FINE_LOCATION is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
            Toast toast = Toast.makeText(getActivity(), "Your permission has already been granted", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    protected void initPlaces() {
        // Initialize Places.
        String api_key = getString(R.string.google_maps_key);
        Places.initialize(Objects.requireNonNull(getActivity()), api_key);
        // Create a new Places client instance.
        mPlacesClient = Places.createClient(getActivity());
        mPlacesDetails = Places.createClient(getActivity());
    }

    protected void initializeCurrantPlace() {
        // Use fields to define the data types to return.
        placeFields = Arrays.asList(Place.Field.ID,
                Place.Field.LAT_LNG,
                Place.Field.NAME,
                Place.Field.TYPES);
    }

    protected void findCurrentPlaceRequest() {
        // Use the builder to create a FindCurrentPlaceRequest.
        request = FindCurrentPlaceRequest.builder(placeFields).build();
    }

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
                Place.Field.LAT_LNG);
        placesNameList = new ArrayList<>();
    }

    protected boolean checkLocationPermission() {
        boolean checkPermission;
        checkPermission = ContextCompat.checkSelfPermission(Objects.requireNonNull(getActivity()), ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        return checkPermission;
    }

    protected void getCurrentPlaceId(Task<FindCurrentPlaceResponse> task) {
        if (task.isSuccessful()) {
            FindCurrentPlaceResponse response = task.getResult();
            assert response != null;
            for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
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
    }

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

    protected boolean typeOfInterestForDetails(Place place) {
        boolean mTypeOfInterest;
        mTypeOfInterest = Objects.requireNonNull(place.getTypes()).toString().contains("RESTAURANT")
                || Objects.requireNonNull(place.getTypes()).toString().contains("FOOD")
                || Objects.requireNonNull(place.getTypes()).toString().contains("BAKERY");
        return mTypeOfInterest;
    }

    protected void apiException(Task task) {
        Exception exception = task.getException();
        if (exception instanceof ApiException) {
            ApiException apiException = (ApiException) exception;
            Log.e(TAG, "Place not found: " + apiException.getStatusCode());
        }
    }

    protected void getPlaceDetails() {
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
                    // Distance between place in meters
                    float distanceInMeters = 0;
                    if (place.getLatLng() != null && currentLocation != null) {
                        Location placeLocation = new Location("");
                        placeLocation.setLatitude(place.getLatLng().latitude);
                        placeLocation.setLongitude(place.getLatLng().longitude);
                        distanceInMeters = currentLocation.distanceTo(placeLocation);
                    }
                    // select a type of interest
                    if (typeOfInterestForDetails(place)) {
                        ArrayList<LunchModel> model;
                        model = new ArrayList<>();
                        model.add(new LunchModel(place.getName(),
                                place.getAddress(),
                                periodList,
                                Objects.requireNonNull(place.getTypes()).get(0).toString(),
                                rating,
                                place.getPhotoMetadatas(),
                                place.getWebsiteUri(),
                                placeDetailFields,
                                placeId,
                                place,
                                mPlacesClient,
                                distanceInMeters));
                        latLngArrayList = new ArrayList<>();
                        updateUi(model);
                    }
                });
            }
        }
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
        }
    }


    protected void getCurrentPlaces() {
        initializeCurrantPlace();
        initializePlaceDetails();
        findCurrentPlaceRequest();
        // Ad place Id of current places in arraylist
        getPlacesComplete();
    }

    protected void updateUi(List<LunchModel> lunchPlaces) {
        placesNameList.addAll(lunchPlaces);
        mAdapter.notifyDataSetChanged();
    }

}
