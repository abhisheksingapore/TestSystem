package me.veganbuddy.veganbuddy.actors;

import java.util.Locale;

import static me.veganbuddy.veganbuddy.util.MeatMathUtils.getCholesterolReducedLifetime;
import static me.veganbuddy.veganbuddy.util.MeatMathUtils.getFatReducedLifetime;

/**
 * Created by abhishek on 23/11/17.
 */

public class HealthDashboard {
    String saturatedFat;
    String cholesterol;

    public HealthDashboard(int numberOfMeals) {
        saturatedFat = "I reduced Saturated Fat intake by "
                + String.format(Locale.ENGLISH, "%,.1f", getFatReducedLifetime(numberOfMeals))
                + "grams";
        cholesterol = "I reduced Cholesterol intake by "
                + String.format(Locale.ENGLISH, "%,.1f", getCholesterolReducedLifetime(numberOfMeals))
                + "mg/dL";
    }

    public String getSaturatedFat() {
        return saturatedFat;
    }

    public String getCholesterol() {
        return cholesterol;
    }
}
