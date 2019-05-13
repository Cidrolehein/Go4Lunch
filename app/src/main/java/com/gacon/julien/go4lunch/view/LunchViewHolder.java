package com.gacon.julien.go4lunch.view;

import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.gacon.julien.go4lunch.R;
import com.gacon.julien.go4lunch.models.LunchModel;
import com.gacon.julien.go4lunch.view.utils.DataFormat;
import com.gacon.julien.go4lunch.view.utils.GetHours;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
     * @param itemView view
     * @param onNoteListener OnClickListener in item of RecyclerView
     */
    LunchViewHolder(@NonNull View itemView, LunchAdapter.OnNoteListener onNoteListener) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.mOnNoteListener = onNoteListener;
        itemView.setOnClickListener(this);
    }

    void updateWithLunch(LunchModel newLunch) {
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
            if(textHour.equals("Closing soon !")) {
                this.mTextViewIsItOpen.setTextColor(Color.parseColor("#ba160c"));
            }
        }
        // set PlaceImage
        if (newLunch.getPhotoMetadatasOfPlace() != null) {
            dataFormat.addImages(newLunch.getPlace(), newLunch.getPlacesClient(), imageView);
        } else imageView.setImageResource(R.drawable.bg_connection);

        // set Distance
        this.mTextViewDistance.setText(dataFormat.formatMeters(newLunch));
    }

    /**
     * Interface for OnClickListener
     * @param v new view inside adapter position
     */
    @Override
    public void onClick(View v) {
        mOnNoteListener.onNoteClick(getAdapterPosition());

    }
}
