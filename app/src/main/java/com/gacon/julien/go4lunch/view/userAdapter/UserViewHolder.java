package com.gacon.julien.go4lunch.view.userAdapter;

import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.gacon.julien.go4lunch.R;
import com.gacon.julien.go4lunch.models.User;
import com.gacon.julien.go4lunch.view.utils.DataFormat;

import butterknife.BindView;
import butterknife.ButterKnife;

class UserViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.user_name)
    TextView mUsername;
    @BindView(R.id.place_name)
    TextView mPlaceName;
    @BindView(R.id.user_image)
    ImageView mUserPicture;

    /**
     * Adapter for RecyclerView with OnClickListener
     *
     * @param itemView view
     */
    UserViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    void updateWithUser(User user, RequestManager glide) {

        DataFormat dataFormat = new DataFormat();

        if (user.getPlaceName() != null) {
        // Update username
        if (user.getUsername() != null) {
            // set username
            this.mUsername.setText(user.getUsername());
        }
        // Update place selected
            // set placename
            this.mPlaceName.setText(" is eating at "+user.getPlaceName());
        } else {
            this.mPlaceName.setText(" hasn't decided yet");
            // change color text
            changeTextColorToGrey(this.mPlaceName, dataFormat);
            changeTextColorToGrey(this.mUsername, dataFormat);
        }

        // Update profile picture ImageView
        if (user.getUrlPicture() != null)
            glide.load(user.getUrlPicture())
                    .apply(RequestOptions.circleCropTransform())
                    .into(mUserPicture);

    }

    private void changeTextColorToGrey(TextView textView, DataFormat dataFormat){
        textView.setTextColor
                (Color.parseColor(dataFormat
                        .changeColorToHex(R.color.grey,
                                mPlaceName.getContext())));
    }

}
