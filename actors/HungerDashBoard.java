package me.veganbuddy.veganbuddy.actors;

import java.util.Locale;

import static me.veganbuddy.veganbuddy.util.MeatMathUtils.getGrainsSavedLifetime;
import static me.veganbuddy.veganbuddy.util.MeatMathUtils.getLandSavedLifetime;


/**
 * Created by abhishek on 24/11/17.
 */

public class HungerDashBoard {
    String landsaved;
    String grainsSaved;

    public HungerDashBoard(int numberOfMeals) {
        landsaved = "I saved "
                + String.format(Locale.ENGLISH, "%,.1f", getLandSavedLifetime(numberOfMeals))
                + "acres of arable land";

        grainsSaved = "I saved grain sufficient for "
                + String.format(Locale.ENGLISH, "%,.1f", getGrainsSavedLifetime(numberOfMeals))
                + " human meals";
    }

    public String getLandsaved() {
        return landsaved;
    }

    public String getGrainsSaved() {
        return grainsSaved;
    }
}
