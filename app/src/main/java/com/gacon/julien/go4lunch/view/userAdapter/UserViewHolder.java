package com.gacon.julien.go4lunch.view.userAdapter;

import android.content.Context;
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

    void updateWithUser(User user, RequestManager glide, Context context) {

        DataFormat dataFormat = new DataFormat();

        if (user.getPlaceName() != null) {
            // Update username
            if (user.getUsername() != null) {
                // set username
                String message = context.getString(R.string.user_place_selected, user.getUsername(),
                        user.getPlaceName());
                this.mUsername.setText(message);
            }
        } else {
            String message = context.getString(R.string.user_not_place_selected, user.getUsername());
            this.mUsername.setText(message);
            // change color text
            changeTextColorToGrey(this.mUsername, dataFormat);
        }

        // Update profile picture ImageView
        if (user.getUrlPicture() != null)
            glide.load(user.getUrlPicture())
                    .apply(RequestOptions.circleCropTransform())
                    .into(mUserPicture);

    }

    /**
     * Change the color of the non selected place
     * @param textView textView where we put text
     * @param dataFormat change format to hexadecimal
     */
    private void changeTextColorToGrey(TextView textView, DataFormat dataFormat) {
        textView.setTextColor
                (Color.parseColor(dataFormat
                        .changeColorToHex(R.color.grey,
                                mUsername.getContext())));
    }

}
