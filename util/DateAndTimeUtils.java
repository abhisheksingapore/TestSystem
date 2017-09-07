package me.veganbuddy.veganbuddy.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import me.veganbuddy.veganbuddy.R;

/**
 * Created by abhishek on 26/8/17.
 */

public class DateAndTimeUtils {

    public static String MEAL_TYPE = "Snacks";
    private static Calendar rightNow = Calendar.getInstance();

    //Variable to measure the meal type based on time of the day
    private static int breakfast_start = 6;
    private static int breakfast_end = 10;
    private static int lunch_start = 12;
    private static int lunch_end = 15;
    private static int dinner_start = 18;
    private static int dinner_end = 20;
    private static int supper_start = 21;
    private static int supper_end = 23;

    public DateAndTimeUtils () {

        }

    public static String getMealTypeBasedOnTimeOfTheDay() {
        int timeNow = timeOfTheDay();

        if (isBetween(timeNow,breakfast_start, breakfast_end)) {
            MEAL_TYPE = "BreakFast";
        }

        if (isBetween(timeNow, lunch_start, lunch_end)) {
            MEAL_TYPE = "Lunch";
        }

        if (isBetween(timeNow, dinner_start, dinner_end)) {
            MEAL_TYPE = "Dinner";
        }

        if (isBetween(timeNow, supper_start, supper_end)) {
            MEAL_TYPE = "Supper";
        }

        return MEAL_TYPE;
    }

    private static boolean isBetween(int x, int lower, int upper) {
        return lower <= x && x <= upper;
    }

    private static int  timeOfTheDay(){
        return rightNow.get(Calendar.HOUR_OF_DAY);
    }

    public static String dateTimeStamp(){
        return new SimpleDateFormat("yyyyMMMdd_HHmmss").format(new Date());
    }

    public static String dateStamp(){
        return new SimpleDateFormat("yyyyMMMdd").format(new Date());
    }

    public static int thisWeek(){
        return rightNow.get(Calendar.WEEK_OF_YEAR);
    }

    public static int thisMonth() {
        return rightNow.get(Calendar.MONTH);
    }

    public static int thisYear () {
        return rightNow.get(Calendar.YEAR);
    }
}
