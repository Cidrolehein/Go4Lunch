package com.gacon.julien.go4lunch.controller.fragments;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.gacon.julien.go4lunch.controller.activities.ProfileActivity;
import com.gacon.julien.go4lunch.controller.activities.api.UserHelper;
import com.gacon.julien.go4lunch.controller.activities.auth.utils.BaseActivity;
import com.gacon.julien.go4lunch.models.LunchModel;
import com.gacon.julien.go4lunch.models.User;
import com.gacon.julien.go4lunch.view.lunchAdapter.LunchAdapter;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;

import static com.gacon.julien.go4lunch.controller.activities.ProfileActivity.REQUEST_SELECT_PLACE;

/**
 * Abstract class to manage fragments
 */
public class BaseFragment extends Fragment implements LunchAdapter.OnNoteListener,
        GoogleMap.OnInfoWindowClickListener {

    /**
     * Here we can navigate to a new fragment and pass some data on Bundle if we need
     *
     * @param position Item position
     */
    @Override
    public void onNoteClick(int position) {
        BaseActivity baseActivity = (BaseActivity) getActivity();
        setLunchList(position);
        assert baseActivity != null;
        baseActivity.createDetailFragment();
    }

    /**
     * Info for map
     *
     * @param marker marker from the map
     */
    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(getContext(), "Info window clicked",
                Toast.LENGTH_SHORT).show();

    }

    /**
     * Set list of place depend on marker we clicked
     *
     * @param position Position of the list
     */
    void setLunchList(int position) {
        BaseActivity baseActivity = (BaseActivity) getActivity();
        ProfileActivity profileActivity = (ProfileActivity) getActivity();

        assert baseActivity != null && baseActivity.getModel().get(position).getPeriods() != null;
        LunchModel lunch = new LunchModel(baseActivity.getModel().get(position).getTitle(),
                baseActivity.getModel().get(position).getAddress(),
                baseActivity.getModel().get(position).getPeriods(),
                baseActivity.getModel().get(position).getPlace_type(),
                baseActivity.getModel().get(position).getPlace_rating(),
                baseActivity.getModel().get(position).getPhotoMetadatasOfPlace(),
                baseActivity.getModel().get(position).getWebsiteUriPlace(),
                baseActivity.getModel().get(position).getFieldList(),
                baseActivity.getModel().get(position).getPlaceId(),
                baseActivity.getModel().get(position).getPlace(),
                baseActivity.getModel().get(position).getPlacesClient(),
                baseActivity.getModel().get(position).getDisanceInMeters(),
                baseActivity.getModel().get(position).getPhoneNumber());

        assert profileActivity != null;
        profileActivity.setLunch(lunch);
    }

    /**
     * create data for recycler view
     *
     * @param adapter   recycler view adapter
     * @param arrayList array of data
     */
    protected void getUsersNames(RecyclerView.Adapter adapter, ArrayList<User> arrayList) {
        // - Get all users names
        UserHelper.getUsersCollection().addSnapshotListener((queryDocumentSnapshots, e) -> {
            arrayList.clear();
            assert queryDocumentSnapshots != null;
            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                User user = documentSnapshot.toObject(User.class);
                arrayList.add(user);
            }
            adapter.notifyDataSetChanged();
        });
    }

    /**
     * Get the AutoComplete Intent Place
     */
    protected void autocompleteIntent() {
        BaseActivity baseActivity = (BaseActivity) getActivity();
        RectangularBounds newRectangleBound = rectangularBoundOfCurrentLocation();
        assert baseActivity != null;
        Intent intent = new Autocomplete.IntentBuilder
                (AutocompleteActivityMode.OVERLAY,
                        baseActivity.placeDetailFields)
                .setLocationRestriction(newRectangleBound) // Location Restriction ~ 1000m
                .setTypeFilter(TypeFilter.ESTABLISHMENT) // Type Filter = businesses
                .build(Objects.requireNonNull(getContext()));
        startActivityForResult(intent, REQUEST_SELECT_PLACE);
    }

    /**
     * New area for place Search = 1000m around
     * @return RectangularBounds for LocationRestriction
     */
    private RectangularBounds rectangularBoundOfCurrentLocation(){
        BaseActivity baseActivity = (BaseActivity) getActivity();
        // Get new Latitude :
        double earth = 6378.137;  //radius of the earth in kilometer
        double pi = Math.PI;
        double m = meterInDegree(pi, earth);  //1 meter in degree

        assert baseActivity != null;
        double new_latitude_a = baseActivity.getCurrentLatitude() + (1000 * m);
        double new_latitude_b = baseActivity.getCurrentLatitude() + (-1000 * m);

        // Get new Longitude :

        double cos = Math.cos(baseActivity.getCurrentLatitude() * (pi / 180));

        double new_longitude_a = baseActivity.getCurrentLongitude()+ (1000 * m) / cos;
        double new_longitude_b = baseActivity.getCurrentLongitude() + (-1000 * m) / cos;

        // Create new RectangleBound

        return RectangularBounds.newInstance(
                new LatLng(new_latitude_b, new_longitude_b),
                new LatLng(new_latitude_a, new_longitude_a));
    }

    public double meterInDegree(double pi, double earth){
        return (1 / ((2 * pi / 360) * earth)) / 1000;
    }

    /**
     * Error on AutoComplete
     *
     * @param status Error status
     */
    protected void onErrorPlaceSelect(Status status) {
        Log.e("Log error", "onError: Status = " + status.toString());
        Toast.makeText(getContext(), "Place selection failed: " + status.getStatusMessage(),
                Toast.LENGTH_SHORT).show();
    }

}
