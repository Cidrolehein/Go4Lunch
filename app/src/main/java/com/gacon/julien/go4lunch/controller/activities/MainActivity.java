package com.gacon.julien.go4lunch.controller.activities;

import android.content.Intent;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.gacon.julien.go4lunch.R;
import com.google.android.material.snackbar.Snackbar;

import android.os.Bundle;

import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.gacon.julien.go4lunch.controller.activities.auth.utils.BaseActivity;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {

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
        } else {
            this.startLoginSignInActivity();
        }

    }

    /**
     * Lunch activity with user data
     *
     * @param requestCode code to call activity
     * @param resultCode  feedback
     * @param data        user data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // - Handle SignIn Activity response on activity result
        this.handleResponseAfterSignIn(requestCode, resultCode, data);
    }

    // --------------------
    // NAVIGATION
    // --------------------

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
