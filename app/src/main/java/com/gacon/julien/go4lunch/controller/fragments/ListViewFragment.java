package com.gacon.julien.go4lunch.controller.fragments;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.gacon.julien.go4lunch.R;
import com.gacon.julien.go4lunch.models.LunchModel;
import com.gacon.julien.go4lunch.view.LunchAdapter;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
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
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListViewFragment extends Fragment {

    private static final String TAG = "PLACES_API";
    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 123;
    @BindView(R.id.recycler_view_list_view)
    RecyclerView mRecyclerView;
    private LunchAdapter mAdapter;
    private List<Place.Field> placesFields;
    private ArrayList<LunchModel> placesNameList;
    // Places API
    private PlacesClient mPlacesClient;
    private PlacesClient mPlacesDetails;
    private ArrayList<String> arrayListPlaceId;
    // Define a Place ID.
    private String placeId = "INSERT_PLACE_ID_HERE";
    private Location currentLocation;

    public ListViewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list_view, container, false);
        ButterKnife.bind(this, view);
        // Initialize Places
        this.initPlaces();
        this.getCurrentPlaces();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new LunchAdapter(this.placesNameList);
        mRecyclerView.setAdapter(mAdapter);
        return view;
    }

    private void initPlaces() {
        // Initialize Places. TODO : secure api key
        Places.initialize(Objects.requireNonNull(getActivity()), "AIzaSyD95VyLsXgPACvDl1lAhl85lI3U2v6vNHs");
        // Create a new Places client instance.
        mPlacesClient = Places.createClient(getActivity());
        mPlacesDetails = Places.createClient(getActivity());
    }

    /**
     * Call findCurrentPlace and handle the response (first check that the user has granted permission).
     * Call FetchPlaceRequest to get place details
     */
    private void getCurrentPlaces() {
        // Use fields to define the data types to return.
        List<Place.Field> placeFields = Collections.singletonList(Place.Field.ID);
        // Places fields to Place Details
        placesFields = Arrays.asList(Place.Field.ID,
                Place.Field.NAME,
                Place.Field.ADDRESS,
                Place.Field.OPENING_HOURS,
                Place.Field.TYPES,
                Place.Field.RATING,
                Place.Field.PHOTO_METADATAS,
                Place.Field.WEBSITE_URI,
                Place.Field.LAT_LNG);
        placesNameList = new ArrayList<>();

// Use the builder to create a FindCurrentPlaceRequest.
        FindCurrentPlaceRequest request =
                FindCurrentPlaceRequest.builder(placeFields).build();

        if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getActivity()), ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getDeviceLocation();
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
                            if(place.getLatLng() != null && currentLocation != null) {
                                Location placeLocation = new Location("");
                                placeLocation.setLatitude(place.getLatLng().latitude);
                                placeLocation.setLongitude(place.getLatLng().longitude);
                                distanceInMeters = currentLocation.distanceTo(placeLocation);
                            }
                            // select a type of interest
                            if (Objects.requireNonNull(place.getTypes()).toString().contains("RESTAURANT")
                                    || Objects.requireNonNull(place.getTypes()).toString().contains("FOOD")
                                    || Objects.requireNonNull(place.getTypes()).toString().contains("BAKERY")) {
                                ArrayList<LunchModel> model;
                                model = new ArrayList<>();
                                model.add(new LunchModel(place.getName(),
                                        place.getAddress(),
                                        periodList,
                                        Objects.requireNonNull(place.getTypes()).get(0).toString(),
                                        rating,
                                        place.getPhotoMetadatas(),
                                        place.getWebsiteUri(),
                                        placesFields,
                                        placeId,
                                        place,
                                        mPlacesClient,
                                        distanceInMeters));
                                updateUi(model);
                            }
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

    private void getDeviceLocation() {
        // For location
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(Objects.requireNonNull(getActivity()));
        try {
            Task location = fusedLocationProviderClient.getLastLocation();
            location.addOnCompleteListener(getActivity(), task -> {
                if (task.isSuccessful()) {
                    currentLocation = (Location) task.getResult();
                    assert currentLocation != null;
            }
        });
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation : SecurityException: " + e.getMessage());
        }
    }

    private void getLocationPermission() {
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

    private void updateUi(List<LunchModel> lunchPlaces) {
        placesNameList.addAll(lunchPlaces);
        mAdapter.notifyDataSetChanged();
    }
}
