package com.gacon.julien.go4lunch.view;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.gacon.julien.go4lunch.R;
import com.gacon.julien.go4lunch.models.LunchModel;
import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

class LunchViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.title)
    TextView mTextViewTitle;
    @BindView(R.id.address)
    TextView mTextViewAddress;
    @BindView(R.id.place_image)
    ImageView imageView;
    @BindView(R.id.type)
    TextView mTextViewType;
    @BindView(R.id.star_rating_1)
    ImageView mStarRating1;
    @BindView(R.id.star_rating_2)
    ImageView mStarRating2;
    @BindView(R.id.star_rating_3)
    ImageView mStarRating3;
    @BindView(R.id.star_rating_4)
    ImageView mStarRating4;



    LunchViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    void updateWithLunch(LunchModel newLunch, RequestManager glide) {
        this.mTextViewTitle.setText(newLunch.getTitle());
        this.mTextViewAddress.setText(newLunch.getAddress());
        this.mTextViewType.setText(newLunch.getPlace_type());
        this.getRatingStar(newLunch);
        this.getImage(newLunch, glide);
    }

    private void getImage(LunchModel placeImage, RequestManager glide) {
        glide.load(placeImage.getPlaceImage()).apply(new RequestOptions()).into(imageView);
    }

    private void getRatingStar(LunchModel lunch) {
        if(lunch.getPlace_rating() == 1 && lunch.getPlace_rating() < 2){
            mStarRating1.setVisibility(View.GONE);
            mStarRating2.setVisibility(View.GONE);
            mStarRating3.setVisibility(View.GONE);
            mStarRating4.setVisibility(View.GONE);
        } else if(lunch.getPlace_rating() >= 2 && lunch.getPlace_rating() < 3){
            mStarRating1.setVisibility(View.VISIBLE);
            mStarRating2.setVisibility(View.GONE);
            mStarRating3.setVisibility(View.GONE);
            mStarRating4.setVisibility(View.GONE);
        } else if(lunch.getPlace_rating() >= 3 && lunch.getPlace_rating() < 4){
            mStarRating1.setVisibility(View.VISIBLE);
            mStarRating2.setVisibility(View.VISIBLE);
            mStarRating3.setVisibility(View.GONE);
            mStarRating4.setVisibility(View.GONE);
        } else if(lunch.getPlace_rating() >= 4 && lunch.getPlace_rating() < 5){
            mStarRating1.setVisibility(View.VISIBLE);
            mStarRating2.setVisibility(View.VISIBLE);
            mStarRating3.setVisibility(View.VISIBLE);
            mStarRating4.setVisibility(View.GONE);
        } else {
            mStarRating1.setVisibility(View.VISIBLE);
            mStarRating2.setVisibility(View.VISIBLE);
            mStarRating3.setVisibility(View.VISIBLE);
            mStarRating4.setVisibility(View.VISIBLE);
        }
    }

}
