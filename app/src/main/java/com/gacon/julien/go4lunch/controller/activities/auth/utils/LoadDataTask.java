package com.gacon.julien.go4lunch.controller.activities.auth.utils;

import android.os.AsyncTask;
import android.widget.ProgressBar;

public class LoadDataTask extends AsyncTask<Void, Void, String> {

    private BaseActivity mBaseActivity;
    private ProgressBar mProgressBar;

    public LoadDataTask(BaseActivity baseActivity) {
        mBaseActivity = baseActivity;
    }

    @Override
    protected void onPreExecute() {
        mBaseActivity.initPlaces();
        mBaseActivity.initializeCurrentPlace();
        mBaseActivity.initializePlaceDetails();
        mBaseActivity.findCurrentPlaceRequest();
        mBaseActivity.getCurrentPlaces();
        //mProgressBar.setVisibility(ProgressBar.VISIBLE);
    }

    @Override
    protected String doInBackground(Void... voids) {
        return "Task succeed !";
    }

    @Override
    protected void onPostExecute(String string) {
        mBaseActivity.getMapViewFragment();

    }
}
