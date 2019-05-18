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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gacon.julien.go4lunch.R;
import com.gacon.julien.go4lunch.controller.activities.ProfileActivity;
import com.gacon.julien.go4lunch.models.LunchModel;
import com.gacon.julien.go4lunch.view.utils.DataFormat;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

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

    private ProfileActivity mProfileActivity;
    private DataFormat mDataFormat;

    public DetailsListViewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_details_list_view, container, false);
        ButterKnife.bind(this, view);
        mProfileActivity = (ProfileActivity) getActivity();
        mDataFormat = new DataFormat();
        // get data and put in the layout
        getLayoutElements();

        return view;

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
     * get data for the layout
     */
    private void getLayoutElements() {
        ProfileActivity profileActivity = (ProfileActivity) getActivity();
        assert profileActivity != null;
        LunchModel lunchModel = profileActivity.getLunch();
        assert lunchModel != null;
        // Set Title and Address in layout
        mTextViewTitle.setText(getTitle(lunchModel));
        mTextAddress.setText(getAddress(lunchModel));
        // set image header
        setImageHeader(lunchModel);
        // WebView
        createWebView(lunchModel);
        // PhoneCall
        createPhoneCall(lunchModel);

    }

    private String getTitle(LunchModel lunch) {
        return lunch.getTitle();
    }

    private String getAddress(LunchModel lunch) {
        return lunch.getAddress();
    }

    /**
     * Get image from model after image pass to DataFormat class
     *
     * @param lunch lunch model
     */
    private void setImageHeader(LunchModel lunch) {
        DataFormat dataFormat = new DataFormat();
        //  image
        if (lunch.getPhotoMetadatasOfPlace() != null) {
            dataFormat.addImages(lunch.getPlace(), lunch.getPlacesClient(), imageHeader);
        } else imageHeader.setImageResource(R.drawable.bg_connection);
    }

    /**
     * Create a WebView in WebViewFragment
     *
     * @param lunch model lunch
     */
    private void createWebView(LunchModel lunch) {
        if (lunch.getWebsiteUriPlace() != null) {
            webImageBtnView.setOnClickListener(v -> {
                Fragment mWiebViewFrag = new WebViewFragment();
                Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, mWiebViewFrag).commit();
            });
        } else Toast.makeText(getContext(), "No website", Toast.LENGTH_LONG).show();
    }

    /**
     * Get Phone Call in new intent Action Dial
     *
     * @param lunch model view
     */
    private void createPhoneCall(LunchModel lunch) {
        if (lunch.getPhoneNumber() != null) {
            callImageView.setOnClickListener(v -> {
                Intent phoneIntent = new Intent(Intent.ACTION_DIAL,
                        Uri.parse("tel:" + lunch.getPhoneNumber()));
                startActivity(phoneIntent);
            });
        } else Toast.makeText(getContext(), "No phone number", Toast.LENGTH_LONG).show();
    }

}
