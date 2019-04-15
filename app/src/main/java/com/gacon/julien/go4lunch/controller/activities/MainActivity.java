package com.gacon.julien.go4lunch.controller.activities;

import android.content.Intent;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.gacon.julien.go4lunch.R;
import com.google.android.material.snackbar.Snackbar;

import android.os.Bundle;
import android.view.View;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.gacon.julien.go4lunch.controller.utils.auth.BaseActivity;
import com.gacon.julien.go4lunch.controller.utils.auth.ProfileActivity;

import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    //FOR DATA
    // - Identifier for Sign-In Activity
    private static final int RC_SIGN_IN = 123;
    //FOR DESIGN
    // - Get Coordinator Layout
    @BindView(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        // Start appropriate activity
        if (this.isCurrentUserLogged()) {
            this.startProfileActivity();
        }
    }

    // --------------------
    // ACTIONS
    // --------------------

    /**
     * Launch Sign-In Activity when user clicked on Login Button
     */
    @OnClick({R.id.google_button_login, R.id.facebook_button_login})
    public void onClickLoginButton(View view) {
        switch (view.getId()) {
            // 3 - Launch Sign-In Activity when user clicked on Login Button
            case R.id.google_button_login:
                this.startGoogleSignInActivity();
                break;
            case R.id.facebook_button_login:
                this.startFacebookSignInActivity();
                break;
        }
    }

    /**
     * Lounch activity with user data
     *
     * @param requestCode code to call activity
     * @param resultCode  feedback
     * @param data        user data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 4 - Handle SignIn Activity response on activity result
        this.handleResponseAfterSignIn(requestCode, resultCode, data);
    }

    // --------------------
    // NAVIGATION
    // --------------------

    /**
     * Launch Google Sign-In Activity
     */
    private void startGoogleSignInActivity() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(
                                Collections.singletonList(
                                        new AuthUI.IdpConfig.GoogleBuilder().build()))
                        .setIsSmartLockEnabled(false, true)
                        .build(),
                RC_SIGN_IN);
    }

    /**
     * Launch Facebook Sign-In Activity
     */
    private void startFacebookSignInActivity() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(
                                Collections.singletonList(
                                        new AuthUI.IdpConfig.FacebookBuilder().build()))// FACEBOOK
                        .setIsSmartLockEnabled(false, true)
                        .build(),
                RC_SIGN_IN);
    }

    /**
     * Launching Profile Activity
     */
    private void startProfileActivity() {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    // --------------------
    // UI
    // --------------------

    // - Show Snack Bar with a message

    /**
     * Show Snack Bar with a message
     *
     * @param coordinatorLayout check layout
     * @param message           show result message
     */
    private void showSnackBar(CoordinatorLayout coordinatorLayout, String message) {
        Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_SHORT).show();
    }

    // --------------------
    // UTILS
    // --------------------

    /**
     * Method that handles response after SignIn Activity close
     *
     * @param requestCode code to access activity
     * @param resultCode  activity feedback
     * @param data        user data
     */
    private void handleResponseAfterSignIn(int requestCode, int resultCode, Intent data) {

        IdpResponse response = IdpResponse.fromResultIntent(data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) { // SUCCESS
                showSnackBar(this.coordinatorLayout, getString(R.string.connection_succeed));
                this.startProfileActivity(); // repeat for synchronization
            } else { // ERRORS
                if (response == null) {
                    showSnackBar(this.coordinatorLayout, getString(R.string.error_authentication_canceled));
                } else if (Objects.requireNonNull(response.getError()).getErrorCode() == ErrorCodes.NO_NETWORK) {
                    showSnackBar(this.coordinatorLayout, getString(R.string.error_no_internet));
                } else if (Objects.requireNonNull(response.getError()).getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    showSnackBar(this.coordinatorLayout, getString(R.string.error_unknown_error));
                }
            }
        }
    }
}
