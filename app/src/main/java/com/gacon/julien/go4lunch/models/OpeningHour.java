package com.gacon.julien.go4lunch.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.time.DayOfWeek;

public class OpeningHour {
    @SerializedName("day")
    @Expose
    private DayOfWeek mDay;

    public OpeningHour(DayOfWeek day) {
        mDay = day;
    }

    public DayOfWeek getDay() {
        return mDay;
    }
}
