package com.gacon.julien.go4lunch.controller.fragments.ListView;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.gacon.julien.go4lunch.R;
import com.gacon.julien.go4lunch.controller.activities.ProfileActivity;
import com.gacon.julien.go4lunch.controller.activities.auth.utils.BaseActivity;
import com.gacon.julien.go4lunch.controller.fragments.BaseFragment;
import com.gacon.julien.go4lunch.models.User;
import com.gacon.julien.go4lunch.view.lunchAdapter.LunchAdapter;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * ListViewFragment implement OnNoteListener for the RecyclerView Clickable
 */
public class ListViewFragment extends BaseFragment {

    @BindView(R.id.recycler_view_list_view)
    RecyclerView mRecyclerView;
    private BaseActivity baseActivity;
    private LunchAdapter adapter;
    private ArrayList<User> usersList;

    /**
     * Required empty public constructor
     */
    public ListViewFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list_view, container, false);
        ButterKnife.bind(this, view);
        baseActivity = (BaseActivity) getActivity();
        usersList = new ArrayList<>();
        setHasOptionsMenu(true);
        this.createRecyclerView();
        this.getUsersNames(adapter, usersList);
        return view;
    }

    /**
     * Create RecyclerView
     */
    private void createRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        assert baseActivity != null;
        adapter = new LunchAdapter(baseActivity.getModel(), this, usersList, getContext());
        adapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(adapter);
        autocomplete();
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
     * Add autocomplete search on fragment with details
     */
    private void autocomplete() {
        String type = "";
        float distanceInMetter = 0;
        ProfileActivity profileActivity = (ProfileActivity) getActivity();
        baseActivity.getAutoComplete(R.id.autocomplete_fragment).setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                assert profileActivity != null;
                profileActivity.setLunch(autoCompleteNewLunchModel(place, baseActivity, type, distanceInMetter));
                createDetailFragment();
            }

            @Override
            public void onError(@NonNull Status status) {
                Toast.makeText(baseActivity, "No place detail found", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
