package com.gacon.julien.go4lunch.view.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Some unit tests on data conversion
 */
public class DataFormatTest {

    private DataFormat dataFormat = new DataFormat();

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

}