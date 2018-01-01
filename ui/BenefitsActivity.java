package me.veganbuddy.veganbuddy.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import com.jackandphantom.circularprogressbar.CircleProgressbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;
import me.veganbuddy.veganbuddy.R;
import me.veganbuddy.veganbuddy.actors.AnimalsSavedDashboard;
import me.veganbuddy.veganbuddy.actors.EnvironmentDashboard;
import me.veganbuddy.veganbuddy.actors.HealthDashboard;
import me.veganbuddy.veganbuddy.actors.HungerDashBoard;
import me.veganbuddy.veganbuddy.actors.MeatMathsDashBoard;

import static me.veganbuddy.veganbuddy.util.Constants.ANIMALS_DASHBOARD;
import static me.veganbuddy.veganbuddy.util.Constants.ANIMALS_TITLE;
import static me.veganbuddy.veganbuddy.util.Constants.DAILY_GRAPH;
import static me.veganbuddy.veganbuddy.util.Constants.ENVIRONMENT_DASHBOARD;
import static me.veganbuddy.veganbuddy.util.Constants.ENVIRONMENT_TITLE;
import static me.veganbuddy.veganbuddy.util.Constants.HEALTH_DASHBOARD;
import static me.veganbuddy.veganbuddy.util.Constants.HEALTH_TITLE;
import static me.veganbuddy.veganbuddy.util.Constants.HUNGER_DASHBOARD;
import static me.veganbuddy.veganbuddy.util.Constants.HUNGER_TITLE;
import static me.veganbuddy.veganbuddy.util.Constants.MEALS_TITLE;
import static me.veganbuddy.veganbuddy.util.Constants.MEAL_MATHS_DASHBOARD;
import static me.veganbuddy.veganbuddy.util.Constants.NUMBER_OF_DAYS_TO_PLOT;
import static me.veganbuddy.veganbuddy.util.Constants.NUMBER_OF_WEEKS_TO_PLOT;
import static me.veganbuddy.veganbuddy.util.Constants.SUMMARY_DASHBOARD;
import static me.veganbuddy.veganbuddy.util.Constants.SUMMARY_TITLE;
import static me.veganbuddy.veganbuddy.util.Constants.WEEKLY_GRAPH;
import static me.veganbuddy.veganbuddy.util.DateAndTimeUtils.easyToReadDate;
import static me.veganbuddy.veganbuddy.util.GlobalVariables.myDashboard;
import static me.veganbuddy.veganbuddy.util.MeatMathUtils.myVeganDays;

public class BenefitsActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private Toolbar benefitsToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_benefits);

        benefitsToolbar = findViewById(R.id.ab_toolbar);
        setSupportActionBar(benefitsToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.ab_container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position + 1) {
                    case SUMMARY_DASHBOARD:
                        benefitsToolbar.setTitle(SUMMARY_TITLE);
                        break;
                    case MEAL_MATHS_DASHBOARD:
                        benefitsToolbar.setTitle(MEALS_TITLE);
                        break;
                    case ANIMALS_DASHBOARD:
                        benefitsToolbar.setTitle(ANIMALS_TITLE);
                        break;
                    case ENVIRONMENT_DASHBOARD:
                        benefitsToolbar.setTitle(ENVIRONMENT_TITLE);
                        break;
                    case HEALTH_DASHBOARD:
                        benefitsToolbar.setTitle(HEALTH_TITLE);
                        break;
                    case HUNGER_DASHBOARD:
                        benefitsToolbar.setTitle(HUNGER_TITLE);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_benefits, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.mb_home) {
            goToSummaryFragment();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void goToSummaryFragment() {
        mViewPager.setCurrentItem(SUMMARY_DASHBOARD - 1, true);
        benefitsToolbar.setTitle(SUMMARY_TITLE);
    }

    public void animalClick(View view) {
        mViewPager.setCurrentItem(ANIMALS_DASHBOARD - 1, true);
        benefitsToolbar.setTitle(ANIMALS_TITLE);
    }

    public void mealsClick(View view) {
        mViewPager.setCurrentItem(MEAL_MATHS_DASHBOARD - 1, true);
        benefitsToolbar.setTitle(MEALS_TITLE);
    }

    public void environmentClick(View view) {
        mViewPager.setCurrentItem(ENVIRONMENT_DASHBOARD - 1, true);
        benefitsToolbar.setTitle(ENVIRONMENT_TITLE);
    }

    public void healthClick(View view) {
        mViewPager.setCurrentItem(HEALTH_DASHBOARD - 1, true);
        benefitsToolbar.setTitle(HEALTH_TITLE);
    }

    public void hungerClick(View view) {
        mViewPager.setCurrentItem(HUNGER_DASHBOARD - 1, true);
        benefitsToolbar.setTitle(HUNGER_TITLE);
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class BenefitsFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_FRAGMENT_NUMBER = "fragment_number";
        private static int FRAG_ID = -939;
        private static int FRAG_NUM = -939;

        public BenefitsFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static BenefitsFragment newInstance(int fragment_number) {
            BenefitsFragment fragment = new BenefitsFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_FRAGMENT_NUMBER, fragment_number);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            if (getArguments() != null) {
                FRAG_NUM = getArguments().getInt(ARG_FRAGMENT_NUMBER);
            }
            switch (FRAG_NUM) {
                case SUMMARY_DASHBOARD:
                    FRAG_ID = R.layout.fragment_benefits_dashboard;
                    break;
                case MEAL_MATHS_DASHBOARD:
                    FRAG_ID = R.layout.fragment_benefits_meal_math;
                    break;
                case ANIMALS_DASHBOARD:
                    FRAG_ID = R.layout.fragment_benefits_animal_welfare;
                    break;
                case ENVIRONMENT_DASHBOARD:
                    FRAG_ID = R.layout.fragment_benefits_environment;
                    break;
                case HEALTH_DASHBOARD:
                    FRAG_ID = R.layout.fragment_benefits_health;
                    break;
                case HUNGER_DASHBOARD:
                    FRAG_ID = R.layout.fragment_benefits_hunger;
                    break;
                default:
                    FRAG_ID = R.layout.fragment_benefits;
                    break;
            }
            View rootView = inflater.inflate(FRAG_ID, container, false);
            return rootView;
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            switch (FRAG_NUM) {
                case SUMMARY_DASHBOARD:
                    updateSummaryDashboard(view);
                    break;
                case MEAL_MATHS_DASHBOARD:
                    loadMealsData(view, DAILY_GRAPH);
                    radioButtonListeners(view);
                    break;
                case ANIMALS_DASHBOARD:
                    loadAnimalsData(view);
                    break;
                case ENVIRONMENT_DASHBOARD:
                    loadEnvironmentData(view);
                    break;
                case HEALTH_DASHBOARD:
                    loadHealthData(view);
                    break;
                case HUNGER_DASHBOARD:
                    loadHungerData(view);
                    break;
                default:
                    break;
            }
        }

        private void loadAnimalsData(View view) {
            AnimalsSavedDashboard asd = new AnimalsSavedDashboard(
                    myDashboard.getMealsForLifetime(), myVeganDays());

            TextView cow = view.findViewById(R.id.fbaw_cow_tv_number);
            TextView chicken = view.findViewById(R.id.fbaw_chicken_tv_number);
            TextView pig = view.findViewById(R.id.fbaw_pig_tv_number);
            TextView fish = view.findViewById(R.id.fbaw_seafood_tv_number);
            TextView wildcats = view.findViewById(R.id.fbaw_wild_cats_tv_number);
            TextView marineBykill = view.findViewById(R.id.fbaw_marine_bykill_tv_number);

            cow.setText(String.format(Locale.ENGLISH, "%,d", asd.getCowSavedNum()));
            chicken.setText(String.format(Locale.ENGLISH, "%,d", asd.getChickenSavedNum()));
            pig.setText(String.format(Locale.ENGLISH, "%,d", asd.getPigSavedNum()));
            fish.setText(String.format(Locale.ENGLISH, "%,d", asd.getFishSavedNum()));
            wildcats.setText(String.format(Locale.ENGLISH, "%,d", asd.getWildcatsSavedNum()));
            marineBykill.setText(String.format(Locale.ENGLISH, "%,d", asd.getMarinebykillSavedNum()));

            ProgressBar cowPB = view.findViewById(R.id.fbaw_pb_cow);
            ProgressBar chickenPB = view.findViewById(R.id.fbaw_pb_chicken);
            ProgressBar pigPB = view.findViewById(R.id.fbaw_pb_pig);
            ProgressBar fishPB = view.findViewById(R.id.fbaw_pb_seafood);
            ProgressBar wildcatsPB = view.findViewById(R.id.fbaw_pb_wild_cats);
            ProgressBar marinePB = view.findViewById(R.id.fbaw_pb_marine_bykill);

            cowPB.setProgress(asd.getCowSavedprogress());
            chickenPB.setProgress(asd.getChickenSavedprogress());
            pigPB.setProgress(asd.getPigSavedprogress());
            fishPB.setProgress(asd.getFishSavedprogress());
            wildcatsPB.setProgress(asd.getWildcatsSavedprogress());
            marinePB.setProgress(asd.getMarinebykillSavedprogress());
        }

        private void loadHungerData(View view) {
            HungerDashBoard hungerDashBoard = new HungerDashBoard(myDashboard.getMealsForLifetime());
            TextView land = view.findViewById(R.id.fbhunger_tv_land);
            TextView grains = view.findViewById(R.id.fbhunger_tv_grains);

            land.setText(hungerDashBoard.getLandsaved());
            grains.setText(hungerDashBoard.getGrainsSaved());
        }

        private void loadHealthData(View view) {
            HealthDashboard healthDashboard = new HealthDashboard(myDashboard.getMealsForLifetime());
            TextView fat = view.findViewById(R.id.fbh_tv_saturated_fats);
            TextView cholesterol = view.findViewById(R.id.fbh_tv_cholesterol);

            fat.setText(healthDashboard.getSaturatedFat());
            cholesterol.setText(healthDashboard.getCholesterol());
        }

        private void loadEnvironmentData(View view) {
            EnvironmentDashboard environmentDashboard
                    = new EnvironmentDashboard(myDashboard.getMealsForLifetime());
            TextView carbonFPTV = view.findViewById(R.id.fbe_tv_carbon_value);
            TextView waterTV = view.findViewById(R.id.fbe_tv_water_value);
            TextView greenGasTV = view.findViewById(R.id.fbe_tv_greenhouse_value);
            TextView excrementTV = view.findViewById(R.id.fbe_tv_excrement_value);
            TextView rainforestTV = view.findViewById(R.id.fbe_tv_rainforest_value);
            TextView fossilFuelTV = view.findViewById(R.id.fbe_tv_fossil_fuel_value);

            carbonFPTV.setText(environmentDashboard.getCarbonfootPrint());
            waterTV.setText(environmentDashboard.getWaterSaved());
            greenGasTV.setText(environmentDashboard.getGreenhouseGases());
            excrementTV.setText(environmentDashboard.getAnimalExcrement());
            rainforestTV.setText(environmentDashboard.getRainforests());
            fossilFuelTV.setText(environmentDashboard.getFossilFuels());
        }

        private void radioButtonListeners(View view) {
            RadioButton dailyMeals = view.findViewById(R.id.fbmm_rb_daily);
            dailyMeals.isChecked();
            RadioButton weeklyMeals = view.findViewById(R.id.fbmm_rb_weekly);

            dailyMeals.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    loadMealsData(view.getRootView(), DAILY_GRAPH);
                }
            });

            weeklyMeals.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    loadMealsData(view.getRootView(), WEEKLY_GRAPH);
                }
            });

        }

        //Todo: convert these graph methods into their own respective class to just retrieve
        // the relevant graph and data
        private void loadMealsData(View view, int whichGraph) {
            LineChartView graphView = view.findViewById(R.id.fbmm_graph);
            graphView.setZoomEnabled(true);

            switch (whichGraph) {
                case DAILY_GRAPH:
                    createDailyMealsGraphData(graphView);
                    break;
                case WEEKLY_GRAPH:
                    createWeeklyMealsGraphData(graphView);
                    break;
            }

        }


        private void createDailyMealsGraphData(LineChartView graphView) {
            Map<String, Integer> dailyDataHash = myDashboard.getMealsForTheDay();

            //sort the dailyData
            Map<String, Integer> dailyData = new TreeMap<>(dailyDataHash);

            //get the size of the dailyData Map
            int dailySize = dailyData.size();

            List<PointValue> pointValues = new ArrayList<PointValue>();
            //create x Axis and Y Axis variables
            List<AxisValue> axisValuesXaxis = new ArrayList<>();
            List<AxisValue> axisValuesYaxis = new ArrayList<>();
            float yMax = 0;
            AxisValue axisValueX;
            AxisValue axisValueY;

            //create a list for the dailyvalues and dailyDates
            List<Integer> dailyValues = new ArrayList<>(dailyData.values());
            List<String> dailyDates = new ArrayList<>(dailyData.keySet());

            //get the latest daily values equal to the NUMBER_OF_DAYS_TO_PLOT if data more than
            // NUMBER_OF_DAYS_TO_PLOT
            if (dailySize > NUMBER_OF_DAYS_TO_PLOT) {
                //add the last NUMBER_OF_DAYS_TO_PLOT to the pointValues
                for (int i = 0; i < NUMBER_OF_DAYS_TO_PLOT; i++) {
                    //get the y Value
                    float yValue = dailyValues.get(dailySize - NUMBER_OF_DAYS_TO_PLOT + i).floatValue();
                    //get the x Value
                    String xValueString = dailyDates.get(dailySize - NUMBER_OF_DAYS_TO_PLOT + i);
                    //add the x Value to the Axis
                    axisValueX = new AxisValue(i);
                    axisValueX.setLabel(easyToReadDate(xValueString));
                    axisValuesXaxis.add(axisValueX);

                    //create the point to be plotted on the graphs
                    PointValue j = new PointValue((float) i, yValue);
                    pointValues.add(j);

                    //find the maximum y value for creating the y axis
                    yMax = (yValue > yMax) ? yValue : yMax;
                }
            } else {//else just get all the latest daily values
                for (int i = 0; i < dailySize; i++) {
                    //repeat same steps as above
                    float yValue = dailyValues.get(i).floatValue();
                    String xValueString = dailyDates.get(i);

                    axisValueX = new AxisValue(i);
                    axisValueX.setLabel(easyToReadDate(xValueString));
                    axisValuesXaxis.add(axisValueX);

                    PointValue j = new PointValue((float) i, yValue);
                    pointValues.add(j);

                    yMax = (yValue > yMax) ? yValue : yMax;
                }
            }

            //create the Line for the graph
            List<Line> lines = new ArrayList<Line>();
            lines.add(new Line(pointValues)
                    .setColor(Color.BLUE).setFilled(true).setCubic(false));

            //create the xAxis
            Axis axisX = new Axis(axisValuesXaxis);

            //create the y Axis
            for (int j = 0; j < yMax; j++) {
                axisValueY = new AxisValue(j);
                axisValueY.setLabel(Integer.toString(j));
                axisValuesYaxis.add(axisValueY);
            }
            Axis axisY = new Axis(axisValuesYaxis);

            //create the graphs
            LineChartData lineChartData = new LineChartData();
            lineChartData.setLines(lines);
            lineChartData.setAxisXBottom(axisX);
            lineChartData.setAxisYLeft(axisY);

            //attach the graphs to the View
            graphView.setLineChartData(lineChartData);
        }

        private void createWeeklyMealsGraphData(LineChartView graphView) {
            Map<String, Integer> weeklyDataHash = myDashboard.getMealsForTheWeek();
            Map<String, Integer> weeklyData = new TreeMap<>(weeklyDataHash);
            int weekSize = weeklyData.size();

            List<Integer> weeklyValues = new ArrayList<>(weeklyData.values());
            List<String> weeklyKeys = new ArrayList<>(weeklyData.keySet());

            List<PointValue> pointValuesWeekly = new ArrayList<PointValue>();

            //create x Axis and Y Axis variables
            List<AxisValue> axisValuesXaxis = new ArrayList<>();
            List<AxisValue> axisValuesYaxis = new ArrayList<>();
            float yMax = 0;
            AxisValue axisValueX;
            AxisValue axisValueY;

            //get the latest weekly values equal to the NUMBER_OF_DAYS_TO_PLOT if data more
            // than NUMBER_OF_WEEKS_TO_PLOT
            if (weekSize > NUMBER_OF_WEEKS_TO_PLOT) {
                //add the last NUMBER_OF_WEEKS_TO_PLOT to the pointValuesWeekly
                for (int i = 0; i < NUMBER_OF_WEEKS_TO_PLOT; i++) {
                    float yValue = weeklyValues
                            .get(weekSize - NUMBER_OF_WEEKS_TO_PLOT + i).floatValue();
                    String xValueString = weeklyKeys.get(weekSize - NUMBER_OF_WEEKS_TO_PLOT + i);

                    axisValueX = new AxisValue(i);
                    axisValueX.setLabel(xValueString);
                    axisValuesXaxis.add(axisValueX);


                    PointValue j = new PointValue((float) i, yValue);
                    pointValuesWeekly.add(j);

                    yMax = (yValue > yMax) ? yValue : yMax;
                }
            } else {
                //else just get the latest weekly values
                for (int i = 0; i < weekSize; i++) {
                    float yValue = weeklyValues.get(i).floatValue();
                    String xValueString = weeklyKeys.get(i);

                    axisValueX = new AxisValue(i);
                    axisValueX.setLabel(xValueString);
                    axisValuesXaxis.add(axisValueX);

                    PointValue j = new PointValue((float) i, yValue);
                    pointValuesWeekly.add(j);

                    yMax = (yValue > yMax) ? yValue : yMax;
                }
            }

            List<Line> lines = new ArrayList<Line>();
            lines.add(new Line(pointValuesWeekly).setColor(Color.BLACK)
                    .setFilled(true).setCubic(false));

            Axis axisX = new Axis(axisValuesXaxis);

            for (int j = 0; j < yMax; j++) {
                axisValueY = new AxisValue(j);
                axisValueY.setLabel(Integer.toString(j));
                axisValuesYaxis.add(axisValueY);
            }
            Axis axisY = new Axis(axisValuesYaxis);
            LineChartData lineChartData = new LineChartData();
            lineChartData.setLines(lines);
            lineChartData.setAxisXBottom(axisX);
            lineChartData.setAxisYLeft(axisY);
            graphView.setLineChartData(lineChartData);

        }

        private void updateSummaryDashboard(View view) {
            MeatMathsDashBoard currentSummaryDashboard =
                    new MeatMathsDashBoard(myDashboard.getMealsForLifetime());
            TextView animals = view.findViewById(R.id.fb_tv_animals);
            TextView environment = view.findViewById(R.id.fb_tv_Environment);
            TextView health = view.findViewById(R.id.fb_tv_health);
            TextView hunger = view.findViewById(R.id.fb_tv_hunger);
            TextView meals = view.findViewById(R.id.fb_tv_meals);
            CircleProgressbar progressbar = view.findViewById(R.id.fb_progressBar);
            TextView veganPercent = view.findViewById(R.id.fb_tv_percentVegan);

            animals.setText(currentSummaryDashboard.getAnimalsSaved());
            environment.setText(currentSummaryDashboard.getEnvironmentImpact());
            health.setText(currentSummaryDashboard.getHealthImpact());
            hunger.setText(currentSummaryDashboard.getHungerImpact());
            meals.setText(currentSummaryDashboard.getVeganMeals());
            veganPercent.setText(currentSummaryDashboard.getVeganPecentage());
            progressbar.setProgress(currentSummaryDashboard.getVeganPercentageFloat());
        }

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return BenefitsFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 6 total pages.
            return 6;
        }
    }


}
