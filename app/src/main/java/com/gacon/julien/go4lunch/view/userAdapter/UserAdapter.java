package com.gacon.julien.go4lunch.view.userAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gacon.julien.go4lunch.R;
import com.gacon.julien.go4lunch.models.LunchModel;
import com.gacon.julien.go4lunch.models.User;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserViewHolder> {

    // For data
    private ArrayList<User> mUserList;

    // Constructor
    public UserAdapter(ArrayList<User> userList) {
        this.mUserList = userList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // CREATE VIEW HOLDER AND INFLATING ITS XML LAYOUT
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.fragment_user_items, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.updateWithUser(this.mUserList.get(position));
    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }

}
