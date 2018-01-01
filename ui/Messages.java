package me.veganbuddy.veganbuddy.ui;

import android.os.Bundle;
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
import me.veganbuddy.veganbuddy.actors.LastChats;

import static me.veganbuddy.veganbuddy.util.Constants.FOLLOWING_NODE;
import static me.veganbuddy.veganbuddy.util.Constants.FRAG_CHATS;
import static me.veganbuddy.veganbuddy.util.Constants.FRAG_CONTACTS;
import static me.veganbuddy.veganbuddy.util.Constants.LAST_CHAT_MESSAGES_NODE;
import static me.veganbuddy.veganbuddy.util.Constants.M_TAG;
import static me.veganbuddy.veganbuddy.util.GlobalVariables.thisAppUser;

public class Messages extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private MessagesSectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        final Toolbar toolbarMessages = findViewById(R.id.am_toolbar);
        setSupportActionBar(toolbarMessages);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarMessages.setTitle("Chats");

        mSectionsPagerAdapter = new MessagesSectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.am_container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position + 1) {
                    case FRAG_CHATS:
                        toolbarMessages.setTitle("Chats");
                        break;
                    case FRAG_CONTACTS:
                        toolbarMessages.setTitle("Contacts");
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
        getMenuInflater().inflate(R.menu.menu_messages, menu);
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
    public static class MessagesFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_FRAG_NUM = "fragment_number";
        RecyclerView messageRV;
        private List<Buddy> buddyList;
        private List<LastChats> lastChatsList;
        private int fragNum;

        public MessagesFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static MessagesFragment newInstance(int fragNum) {
            MessagesFragment fragment = new MessagesFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_FRAG_NUM, fragNum);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            fragNum = getArguments().getInt(ARG_FRAG_NUM);

            View rootView = inflater.inflate(R.layout.fragment_messages, container, false);
            messageRV = rootView.findViewById(R.id.fm_rv);
            if (fragNum == FRAG_CONTACTS) retrieveContacts();
            else if (fragNum == FRAG_CHATS) retrieveChats();
            return rootView;
        }


        private void retrieveChats() {
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = firebaseDatabase.getReference();
            Query queryChats = databaseReference.child(thisAppUser.getFireBaseID())
                    .child(LAST_CHAT_MESSAGES_NODE);
            queryChats.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        lastChatsList = new ArrayList<>();
                        for (DataSnapshot dataSnapshotSingle : dataSnapshot.getChildren()) {
                            LastChats lastChat = dataSnapshotSingle.getValue(LastChats.class);
                            lastChat.buddyID = dataSnapshotSingle.getKey();
                            lastChatsList.add(lastChat);
                        }
                        Log.v(M_TAG, "Last Chats retrieved successfully");
                        if (lastChatsList != null) addLastChatsToView(true);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(M_TAG, "Failed to retrieve last chats for this user");
                }
            });

        }

        private void addLastChatsToView(boolean gotChats) {
            if (gotChats) {
                MessageRecyclerviewAdapterChats messageRecyclerviewAdapterChats
                        = new MessageRecyclerviewAdapterChats(lastChatsList);
                messageRV.setLayoutManager(new LinearLayoutManager(getContext()));
                messageRV.setAdapter(messageRecyclerviewAdapterChats);
            }
        }

        private void retrieveContacts() {
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = firebaseDatabase.getReference();
            Query queryContacts = databaseReference.child(thisAppUser.getFireBaseID())
                    .child(FOLLOWING_NODE);
            queryContacts.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        try {
                            buddyList = new ArrayList<>();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Buddy buddyThis = snapshot.getValue(Buddy.class);
                                buddyThis.setBuddyID(snapshot.getKey());
                                buddyList.add(buddyThis);
                            }
                        } catch (NullPointerException NPE) {
                            Log.e(M_TAG, NPE.getMessage());
                            FirebaseCrash.log(NPE.getMessage());
                        }
                        Log.v(M_TAG, "Successfully retrieved contacts to message");

                        if (buddyList != null) addBuddyListToView(true);
                        else addBuddyListToView(false);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(M_TAG, "Failed to retrieve contacts to message");
                }
            });
        }

        private void addBuddyListToView(boolean gotContacts) {
            if (gotContacts) {
                MessageRecyclerviewAdapter messageRecyclerviewAdapter =
                        new MessageRecyclerviewAdapter(buddyList);
                messageRV.setLayoutManager(new LinearLayoutManager(getContext()));
                messageRV.setAdapter(messageRecyclerviewAdapter);
            }
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class MessagesSectionsPagerAdapter extends FragmentPagerAdapter {

        public MessagesSectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return MessagesFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }
    }
}
