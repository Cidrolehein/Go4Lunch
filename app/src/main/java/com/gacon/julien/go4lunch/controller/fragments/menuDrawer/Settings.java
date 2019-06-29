package com.gacon.julien.go4lunch.controller.fragments.menuDrawer;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Toast;

import com.gacon.julien.go4lunch.R;
import com.gacon.julien.go4lunch.controller.activities.utils.AlarmReceiver;

import java.util.Calendar;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * User settings for activate notifications and alarm manager
 */
public class Settings extends Fragment {

    private static final String CHECKBOX = "CHECKBOX";
    @BindView(R.id.checkbox)
    CheckBox mCheckBox;
    private SharedPreferences preferences;


    public Settings() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, view);
        // sharedpref
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        // set the sharedpreferences and checkbox
        checkBoxSharedPreferences();
        return view;
    }

    private void checkBoxSharedPreferences() {
        final SharedPreferences.Editor editor = preferences.edit();
        if (preferences.contains(CHECKBOX) && preferences.getBoolean(CHECKBOX, false)) {
            mCheckBox.setChecked(true);
        } else {
            mCheckBox.setChecked(false);
        }
        mCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (mCheckBox.isChecked()) {
                editor.putBoolean(CHECKBOX, true);
                startAlarm();
                Toast.makeText(getContext(), "Alarm manager start", Toast.LENGTH_LONG).show();
                editor.apply();
            } else {
                editor.putBoolean(CHECKBOX, false);
                cancelAlarm();
                Toast.makeText(getContext(), "Alarm manager cancel", Toast.LENGTH_LONG).show();
                editor.apply();
            }
        });
    }

    private void startAlarm() {
        // Set the alarm to start at approximately 12:00 p.m.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        // With setInexactRepeating(), you have to use one of the AlarmManager interval
        // constants--in this case, AlarmManager.INTERVAL_DAY.
        AlarmManager alarmManager = (AlarmManager) Objects.requireNonNull(getActivity())
                .getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 1, intent, 0);

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis()
                , AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    private void cancelAlarm() {
        AlarmManager alarmManager = (AlarmManager) Objects.requireNonNull(getActivity())
                .getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 1, intent, 0);

        alarmManager.cancel(pendingIntent);
    }

}
