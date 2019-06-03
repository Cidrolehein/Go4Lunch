package com.gacon.julien.go4lunch.view.userJoiningAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.gacon.julien.go4lunch.R;
import com.gacon.julien.go4lunch.models.User;

import java.util.ArrayList;

/**
 * An adapter for joining users
 */
public class UserJoiningAdapter extends RecyclerView.Adapter<UserJoiningHolder> {

    // For data
    private ArrayList<User> mUserList;
    private RequestManager mGlide;
    private Context mContext;

    // Constructor
    public UserJoiningAdapter(ArrayList<User> userList, RequestManager glide, Context context) {
        this.mUserList = userList;
        this.mGlide = glide;
        this.mContext = context; // get context for string resources
    }

    @NonNull
    @Override
    public UserJoiningHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // CREATE VIEW HOLDER AND INFLATING ITS XML LAYOUT
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.fragment_list_users_joining, parent, false);
        return new UserJoiningHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserJoiningHolder holder, int position) {
            holder.updateWithUser(this.mUserList.get(position), this.mGlide, mContext);

    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }

}
