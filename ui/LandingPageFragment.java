package me.veganbuddy.veganbuddy.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.jackandphantom.circularprogressbar.CircleProgressbar;


import me.veganbuddy.veganbuddy.R;
import me.veganbuddy.veganbuddy.actors.Dashboard;
import me.veganbuddy.veganbuddy.util.MeatMathUtils;

import static me.veganbuddy.veganbuddy.util.Constants.ANIMALS_DASHBOARD_LAYOUT;
import static me.veganbuddy.veganbuddy.util.Constants.VEGAN_DASHBOARD_LAYOUT;
import static me.veganbuddy.veganbuddy.util.GlobalVariables.myDashboard;
import static me.veganbuddy.veganbuddy.util.MeatMathUtils.GREEN_COLOR;
import static me.veganbuddy.veganbuddy.util.MeatMathUtils.RED_COLOR;
import static me.veganbuddy.veganbuddy.util.MeatMathUtils.YELLOW_COLOR;

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
            case VEGAN_DASHBOARD_LAYOUT: resourceID = R.layout.fragment_dashboard_vegan_percentage;
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
            case VEGAN_DASHBOARD_LAYOUT: loadVeganDashBoardData (view);
            break;
        }
    }

    private void loadVeganDashBoardData(View view) {
        TextView startDate = view.findViewById(R.id.fdvp_start_Date);
        TextView veganMeals = view.findViewById(R.id.fdvp_vegan_meals);
        TextView potentialMeals = view.findViewById(R.id.fdvp_potential_meals);
        TextView percentVeganText = view.findViewById(R.id.fdvp_percent_complete);
        CircleProgressbar circleProgressbar = view.findViewById(R.id.fdvp_graph_progressBar);
        Spinner spinnerDuration = view.findViewById(R.id.fdvp_spinner1);
        Spinner spinnerWho = view.findViewById(R.id.fdvp_spinner2);
        String filter1 = spinnerDuration.getSelectedItem().toString();
        String filter2 = spinnerWho.getSelectedItem().toString();

        MeatMathUtils.setFiltersAndCalculate(getContext(),filter1, filter2);

        startDate.setText(myDashboard.getStartDateOfVegan());
        veganMeals.setText(MeatMathUtils.getNumberofVeganMealsLogged());
        potentialMeals.setText(MeatMathUtils.getTotalMealsLogged());
        percentVeganText.setText(MeatMathUtils.percentVeganString());
        circleProgressbar.setProgress(MeatMathUtils.percentVeganFloat());
    }

    private void loadDashboardAnimals(View view) {

        ImageView cow = view.findViewById(R.id.icon_cow);
        ImageView chicken = view.findViewById(R.id.icon_chicken);
        ImageView pig = view.findViewById(R.id.icon_pig);
        ImageView seafood = view.findViewById(R.id.icon_seafood);
        Context thisContext = getContext();

        //in case the app arrives here without retrieving the dashboard data
        if (myDashboard == null) myDashboard = new Dashboard(0);
        MeatMathUtils.getDashboardAnimalColor(myDashboard.getMealsForToday(), MeatMathUtils.IN_ONE_DAY);
        String animalsColor = MeatMathUtils.DASHBOARD_ANIMAL_COLOR;
        switch (animalsColor) {
            case RED_COLOR:
                cow.setImageDrawable(ContextCompat.getDrawable(thisContext,R.drawable.cow_red));
                chicken.setImageDrawable(ContextCompat.getDrawable(thisContext,R.drawable.chicken_red));
                pig.setImageDrawable(ContextCompat.getDrawable(thisContext,R.drawable.pig_red));
                seafood.setImageDrawable(ContextCompat.getDrawable(thisContext,R.drawable.fish_red));
                break;
            case YELLOW_COLOR:
                cow.setImageDrawable(ContextCompat.getDrawable(thisContext,R.drawable.cow_yellow));
                chicken.setImageDrawable(ContextCompat.getDrawable(thisContext,R.drawable.chicken_yellow));
                pig.setImageDrawable(ContextCompat.getDrawable(thisContext,R.drawable.pig_yellow));
                seafood.setImageDrawable(ContextCompat.getDrawable(thisContext,R.drawable.fish_yellow));
                break;
            case GREEN_COLOR:
                cow.setImageDrawable(ContextCompat.getDrawable(thisContext,R.drawable.cow_green));
                chicken.setImageDrawable(ContextCompat.getDrawable(thisContext,R.drawable.chicken_green));
                pig.setImageDrawable(ContextCompat.getDrawable(thisContext,R.drawable.pig_green));
                seafood.setImageDrawable(ContextCompat.getDrawable(thisContext,R.drawable.fish_green));
                break;
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
