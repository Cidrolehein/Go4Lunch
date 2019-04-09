package com.gacon.julien.go4lunch.auth;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * firebase users checking
 */
public class BaseActivity extends AppCompatActivity {

    // --------------------
    // UTILS
    // --------------------

    /**
     * User on firebase
     *
     * @return user connected
     */
    @Nullable
    protected FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    /**
     * Check if a user is connected
     *
     * @return true or false
     */
    protected Boolean isCurrentUserLogged() {
        return (this.getCurrentUser() != null);
    }

}
