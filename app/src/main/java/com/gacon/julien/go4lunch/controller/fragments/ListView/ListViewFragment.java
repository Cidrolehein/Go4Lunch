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

import com.gacon.julien.go4lunch.R;
import com.gacon.julien.go4lunch.controller.activities.auth.utils.BaseActivity;
import com.gacon.julien.go4lunch.controller.fragments.BaseFragment;
import com.gacon.julien.go4lunch.view.LunchAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * ListViewFragment implement OnNoteListener for the RecyclerView Clickable
 */
public class ListViewFragment extends BaseFragment {

    @BindView(R.id.recycler_view_list_view)
    RecyclerView mRecyclerView;
    private BaseActivity baseActivity;

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

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.menu.menu_activity);
        if (item != null)
            item.setVisible(false);
    }

}
