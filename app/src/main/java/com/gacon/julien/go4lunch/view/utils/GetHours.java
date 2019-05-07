package com.gacon.julien.go4lunch.view.utils;

import com.gacon.julien.go4lunch.models.LunchModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class GetHours {

    private int openPlaceHour;
    private int closePlaceHour;
    private int currentHour;
    private int currentMinutes;
    private String openPlaceHourString, openPlaceMinutesString, closePlaceHourString,
            closePlaceMinutesString, currentHourString, currentMinutesString;
    private String dayOfWeek;
    private String isItOpen;
    private ArrayList daysOpen;
    private Date dateOpenPlace, dateClosePlace, dateCurrentHour;

    /**
     * Set Place Day and Time
     *
     * @param lunch Lunch Model
     * @return A string to put in textView
     */
    public String getDayOpen(LunchModel lunch) {
        isItOpen = "";
        // Get the current day & hour
        Calendar calendar = Calendar.getInstance();
        dayOfWeek = getCurrentDay(calendar);
        currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        currentMinutes = calendar.get(Calendar.MINUTE);
        // Get openPlaceDay, openPlaceHour, closePlaceHour
        daysOpen = getArrayOfDays(lunch); // open's days
        for (int i = 0; i < lunch.getPeriods().size(); i++) {
            if (lunch.getPeriods().get(i).getOpen() != null && lunch.getPeriods().get(i).getClose() != null) {
                formatHour(lunch, i);
                parseToSimpleDateFormat();
                compareAndGetIsItOpen();
            }
        }
        return isItOpen;
    }

    private void formatHour(LunchModel lunch, int i) {
        openPlaceHour = Objects.requireNonNull(lunch.getPeriods().get(i).getOpen()).getTime().getHours();
        closePlaceHour = Objects.requireNonNull(lunch.getPeriods().get(i).getClose()).getTime().getHours();
        int openPlaceMinutes = Objects.requireNonNull(lunch.getPeriods().get(i).getOpen()).getTime().getMinutes();
        int closePlaceMinutes = Objects.requireNonNull(lunch.getPeriods().get(i).getOpen()).getTime().getMinutes();
        openPlaceHourString = String.valueOf(openPlaceHour);
        openPlaceMinutesString = String.valueOf(openPlaceMinutes);
        closePlaceHourString = String.valueOf(closePlaceHour);
        closePlaceMinutesString = String.valueOf(closePlaceMinutes);
        currentHourString = String.valueOf(currentHour);
        currentMinutesString = String.valueOf(currentMinutes);
    }

    private void parseToSimpleDateFormat() {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm", Locale.getDefault());
        try {
            dateOpenPlace = sdf.parse(openPlaceHourString + ":" + openPlaceMinutesString);
            dateClosePlace = sdf.parse(closePlaceHourString + ":" + closePlaceMinutesString);
            dateCurrentHour = sdf.parse(currentHourString + ":" + currentMinutesString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void compareAndGetIsItOpen() {
        // Compare and  get isItOpen
        if (daysOpen.contains(dayOfWeek) &&
                (dateOpenPlace.equals(dateCurrentHour) ||
                        dateOpenPlace.after(dateCurrentHour) ||
                        dateClosePlace.before(dateCurrentHour))) {
            isItOpen = "Open until " + closePlaceHour + "h";
        } else if (!daysOpen.contains(dayOfWeek)) {
            isItOpen = "Close today";
        } else if (daysOpen.contains(dayOfWeek) && currentHour == closePlaceHour - 1) {
            isItOpen = "Closing soon !";
        } else isItOpen = "Close until " + openPlaceHour + "h";

    }

    /**
     * Get the current day
     *
     * @param calendar Calendar
     * @return The current day
     */
    private String getCurrentDay(Calendar calendar) {
        String day = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
        day = day.toUpperCase(); // change currentDay to upper case
        return day;
    }

    /**
     * All the open days
     *
     * @param lunch Model lunch
     * @return Array of days
     */
    private ArrayList getArrayOfDays(LunchModel lunch) {
        ArrayList<String> arrayListOfDays = new ArrayList<>();
        String openPlaceDay;
        for (int i = 0; i < lunch.getPeriods().size(); i++) {
            openPlaceDay = Objects.requireNonNull(lunch.getPeriods().get(i).getOpen()).getDay().toString();
            arrayListOfDays.add(openPlaceDay);
        }
        return arrayListOfDays;
    }
}
