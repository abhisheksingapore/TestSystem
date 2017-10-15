package me.veganbuddy.veganbuddy.actors;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import me.veganbuddy.veganbuddy.util.DateAndTimeUtils;

import static me.veganbuddy.veganbuddy.util.Constants.DEFAULT_STATS_PIC_NAME;
import static me.veganbuddy.veganbuddy.util.Constants.DEFAULT_VEGAN_DATE;
import static me.veganbuddy.veganbuddy.util.DateAndTimeUtils.dateStamp;
import static me.veganbuddy.veganbuddy.util.DateAndTimeUtils.dateStampHumanReadable;
import static me.veganbuddy.veganbuddy.util.DateAndTimeUtils.dateofToday;
import static me.veganbuddy.veganbuddy.util.DateAndTimeUtils.thisMonthString;
import static me.veganbuddy.veganbuddy.util.DateAndTimeUtils.thisWeekString;
import static me.veganbuddy.veganbuddy.util.DateAndTimeUtils.thisYearString;

/**
 * Created by abhishek on 2/9/17.
 */

public class Dashboard {

    /*Todo: Abstract Names of all the variables here because they become names of the "keys" in
    Todo..contd...  the FirebaseBase Database resulting in some hardcoding in "valueListeners" in FirebaseUtils class
    */
    private int mealsForToday = 0;
    private int mealsForLifetime = 0;

    private String startDateOfVegan = DEFAULT_VEGAN_DATE; //default
    private String lastPicName = DEFAULT_STATS_PIC_NAME;
    
    private Map <String, Integer> mealsForTheDay = new HashMap<>();
    private Map <String, Integer> mealsForTheYear = new HashMap<>();
    private Map <String, Integer> mealsForTheMonth = new HashMap<>();
    private Map <String, Integer> mealsForTheWeek = new HashMap<>();


    public Dashboard (){
        //Empty Constructor
    }

    //This constructor to be used for intializing the Dashboard for the first time in the application
    public Dashboard (int mealForToday ) {
        if (mealForToday==0) {
            this.mealsForLifetime = mealForToday;
            this.mealsForToday = mealForToday;
            this.startDateOfVegan = dateStampHumanReadable();
            this.lastPicName = DEFAULT_STATS_PIC_NAME;
            this.mealsForTheDay.put(dateofToday(), mealForToday);
            this.mealsForTheWeek.put(thisWeekString(), mealForToday);
            this.mealsForTheMonth.put(thisMonthString(), mealForToday);
            this.mealsForTheYear.put(thisYearString(), mealForToday);
        }
    }


    public Dashboard incrementDashboardByOne () {
        mealsForLifetime = mealsForLifetime + 1;
        if (notFirstMealOfToday()) {
            mealsForToday = mealsForToday + 1;
        } else {
            mealsForToday = 1;
        }
        if (startDateOfVegan.equals(DEFAULT_VEGAN_DATE) || startDateOfVegan == null)
            this.startDateOfVegan = dateStampHumanReadable();
        incrementMealsForTheDay(getTodaysCurrentValue());
        incrementMealsForTheMonth(getThisMonthCurrentValue());
        incrementMealsForTheWeek(getThisWeekCurrentValue());
        incrementMealsForTheYear(getThisYearCurrentValue());
        return this;
    }

    private boolean notFirstMealOfToday() {
        String dateofTodayStr = dateStamp();
        return mealsForTheDay.containsKey(dateofTodayStr);
    }

    public boolean todayExistsInDashboard(){
        String dateofTodayStr = dateStamp();
        return mealsForTheDay.containsKey(dateofTodayStr);
    }

    private Integer getTodaysCurrentValue() {
        return mealsForTheDay.get(dateofToday());
    }

    private Integer getThisYearCurrentValue() {
        return mealsForTheYear.get(thisYearString());
    }

    private Integer getThisWeekCurrentValue() {
        return mealsForTheWeek.get(thisWeekString());
    }

    private Integer getThisMonthCurrentValue() {
        return mealsForTheMonth.get(thisMonthString());
    }

    //Methods to create Dashboard data for the day, this week, this month and this year
    private void incrementMealsForTheDay(Integer currentValue) {
        Integer newValue;
        if (currentValue==null) {
            newValue = 1;
        } else {
            newValue = currentValue + 1;
        }
        this.mealsForTheDay.put(dateofToday(), newValue);
    }

    private void incrementMealsForTheYear(Integer currentValue) {
        Integer newValue;
        if (currentValue==null) {
            newValue = 1;
        } else {
            newValue = currentValue + 1;
        }
        this.mealsForTheYear.put(thisYearString(), newValue);
    }

    private void incrementMealsForTheMonth(Integer currentValue) {
        Integer newValue;
        if (currentValue==null) {
            newValue = 1;
        } else {
            newValue = currentValue + 1;
        }
        this.mealsForTheMonth.put(thisMonthString(), newValue);
    }

    private void incrementMealsForTheWeek(Integer currentValue) {
        int thisWeek = DateAndTimeUtils.thisWeek();
        Integer newValue;
        if (currentValue==null) {
            newValue = 1;
        } else {
            newValue = currentValue + 1;
        }
        this.mealsForTheWeek.put(thisWeekString(),newValue);
    }

    public boolean checkIfTodaysDateExistsInDatabase() {
        if (notFirstMealOfToday()) return true;
        else {
            mealsForToday = 0;
            return false;
        }
    }

    public void setMealsForToday(int mealsForToday) {
        this.mealsForToday = mealsForToday;

        //check if it is first meal of the day, if yes, then initialize today's date in mealsForTheDay
        if (mealsForToday ==0 ) this.mealsForTheDay.put(dateofToday(), mealsForToday);
    }

    public void setLastPicName(String lastPicName) {
        this.lastPicName = lastPicName;
    }

    //////////Get Methods for each of the Dashboard variables/////////////
    public int getMealsForToday() {
        return mealsForToday;
    }

    public int getMealsForLifetime() {
        return mealsForLifetime;
    }

    public Map<String, Integer> getMealsForTheDay() {
        return mealsForTheDay;
    }

    public Map<String, Integer> getMealsForTheYear() {
        return mealsForTheYear;
    }

    public Map<String, Integer> getMealsForTheMonth() {
        return mealsForTheMonth;
    }

    public Map<String, Integer> getMealsForTheWeek() {
        return mealsForTheWeek;
    }

    public String getStartDateOfVegan() {
         if (startDateOfVegan.equals(DEFAULT_VEGAN_DATE) || startDateOfVegan == null)
             this.startDateOfVegan = dateStampHumanReadable();
         return startDateOfVegan;
    }

    public String getLastPicName() {
        return lastPicName;
    }

}
