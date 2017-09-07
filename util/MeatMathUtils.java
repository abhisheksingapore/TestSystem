package me.veganbuddy.veganbuddy.util;

/**
 * Created by abhishek on 29/8/17.
 */

public class MeatMathUtils {

    public static double NUMBER_OF_MEALS_PER_YEAR = 1095; // 3 meals per day are assumed including all snacks
    public static double NUMBER_OF_MEALS_PER_DAY = 3;
    public static double NUMBER_OF_MEALS_PER_WEEK = 21; // = 7 * 3
    public static double NUMBER_OF_MEALS_PER_MONTh = 630; // = 7 * 3 * 30


    //Number of Animals killed per person per year (TODO: To be verified through credible source)
    public static double NUMBER_OF_COWS = 0.12;
    public static double NUMBER_OF_CHICKENS = 26.52;
    public static double NUMBER_OF_PIGS = 0.36;
    public static double NUMBER_OF_FISH = 181;
    public static double NUMBER_OF_ANIMALS = 208;

    //Weight of animal product consumed per person per meal (in Grams) based on above numbers

    public static double NUMBER_OF_COWS_PER_MEAL = 120; //average steak consumed is 360g of bone-in-meat
    public static double NUMBER_OF_CHICKENS_PER_MEAL = 56; // average weight of a boiler chicken is 2.27kg
    public static double NUMBER_OF_PIGS_PER_MEAL = 34;
    public static double NUMBER_OF_FISH_PER_MEAL = 170;
    public static double TOTAL_ANIMAL_WEIGHT_PER_MEAL = 380;

    public static String DASHBOARD_ANIMAL_COLOR;

    public static final String RED_COLOR = "RED";
    public static final String YELLOW_COLOR = "YELLOW";
    public static final String GREEN_COLOR = "GREEN";


    public static int IN_ONE_DAY = 0;
    public static int IN_ONE_WEEK = 1;
    public static int IN_ONE_MONTH = 2;
    public static int IN_ONE_YEAR = 3;

    public MeatMathUtils () {

    }

    public static void getDashboardAnimalColor(int numberOfMeals, int mealConsumptionPeriod) {
        if (mealConsumptionPeriod == IN_ONE_DAY) {

            if (isBetween(numberOfMeals, 0.0,NUMBER_OF_MEALS_PER_DAY/3 )) {
                DASHBOARD_ANIMAL_COLOR = RED_COLOR;
            } else if (isBetween(numberOfMeals, NUMBER_OF_MEALS_PER_DAY/3 ,2 * NUMBER_OF_MEALS_PER_DAY/3 )) {
                DASHBOARD_ANIMAL_COLOR = YELLOW_COLOR;
            } else if (numberOfMeals >= (2 * NUMBER_OF_MEALS_PER_DAY/3)) {
                DASHBOARD_ANIMAL_COLOR = GREEN_COLOR; //Todo: figure out the maximum number of meals allowed per day
            }
        }
    }

    private static boolean isBetween(int x, Double lower, double upper) {
        return lower <= x && x < upper;
    }

}