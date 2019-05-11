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
import com.gacon.julien.go4lunch.controller.fragments.BaseFragment;
import com.gacon.julien.go4lunch.view.LunchAdapter;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * ListViewFragment implement OnNoteListener for the RecyclerView Clickable
 */
public class ListViewFragment extends BaseFragment implements LunchAdapter.OnNoteListener {

    @BindView(R.id.recycler_view_list_view)
    RecyclerView mRecyclerView;

    public ListViewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list_view, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);
        // Initialize Places
        this.initPlaces();
        this.getCurrentPlaces();
        this.createRecyclerView();
        return view;
    }

    private void createRecyclerView(){
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new LunchAdapter(this.placesNameList, this);
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * Here we can navigate to a new fragment and pass some data on Bundle if we need
     * @param position Item position
     */
    @Override
    public void onNoteClick(int position) {
        //placesNameList.get(position); // If we have to pass data. TODO : create parcelable ArrayList<LunchModel> placesNameList
        Fragment myFragment = new DetailsListViewFragment();
        Objects.requireNonNull(this.getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.activity_main_frame_layout,
                myFragment).commit();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item=menu.findItem(R.menu.menu_activity);
        if(item!=null)
            item.setVisible(false);
    }
}
