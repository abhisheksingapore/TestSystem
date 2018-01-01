package me.veganbuddy.veganbuddy.actors;

import java.math.BigDecimal;
import java.util.Locale;

import static me.veganbuddy.veganbuddy.util.MeatMathUtils.getCarbonFootPrintRatioLifetime;
import static me.veganbuddy.veganbuddy.util.MeatMathUtils.getCo2SavedPerVeganMeal;
import static me.veganbuddy.veganbuddy.util.MeatMathUtils.getExcrementSavedLifetime;
import static me.veganbuddy.veganbuddy.util.MeatMathUtils.getForestsSavedLifetime;
import static me.veganbuddy.veganbuddy.util.MeatMathUtils.getFossilFuelsSavedLifetime;
import static me.veganbuddy.veganbuddy.util.MeatMathUtils.getWaterSavedPerVeganMeal;

/**
 * Created by abhishek on 20/11/17.
 */

public class EnvironmentDashboard {
    String carbonfootPrint;
    String waterSaved;
    String greenhouseGases;
    String animalExcrement;
    String rainforests;
    String fossilFuels;


    public EnvironmentDashboard(int numberOfMeals) {

        carbonfootPrint = calculateCarbonFootPrint(numberOfMeals);
        waterSaved = calculateWaterSaved(numberOfMeals);
        greenhouseGases = calculateGGsaved(numberOfMeals);
        animalExcrement = calculateExcrement(numberOfMeals);
        rainforests = calculateForestAreaSaved(numberOfMeals);
        fossilFuels = calculateFossilFuelsSaved(numberOfMeals);
    }

    EnvironmentDashboard(String c, String w, String g, String a, String r, String f) {
        carbonfootPrint = c;
        waterSaved = w;
        greenhouseGases = g;
        animalExcrement = a;
        rainforests = r;
        fossilFuels = f;
    }

    private String calculateCarbonFootPrint(int meals) {
        BigDecimal cfp = new BigDecimal(getCarbonFootPrintRatioLifetime() * meals);
        return "I reduced it by: " + String.format(Locale.ENGLISH, "%,.1f", cfp) + "%";
    }

    private String calculateWaterSaved(int numberOfMeals) {
        double totalWaterSaved = getWaterSavedPerVeganMeal() * numberOfMeals;
        return "I saved " + String.format(Locale.ENGLISH, "%,.1f", totalWaterSaved) + "gallons";
    }

    private String calculateGGsaved(int numberOfMeals) {
        double gasesSaved = getCo2SavedPerVeganMeal() * numberOfMeals;
        return "I prevented\n" + String.format(Locale.ENGLISH, "%,.1f", gasesSaved) + "lbs of emissions";
    }

    private String calculateExcrement(int numberOfMeals) {
        return "I reduced waste by "
                + String.format(Locale.ENGLISH, "%,.1f", getExcrementSavedLifetime(numberOfMeals)) + "%";
    }

    private String calculateForestAreaSaved(int numberOfMeals) {
        return "I saved\n"
                + String.format(Locale.ENGLISH, "%,.1f", getForestsSavedLifetime(numberOfMeals))
                + "sq ft of rainforests";
    }

    private String calculateFossilFuelsSaved(int numberOfMeals) {
        return "I saved\n"
                + String.format(Locale.ENGLISH, "%,.1f", getFossilFuelsSavedLifetime(numberOfMeals))
                + "% of energy";
    }


    public String getCarbonfootPrint() {
        return carbonfootPrint;
    }

    public String getWaterSaved() {
        return waterSaved;
    }

    public String getGreenhouseGases() {
        return greenhouseGases;
    }

    public String getAnimalExcrement() {
        return animalExcrement;
    }

    public String getRainforests() {
        return rainforests;
    }

    public String getFossilFuels() {
        return fossilFuels;
    }
}
