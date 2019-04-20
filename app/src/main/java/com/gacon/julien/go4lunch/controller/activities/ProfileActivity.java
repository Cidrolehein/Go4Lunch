package com.gacon.julien.go4lunch.controller.activities;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Adapter;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.gacon.julien.go4lunch.R;
import com.gacon.julien.go4lunch.controller.activities.auth.utils.BaseActivity;
import com.gacon.julien.go4lunch.models.LunchModel;
import com.gacon.julien.go4lunch.view.LunchAdapter;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProfileActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    // - FOR DESIGN
    @BindView(R.id.activity_main_toolbar)
    Toolbar toolbar;
    @BindView(R.id.profile_main_drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.profile_main_nav_view)
    NavigationView navigationView;
    @BindView(R.id.profile_main_bottom_navigation)
    BottomNavigationView bottomNavigationView;
    @BindView(R.id.recycler_view_list_view)
    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        // Configuring Toolbar, DrawerLayout and BottomView
        this.configureToolbar();
        this.configureDrawerLayout();
        this.configureBottomView();
        // Initialize Places
        this.initPlaces();
        this.getCurrentPlaces();
        // RecyclerView
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mAdapter = new LunchAdapter(this.placesNameList);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * Log Out Button
     */
    @OnClick(R.id.button_log_out)
    public void onClickSignOutButton() {
        this.signOutUserFromFirebase();
    }

    /**
     * Inflate the menu and add it to the Toolbar
     *
     * @param menu activity menu
     * @return the menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity, menu);
        return true;
    }

    /**
     * Handle actions on menu items
     *
     * @param item search
     * @return item selected
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_activity_main_search) {
            Toast.makeText(this, "No search", Toast.LENGTH_LONG).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Handle back click to close menu
     */
    @Override
    public void onBackPressed() {
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        // 4 - Handle Navigation Item Click
        int id = item.getItemId();

        switch (id) {
            case R.id.profile_lunch:
                break;
            case R.id.profile_sittings:
                break;
            case R.id.profile_logout:
                break;
            default:
                break;
        }

        this.drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    // --------------------
    // REST REQUESTS
    // --------------------

    /**
     * Create http requests (SignOut)
     */
    private void signOutUserFromFirebase() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnSuccessListener(this, this.updateUIAfterRESTRequestsCompleted());
    }

    // --------------------
    // UI
    // --------------------

    /**
     * Create OnCompleteListener called after tasks ended
     *
     * @return is REST Resquests completed ?
     */
    private OnSuccessListener<Void> updateUIAfterRESTRequestsCompleted() {
        return aVoid -> {
            finish();
            startActivity(new Intent(this, MainActivity.class));
        };
    }

    // ---------------------
    // CONFIGURATION
    // ---------------------

    /**
     * Update Main Fragment design TODO : create fragments
     *
     * @param integer Bottom View
     * @return Fragment
     */
    private Boolean updateMainFragment(Integer integer) {
        switch (integer) {
            /*
            case R.id.action_android:
                this.mainFragment.updateDesignWhenUserClickedBottomView(MainFragment.REQUEST_ANDROID);
                break;
            case R.id.action_logo:
                this.mainFragment.updateDesignWhenUserClickedBottomView(MainFragment.REQUEST_LOGO);
                break;
            case R.id.action_landscape:
                this.mainFragment.updateDesignWhenUserClickedBottomView(MainFragment.REQUEST_LANDSCAPE);
                break;
                */
        }
        return true;
    }

    /**
     * Add Toolbar
     */
    private void configureToolbar() {
        // Sets the Toolbar
        setSupportActionBar(toolbar);
    }

    /**
     * Configure Drawer Layout
     */
    private void configureDrawerLayout() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    /**
     * Configure NavigationView
     */
    private void configureNavigationView() {
        navigationView.setNavigationItemSelectedListener(this);
    }

    /**
     * Configure BottomNavigationView Listener
     */
    private void configureBottomView() {
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> updateMainFragment(item.getItemId()));
    }

}
