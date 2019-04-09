package com.gacon.julien.go4lunch.auth;

import android.os.Bundle;

import com.firebase.ui.auth.AuthUI;
import com.gacon.julien.go4lunch.R;
import com.google.android.gms.tasks.OnSuccessListener;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProfileActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.button_log_out)
    public void onClickSignOutButton() {
        this.signOutUserFromFirebase();
    }

    // --------------------
    // REST REQUESTS
    // --------------------
    // - Create http requests (SignOut)

    private void signOutUserFromFirebase() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnSuccessListener(this, this.updateUIAfterRESTRequestsCompleted());
    }

    // --------------------
    // UI
    // --------------------

    // - Create OnCompleteListener called after tasks ended
    private OnSuccessListener<Void> updateUIAfterRESTRequestsCompleted() {
        return new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                finish();
            }
        };
    }

}
