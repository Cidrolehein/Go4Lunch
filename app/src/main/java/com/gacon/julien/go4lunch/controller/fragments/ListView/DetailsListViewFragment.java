package com.gacon.julien.go4lunch.controller.fragments.ListView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gacon.julien.go4lunch.R;
import com.gacon.julien.go4lunch.controller.activities.ProfileActivity;
import com.gacon.julien.go4lunch.controller.activities.api.PlacesHelper;
import com.gacon.julien.go4lunch.controller.activities.api.UserHelper;
import com.gacon.julien.go4lunch.controller.activities.auth.utils.BaseActivity;
import com.gacon.julien.go4lunch.models.LunchModel;
import com.gacon.julien.go4lunch.view.utils.DataFormat;

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
    @BindView(R.id.like_imageview)
    ImageView likeImageView;
    @BindView(R.id.call_imageview)
    ImageButton callImageView;
    @BindView(R.id.select_place_btn)
    Button btnSelectPlace;

    private ProfileActivity mProfileActivity;
    private BaseActivity mBaseActivity;
    private DataFormat mDataFormat;
    private LunchModel mLunchModel;

    public DetailsListViewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_details_list_view, container, false);
        ButterKnife.bind(this, view);
        mProfileActivity = (ProfileActivity) getActivity();
        mBaseActivity = (BaseActivity) getActivity();
        mDataFormat = new DataFormat();
        // get data and put in the layout
        getLayoutElements();

        return view;

    }

    @OnClick(R.id.select_place_btn)
    void placeBtnSelected () {
        updatePlaceSelectedId();
        updatePlaceName();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Hide the toolbar
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity()))
                .getSupportActionBar()).hide();
        // change color of system bar
        mProfileActivity.updateStatusBarColor(getActivity(),
                mDataFormat.changeColorToHex(R.color.black_transparency, getContext()));
    }

    @Override
    public void onStop() {
        super.onStop();
        // show the toolbar
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity()))
                .getSupportActionBar()).show();
        // change color of system bar
        mProfileActivity.updateStatusBarColor(getActivity(),
                mDataFormat.changeColorToHex(R.color.colorPrimaryDark, getContext()));
    }

    /**
     * get data for layout
     */
    private void getLayoutElements() {
        ProfileActivity profileActivity = (ProfileActivity) getActivity();
        assert profileActivity != null;
        mLunchModel = profileActivity.getLunch();
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

    private String getTitle() {
        return mLunchModel.getTitle();
    }

    private String getAddress() {
        return mLunchModel.getAddress();
    }

    /**
     * Get image from model after image pass to DataFormat class
     *
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
     *
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
     *
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

    // - Http request that create user in firestore
    private void createPlaceInFirestore(){

        if (this.mLunchModel != null){


            String placename = this.mLunchModel.getTitle();
            String uid = this.mLunchModel.getPlaceId();

            PlacesHelper.createPlace(uid, placename).addOnFailureListener(this.mBaseActivity.onFailureListener());
        }
    }

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

}
