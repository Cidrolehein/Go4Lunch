package com.gacon.julien.go4lunch.view.lunchAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gacon.julien.go4lunch.R;
import com.gacon.julien.go4lunch.models.LunchModel;
import com.gacon.julien.go4lunch.models.User;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class LunchAdapter extends RecyclerView.Adapter<LunchViewHolder> {

    // For data
    private List<LunchModel> mLunchModelList;
    private ArrayList<User> mUserArrayList;
    private OnNoteListener mOnNoteListener;
    private Context mContext;

    // Constructor
    public LunchAdapter(List<LunchModel> lunchList, OnNoteListener onNoteListener, ArrayList<User> usersList, Context context) {
        this.mLunchModelList = lunchList;
        this.mOnNoteListener = onNoteListener;
        this.mUserArrayList = usersList;
        this.mContext = context;
    }

    @NonNull
    @Override
    public LunchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // CREATE VIEW HOLDER AND INFLATING ITS XML LAYOUT
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.fragment_list_view_items, parent, false);
        return new LunchViewHolder(view, mOnNoteListener);
    }

    @Override
    public void onBindViewHolder(@NonNull LunchViewHolder holder, int position) {
        holder.updateWithLunch(this.mLunchModelList.get(position), this.mUserArrayList, this.mContext);
    }

    @Override
    public int getItemCount() {
        return mLunchModelList.size();
    }

    /**
     * Interface for detect the click on the same position of the click item
     */
    public interface  OnNoteListener{
        void onNoteClick(int position);
    }
}
