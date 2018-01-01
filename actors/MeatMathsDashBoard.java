package me.veganbuddy.veganbuddy.actors;

import java.math.BigDecimal;
import java.util.Locale;

import static me.veganbuddy.veganbuddy.util.MeatMathUtils.getCarbonFootPrintRatioLifetime;
import static me.veganbuddy.veganbuddy.util.MeatMathUtils.getCholesterolIntake;
import static me.veganbuddy.veganbuddy.util.MeatMathUtils.getMealsForMeatSaved;
import static me.veganbuddy.veganbuddy.util.MeatMathUtils.getNumberOfAnimalsSaved;
import static me.veganbuddy.veganbuddy.util.MeatMathUtils.myVeganDays;
import static me.veganbuddy.veganbuddy.util.MeatMathUtils.percentVeganFloat;
import static me.veganbuddy.veganbuddy.util.MeatMathUtils.percentVeganForLifetime;
import static me.veganbuddy.veganbuddy.util.MeatMathUtils.percentVeganString;

/**
 * Created by abhishek on 14/11/17.
 */


public class MeatMathsDashBoard {
    String animalsSaved;
    String veganMeals;
    String environmentImpact;
    String healthImpact;
    String hungerImpact;
    String veganPecentage;
    float veganPercentageFloat;

    public MeatMathsDashBoard(int numberOfMeals) {
        BigDecimal animals = new BigDecimal(getNumberOfAnimalsSaved(numberOfMeals, null));
        BigDecimal environment = new BigDecimal(getCarbonFootPrintRatioLifetime() * numberOfMeals);
        BigDecimal health = new BigDecimal(getCholesterolIntake() * numberOfMeals);
        BigDecimal hunger = new BigDecimal(getMealsForMeatSaved() * numberOfMeals);


        animalsSaved = String.format(Locale.ENGLISH, "%,.0f", animals);
        veganMeals = Integer.toString(numberOfMeals);
        environmentImpact = String.format(Locale.ENGLISH, "%.1f", environment);
        healthImpact = String.format(Locale.ENGLISH, "%,.0f", health);
        hungerImpact = String.format(Locale.ENGLISH, "%,.0f", hunger);
        veganPecentage = percentVeganString(percentVeganForLifetime());
        veganPercentageFloat = percentVeganFloat(percentVeganForLifetime());
    }

    public String getAnimalsSaved() {
        return "I saved \n" + animalsSaved + " animals";
    }

    public String getVeganMeals() {
        return "I ate " + veganMeals + " Vegan \nMeals in " + myVeganDays() + " days";
    }

    public String getEnvironmentImpact() {
        return "I reduced my \nCarbon Footprint by: " + environmentImpact + "%";
    }

    public String getHealthImpact() {
        return "I reduced my  \n Cholesterol by: " + healthImpact + "mg";
    }

    public String getHungerImpact() {
        return "I saved sufficient grains \nfor " + hungerImpact + " human meals";
    }

    public String getVeganPecentage() {
        return "I am\n" + veganPecentage + " Vegan";
    }

    public float getVeganPercentageFloat() {
        return veganPercentageFloat;
    }
}