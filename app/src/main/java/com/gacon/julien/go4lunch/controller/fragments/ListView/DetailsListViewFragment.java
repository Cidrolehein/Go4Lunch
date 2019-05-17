package com.gacon.julien.go4lunch.controller.fragments.ListView;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gacon.julien.go4lunch.R;
import com.gacon.julien.go4lunch.controller.activities.ProfileActivity;
import com.gacon.julien.go4lunch.models.LunchModel;
import com.gacon.julien.go4lunch.view.utils.DataFormat;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailsListViewFragment extends Fragment {

    @BindView(R.id.header_title)
    TextView mTextViewTitle;
    @BindView(R.id.address_header)
    TextView mTextAddress;
    @BindView(R.id.star_rating_1)
    ImageView mStarRating1;
    @BindView(R.id.star_rating_2)
    ImageView mStarRating2;
    @BindView(R.id.star_rating_3)
    ImageView mStarRating3;
    @BindView(R.id.star_rating_4)
    ImageView mStarRating4;
    @BindView(R.id.image_header)
    ImageView imageHeader;

    public DetailsListViewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_details_list_view, container, false);
        ButterKnife.bind(this, view);
        ProfileActivity profileActivity = (ProfileActivity) getActivity();
        assert profileActivity != null;
        LunchModel lunchModel = profileActivity.getLunch();
        assert lunchModel != null;
        String title = lunchModel.getTitle();
        String address = lunchModel.getAddress();
        mTextViewTitle.setText(title);
        mTextAddress.setText(address);
        DataFormat dataFormat = new DataFormat();
        // get Rating
        dataFormat.getRatingStar(lunchModel.getPlace_rating(), mStarRating1, mStarRating2, mStarRating3, mStarRating4);
        // get image
        if (lunchModel.getPhotoMetadatasOfPlace() != null) {
            dataFormat.addImages(lunchModel.getPlace(), lunchModel.getPlacesClient(), imageHeader);
        } else imageHeader.setImageResource(R.drawable.bg_connection);

        return view;

    }

}
