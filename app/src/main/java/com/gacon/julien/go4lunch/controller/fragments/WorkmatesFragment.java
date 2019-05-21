package com.gacon.julien.go4lunch.controller.fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.gacon.julien.go4lunch.R;
import com.gacon.julien.go4lunch.controller.activities.ProfileActivity;
import com.gacon.julien.go4lunch.controller.activities.auth.utils.BaseActivity;
import com.gacon.julien.go4lunch.view.lunchAdapter.LunchAdapter;
import com.gacon.julien.go4lunch.view.userAdapter.UserAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class WorkmatesFragment extends Fragment {

    @BindView(R.id.recycler_user_view)
    RecyclerView mRecyclerView;
    private ProfileActivity mProfileActivity;

    public WorkmatesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_workmates, container, false);
        ButterKnife.bind(this, view);
        mProfileActivity = (ProfileActivity) getActivity();
        this.createRecyclerView();
        return view;
    }

    private void createRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        assert mProfileActivity != null;
        UserAdapter adapter = new UserAdapter(mProfileActivity.getUserArrayList());
        adapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(adapter);
    }

}
