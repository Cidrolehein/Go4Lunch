package com.gacon.julien.go4lunch.controller.activities.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.gacon.julien.go4lunch.R;
import com.gacon.julien.go4lunch.controller.activities.api.UserHelper;
import com.gacon.julien.go4lunch.controller.activities.auth.utils.BaseActivity;
import com.gacon.julien.go4lunch.models.User;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class AlarmReceiver extends BroadcastReceiver {

    protected String namePlaceSelected;
    protected String addressPlaceSelected;
    protected String usersJoined;

    @Override
    public void onReceive(Context context, Intent intent) {
        BaseActivity baseActivity = new BaseActivity();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // - Get all users names
            UserHelper.getUsersCollection().addSnapshotListener(((queryDocumentSnapshots, e) -> {
                String placeSelectedByCurrentUserId;
                namePlaceSelected = "";
                addressPlaceSelected = "";
                usersJoined = "";
                ArrayList<String> listOfUsers = new ArrayList<>();
                assert queryDocumentSnapshots != null;
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    User user = documentSnapshot.toObject(User.class);
                    // select current place of user
                    if (user.getUsername().equals(Objects.requireNonNull(baseActivity.getCurrentUser()).getDisplayName())) {
                        if (user.getPlaceSelectedId() != null && user.getPlaceName() != null
                                && user.getPlaceSelectedAddress() != null) {
                            placeSelectedByCurrentUserId = user.getPlaceSelectedId();
                            namePlaceSelected = user.getPlaceName();
                            addressPlaceSelected = user.getPlaceSelectedAddress();
                        } else placeSelectedByCurrentUserId = "No place selected";
                        // compare with other users
                        if (user.getPlaceSelectedId() != null) {
                            if (user.getPlaceSelectedId().equals(placeSelectedByCurrentUserId)) {
                                listOfUsers.add(user.getUsername());
                                usersJoined = String.join(",", listOfUsers);
                                namePlaceSelected = context.getString(R.string.notification_title, namePlaceSelected, usersJoined);
                                addressPlaceSelected = context.getString(R.string.notification_message, user.getPlaceSelectedAddress());
                                addNotification(context, namePlaceSelected, addressPlaceSelected);
                            }
                        }

                    }
                }
            }));
        }
    }

    private void addNotification(Context context, String title, String message){
        // Trigger the notification
        NotificationHelper notificationHelper = new NotificationHelper(context);
        NotificationCompat.Builder nb = notificationHelper.getChannel1Notification(title, message);
        notificationHelper.getManager().notify(1, nb.build());
    }

}
