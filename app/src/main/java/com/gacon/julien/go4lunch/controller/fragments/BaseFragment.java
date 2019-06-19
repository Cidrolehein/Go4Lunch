package com.gacon.julien.go4lunch.controller.fragments;

import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.gacon.julien.go4lunch.R;
import com.gacon.julien.go4lunch.controller.activities.ProfileActivity;
import com.gacon.julien.go4lunch.controller.activities.api.UserHelper;
import com.gacon.julien.go4lunch.controller.activities.auth.utils.BaseActivity;
import com.gacon.julien.go4lunch.controller.fragments.ListView.DetailsListViewFragment;
import com.gacon.julien.go4lunch.models.LunchModel;
import com.gacon.julien.go4lunch.models.User;
import com.gacon.julien.go4lunch.view.lunchAdapter.LunchAdapter;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.libraries.places.api.model.Place;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;

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

        assert baseActivity != null;
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

}
