package me.veganbuddy.veganbuddy.actors;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import me.veganbuddy.veganbuddy.util.DateAndTimeUtils;

import static me.veganbuddy.veganbuddy.util.DateAndTimeUtils.dateStamp;

/**
 * Created by abhishek on 2/9/17.
 */

public class Dashboard {

    /*Todo: Abstract Names of all the variables here because they become names of the "keys" in
    Todo..contd...  the FirebaseBase Database resulting in some hardcoding in "valueListeners" in FirebaseUtils class
    */
    public int mealsForToday = 0;
    private int mealsForLifetime = 0;

    private String startDateOfVegan = "start"; //default
    
    private Map <String, Integer> mealsForTheDay = new HashMap<>();
    private Map <String, Integer> mealsForTheYear = new HashMap<>();
    private Map <String, Integer> mealsForTheMonth = new HashMap<>();
    private Map <String, Integer> mealsForTheWeek = new HashMap<>();


    public Dashboard (){
        //Empty Constructor
    }

    public Dashboard (int mealForToday ) {

        this.mealsForLifetime = mealForToday;
        this.mealsForToday = mealForToday;
        this.startDateOfVegan = "start";
        incrementMealsForTheDay(0);
        incrementMealsForTheWeek(0);
        incrementMealsForTheMonth(0);
        incrementMealsForTheYear(0);

    }

    public Dashboard incrementDashboardByOne () {
        mealsForLifetime = mealsForLifetime + 1;
        if (notFirstMealOfToday()) {
            mealsForToday = mealsForToday + 1;
        } else {
            mealsForToday = 1;
        }

        if (startDateOfVegan.equals("start")) {
            startDateOfVegan = dateStamp();
        }
        incrementMealsForTheDay(getTodaysCurrentValue());
        incrementMealsForTheMonth(getThisMonthCurrentValue());
        incrementMealsForTheWeek(getThisWeekCurrentValue());
        incrementMealsForTheYear(getThisYearCurrentValue());
        return this;
    }

    private boolean notFirstMealOfToday() {
        String dateofToday = dateStamp();
        return mealsForTheDay.containsKey(dateofToday);
    }


    private Integer getTodaysCurrentValue() {
        String dateOfToday = dateStamp();
        
        return mealsForTheDay.get(dateOfToday);
    }

    private Integer getThisYearCurrentValue() {
        int thisYear = DateAndTimeUtils.thisYear();
        String thisYearString = Integer.toString(thisYear);

        return mealsForTheYear.get(thisYearString);
    }

    private Integer getThisWeekCurrentValue() {
        int thisWeek = DateAndTimeUtils.thisWeek();
        String thisWeekString = Integer.toString(thisWeek);

        return mealsForTheWeek.get(thisWeekString);
    }

    private Integer getThisMonthCurrentValue() {
        int thisMonth = DateAndTimeUtils.thisMonth();
        String thisMonthString = Integer.toString(thisMonth);

        return mealsForTheMonth.get(thisMonthString);
    }
    
    //Get Methods for each of the Dashboard variables
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
        User.startDateForVeganism = startDateOfVegan;
        return startDateOfVegan;
    }


    //Methods to create Dashboard data for the day, this week, this month and this year

    private void incrementMealsForTheDay(Integer currentValue) {
        String dateofToday = dateStamp();
        Integer newValue;
        if (currentValue==null) {
            newValue = 1;
        } else {
            newValue = currentValue + 1;
        }
        this.mealsForTheDay.put(dateofToday, newValue);
    }

    public void incrementMealsForTheYear(Integer currentValue) {
        int thisYear = DateAndTimeUtils.thisYear();
        Integer newValue;
        if (currentValue==null) {
            newValue = 1;
        } else {
            newValue = currentValue + 1;
        }
        String thisYearString = Integer.toString(thisYear);
        this.mealsForTheYear.put(thisYearString, newValue);
    }

    public void incrementMealsForTheMonth(Integer currentValue) {
        int thisMonth = DateAndTimeUtils.thisMonth();
        Integer newValue;
        if (currentValue==null) {
            newValue = 1;
        } else {
            newValue = currentValue + 1;
        }
        String thisMonthString = Integer.toString(thisMonth);
        this.mealsForTheMonth.put(thisMonthString, newValue);
    }

    public void incrementMealsForTheWeek(Integer currentValue) {
        int thisWeek = DateAndTimeUtils.thisWeek();
        Integer newValue;
        if (currentValue==null) {
            newValue = 1;
        } else {
            newValue = currentValue + 1;
        }
        String thisWeekString = Integer.toString(thisWeek);
        this.mealsForTheWeek.put(thisWeekString,newValue);
    }

    public boolean checkIfTodaysDateExistsInDatabase() {
        if (notFirstMealOfToday()) return true;
        else {
            mealsForToday = 0;
            return false;
        }
    }
}
