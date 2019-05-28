package com.gacon.julien.go4lunch.controller.fragments;

import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gacon.julien.go4lunch.R;
import com.gacon.julien.go4lunch.controller.activities.api.UserHelper;
import com.gacon.julien.go4lunch.controller.activities.auth.utils.BaseActivity;
import com.gacon.julien.go4lunch.models.User;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapViewFragment extends BaseFragment {

    private static final String TAG = "PLACES_API";
    // FOR DESIGN
    private MapView mMapView;
    // Google Map
    private GoogleMap googleMap;
    private BaseActivity baseActivity;
    private ArrayList<String> mUserArrayList;


    public MapViewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map_view, container, false);
        ButterKnife.bind(this, view);
        baseActivity = (BaseActivity) getActivity();
        // MapView
        mMapView = view.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(Objects.requireNonNull(getActivity()).getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {

            /**
             * Manipulates the map when it's available.
             * The API invokes this callback when the map is ready for use.
             */
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                try {
                    // Customise the styling of the base map using a JSON object defined
                    // in a raw resource file.
                    boolean success = googleMap.setMapStyle(
                            MapStyleOptions.loadRawResourceStyle(
                                    Objects.requireNonNull(getActivity()), R.raw.json_style_map));

                    if (!success) {
                        Log.e(TAG, "Style parsing failed.");
                    }
                } catch (Resources.NotFoundException e) {
                    Log.e(TAG, "Can't find style. Error: ", e);
                }

                if (baseActivity.checkLocationPermission()) {
                    // For showing a move to my location button
                    googleMap.setMyLocationEnabled(true);

                    // Create GoogleMap Markers Clickable
                    getLongLat();
                    // Set a listener for info window events
                    mMap.setOnInfoWindowClickListener(marker -> {
                        Integer markerTag = (Integer) marker.getTag();
                        if (markerTag != null)
                            setLunchList(markerTag);
                        createDetailFragment();
                    });


                } else {
                    // A local method to request required permissions;
                    // See https://developer.android.com/training/permissions/requesting
                    baseActivity.getLocationPermission();
                }
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        getAllPlacesSelected();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
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

    private void getLongLat() {
        assert baseActivity != null;
        // Get Current Location
        baseActivity.getDeviceLocation();
        int modelSize = baseActivity.getModel().size();
        int markerTag = 0;
        for (int i = 0; i < modelSize; i++) {
            LatLng latLng = baseActivity.getLatLngArrayList().get(i);
            String markerTitle = baseActivity.getModel().get(i).getTitle();
            if (latLng != null && markerTitle != null) {
                Marker marker = googleMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(markerTitle)
                        .snippet("Marker Description"));
                marker.setTag(markerTag);
                markerTag = markerTag + 1;
                HashMap markerHashMap = new HashMap();
                String placeId = baseActivity.getModel().get(i).getPlaceId();
                markerHashMap.put(placeId,marker);
                // mettre dans all places selected
                //markerHashMap.get()
                boolean isPlaceSelected = mUserArrayList.contains(placeId);
                if (isPlaceSelected) {
                    marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                }
            }
        }

        // For dropping a marker at a point on the Map
        LatLng currentPosition = new LatLng(baseActivity.getCurrentLatitude(), baseActivity.getCurrentLongitude());
        // For zooming automatically to the location of the marker
        CameraPosition cameraPosition = new CameraPosition.Builder().target(currentPosition).zoom(19).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private void getAllPlacesSelected() {
        mUserArrayList = new ArrayList<>();
        // - Get all places selected
        UserHelper.getUsersCollection().addSnapshotListener((queryDocumentSnapshots, e) -> {

            mUserArrayList.clear();
            assert queryDocumentSnapshots != null;
            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                User user = documentSnapshot.toObject(User.class);
                mUserArrayList.add(user.getPlaceSelectedId());
            }
        });
    }

}
