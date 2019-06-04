package com.gacon.julien.go4lunch.controller.fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.gacon.julien.go4lunch.R;
import com.gacon.julien.go4lunch.models.User;
import com.gacon.julien.go4lunch.view.userAdapter.UserAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Workmates Fragment
 */
public class WorkmatesFragment extends BaseFragment {

    @BindView(R.id.recycler_user_view)
    RecyclerView mRecyclerView;
    // Users
    private ArrayList<User> mUserArrayList;
    private UserAdapter adapter;

    public WorkmatesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_workmates, container, false);
        ButterKnife.bind(this, view);
        this.createRecyclerView();
        getUsersNames(adapter, mUserArrayList);
        return view;
    }

    /**
     * Create RecyclerView of workmates
     */
    private void createRecyclerView() {
        mUserArrayList = new ArrayList<>();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new UserAdapter(mUserArrayList, Glide.with(this), getContext());
        mRecyclerView.setAdapter(adapter);
    }

}
