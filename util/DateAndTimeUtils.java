package me.veganbuddy.veganbuddy.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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
        return new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());
    }

    public static String dateStamp(){
        return new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH).format(new Date());
    }

    public static String dateStampHumanReadable(){
        return new SimpleDateFormat("yyyy-MMM-dd", Locale.ENGLISH).format(new Date());
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

    public static String timeDifference(String receivedDate) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH);
        String timeAndDaysDifference = "";
        try {
            String todaysDate = dateTimeStamp();
            Date d1 = format.parse(receivedDate);
            Date d2 = format.parse(todaysDate);

            long diff = d2.getTime() - d1.getTime();

            long diffSeconds = diff / 1000 % 60;
            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000) % 24;
            long diffDays = diff / (24 * 60 * 60 * 1000);

            if (diffDays > 30) {
                timeAndDaysDifference = "More than 1 month ago";
            } else if (diffDays > 0) {
                if (diffDays > 1) {
                    int diffDaysInt = (int) diffDays;
                    timeAndDaysDifference = Integer.toString(diffDaysInt) + " days ago";
                } else timeAndDaysDifference = "1 day ago";
                //Todo: fix the messaging if timeanddaysdiference is > 1 day. e.g. 1 day and 15 hours
            } else if (diffHours > 0) {
                if (diffHours > 1) {
                    int diffHoursInt = (int) diffHours;
                    timeAndDaysDifference = Integer.toString(diffHoursInt) + " hours ago";
                } else timeAndDaysDifference = "1 hour ago";
            } else if (diffMinutes > 1) {
                int diffMinutesInt = (int) diffMinutes;
                timeAndDaysDifference = Integer.toString(diffMinutesInt) + " mins ago";
            } else {
                timeAndDaysDifference = "Moments ago";
            }
        } catch (ParseException PE){
            PE.printStackTrace();
        }
        return timeAndDaysDifference;
    }

    public static int daysSinceVegan(String veganDate) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);
        int diffDays=0;
        try {
            String todaysDate = dateStampHumanReadable();
            Date d1 = format.parse(veganDate);
            Date d2 = format.parse(todaysDate);

            long diff = d2.getTime() - d1.getTime();

            diffDays = (int)(diff / (24 * 60 * 60 * 1000));
        } catch (ParseException PE) {
            PE.printStackTrace();
        }
        return diffDays;
    }

    public static String dateofToday() {
        String dateOfToday = dateStamp();
        return dateOfToday;
    }

    public static String thisWeekString() {
        int thisWeek = DateAndTimeUtils.thisWeek();
        String thisWeekString = Integer.toString(thisWeek);
        return thisWeekString;
    }

    public static String thisMonthString() {
        int thisMonth = DateAndTimeUtils.thisMonth();
        String thisMonthString = Integer.toString(thisMonth);
        return thisMonthString;
    }

    public static String thisYearString() {
        int thisYear = DateAndTimeUtils.thisYear();
        String thisYearString = Integer.toString(thisYear);
        return thisYearString;
    }


}
