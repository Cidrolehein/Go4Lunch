package com.gacon.julien.go4lunch;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.firebase.ui.auth.AuthUI;
import java.util.Arrays;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    //FOR DATA
    // - Identifier for Sign-In Activity
    private static final int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    // --------------------
    // ACTIONS
    // --------------------

    @OnClick(R.id.google_button_login)
    public void onClickLoginButton() {
        // 3 - Launch Sign-In Activity when user clicked on Login Button
        this.startSignInActivity();
    }

    // --------------------
    // NAVIGATION
    // --------------------

    // 2 - Launch Sign-In Activity
    private void startSignInActivity(){
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(
                                Arrays.asList(new AuthUI.IdpConfig.GoogleBuilder().build()))
                        .setIsSmartLockEnabled(false, true)
                        .build(),
                RC_SIGN_IN);
    }
}
