package com.gacon.julien.go4lunch.controller.fragments.ListView;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.gacon.julien.go4lunch.R;
import com.gacon.julien.go4lunch.controller.activities.ProfileActivity;
import com.gacon.julien.go4lunch.controller.activities.auth.utils.BaseActivity;
import com.gacon.julien.go4lunch.models.LunchModel;
import com.gacon.julien.go4lunch.view.LunchAdapter;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * ListViewFragment implement OnNoteListener for the RecyclerView Clickable
 */
public class ListViewFragment extends Fragment implements LunchAdapter.OnNoteListener {

    @BindView(R.id.recycler_view_list_view)
    RecyclerView mRecyclerView;
    private BaseActivity baseActivity;
    private ProfileActivity profileActivity;

    public ListViewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list_view, container, false);
        ButterKnife.bind(this, view);
        baseActivity = (BaseActivity) getActivity();
        profileActivity = (ProfileActivity) getActivity();
        setHasOptionsMenu(true);
        this.createRecyclerView();
        return view;
    }

    private void createRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        assert baseActivity != null;
        LunchAdapter adapter = new LunchAdapter(baseActivity.getModel(), this);
        adapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(adapter);
    }

    /**
     * Here we can navigate to a new fragment and pass some data on Bundle if we need
     *
     * @param position Item position
     */
    @Override
    public void onNoteClick(int position) {
        Fragment detailsListView = new DetailsListViewFragment();
        setLunchList(position);
        Objects.requireNonNull(this.getActivity()).getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_main_frame_layout, detailsListView)
                .commit();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.menu.menu_activity);
        if (item != null)
            item.setVisible(false);
    }

    private void setLunchList(int position) {
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
                baseActivity.getModel().get(position).getDisanceInMeters());

        profileActivity.setLunch(lunch);
    }
}
