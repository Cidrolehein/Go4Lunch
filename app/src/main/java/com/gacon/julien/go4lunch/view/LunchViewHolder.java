package com.gacon.julien.go4lunch.view;

import android.view.View;
import android.widget.TextView;

import com.gacon.julien.go4lunch.R;
import com.gacon.julien.go4lunch.models.LunchModel;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

class LunchViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.title)
    TextView mTextViewTitle;
    @BindView(R.id.address)
    TextView mTextViewAddress;

    LunchViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    void updateWithLunch(LunchModel newLunch) {
        this.mTextViewTitle.setText(newLunch.getTitle());
        this.mTextViewAddress.setText(newLunch.getAddress());
    }
}
