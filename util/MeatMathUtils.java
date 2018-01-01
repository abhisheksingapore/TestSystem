package me.veganbuddy.veganbuddy.util;

import java.util.Locale;

import me.veganbuddy.veganbuddy.actors.AnimalsSavedDashboard;
import me.veganbuddy.veganbuddy.actors.Dashboard;

import static me.veganbuddy.veganbuddy.util.DateAndTimeUtils.daysSinceVeganStart;
import static me.veganbuddy.veganbuddy.util.GlobalVariables.myDashboard;
import static me.veganbuddy.veganbuddy.util.GlobalVariables.thisAppUser;

/**
 * Created by abhishek on 29/8/17.
 */

public class MeatMathUtils {

    public static final String RED_COLOR = "RED";
    public static final String YELLOW_COLOR = "YELLOW";
    public static final String GREEN_COLOR = "GREEN";
    private static final double MEAT_EATER_CARBON_FOOTPRINT = 3.3;
    private static final double COMPLETE_VEGAN_CARBON_FOOTPRINT = 1.5;
    private static final int NUMBER_OF_DAYS_IN_YEAR = 365;
    public static int NUMBER_OF_MEALS_PER_YEAR = 1095; // 3 meals per day are assumed including all snacks
    public static int NUMBER_OF_MEALS_PER_DAY = 3;
    public static int NUMBER_OF_MEALS_PER_WEEK = 21; // = 7 * 3
    public static int NUMBER_OF_MEALS_PER_MONTh = 630; // = 7 * 3 * 30
    //Number of Animals killed per person per year (TODO: To be verified through credible source)
    public static int NUMBER_OF_FISH_PERYEAR = 178;
    public static int NUMBER_OF_CHICKENS_PERYEAR = 27;
    public static int NUMBER_OF_MARINE_BYKILL_PERYEAR = 12;
    public static int NUMBER_OF_PIGS_PERYEAR = 1;
    public static int NUMBER_OF_COWS_PERYEAR = 1;
    public static int NUMBER_OF_WILD_CATS_PERYEAR = 1;
    public static int NUMBER_OF_ANIMALS_PERYEAR = 220;
    //Number of meals per animal
    public static int MEALS_PER_FISH = 2;
    public static int MEALS_PER_CHICKEN = 3;
    public static int MEALS_PER_MARINE_BYKILL = 28;
    public static int MEALS_PER_PIG = 51;
    public static int MEALS_PER_COW = 103;
    public static int MEALS_PER_WILD_CAT = 168; //Total number of meals per year must equal to NUMBER_OF_MEALS_PER_YEAR
    //Weight of animal product consumed per person per meal (in Grams) based on above numbers
    public static double WEIGHT_OF_COWS_PER_MEAL = 120; //average steak consumed is 360g of bone-in-meat
    public static double WEIGHT_OF_CHICKENS_PER_MEAL = 56; // average weight of a boiler chicken is 2.27kg
    public static double WEIGHT_OF_PIGS_PER_MEAL = 34;
    public static double WEIGHT_OF_FISH_PER_MEAL = 170;
    public static double TOTAL_ANIMAL_WEIGHT_PER_MEAL = 380;
    public static String DASHBOARD_ANIMAL_COLOR;
    public static int IN_ONE_DAY = 0;
    public static int IN_ONE_WEEK = 1;
    public static int IN_ONE_MONTH = 2;
    public static int IN_ONE_YEAR = 3;
    public static double veganMealsForDuration;
    //Variables related to the "How Vegan?"
    private static double totalPotentialNumberOfMeals;
    private static double percentVegan = 0.0;


    private static double WATER_SAVED_PER_VEGAN_MEAL = 3700 / NUMBER_OF_MEALS_PER_DAY; //IN GALLONS TERMS
    private static double CO2_SAVED_PER_VEGAN_MEAL = 8 / NUMBER_OF_MEALS_PER_DAY; //IN POUNDS/lbs TERMS
    private static double EXCREMENT_PER_MEAT_EATER = 20; //IN NUMBER OF PEOPLE TERMS
    private static double FORESTS_SAVED_PER_VEGAN_MEAL = 10; //in square feet terms
    private static double ENERGY_PERCENT_SAVED_PER_VEGAN_MEAL = 90; //in percentage terms

    private static double SATURATED_FAT_PER_MEAL = 31 / 3; //in terms of grams per day
    private static double CHOLESTEROL_PER_MEAL = 100;//IN TERMS OF MG/DL

    private static double LAND_SAVED_PER_MEAL = 2; //IN TERMS OF ACRES OF ARABLE LAND
    private static double MEALS_FOR_MEAT_SAVED = 23; //in terms of number of human meal count


    public MeatMathUtils () {

    }

    public static void getDashboardAnimalColor(int numberOfMeals, int mealConsumptionPeriod) {
        if (mealConsumptionPeriod == IN_ONE_DAY) {

            if (isBetween(numberOfMeals, 0.0,NUMBER_OF_MEALS_PER_DAY/3 )) {
                DASHBOARD_ANIMAL_COLOR = RED_COLOR;
            } else if (isBetween(numberOfMeals, (double) (NUMBER_OF_MEALS_PER_DAY / 3), 2 * NUMBER_OF_MEALS_PER_DAY / 3)) {
                DASHBOARD_ANIMAL_COLOR = YELLOW_COLOR;
            } else if (numberOfMeals >= (2 * NUMBER_OF_MEALS_PER_DAY/3)) {
                DASHBOARD_ANIMAL_COLOR = GREEN_COLOR; //Todo: figure out the maximum number of meals allowed per day
            }
        }
    }

    private static boolean isBetween(int x, Double lower, double upper) {
        return lower <= x && x < upper;
    }


    public static void calculatePercentVeganForToday() {
        totalPotentialNumberOfMeals = NUMBER_OF_MEALS_PER_DAY;

        //catch the exception where the app opens without a valid myDashboard Data
        if (myDashboard == null) myDashboard = new Dashboard(0);
        veganMealsForDuration = (double) myDashboard.getMealsForToday();
        calculatePercentage();
    }

    public static double percentVeganForLifetime() {
        int veganMeals;
        double numberOfVeganDays;
        double percentageVegan;

        //catch the exception where the app opens without a valid myDashboard Data
        if (myDashboard == null) myDashboard = new Dashboard(0);
        veganMeals = myDashboard.getMealsForLifetime();
        numberOfVeganDays = daysSinceVeganStart(thisAppUser.getStartDateOfVegan()) * NUMBER_OF_MEALS_PER_DAY;

        percentageVegan = (veganMeals / numberOfVeganDays) * 100;
        if (percentageVegan > 100) {
            //In case meals logged as vegan are greater than the "totalPotentialNumberOfMeals" for the duration
            percentageVegan = 100;
        }

        return percentageVegan;
    }

    public static double getVeganMealsForDuration() {
        return veganMealsForDuration;
    }

    public static String getNumberofVeganMealsLogged() {
        return Double.toString(veganMealsForDuration);
    }

    public static String getTotalMealsLogged() {
        return Double.toString(totalPotentialNumberOfMeals);
        //Todo: It is possible for someone to log more than 3 meals a day as Vegan. Must handle....
    }

    public static String percentVeganString() {
        return (String.format(Locale.ENGLISH, "%.1f", percentVegan) + "%");
    }

    public static float percentVeganFloat() {
        return ((float) percentVegan);
    }

    public static String percentVeganString(double value) {
        return (String.format(Locale.ENGLISH, "%.1f", value) + "%");
    }

    public static float percentVeganFloat(double value) {
        return ((float) value);
    }

    private static void calculatePercentage(){
        percentVegan = (veganMealsForDuration/totalPotentialNumberOfMeals) * 100;
        if (percentVegan > 100) {
            //In case meals logged as vegan are greater than the "totalPotentialNumberOfMeals" for the duration
            percentVegan =100;
        }
    }


    public static double getCholesterolIntake() {
        return CHOLESTEROL_PER_MEAL;
    }

    public static double getMealsForMeatSaved() {
        return MEALS_FOR_MEAT_SAVED;
    }


    public static int getNumberOfAnimalsSaved(int numOfMeals, AnimalsSavedDashboard asd) {
        double numberOfAnimalsSaved;
        double numberOfFishSaved = 0;
        double numberOfChickenSaved = 0;
        double numberOfMarineByKillSaved = 0;
        double numberOfPigsSaved = 0;
        double numberOfCowsSaved = 0;
        double numberOfWildCatsSaved = 0;

        int numberofVeganDays = myVeganDays();
        int numberOfVeganYears = (int) Math.floor(numberofVeganDays / NUMBER_OF_DAYS_IN_YEAR);
        int remainingVeganDays = myVeganDays() - (numberOfVeganYears * NUMBER_OF_DAYS_IN_YEAR);
        int numberOfMeals = numOfMeals - ((numberOfVeganYears * NUMBER_OF_MEALS_PER_YEAR));

        int numberOfMealsForFishPerYear = NUMBER_OF_FISH_PERYEAR * MEALS_PER_FISH;
        int numberOfMealsForChickenPerYear = NUMBER_OF_CHICKENS_PERYEAR * MEALS_PER_CHICKEN;
        int numberOfMealsForMarineBykillPerYear = NUMBER_OF_MARINE_BYKILL_PERYEAR * MEALS_PER_MARINE_BYKILL;
        int numberOfMealsForPigPerYear = NUMBER_OF_PIGS_PERYEAR * MEALS_PER_PIG;
        int numberOfMealsForCowPerYear = NUMBER_OF_COWS_PERYEAR * MEALS_PER_COW;
        int numberOfMealsForWildCatsPerYear = NUMBER_OF_WILD_CATS_PERYEAR * MEALS_PER_WILD_CAT;

        int fishPlusChick = numberOfMealsForFishPerYear + numberOfMealsForChickenPerYear;
        int fishPlusChickMarine = fishPlusChick + numberOfMealsForMarineBykillPerYear;
        int fishPlusChickMarinePig = fishPlusChickMarine + numberOfMealsForPigPerYear;
        int fishPlusChickMarinePigCow = fishPlusChickMarinePig + numberOfMealsForCowPerYear;
        int fishPlusChickMarinePigCowCats = fishPlusChickMarinePigCow + numberOfMealsForWildCatsPerYear;

        //Calculation for days remaining 365 days
        if (remainingVeganDays < 365) {
            if (numberOfMeals < numberOfMealsForFishPerYear) {
                //Only saved Fish/Sea animals
                numberOfFishSaved = numberOfMeals / MEALS_PER_FISH;
                //if this data is for the Animal Saved Dashboard
                if (asd != null) {
                    //fish saved = (numberOfVeganYears * NUMBER_OF_FISH_PERYEAR) + numberOfFishSaved
                    asd.setFishSavedNum((numberOfVeganYears * NUMBER_OF_FISH_PERYEAR)
                            + (int) Math.round(numberOfFishSaved));
                    //Calculate percentage for the progressBar
                    asd.setFishSavedprogress(
                            (int) Math.round((numberOfFishSaved * 100) / NUMBER_OF_FISH_PERYEAR));
                }
            } else if (numberOfMeals < fishPlusChick) {
                //Saved fish and chickens
                numberOfChickenSaved = (numberOfMeals - numberOfMealsForFishPerYear) / MEALS_PER_CHICKEN;
                //Fish saved number is complete for the year
                numberOfFishSaved = (numberOfVeganYears + 1) * NUMBER_OF_FISH_PERYEAR;
                if (asd != null) {
                    //chicken saved = (numberOfVeganYears * NUMBER_OF_CHICKEN_PERYEAR) + numberOfChickenSaved
                    asd.setChickenSavedNum((numberOfVeganYears * NUMBER_OF_CHICKENS_PERYEAR) +
                            (int) Math.round(numberOfChickenSaved));
                    asd.setChickenSavedprogress(
                            (int) Math.round((numberOfChickenSaved * 100) / NUMBER_OF_CHICKENS_PERYEAR));
                    //Fish saved number and progress is complete for the year
                    asd.setFishSavedprogress(100);
                    asd.setFishSavedNum((int) numberOfFishSaved);
                }
            } else if (numberOfMeals < fishPlusChickMarine) {
                //saved fish, chicken and marine bykill
                numberOfMarineByKillSaved = (numberOfMeals - fishPlusChick) / MEALS_PER_MARINE_BYKILL;
                //CHICKEN is complete for the year
                numberOfChickenSaved = (numberOfVeganYears + 1) * NUMBER_OF_CHICKENS_PERYEAR;
                //Fish saved number and progress is complete for the year
                numberOfFishSaved = (numberOfVeganYears + 1) * NUMBER_OF_FISH_PERYEAR;
                if (asd != null) {
                    //Marine bykill number and progress
                    asd.setMarinebykillSavedNum((numberOfVeganYears * NUMBER_OF_MARINE_BYKILL_PERYEAR)
                            + (int) Math.round(numberOfMarineByKillSaved));
                    asd.setMarinebykillSavedprogress(
                            (int) Math.round((numberOfMarineByKillSaved * 100) / NUMBER_OF_MARINE_BYKILL_PERYEAR));
                    asd.setChickenSavedprogress(100);
                    asd.setChickenSavedNum((int) numberOfChickenSaved);
                    asd.setFishSavedprogress(100);
                    asd.setFishSavedNum((int) numberOfFishSaved);
                }
            } else if (numberOfMeals < fishPlusChickMarinePig) {
                // saved fish, chicken, marine bykill and Pig
                numberOfPigsSaved = (numberOfMeals - fishPlusChickMarine) / MEALS_PER_PIG;
                //Marine by kill is complete for the year
                numberOfMarineByKillSaved = (numberOfVeganYears + 1) * NUMBER_OF_MARINE_BYKILL_PERYEAR;
                //CHICKEN is complete for the year
                numberOfChickenSaved = (numberOfVeganYears + 1) * NUMBER_OF_CHICKENS_PERYEAR;
                //Fish saved number and progress is complete for the year
                numberOfFishSaved = (numberOfVeganYears + 1) * NUMBER_OF_FISH_PERYEAR;
                if (asd != null) {
                    //Pig number and progress
                    asd.setPigSavedNum((int) Math.round(numberOfPigsSaved));
                    asd.setPigSavedprogress(
                            (int) Math.round((numberOfPigsSaved * 100) / NUMBER_OF_PIGS_PERYEAR));
                    asd.setMarinebykillSavedprogress(100);
                    asd.setMarinebykillSavedNum((int) numberOfMarineByKillSaved);
                    asd.setChickenSavedprogress(100);
                    asd.setChickenSavedNum((int) numberOfChickenSaved);
                    asd.setFishSavedprogress(100);
                    asd.setFishSavedNum((int) numberOfFishSaved);
                }
            } else if (numberOfMeals < fishPlusChickMarinePigCow) {
                // saved fish, chicken, marine bykill, Pig and Cow
                numberOfCowsSaved = (numberOfMeals - fishPlusChickMarinePig) / MEALS_PER_COW;
                //Pig is complete for the year
                numberOfPigsSaved = (numberOfVeganYears + 1) * NUMBER_OF_PIGS_PERYEAR;
                //Marine by kill is complete for the year
                numberOfMarineByKillSaved = (numberOfVeganYears + 1) * NUMBER_OF_MARINE_BYKILL_PERYEAR;
                //CHICKEN is complete for the year
                numberOfChickenSaved = (numberOfVeganYears + 1) * NUMBER_OF_CHICKENS_PERYEAR;
                //Fish saved number and progress is complete for the year
                numberOfFishSaved = (numberOfVeganYears + 1) * NUMBER_OF_FISH_PERYEAR;

                if (asd != null) {
                    asd.setCowSavedNum((int) Math.round(numberOfCowsSaved));
                    asd.setCowSavedprogress(
                            (int) Math.round((numberOfCowsSaved * 100) / NUMBER_OF_COWS_PERYEAR));
                    asd.setPigSavedprogress(100);
                    asd.setPigSavedNum((int) numberOfPigsSaved);
                    asd.setMarinebykillSavedprogress(100);
                    asd.setMarinebykillSavedNum((int) numberOfMarineByKillSaved);
                    asd.setChickenSavedprogress(100);
                    asd.setChickenSavedNum((int) numberOfChickenSaved);
                    asd.setFishSavedprogress(100);
                    asd.setFishSavedNum((int) numberOfFishSaved);
                }
            } else if (numberOfMeals < fishPlusChickMarinePigCowCats) {
                // saved fish, chicken, marine bykill, Pig, Cow and Wild Cats
                numberOfWildCatsSaved = (numberOfMeals - fishPlusChickMarinePigCow) / MEALS_PER_WILD_CAT;
                //COW is complete for the year
                numberOfCowsSaved = (numberOfVeganYears + 1) * NUMBER_OF_COWS_PERYEAR;
                //Pig is complete for the year
                numberOfPigsSaved = (numberOfVeganYears + 1) * NUMBER_OF_PIGS_PERYEAR;
                //Marine by kill is complete for the year
                numberOfMarineByKillSaved = (numberOfVeganYears + 1) * NUMBER_OF_MARINE_BYKILL_PERYEAR;
                //CHICKEN is complete for the year
                numberOfChickenSaved = (numberOfVeganYears + 1) * NUMBER_OF_CHICKENS_PERYEAR;
                //Fish saved number and progress is complete for the year
                numberOfFishSaved = (numberOfVeganYears + 1) * NUMBER_OF_FISH_PERYEAR;
                if (asd != null) {
                    asd.setWildcatsSavedNum((int) Math.round(numberOfWildCatsSaved));
                    asd.setWildcatsSavedprogress(
                            (int) Math.round((numberOfWildCatsSaved * 100) / NUMBER_OF_WILD_CATS_PERYEAR));
                    asd.setCowSavedprogress(100);
                    asd.setCowSavedNum((int) numberOfCowsSaved);
                    asd.setPigSavedprogress(100);
                    asd.setPigSavedNum((int) numberOfPigsSaved);
                    asd.setMarinebykillSavedprogress(100);
                    asd.setMarinebykillSavedNum((int) numberOfMarineByKillSaved);
                    asd.setChickenSavedprogress(100);
                    asd.setChickenSavedNum((int) numberOfChickenSaved);
                    asd.setFishSavedprogress(100);
                    asd.setFishSavedNum((int) numberOfFishSaved);
                }
            } else if (numberOfMeals > fishPlusChickMarinePigCowCats) {
                //Wild cats is complete for the year
                numberOfWildCatsSaved = (numberOfVeganYears + 1) * NUMBER_OF_WILD_CATS_PERYEAR;
                //COW is complete for the year
                numberOfCowsSaved = (numberOfVeganYears + 1) * NUMBER_OF_COWS_PERYEAR;
                //Pig is complete for the year
                numberOfPigsSaved = (numberOfVeganYears + 1) * NUMBER_OF_PIGS_PERYEAR;
                //Marine by kill is complete for the year
                numberOfMarineByKillSaved = (numberOfVeganYears + 1) * NUMBER_OF_MARINE_BYKILL_PERYEAR;
                //CHICKEN is complete for the year
                numberOfChickenSaved = (numberOfVeganYears + 1) * NUMBER_OF_CHICKENS_PERYEAR;
                //Fish saved number and progress is complete for the year
                numberOfFishSaved = (numberOfVeganYears + 1) * NUMBER_OF_FISH_PERYEAR;

                if (asd != null) {
                    asd.setWildcatsSavedprogress(100);
                    asd.setWildcatsSavedNum((int) numberOfWildCatsSaved);
                    asd.setCowSavedprogress(100);
                    asd.setCowSavedNum((int) numberOfCowsSaved);
                    asd.setPigSavedprogress(100);
                    asd.setPigSavedNum((int) numberOfPigsSaved);
                    asd.setMarinebykillSavedprogress(100);
                    asd.setMarinebykillSavedNum((int) numberOfMarineByKillSaved);
                    asd.setChickenSavedprogress(100);
                    asd.setChickenSavedNum((int) numberOfChickenSaved);
                    asd.setFishSavedprogress(100);
                    asd.setFishSavedNum((int) numberOfFishSaved);
                }
            }
        }

        numberOfAnimalsSaved = numberOfChickenSaved + numberOfCowsSaved + numberOfFishSaved
                + numberOfMarineByKillSaved + numberOfWildCatsSaved + numberOfPigsSaved;

        return (int) Math.round(numberOfAnimalsSaved);
    }

    public static int getNumberOfFishPeryear() {
        return NUMBER_OF_FISH_PERYEAR;
    }

    public static int getNumberOfChickensPeryear() {
        return NUMBER_OF_CHICKENS_PERYEAR;
    }

    public static int getNumberOfMarineBykillPeryear() {
        return NUMBER_OF_MARINE_BYKILL_PERYEAR;
    }

    public static int getNumberOfPigsPeryear() {
        return NUMBER_OF_PIGS_PERYEAR;
    }

    public static int getNumberOfCowsPeryear() {
        return NUMBER_OF_COWS_PERYEAR;
    }

    public static int getNumberOfWildCatsPeryear() {
        return NUMBER_OF_WILD_CATS_PERYEAR;
    }

    public static double getCarbonFootPrintRatioLifetime() {
        double cfDifference = MEAT_EATER_CARBON_FOOTPRINT - COMPLETE_VEGAN_CARBON_FOOTPRINT;
        double totalNumberOfPotentialMeals = myVeganDays() * NUMBER_OF_MEALS_PER_DAY;
        double carbonRatio = cfDifference / MEAT_EATER_CARBON_FOOTPRINT;
        return (carbonRatio * 100) / (totalNumberOfPotentialMeals);
    }

    public static double getWaterSavedPerVeganMeal() {
        return WATER_SAVED_PER_VEGAN_MEAL;
    }

    public static double getCo2SavedPerVeganMeal() {
        return CO2_SAVED_PER_VEGAN_MEAL;
    }

    public static double getExcrementSavedLifetime(int numberOfMeals) {
        double totalNumberOfPotentialMeals = myVeganDays() * NUMBER_OF_MEALS_PER_DAY;
        double veganRatio = numberOfMeals / totalNumberOfPotentialMeals;
        double equivalentMeatEaters = veganRatio * EXCREMENT_PER_MEAT_EATER;
        return (equivalentMeatEaters / (equivalentMeatEaters + 1)) * 100;
    }

    public static double getForestsSavedLifetime(int numberOfMeals) {
        return numberOfMeals * FORESTS_SAVED_PER_VEGAN_MEAL;
    }

    public static double getFossilFuelsSavedLifetime(int numberOfMeals) {
        double totalNumberOfPotentialMeals = myVeganDays() * NUMBER_OF_MEALS_PER_DAY;
        double veganRatio = numberOfMeals / totalNumberOfPotentialMeals;
        return veganRatio * ENERGY_PERCENT_SAVED_PER_VEGAN_MEAL;
    }

    public static double getFatReducedLifetime(int meals) {
        return meals * SATURATED_FAT_PER_MEAL;
    }

    public static double getCholesterolReducedLifetime(int meals) {
        return meals * CHOLESTEROL_PER_MEAL;
    }

    public static double getGrainsSavedLifetime(int meals) {
        return meals * MEALS_FOR_MEAT_SAVED;
    }

    public static double getLandSavedLifetime(int meals) {
        return meals * LAND_SAVED_PER_MEAL;
    }

    public static int myVeganDays() {
        return daysSinceVeganStart(thisAppUser.getStartDateOfVegan());
    }

}
