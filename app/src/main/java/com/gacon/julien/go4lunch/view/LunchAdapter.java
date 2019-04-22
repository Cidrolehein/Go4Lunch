package com.gacon.julien.go4lunch.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.RequestManager;
import com.gacon.julien.go4lunch.R;
import com.gacon.julien.go4lunch.models.LunchModel;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class LunchAdapter extends RecyclerView.Adapter<LunchViewHolder> {

    // For data
    private List<LunchModel> mLunchModelList;
    // - Declaring a Glide object
    private RequestManager glide;

    // Constructor
    public LunchAdapter(List<LunchModel> lunchList, RequestManager glide) {
        this.mLunchModelList = lunchList;
        this.glide = glide;
    }

    @NonNull
    @Override
    public LunchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // CREATE VIEW HOLDER AND INFLATING ITS XML LAYOUT
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.fragment_list_view_items, parent, false);
        return new LunchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LunchViewHolder holder, int position) {
        holder.updateWithLunch(this.mLunchModelList.get(position), this.glide);
    }

    @Override
    public int getItemCount() {
        return mLunchModelList.size();
    }
}
