package com.gacon.julien.go4lunch.view.lunchAdapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gacon.julien.go4lunch.R;
import com.gacon.julien.go4lunch.controller.activities.api.PlaceRatingHelper;
import com.gacon.julien.go4lunch.models.LunchModel;
import com.gacon.julien.go4lunch.models.PlaceRating;
import com.gacon.julien.go4lunch.models.User;
import com.gacon.julien.go4lunch.view.utils.DataFormat;
import com.gacon.julien.go4lunch.view.utils.GetHours;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

class LunchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    @BindView(R.id.title)
    TextView mTextViewTitle;
    @BindView(R.id.address)
    TextView mTextViewAddress;
    @BindView(R.id.place_image)
    ImageView imageView;
    @BindView(R.id.Date)
    TextView mTextViewIsItOpen;
    @BindView(R.id.distance)
    TextView mTextViewDistance;
    @BindView(R.id.people_number)
    TextView mTextViewPeopleNumber;
    @BindView(R.id.star_rating_1)
    ImageView mStarRating1;
    @BindView(R.id.star_rating_2)
    ImageView mStarRating2;
    @BindView(R.id.star_rating_3)
    ImageView mStarRating3;

    private LunchAdapter.OnNoteListener mOnNoteListener;

    /**
     * Adapter for RecyclerView with OnClickListener
     *
     * @param itemView       view
     * @param onNoteListener OnClickListener in item of RecyclerView
     */
    LunchViewHolder(@NonNull View itemView, LunchAdapter.OnNoteListener onNoteListener) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.mOnNoteListener = onNoteListener;
        itemView.setOnClickListener(this);
    }

    void updateWithLunch(LunchModel newLunch, ArrayList<User> user, Context context) {
        DataFormat dataFormat = new DataFormat();
        GetHours getHours = new GetHours();

        // set title
        this.mTextViewTitle.setText(newLunch.getTitle());

        // set address
        this.mTextViewAddress.setText(newLunch.getAddress());

        // set Rating
        setRates(newLunch, context, dataFormat);

        // set time
        if (newLunch.getPeriods() != null) {
            String textHour = getHours.getDayOpen(newLunch);
            this.mTextViewIsItOpen.setText(textHour);
            // change the color if the hour is close to close
            if (textHour.equals("Closing soon !")) {
                this.mTextViewIsItOpen.setTextColor
                        (Color.parseColor(dataFormat
                                .changeColorToHex(R.color.colorPrimaryDark,
                                        mTextViewIsItOpen.getContext())));
            }
        }
        // set PlaceImage to ImageView
        if (newLunch.getPhotoMetadatasOfPlace() != null) {

            PhotoMetadata photoMetadata = Objects.requireNonNull(newLunch.getPhotoMetadatasOfPlace()).get(0);
            // Create a FetchPhotoRequest.
            FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                    .build();
            newLunch.getPlacesClient().fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
                Bitmap bitmap = fetchPhotoResponse.getBitmap();
                imageView.setImageBitmap(bitmap);
            });
        } else imageView.setImageResource(R.drawable.bg_connection);

        // set Distance to TextView
        this.mTextViewDistance.setText(dataFormat.formatMeters(newLunch));

        // count the number of user who selected the place
        int count = countUsersPlaceSelected(user, newLunch);

        String countToString = context.getString(R.string.count_users, count);
        this.mTextViewPeopleNumber.setText(countToString);

    }

    private void setRates(LunchModel newLunch, Context context, DataFormat dataFormat) {
        // get rate from firestore
        // get the current place id
        String placeId = newLunch.getPlaceId();
        // if exist, get the rate from firestore
        PlaceRatingHelper.getRating(placeId).addOnSuccessListener(documentSnapshot -> {
            PlaceRating currentRate = documentSnapshot.toObject(PlaceRating.class);
            String oldRateOfString = "0";
            if (currentRate != null && currentRate.getPlaceRating() != null) {
                oldRateOfString = TextUtils.isEmpty(currentRate.getPlaceRating()) ?
                        context.getString(R.string.rate_is_not_found) : currentRate.getPlaceRating();
            }
            // pass string to float
            dataFormat.getRatingStar(dataFormat.passStringToFloat(oldRateOfString), mStarRating1, mStarRating2, mStarRating3);
        });
    }

    /**
     * Count how many users selected the current place
     *
     * @param users    arraylist of users
     * @param newLunch data for place id
     * @return number of users
     */
    private int countUsersPlaceSelected(ArrayList<User> users, LunchModel newLunch) {
        int count = 0;
        if (users != null) {
            for (int i = 0; i < users.size(); i++) {
                if (users.get(i).getPlaceSelectedId() != null) {
                    String userId = users.get(i).getPlaceSelectedId();
                    if (userId.equals(newLunch.getPlaceId())) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    /**
     * Interface for OnClickListener
     *
     * @param v new view inside adapter position
     */
    @Override
    public void onClick(View v) {
        mOnNoteListener.onNoteClick(getAdapterPosition());

    }

}
