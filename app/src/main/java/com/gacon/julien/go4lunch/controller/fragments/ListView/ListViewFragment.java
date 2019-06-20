package com.gacon.julien.go4lunch.controller.fragments.ListView;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.gacon.julien.go4lunch.controller.activities.auth.utils.BaseActivity;
import com.gacon.julien.go4lunch.controller.fragments.BaseFragment;
import com.gacon.julien.go4lunch.models.User;
import com.gacon.julien.go4lunch.view.lunchAdapter.LunchAdapter;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.gacon.julien.go4lunch.controller.activities.ProfileActivity.REQUEST_SELECT_PLACE;

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
        this.createRecyclerView();
        this.getUsersNames(adapter, usersList);
        setHasOptionsMenu(true);
        return view;
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
        inflater.inflate(R.menu.menu_fagment_listview, menu);
        super.onCreateOptionsMenu(menu, inflater);
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
     * Handle actions on menu items
     *
     * @param item search
     * @return item selected
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_fragment_listview) {
            Toast.makeText(getContext(), "Fragment List View", Toast.LENGTH_LONG).show();
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
                String type = ""; // no need in this case
                float distanceInMetter = 0; // no need in this case
                ProfileActivity profileActivity = (ProfileActivity) getActivity();
                if(profileActivity != null && place.getOpeningHours() != null) {
                    profileActivity.setLunch(baseActivity.autoCompleteNewLunchModel(place, type, distanceInMetter));
                    baseActivity.createDetailFragment();
                }
            } else if (resultCode == RESULT_CANCELED) {
                Status status = Autocomplete.getStatusFromIntent(data);
                this.onErrorPlaceSelect(status);
            }
        }
    }

}
