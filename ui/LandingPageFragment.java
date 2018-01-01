package me.veganbuddy.veganbuddy.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jackandphantom.circularprogressbar.CircleProgressbar;

import me.veganbuddy.veganbuddy.R;
import me.veganbuddy.veganbuddy.actors.Dashboard;
import me.veganbuddy.veganbuddy.util.MeatMathUtils;

import static me.veganbuddy.veganbuddy.util.Constants.ANIMALS_DASHBOARD_LAYOUT;
import static me.veganbuddy.veganbuddy.util.FirebaseStorageUtils.createNewFoodWisdomForThisAppUser;
import static me.veganbuddy.veganbuddy.util.FirebaseStorageUtils.getFoodWisdomThreshold;
import static me.veganbuddy.veganbuddy.util.GlobalVariables.myDashboard;
import static me.veganbuddy.veganbuddy.util.GlobalVariables.thisAppUser;
import static me.veganbuddy.veganbuddy.util.MeatMathUtils.GREEN_COLOR;
import static me.veganbuddy.veganbuddy.util.MeatMathUtils.RED_COLOR;
import static me.veganbuddy.veganbuddy.util.MeatMathUtils.YELLOW_COLOR;
import static me.veganbuddy.veganbuddy.util.MeatMathUtils.calculatePercentVeganForToday;
import static me.veganbuddy.veganbuddy.util.MeatMathUtils.getNumberofVeganMealsLogged;
import static me.veganbuddy.veganbuddy.util.MeatMathUtils.getTotalMealsLogged;
import static me.veganbuddy.veganbuddy.util.MeatMathUtils.percentVeganFloat;
import static me.veganbuddy.veganbuddy.util.MeatMathUtils.percentVeganString;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LandingPageFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LandingPageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LandingPageFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private int mParam1;

    private OnFragmentInteractionListener mListener;

    public LandingPageFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static LandingPageFragment newInstance(int param1) {
        LandingPageFragment fragment = new LandingPageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getInt(ARG_PARAM1);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for a fragment based on the resource ID
        int resourceID;
        switch (mParam1) {
            case ANIMALS_DASHBOARD_LAYOUT: resourceID = R.layout.fragment_animals_saved;
            break;
            default:resourceID = R.layout.fragment_landing_page;
            break;
        }
        return inflater.inflate(resourceID, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        switch (mParam1){
            case ANIMALS_DASHBOARD_LAYOUT: loadDashboardAnimals(view);
            break;
        }
    }


    private void loadDashboardAnimals(View view) {

        ImageView imageViewAnimals = view.findViewById(R.id.fas_icon_animals);
        TextView textViewPercent = view.findViewById(R.id.fas_text_animal);
        TextView startDate = view.findViewById(R.id.fas_start_Date);
        TextView veganMeals = view.findViewById(R.id.fas_vegan_meals);
        TextView potentialMeals = view.findViewById(R.id.fas_potential_meals);

        CircleProgressbar circleProgressbar = view.findViewById(R.id.fas_progressBar);


        Context thisContext = getContext();

        //in case the app arrives here without retrieving the dashboard data
        if (myDashboard == null) myDashboard = new Dashboard(0);
        MeatMathUtils.getDashboardAnimalColor(myDashboard.getMealsForToday(), MeatMathUtils.IN_ONE_DAY);
        String animalsColor = MeatMathUtils.DASHBOARD_ANIMAL_COLOR;
        switch (animalsColor) {
            case RED_COLOR:
                imageViewAnimals.setImageDrawable(ContextCompat
                        .getDrawable(thisContext, R.drawable.all_animals_red));
                break;
            case YELLOW_COLOR:
                imageViewAnimals.setImageDrawable(ContextCompat
                        .getDrawable(thisContext, R.drawable.all_animals_orange));
                break;
            case GREEN_COLOR:
                imageViewAnimals.setImageDrawable(ContextCompat
                        .getDrawable(thisContext, R.drawable.all_animals_green));
                break;
        }

        calculatePercentVeganForToday();
        textViewPercent.setText(percentVeganString());
        if (thisAppUser != null) startDate.setText(thisAppUser.getStartDateOfVegan());
        veganMeals.setText(getNumberofVeganMealsLogged());
        potentialMeals.setText(getTotalMealsLogged());
        circleProgressbar.setProgress(percentVeganFloat());
        unlockFoodWisdom();
    }

    private void unlockFoodWisdom() {
        if (thisAppUser == null) return;

        if (thisAppUser.getFoodWisdomCounter() == getFoodWisdomThreshold()
                && getFoodWisdomThreshold() != -939) {

            //Create the new Food Wisdom for thisAppUser
            createNewFoodWisdomForThisAppUser();

            Snackbar snackbar = Snackbar
                    .make(getView(), R.string.food_wisdom_unlocked_message, Snackbar.LENGTH_LONG);

            snackbar.setAction(R.string.lpf_dialog_ok, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intentFoodWisdom = new Intent(getContext(), FoodWisdomActivity.class);
                    startActivity(intentFoodWisdom);
                }
            });
            snackbar.show();
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mListener.onFragmentInteraction(getId());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(int position);
    }

}
