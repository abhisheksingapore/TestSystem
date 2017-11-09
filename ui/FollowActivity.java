package me.veganbuddy.veganbuddy.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import me.veganbuddy.veganbuddy.R;
import me.veganbuddy.veganbuddy.actors.Buddy;

import static me.veganbuddy.veganbuddy.util.Constants.FA_TAG;
import static me.veganbuddy.veganbuddy.util.Constants.FOLLOWERS;
import static me.veganbuddy.veganbuddy.util.Constants.FOLLOWERS_NODE;
import static me.veganbuddy.veganbuddy.util.Constants.FOLLOWING;
import static me.veganbuddy.veganbuddy.util.Constants.FOLLOWING_NODE;
import static me.veganbuddy.veganbuddy.util.Constants.POST_FAN_NODE;
import static me.veganbuddy.veganbuddy.util.Constants.Plikes_TAG;
import static me.veganbuddy.veganbuddy.util.Constants.RELATION;
import static me.veganbuddy.veganbuddy.util.GlobalVariables.thisAppUser;

public class FollowActivity extends AppCompatActivity {

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
    int relationship = -939;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow);
        relationship = getIntent().getExtras().getInt(RELATION);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        switch (relationship) {
            case FOLLOWERS: mViewPager.setCurrentItem(FOLLOWERS);
            break;
            case FOLLOWING: mViewPager.setCurrentItem(FOLLOWING);
            break;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present
        //Todo: To synchronise the menu items with the overall menu
        getMenuInflater().inflate(R.menu.menu_follow, menu);
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


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class BuddyFragment extends Fragment {

        private static final String ARG_MY_BUDDIES = "my buddies";
        private static List<Buddy> buddyList = new ArrayList<>();

        List<Buddy> listFollowers;
        List <Buddy> listFollowing;

        FollowRecyclerViewAdapter followRecyclerViewAdapter;


        int relation = -939;

        public BuddyFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static BuddyFragment newInstance(int buddyRelation) {
            BuddyFragment fragment = new BuddyFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_MY_BUDDIES, buddyRelation);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onStart() {
            super.onStart();
            retrieveMyFollowersAndFollowingData();
        }

        @Override
        public void onResume() {
            super.onResume();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_follow, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            String suffix = "";
            relation = getArguments().getInt(ARG_MY_BUDDIES);

            try {
                switch (relation) {
                    case FOLLOWING:
                        suffix = " that you are following";
                        break;
                    case FOLLOWERS:
                        suffix = " that are your followers";
                        break;
                }
                textView.setText(getString(R.string.section_format) + suffix);
            } catch (NullPointerException NPE) {
                FirebaseCrash.log("NPE in FollowActivity.java " + NPE.getMessage());
                Log.e(FA_TAG, "NPE in FollowActivity.java " + NPE.getMessage());
            }
            RecyclerView recyclerView = rootView.findViewById(R.id.ff_rv);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            followRecyclerViewAdapter = new FollowRecyclerViewAdapter(buddyList, relation, listFollowing);
            recyclerView.setAdapter(followRecyclerViewAdapter);

            return rootView;
        }

        public void setBuddyList() {
            switch (relation) {
                case FOLLOWING:
                    buddyList = listFollowing;
                    break;
                case FOLLOWERS:
                    buddyList = listFollowers;
                    break;
            }
            followRecyclerViewAdapter.setBuddyList(buddyList, listFollowing);
        }


        /***********************************************************************
         ***********************************************************************
         Adding Firebase database Listeners to retrieve the data and update the UI on data change
         ***********************************************************************
         ************************************************************************/


        private void retrieveMyFollowersAndFollowingData(){
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = firebaseDatabase.getReference();
            Query queryMyBuddy = databaseReference.child(thisAppUser.getFireBaseID())
                    .child(FOLLOWERS_NODE);

            queryMyBuddy.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    listFollowers = new ArrayList<>();
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot dataSnapshotSingle: dataSnapshot.getChildren()){
                            try {
                                Buddy buddy = dataSnapshotSingle.getValue(Buddy.class);
                                buddy.setBuddyID(dataSnapshotSingle.getKey());
                                listFollowers.add(buddy);
                            } catch (NullPointerException NPE) {
                                FirebaseCrash.log(Plikes_TAG + NPE.getMessage());
                                Log.e(Plikes_TAG, NPE.getMessage());
                            }
                        }
                    }
                    retrieveMeFollowingData();
                    Log.v(FA_TAG, "Successfully retrieved my Followers data");
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    FirebaseCrash.log(FA_TAG + "Error retrieving my Followers data "
                            + databaseError.getMessage());
                    Log.e(FA_TAG, "Error retrieving my Followers data"
                            + databaseError.getMessage());
                }
            });
        }


        private void retrieveMeFollowingData() {
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = firebaseDatabase.getReference();
            Query queryMyBuddy = databaseReference.child(thisAppUser.getFireBaseID())
                    .child(FOLLOWING_NODE);

            queryMyBuddy.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    listFollowing = new ArrayList<>();
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot dataSnapshotSingle: dataSnapshot.getChildren()){
                            try {
                                Buddy buddy = dataSnapshotSingle.getValue(Buddy.class);
                                buddy.setBuddyID(dataSnapshotSingle.getKey());
                                listFollowing.add(buddy);
                            } catch (NullPointerException NPE) {
                                FirebaseCrash.log(Plikes_TAG + NPE.getMessage());
                                Log.e(Plikes_TAG, NPE.getMessage());
                            }
                        }
                    }
                    setBuddyList();
                    Log.v(FA_TAG, "Successfully retrieved me Following data");
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    FirebaseCrash.log(FA_TAG + "Error retrieving me Following data "
                            + databaseError.getMessage());
                    Log.e(FA_TAG, "Error retrieving me Following data"
                            + databaseError.getMessage());
                }
            });
        }


    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).

            return BuddyFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }
    }

}
