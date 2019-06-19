package com.gacon.julien.go4lunch.controller.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.gacon.julien.go4lunch.R;
import com.gacon.julien.go4lunch.controller.activities.auth.utils.BaseActivity;
import com.gacon.julien.go4lunch.controller.fragments.ListView.DetailsListViewFragment;
import com.gacon.julien.go4lunch.controller.fragments.ListView.ListViewFragment;
import com.gacon.julien.go4lunch.controller.fragments.MapViewFragment;
import com.gacon.julien.go4lunch.controller.fragments.WorkmatesFragment;
import com.gacon.julien.go4lunch.models.LunchModel;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final int REQUEST_SELECT_PLACE = 123;
    // - FOR DESIGN
    @BindView(R.id.activity_main_toolbar)
    Toolbar toolbar;
    @BindView(R.id.profile_main_drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.profile_main_nav_view)
    NavigationView navigationView;
    @BindView(R.id.profile_main_bottom_navigation)
    BottomNavigationView bottomNavigationView;
    // Data
    Fragment mMapViewFragment, mListViewFragment, mWormatesFragment;
    private LunchModel lunch;
    /**
     * For the Bottom Navigation Menu
     */
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            item -> {

                switch (item.getItemId()) {
                    case R.id.map_view:
                        if (mMapViewFragment == null) {
                            mMapViewFragment = new MapViewFragment();
                        }
                        getFragment(mMapViewFragment);
                        break;
                    case R.id.list_view:
                        if (mListViewFragment == null) {
                            mListViewFragment = new ListViewFragment();
                        }
                        getFragment(mListViewFragment);
                        break;
                    case R.id.workmates:
                        if (mWormatesFragment == null) {
                            mWormatesFragment = new WorkmatesFragment();
                        }
                        getFragment(mWormatesFragment);
                        break;
                }

                return true;
            };

    /**
     * for change status bar color
     *
     * @param activity this activity
     * @param bits     id
     * @param on       can switch on
     */
    public static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        // Configuring Toolbar, DrawerLayout and BottomView
        this.configureToolbar();
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        this.configureNavigationView();
        this.configureDrawerLayout();
        // Data for place API
        this.initPlaces();
        this.getCurrentPlaces();

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

    // --------------------
    // REST REQUESTS
    // --------------------

    /**
     * Handle actions on menu items
     *
     * @param item search
     * @return item selected
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_activity_main_search:
                Toast.makeText(this, "Activité profile", Toast.LENGTH_LONG).show();
                return true;
            case R.id.menu_fragment_listview:
                Toast.makeText(this, "Fragment List View", Toast.LENGTH_LONG).show();
                // Autocomplete
                autocomplete();
                return true;
        }
        if (item.getItemId() == R.id.menu_activity_main_search) {
            // AutoComplete

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Add autocomplete search on fragment with details
     */
    private void autocomplete() {
        String type = "";
        float distanceInMetter = 0;
        this.getAutoComplete(R.id.autocomplete_fragment).setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                setLunch(autoCompleteNewLunchModel(place, type, distanceInMetter));
                createDetailFragment();
            }

            @Override
            public void onError(@NonNull Status status) {
                Log.e("Place error", "Place error");
            }
        });
    }

    // NAVIGATION DRAWER

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
     * Navigation on the Navigation Drawer Menu
     *
     * @param item Selected item
     * @return true
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.profile_lunch:
                if (mMapViewFragment == null) {
                    mMapViewFragment = new MapViewFragment();
                }
                getFragment(mMapViewFragment);
                break;
            case R.id.profile_sittings:

                break;
            case R.id.profile_logout:
                this.signOutUserFromFirebase();

                break;
            default:
                break;
        }

        this.drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    /**
     * Create http requests (SignOut)
     */
    private void signOutUserFromFirebase() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnSuccessListener(this, this.updateUIAfterRESTRequestsCompleted());
    }

    /**
     * Get Fragment
     *
     * @param selectedFragment Fragment selected
     */
    private void getFragment(Fragment selectedFragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.activity_main_frame_layout,
                selectedFragment).commit();
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
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    // STATUS BAR COLOR

    /**
     * Configure NavigationView
     */
    private void configureNavigationView() {

        navigationView.setNavigationItemSelectedListener(this);

        if (this.getCurrentUser() != null) {
            // Customize image profile
            View headView = navigationView.getHeaderView(0);
            ImageView imgProfile = headView.findViewById(R.id.imageAvatar);
            //Get picture URL from Firebase
            if (this.getCurrentUser().getPhotoUrl() != null) {
                Glide.with(this)
                        .load(this.getCurrentUser().getPhotoUrl())
                        .override(200, 200)
                        .apply(RequestOptions.circleCropTransform())
                        .into(imgProfile);
                //Customize user data username and name
                String username = TextUtils.isEmpty(this.getCurrentUser().getDisplayName()) ?
                        getString(R.string.info_no_username_found) : this.getCurrentUser().getDisplayName();
                String email = TextUtils.isEmpty(this.getCurrentUser().getEmail()) ?
                        getString(R.string.info_no_email_found) : this.getCurrentUser().getEmail();

                TextView textUsername = headView.findViewById(R.id.name);
                textUsername.setText(username);
                TextView textEmail = headView.findViewById(R.id.email);
                textEmail.setText(email);
            }
        }
    }

    /**
     * Change status bar color depending of lvl application
     *
     * @param activity this activity
     * @param color    get color (in hexadecimal format)
     */
    public void updateStatusBarColor(Activity activity, String color) {// Color must be in hexadecimal fromat

        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(activity, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        //make fully Android Transparent Status bar
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(activity, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(Color.parseColor(color));
        }
    }

    // GETTERS and SETTERS

    public LunchModel getLunch() {
        return lunch;
    }

    public void setLunch(LunchModel lunch) {
        this.lunch = lunch;
    }
}
