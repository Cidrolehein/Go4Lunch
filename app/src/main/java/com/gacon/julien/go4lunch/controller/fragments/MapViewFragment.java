package com.gacon.julien.go4lunch.controller.fragments;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gacon.julien.go4lunch.R;
import com.gacon.julien.go4lunch.models.LunchModel;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapViewFragment extends BaseFragment {

    // FOR DESIGN
    MapView mMapView;


    public MapViewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map_view, container, false);
        ButterKnife.bind(this, view);
        getLongLat();
        // MapView
        mMapView = (MapView) view.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(Objects.requireNonNull(getActivity()).getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {

            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getActivity()), ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    // For showing a move to my location button
                    googleMap.setMyLocationEnabled(true);

                } else {
                    // A local method to request required permissions;
                    // See https://developer.android.com/training/permissions/requesting
                    getLocationPermission();
                }
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    private boolean typeOfInterestForMap(PlaceLikelihood placeLikelihood) {
        boolean mTypeOfInterest;
        mTypeOfInterest = Objects.requireNonNull(placeLikelihood.getPlace().getTypes()).toString().contains("RESTAURANT")
                || Objects.requireNonNull(placeLikelihood.getPlace().getTypes()).toString().contains("FOOD")
                || Objects.requireNonNull(placeLikelihood.getPlace().getTypes()).toString().contains("BAKERY");
        return mTypeOfInterest;
    }

    private void getLongLat() {
        initPlaces();
        initializeCurrantPlace();
        findCurrentPlaceRequest();
        if (this.checkLocationPermission()) {
            // Get Current Location
            getDeviceLocation();
            // Get LatLng
            taskFindCurrentPlaceResponse();
            placeResponse.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FindCurrentPlaceResponse response = task.getResult();
                    assert response != null;
                    for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
                        if (placeLikelihood.getPlace().getLatLng() != null && typeOfInterestForMap(placeLikelihood)) {
                            LatLng latLng = placeLikelihood.getPlace().getLatLng();
                            googleMap.addMarker(new MarkerOptions().position(latLng).title("Marker Title").snippet("Marker Description"));
                            // For dropping a marker at a point on the Map
                            LatLng currentPosition = new LatLng(currentLatitude, currentLongitude);
                            // For zooming automatically to the location of the marker
                            CameraPosition cameraPosition = new CameraPosition.Builder().target(currentPosition).zoom(19).build();
                            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        }
                    }
                } else {
                    apiException(task);
                }
            });
        } else {
            // A local method to request required permissions;
            // See https://developer.android.com/training/permissions/requesting
            getLocationPermission();
        }
    }

}
