package com.gacon.julien.go4lunch.auth;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.gacon.julien.go4lunch.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.button_log_out)
    public void onClickSignOutButton() { }
}
