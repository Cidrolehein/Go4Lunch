package com.gacon.julien.go4lunch.view.userJoiningAdapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.gacon.julien.go4lunch.R;
import com.gacon.julien.go4lunch.models.User;

import butterknife.BindView;
import butterknife.ButterKnife;

class UserJoiningHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.join_users)
    TextView mUsername;
    @BindView(R.id.user_image)
    ImageView mUserPicture;

    /**
     * Adapter for RecyclerView with OnClickListener
     *
     * @param itemView view
     */
    UserJoiningHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    void updateWithUser(User user, RequestManager glide, Context context) {

            String message = context.getString(R.string.joining_list_txt, user.getUsername());
            this.mUsername.setText(message);
            // Update profile picture ImageView
            if (user.getUrlPicture() != null)
                glide.load(user.getUrlPicture())
                        .apply(RequestOptions.circleCropTransform())
                        .into(mUserPicture);
    }
}
