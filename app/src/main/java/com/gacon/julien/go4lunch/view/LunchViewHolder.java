package com.gacon.julien.go4lunch.view;

import android.view.View;
import android.widget.TextView;

import com.gacon.julien.go4lunch.R;
import com.gacon.julien.go4lunch.controller.Models.LunchModel;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class LunchViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.title)
    TextView mTextViewTitle;

    public LunchViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void updateWithLunch(LunchModel newLunch) {
        this.mTextViewTitle.setText(newLunch.getTitle());
    }
}
