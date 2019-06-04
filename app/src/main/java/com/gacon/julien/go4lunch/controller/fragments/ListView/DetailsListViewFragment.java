package com.gacon.julien.go4lunch.controller.fragments.ListView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.gacon.julien.go4lunch.R;
import com.gacon.julien.go4lunch.controller.activities.ProfileActivity;
import com.gacon.julien.go4lunch.controller.activities.api.UserHelper;
import com.gacon.julien.go4lunch.controller.activities.auth.utils.BaseActivity;
import com.gacon.julien.go4lunch.models.LunchModel;
import com.gacon.julien.go4lunch.models.User;
import com.gacon.julien.go4lunch.view.userJoiningAdapter.UserJoiningAdapter;
import com.gacon.julien.go4lunch.view.utils.DataFormat;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A detail view of place
 */
public class DetailsListViewFragment extends Fragment {

    @BindView(R.id.header_title)
    TextView mTextViewTitle;
    @BindView(R.id.address_header)
    TextView mTextAddress;
    @BindView(R.id.image_header)
    ImageView imageHeader;
    @BindView(R.id.web_imageview)
    ImageButton webImageBtnView;
    @BindView(R.id.like_imagebutton)
    ImageButton likeImageView;
    @BindView(R.id.call_imageview)
    ImageButton callImageView;
    @BindView(R.id.select_place_btn)
    Button btnSelectPlace;
    @BindView(R.id.recycler_view_users_joining)
    RecyclerView mRecyclerView;
    @BindView(R.id.rating_bar)
    RatingBar mRatingBar;

    private BaseActivity mBaseActivity;
    private DataFormat mDataFormat;
    private LunchModel mLunchModel;
    private ArrayList<User> mUsersJoiningArrayList;
    private UserJoiningAdapter adapter;
    private ProfileActivity profileActivity;

    /**
     * Required empty public constructor
     */
    public DetailsListViewFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_details_list_view, container, false);
        ButterKnife.bind(this, view);
        mBaseActivity = (BaseActivity) getActivity();
        profileActivity = (ProfileActivity) getActivity();
        assert profileActivity != null;
        mLunchModel = profileActivity.getLunch();
        mDataFormat = new DataFormat();
        // get data and put in the layout
        getLayoutElements();
        // create recycler view of joining users
        this.createRecyclerView();
        this.getUsersJoiningNames();

        return view;

    }

    /**
     * Select place button
     */
    @OnClick(R.id.select_place_btn)
    void placeBtnSelected() {
        updatePlaceSelectedId();
        updatePlaceName();
    }

    @OnClick(R.id.like_imagebutton)
    void likeBtnSelected() {
        // Rating bar and like
        addPlaceLike();
    }

    /**
     * Hide toolbar
     */
    @Override
    public void onResume() {
        super.onResume();
        // Hide the toolbar
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity()))
                .getSupportActionBar()).hide();
        // change color of system bar
        profileActivity.updateStatusBarColor(getActivity(),
                mDataFormat.changeColorToHex(R.color.black_transparency, getContext()));
    }

    /**
     * Change the color of status bar
     * TODO : add transparency
     */
    @Override
    public void onStop() {
        super.onStop();
        // show the toolbar
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity()))
                .getSupportActionBar()).show();
        // change color of system bar
        profileActivity.updateStatusBarColor(getActivity(),
                mDataFormat.changeColorToHex(R.color.colorPrimaryDark, getContext()));
    }

    /**
     * get data for layout
     */
    private void getLayoutElements() {
        assert mLunchModel != null;
        // Set Title and Address in layout
        mTextViewTitle.setText(getTitle());
        mTextAddress.setText(getAddress());
        // set image header
        setImageHeader();
        // WebView
        createWebView();
        // PhoneCall
        createPhoneCall();

    }

    private void addPlaceLike(){
        likeImageView.setOnClickListener(v -> {
            // Getting the rating and displaying it on the toast
            String rating = String.valueOf(mRatingBar.getRating());
            Toast.makeText(getContext(), rating, Toast.LENGTH_LONG).show();
            createPlaceRatingInFirestore();
        });
    }

    private void createPlaceRatingInFirestore(){
        if (mRatingBar != null) {
            String rating = String.valueOf(mRatingBar.getRating());
            String placeId = mLunchModel.getPlaceId();
            UserHelper.createPlaceRating(placeId, rating,
                    Objects.requireNonNull(mBaseActivity.getCurrentUser()).getUid())
                    .addOnFailureListener(mBaseActivity.onFailureListener());
        }
    }

    /**
     * Get image from model after image pass to DataFormat class
     */
    private void setImageHeader() {
        DataFormat dataFormat = new DataFormat();
        //  image
        if (mLunchModel.getPhotoMetadatasOfPlace() != null) {
            dataFormat.addImages(mLunchModel.getPlace(), mLunchModel.getPlacesClient(), imageHeader);
        } else imageHeader.setImageResource(R.drawable.bg_connection);
    }

    /**
     * Create a WebView in WebViewFragment
     */
    private void createWebView() {
        if (mLunchModel.getWebsiteUriPlace() != null) {
            webImageBtnView.setOnClickListener(v -> {
                Fragment mWiebViewFrag = new WebViewFragment();
                Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, mWiebViewFrag).commit();
            });
        } else Toast.makeText(getContext(), "No website", Toast.LENGTH_LONG).show();
    }

    /**
     * Get Phone Call in new intent Action Dial
     */
    private void createPhoneCall() {
        if (mLunchModel.getPhoneNumber() != null) {
            callImageView.setOnClickListener(v -> {
                Intent phoneIntent = new Intent(Intent.ACTION_DIAL,
                        Uri.parse("tel:" + mLunchModel.getPhoneNumber()));
                startActivity(phoneIntent);
            });
        } else Toast.makeText(getContext(), "No phone number", Toast.LENGTH_LONG).show();
    }

    /**
     * Get place name for title
     */
    private void updatePlaceName() {
        if (mBaseActivity.getCurrentUser() != null) {
            String placeName = mLunchModel.getTitle();
            if (placeName != null) {
                UserHelper.updatePlaceName(placeName,
                        mBaseActivity.getCurrentUser().getUid())
                        .addOnFailureListener(mBaseActivity.onFailureListener());
            }
        }
    }

    /**
     * Get the current id
     */
    private void updatePlaceSelectedId() {
        if (mBaseActivity.getCurrentUser() != null) {
            String placeSelectedId = mLunchModel.getPlaceId();
            if (placeSelectedId != null) {
                UserHelper.updatePlaceSelectedId(placeSelectedId,
                        mBaseActivity.getCurrentUser().getUid())
                        .addOnFailureListener(mBaseActivity.onFailureListener());
            }
        }
    }

    /**
     * Create RecyclerView
     */
    private void createRecyclerView() {
        mUsersJoiningArrayList = new ArrayList<>();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new UserJoiningAdapter(mUsersJoiningArrayList, Glide.with(this), getContext());
        adapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(adapter);
    }

    /**
     * Get joining users names for RecyclerView
     */
    private void getUsersJoiningNames(){
        // - Get all users names
        UserHelper.getUsersCollection().addSnapshotListener((queryDocumentSnapshots, e) -> {
            mUsersJoiningArrayList.clear();
            assert queryDocumentSnapshots != null;
            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                User user = documentSnapshot.toObject(User.class);
                // select joining users
                if(mLunchModel.getPlaceId().equals(user.getPlaceSelectedId())){
                    mUsersJoiningArrayList.add(user);
                }
            }
            adapter.notifyDataSetChanged();
        });
    }

    /**
     * Get title
     * @return title place
     */
    private String getTitle() {
        return mLunchModel.getTitle();
    }

    /**
     * Get address
     * @return get address of place
     */
    private String getAddress() {
        return mLunchModel.getAddress();
    }

}
