package me.veganbuddy.veganbuddy.ui;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
import static me.veganbuddy.veganbuddy.util.Constants.Plikes_TAG;
import static me.veganbuddy.veganbuddy.util.Constants.RELATION;
import static me.veganbuddy.veganbuddy.util.GlobalVariables.listFollowing;
import static me.veganbuddy.veganbuddy.util.GlobalVariables.thisAppUser;

public class FollowActivity extends AppCompatActivity {

    int relationship = -939;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow);
        relationship = getIntent().getExtras().getInt(RELATION);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.af_tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout) {
            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                try {
                    ((BuddyFragment) getSupportFragmentManager().getFragments().get(position))
                            .refreshFollowingFollowerList();
                } catch (IndexOutOfBoundsException IOBE) {
                    FirebaseCrash.log(IOBE.getMessage());
                    Log.v(FA_TAG, "caught IndexOutOfBoundsException :" + IOBE.getMessage());
                }
            }
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
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
        getMenuInflater().inflate(R.menu.menu_small_activities, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.fpm_close:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class BuddyFragment extends Fragment {

        private static final String ARG_MY_BUDDIES = "my buddies";
        private static List<Buddy> buddyList = new ArrayList<>();

        List<Buddy> listFollowers;

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
            retrieveMyFollowersData();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_follow, container, false);
            TextView textView = rootView.findViewById(R.id.ff_section_label);
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
                textView.setText(getString(R.string.af_section_format) + suffix);
            } catch (NullPointerException NPE) {
                FirebaseCrash.log("NPE in FollowActivity.java " + NPE.getMessage());
                Log.e(FA_TAG, "NPE in FollowActivity.java " + NPE.getMessage());
            }
            RecyclerView recyclerView = rootView.findViewById(R.id.ff_rv);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            followRecyclerViewAdapter = new FollowRecyclerViewAdapter(buddyList, relation,
                    listFollowing);
            recyclerView.setAdapter(followRecyclerViewAdapter);

            return rootView;
        }

        public void refreshFollowingFollowerList() {
            followRecyclerViewAdapter.setBuddyList(listFollowers, listFollowing);
        }


        /***********************************************************************
         ***********************************************************************
         Adding Firebase database Listeners to retrieve the data and update the UI on data change
         ***********************************************************************
         ************************************************************************/

        private void retrieveMyFollowersData() {
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
                                followRecyclerViewAdapter.setBuddyList(listFollowers, listFollowing);
                            } catch (NullPointerException NPE) {
                                FirebaseCrash.log(Plikes_TAG + NPE.getMessage());
                                Log.e(Plikes_TAG, NPE.getMessage());
                            }
                        }
                    }
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
            // Show 2 total pages.
            return 2;
        }
    }

}
