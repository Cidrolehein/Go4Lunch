package com.gacon.julien.go4lunch.controller.fragments.ListView;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gacon.julien.go4lunch.R;
import com.gacon.julien.go4lunch.controller.fragments.BaseFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailsListViewFragment extends BaseFragment {


    public DetailsListViewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_details_list_view, container, false);
    }

}
