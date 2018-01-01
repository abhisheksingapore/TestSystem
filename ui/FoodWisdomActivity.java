package me.veganbuddy.veganbuddy.ui;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources.Theme;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.veganbuddy.veganbuddy.R;
import me.veganbuddy.veganbuddy.actors.FoodWisdom;

import static me.veganbuddy.veganbuddy.util.BitmapUtils.createTempUploadFile;
import static me.veganbuddy.veganbuddy.util.Constants.ALL_FOODWISDOM;
import static me.veganbuddy.veganbuddy.util.Constants.ALL_FOODWISDOM_TAG;
import static me.veganbuddy.veganbuddy.util.Constants.ALL_FW;
import static me.veganbuddy.veganbuddy.util.Constants.FOOD_WISDOM_NODE;
import static me.veganbuddy.veganbuddy.util.Constants.MY_FOODWISDOM;
import static me.veganbuddy.veganbuddy.util.Constants.MY_FOODWISDOM_TAG;
import static me.veganbuddy.veganbuddy.util.Constants.MY_FOOD_WISDOM_CREATED_NODE;
import static me.veganbuddy.veganbuddy.util.Constants.MY_FOOD_WISDOM_NODE;
import static me.veganbuddy.veganbuddy.util.Constants.MY_FW;
import static me.veganbuddy.veganbuddy.util.GlobalVariables.thisAppUser;

public class FoodWisdomActivity extends AppCompatActivity {

    public static long FOOD_WISDOM_COUNT = 0;
    static List<FoodWisdom> listAll = new ArrayList<>();
    static List<FoodWisdom> listMyCreation = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_wisdom);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Setup spinner
        Spinner spinner = findViewById(R.id.spinner);
        final String[] fragmentList = new String[]{
                MY_FW, ALL_FW
                //Todo: Insert the different sorting options
        };

        spinner.setAdapter(new MyAdapter(toolbar.getContext(), fragmentList));

        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // When the given dropdown item is selected, show its contents in the
                // container view.
                String FRAG_TAG = "NO TAG";
                if (position == ALL_FOODWISDOM) FRAG_TAG = ALL_FOODWISDOM_TAG;
                if (position == MY_FOODWISDOM) FRAG_TAG = MY_FOODWISDOM_TAG;

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.afw_container,
                                FoodWisdomFragment.newInstance(position + 1,
                                        fragmentList[position]), FRAG_TAG)
                        .commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //retrieve the Food Wisdom items from database and create a list of them
        retrieveFoodWisdom();
        retrieveMyFoodWisdomCreated();
        retrieveFoodWisdomCount();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_food_wisdom, menu);
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

        return super.onOptionsItemSelected(item);
    }

    public void shareFWclick(View view) {
        CardView viewParent = (CardView) view.getParent();
        ImageView imageView = viewParent.findViewById(R.id.ifw_iv);
        Bitmap tempImage = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        File tempImageFile = createTempUploadFile(tempImage);
        Uri tempImageFileUri = Uri.fromFile(tempImageFile);


        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, tempImageFileUri);
        startActivity(Intent.createChooser(shareIntent, "Share this photo..."));

    }

    public void addFoodWisdomClick(View view) {
        startActivity(new Intent(this, FoodWisdomAdd.class));
    }

    /***********************************************************************
     ***********************************************************************
     Adding Firebase database Listeners to retrieve the data and update the UI on data change
     ***********************************************************************
     ************************************************************************/
    public void retrieveFoodWisdom() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database
                .getReference(thisAppUser.getFireBaseID()).child(MY_FOOD_WISDOM_NODE);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    listAll = new ArrayList<>();
                    for (DataSnapshot dataSnapshotSingle : dataSnapshot.getChildren()) {
                        FoodWisdom foodWisdom = dataSnapshotSingle.getValue(FoodWisdom.class);
                        listAll.add(foodWisdom);
                    }
                    updateRecyclerViewList(ALL_FOODWISDOM);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void retrieveMyFoodWisdomCreated() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database
                .getReference(thisAppUser.getFireBaseID()).child(MY_FOOD_WISDOM_CREATED_NODE);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.exists()) {
                        listMyCreation = new ArrayList<>();
                        FOOD_WISDOM_COUNT = dataSnapshot.getChildrenCount();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            FoodWisdom foodWisdomThis = snapshot.getValue(FoodWisdom.class);
                            listMyCreation.add(foodWisdomThis);
                        }
                    }
                    updateRecyclerViewList(MY_FOODWISDOM);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void retrieveFoodWisdomCount() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database
                .getReference(FOOD_WISDOM_NODE);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    FOOD_WISDOM_COUNT = dataSnapshot.getChildrenCount();
                } else FirebaseCrash.log("No Food Wisdom Children found");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void updateRecyclerViewList(int position) {

        if (position == MY_FOODWISDOM) {
            Fragment fragmentThis = getSupportFragmentManager().findFragmentByTag(MY_FOODWISDOM_TAG);
            if (fragmentThis != null) {
                FoodWisdomRecyclerViewAdapter foodWisdomRecyclerViewAdapterThis =
                        ((FoodWisdomFragment) fragmentThis).getFoodWisdomRecyclerViewAdapterThis();
                foodWisdomRecyclerViewAdapterThis.setDataList(listMyCreation);
            }
        }

        if (position == ALL_FOODWISDOM) {
            Fragment fragmentThis = getSupportFragmentManager().findFragmentByTag(ALL_FOODWISDOM_TAG);
            if (fragmentThis != null) {
                FoodWisdomRecyclerViewAdapter foodWisdomRecyclerViewAdapterThis =
                        ((FoodWisdomFragment) fragmentThis).getFoodWisdomRecyclerViewAdapterThis();
                foodWisdomRecyclerViewAdapterThis.setDataList(listMyCreation);
            }
        }

    }

    private static class MyAdapter extends ArrayAdapter<String> implements ThemedSpinnerAdapter {
        private final ThemedSpinnerAdapter.Helper mDropDownHelper;

        public MyAdapter(Context context, String[] objects) {
            super(context, android.R.layout.simple_list_item_1, objects);
            mDropDownHelper = new ThemedSpinnerAdapter.Helper(context);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            View view;

            if (convertView == null) {
                // Inflate the drop down using the helper's LayoutInflater
                LayoutInflater inflater = mDropDownHelper.getDropDownViewInflater();
                view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            } else {
                view = convertView;
            }

            TextView textView = view.findViewById(android.R.id.text1);
            textView.setText(getItem(position));

            return view;
        }

        @Override
        public Theme getDropDownViewTheme() {
            return mDropDownHelper.getDropDownViewTheme();
        }

        @Override
        public void setDropDownViewTheme(Theme theme) {
            mDropDownHelper.setDropDownViewTheme(theme);
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class FoodWisdomFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_FRAG_NAME = "fragment name";
        TextView sectionLabel;
        FoodWisdomRecyclerViewAdapter foodWisdomRecyclerViewAdapterThis;

        public FoodWisdomFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static FoodWisdomFragment newInstance(int sectionNumber, String fragName) {
            FoodWisdomFragment fragment = new FoodWisdomFragment();
            Bundle args = new Bundle();
            args.putString(ARG_FRAG_NAME, fragName);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_food_wisdom, container, false);
            sectionLabel = rootView.findViewById(R.id.ffw_section_label);
            sectionLabel.setText("You are yet to unlock these Food Wisdom");

            FloatingActionButton fab = rootView.findViewById(R.id.ffw_add_fab);
            String fName = getArguments().getString(ARG_FRAG_NAME);
            if (fName != null && fName.equals(MY_FW)) {
                fab.setVisibility(View.VISIBLE);

                RecyclerView recyclerViewFoodWisdom = rootView.findViewById(R.id.ffw_rv);
                recyclerViewFoodWisdom.setLayoutManager(new LinearLayoutManager(getContext()));

                FoodWisdomRecyclerViewAdapter myFoodWisdomRecyclerViewAdapter
                        = new FoodWisdomRecyclerViewAdapter(listMyCreation);

                recyclerViewFoodWisdom.setAdapter(myFoodWisdomRecyclerViewAdapter);
                setFoodWisdomRecyclerViewAdapterThis(myFoodWisdomRecyclerViewAdapter);
            } else {
                fab.setVisibility(View.GONE);
                RecyclerView recyclerViewFoodWisdom = rootView.findViewById(R.id.ffw_rv);
                recyclerViewFoodWisdom.setLayoutManager(new LinearLayoutManager(getContext()));

                FoodWisdomRecyclerViewAdapter foodWisdomRecyclerViewAdapter
                        = new FoodWisdomRecyclerViewAdapter(listAll);

                recyclerViewFoodWisdom.setAdapter(foodWisdomRecyclerViewAdapter);
                setFoodWisdomRecyclerViewAdapterThis(foodWisdomRecyclerViewAdapter);
            }

            return rootView;
        }


        public FoodWisdomRecyclerViewAdapter getFoodWisdomRecyclerViewAdapterThis() {
            return foodWisdomRecyclerViewAdapterThis;
        }

        public void setFoodWisdomRecyclerViewAdapterThis(FoodWisdomRecyclerViewAdapter foodWisdomRecyclerViewAdapterThis) {
            this.foodWisdomRecyclerViewAdapterThis = foodWisdomRecyclerViewAdapterThis;
        }

    }

}
