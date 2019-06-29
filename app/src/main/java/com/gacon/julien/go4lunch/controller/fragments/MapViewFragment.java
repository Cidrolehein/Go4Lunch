package com.gacon.julien.go4lunch.controller.fragments;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.gacon.julien.go4lunch.R;
import com.gacon.julien.go4lunch.controller.activities.ProfileActivity;
import com.gacon.julien.go4lunch.controller.activities.api.UserHelper;
import com.gacon.julien.go4lunch.controller.activities.auth.utils.BaseActivity;
import com.gacon.julien.go4lunch.models.User;
import com.google.android.gms.common.api.Status;
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
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import butterknife.ButterKnife;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.gacon.julien.go4lunch.controller.activities.ProfileActivity.REQUEST_SELECT_PLACE;

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
    private HashMap<String, Marker> markerHashMap;
    private ArrayList<String> markersPlacesIdsList;
    private int markerTag;


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
        setHasOptionsMenu(true); // need to change the toolbar
        createMapView(view, savedInstanceState); // add Google Map


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
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

    /**
     * Search Menu
     *
     * @param menu Menu
     */
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.menu.menu_activity);
        if (item != null)
            item.setVisible(false);
    }

    /**
     * Create a new menu for search tools
     *
     * @param menu     Toolbar
     * @param inflater Menu search on Toolbar
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_fagment_map, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * Create Google Map
     *
     * @param view               MapView
     * @param savedInstanceState getData from bundle
     */
    private void createMapView(View view, Bundle savedInstanceState) {
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
                // initialize GoogleMap
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
                    if (baseActivity.getModel() != null){
                        getMarkers();
                    }
                    getAllPlacesSelected();
                    // Set a listener for info window events
                    initializeLunchListAndCreatDetailFragment(mMap);
                } else {
                    // A local method to request required permissions;
                    // See https://developer.android.com/training/permissions/requesting
                    baseActivity.getLocationPermission();
                }
            }
        });
    }

    /**
     * add data on lunch model and build details fragment
     *
     * @param mMap GoogleMap
     */
    private void initializeLunchListAndCreatDetailFragment(GoogleMap mMap) {
        mMap.setOnInfoWindowClickListener(marker -> {
            Integer markerTag = (Integer) marker.getTag();
            if (markerTag != null)
                setLunchList(markerTag);
            baseActivity.createDetailFragment();
        });
    }

    /**
     * Put all markers on the map
     */
    private void getMarkers() {
        assert baseActivity != null;
        markersPlacesIdsList = new ArrayList<>();
        markerHashMap = new HashMap<>();
        // Get Current Location
        baseActivity.getDeviceLocation();
        int modelSize = baseActivity.getModel().size();
        markerTag = 0;
        for (int i = 0; i < modelSize; i++) {
            createAllMarkersWithOptions(i);
            String markerId = baseActivity.getModel().get(i).getPlaceId();
            // create a list of markers ids
            markersPlacesIdsList.add(markerId);
        }
        // For dropping a marker at a point on the Map
        LatLng currentPosition = new LatLng(baseActivity.getCurrentLatitude(), baseActivity.getCurrentLongitude());
        // For zooming automatically to the location of the marker
        getCameraPosition(currentPosition);
    }

    /**
     * Change the camera position on the map
     *
     * @param position new position LatLng
     */
    private void getCameraPosition(LatLng position) {
        // For zooming automatically to the location of the marker
        CameraPosition cameraPosition = new CameraPosition.Builder().target(position).zoom(17).build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    /**
     * Create marker with options
     *
     * @param position return the select place
     */
    private void createAllMarkersWithOptions(int position) {
        LatLng latLng = baseActivity.getLatLngArrayList().get(position);
        String markerTitle = baseActivity.getModel().get(position).getTitle();
        if (latLng != null && markerTitle != null) {
            // Add marker on map
            Marker marker = googleMapMarker(latLng, markerTitle);
            // set marker to identify the position on the detail list
            marker.setTag(markerTag);
            markerTag = markerTag + 1;
            // make hashmap to put all markers
            String placeId = baseActivity.getModel().get(position).getPlaceId();
            markerHashMap.put(placeId, marker);
        }
    }

    /**
     * Create a new marker on the map
     *
     * @param latLng      marker position
     * @param markerTitle marker title
     * @return return new marker
     */
    private Marker googleMapMarker(LatLng latLng, String markerTitle) {
        return googleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(markerTitle));
    }

    /**
     * Handle actions on menu items
     *
     * @param item search
     * @return item selected
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_fragment_map) {
            Toast.makeText(getContext(), "Fragment map", Toast.LENGTH_LONG).show();
            this.autocompleteIntent();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * When user select place on autocomplete place search
     *
     * @param requestCode need request code
     * @param resultCode  need result code
     * @param data        intent
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SELECT_PLACE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i("TAG", "onActivityResult: Activity successful " + place);

                ProfileActivity profileActivity = (ProfileActivity) getActivity();
                LatLng newMarkerLatlng = place.getLatLng(); // longitude and latitude of the place
                String newMarkerTitle = place.getName(); // name place
                float distanceinmetter = 0;
                String type = "";
                markerTag = markerTag + 1; // increment marker tag
                Marker newMarker = googleMapMarker(newMarkerLatlng, newMarkerTitle); // create a new marker place
                newMarker.setTag(markerTag); // with a new tag
                newMarker.showInfoWindow(); // show info windows enable
                markerHashMap.put(place.getId(), newMarker); // add marker on the hashmap
                getAllPlacesSelected(); // add the place selected
                getCameraPosition(newMarkerLatlng); // move the camera
                // marker clickable
                if (profileActivity != null && place.getOpeningHours() != null) {
                    googleMap.setOnInfoWindowClickListener(marker -> {
                        Integer markerTag = (Integer) marker.getTag();
                        if (markerTag != null)
                        // push data to details view fragment
                        {
                            profileActivity.setLunch
                                    (baseActivity.autoCompleteNewLunchModel(place, type, distanceinmetter));
                        }
                        baseActivity.createDetailFragment();
                    });
                }

            } else if (resultCode == RESULT_CANCELED) {
                Status status = Autocomplete.getStatusFromIntent(data);
                this.onErrorPlaceSelect(status);
            }
        }
    }

    /**
     * Get all places selected from firebase and create news green markers
     */
    private void getAllPlacesSelected() {
        UserHelper.getUsersCollection().addSnapshotListener((queryDocumentSnapshots, e) -> {
            if(queryDocumentSnapshots != null){
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    User user = documentSnapshot.toObject(User.class);
                    createNewGreenSelectedMarker(user);
                }
            }
        });
    }

    /**
     * create a new marker for places user selected
     *
     * @param user User from firebase
     */
    private void createNewGreenSelectedMarker(User user) {
        // get markers from UserPlaceId if user has selected a favorite place
        String userFavoritePlaceId = user.getPlaceSelectedId();
        // user place id can't be null and if the marker positioned
        if (userFavoritePlaceId != null && markersPlacesIdsList.contains(userFavoritePlaceId)) {
            Marker marker = markerHashMap.get(userFavoritePlaceId); // get marker
            assert marker != null;
            marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)); // change color of marker
        }

    }

}
