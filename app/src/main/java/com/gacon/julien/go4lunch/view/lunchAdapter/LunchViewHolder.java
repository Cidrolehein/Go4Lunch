package com.gacon.julien.go4lunch.view.lunchAdapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gacon.julien.go4lunch.R;
import com.gacon.julien.go4lunch.controller.activities.api.UserHelper;
import com.gacon.julien.go4lunch.models.LunchModel;
import com.gacon.julien.go4lunch.models.PlaceRating;
import com.gacon.julien.go4lunch.models.User;
import com.gacon.julien.go4lunch.view.utils.DataFormat;
import com.gacon.julien.go4lunch.view.utils.GetHours;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

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
    @BindView(R.id.star_rating_4)
    ImageView mStarRating4;

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
        dataFormat.getRatingStar(newLunch.getPlace_rating(), mStarRating1, mStarRating2, mStarRating3, mStarRating4);

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
            dataFormat.addImages(newLunch.getPlace(), newLunch.getPlacesClient(), imageView);
        } else imageView.setImageResource(R.drawable.bg_connection);

        // set Distance to TextView
        this.mTextViewDistance.setText(dataFormat.formatMeters(newLunch));

        // count the number of user who selected the place
        int count = countUsersPlaceSelected(user, newLunch);

        String countToString = context.getString(R.string.count_users, count);
        this.mTextViewPeopleNumber.setText(countToString);

    }

    /**
     * Count how many users selected the current place
     * @param users arraylist of users
     * @param newLunch data for place id
     * @return number of users
     */
    private int countUsersPlaceSelected(ArrayList<User> users, LunchModel newLunch){
        int count = 0;
        if(users != null){
            for (int i  = 0; i < users.size(); i++){
                if(users.get(i).getPlaceSelectedId() != null){
                    String userId = users.get(i).getPlaceSelectedId();
                    if (userId.equals(newLunch.getPlaceId())){
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
