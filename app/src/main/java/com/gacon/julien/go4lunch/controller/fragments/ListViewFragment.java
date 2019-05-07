package com.gacon.julien.go4lunch.controller.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.gacon.julien.go4lunch.R;
import com.gacon.julien.go4lunch.view.LunchAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListViewFragment extends BaseFragment {

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
        // Initialize Places
        this.initPlaces();
        this.getCurrentPlaces();
        this.createRecyclerView();
        return view;
    }

    private void createRecyclerView(){
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new LunchAdapter(this.placesNameList);
        mRecyclerView.setAdapter(mAdapter);
    }

}
