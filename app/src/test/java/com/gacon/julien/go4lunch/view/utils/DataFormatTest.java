package com.gacon.julien.go4lunch.view.utils;

import com.gacon.julien.go4lunch.controller.activities.auth.utils.BaseActivity;
import com.gacon.julien.go4lunch.controller.fragments.BaseFragment;
import com.google.android.gms.maps.model.LatLng;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Some unit tests on data conversion
 */
public class DataFormatTest {

    private static final String PROVIDER = "flp";

    private DataFormat dataFormat = new DataFormat();
    private BaseActivity mBaseActivity = new BaseActivity();

    @Test
    public void formatMeters() {
        float input = 12.4f;
        String output;
        String expected = "12m";

        output = dataFormat.formatMeters(input);

        assertEquals(expected, output);
    }

    @Test
    public void passStringToFloat() {
        String input = "12.4";
        float delta = 0.1f;
        float output;
        float expected = 12.4f;

        output = dataFormat.passStringToFloat(input);

        assertEquals(expected, output, delta);
    }

    @Test
    public void distanceInMeter() {
        LatLng input = new LatLng(48.8254802,2.347486);
        double delta = 1.000001;
        float output;
        float expected = 0; // 0 because current location is not authorised

        output = mBaseActivity.distanceInMeter(input);

        assertEquals(expected, output, delta);
    }

    @Test
    public void meterInDegree(){
        double pi = Math.PI;
        double earth = 6378.137;
        double output;
        double expected = 8.983152841195216E-6;
        double delta = 0.0000001;

        BaseFragment baseFragment = new BaseFragment();
        output = baseFragment.meterInDegree(pi, earth);

        assertEquals(expected, output, delta);

    }

}