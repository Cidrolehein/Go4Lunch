package com.gacon.julien.go4lunch.controller.fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.gacon.julien.go4lunch.R;
import com.gacon.julien.go4lunch.controller.activities.api.UserHelper;
import com.gacon.julien.go4lunch.models.User;
import com.gacon.julien.go4lunch.view.userAdapter.UserAdapter;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class WorkmatesFragment extends Fragment {

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
        this.getUsersNames();
        return view;
    }

    private void createRecyclerView() {
        mUserArrayList = new ArrayList<>();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new UserAdapter(mUserArrayList, Glide.with(this));
        mRecyclerView.setAdapter(adapter);
    }

    private void getUsersNames(){
        // - Get all users names
        UserHelper.getUsersCollection().addSnapshotListener((queryDocumentSnapshots, e) -> {
            mUserArrayList.clear();
            assert queryDocumentSnapshots != null;
            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                User user = documentSnapshot.toObject(User.class);
                mUserArrayList.add(user);
            }
            adapter.notifyDataSetChanged();
        });
    }

}
