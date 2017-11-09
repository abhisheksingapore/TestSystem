package me.veganbuddy.veganbuddy.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.veganbuddy.veganbuddy.R;
import me.veganbuddy.veganbuddy.actors.Buddy;

import static me.veganbuddy.veganbuddy.util.Constants.FOLLOWING_NODE;
import static me.veganbuddy.veganbuddy.util.Constants.LAST_POSTS_NODE;
import static me.veganbuddy.veganbuddy.util.Constants.ORIGIN_FRAGMENT;
import static me.veganbuddy.veganbuddy.util.Constants.POSTID;
import static me.veganbuddy.veganbuddy.util.Constants.POSTS_NODE;
import static me.veganbuddy.veganbuddy.util.Constants.POST_FAN_NODE;
import static me.veganbuddy.veganbuddy.util.Constants.Plikes_TAG;
import static me.veganbuddy.veganbuddy.util.FirebaseStorageUtils.addMeAsFollowerData;
import static me.veganbuddy.veganbuddy.util.FirebaseStorageUtils.addMeFollowingData;
import static me.veganbuddy.veganbuddy.util.FirebaseStorageUtils.deleteMeAsFollowerData;
import static me.veganbuddy.veganbuddy.util.FirebaseStorageUtils.deleteMeFollowingData;
import static me.veganbuddy.veganbuddy.util.GlobalVariables.thisAppUser;

public class PostLikesActivity extends AppCompatActivity {

    private List<Buddy> usersList;
    private List<String> meFollowingList;
    DatabaseReference myRef;
    String postID;
    String whichFragment;
    PostLikesRecyclerViewAdapter postLikesRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_likes);


        RecyclerView recyclerView = findViewById(R.id.apl_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        postLikesRecyclerViewAdapter
                = new PostLikesRecyclerViewAdapter(usersList);
        recyclerView.setAdapter(postLikesRecyclerViewAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        postID = getIntent().getStringExtra(POSTID);
        whichFragment = getIntent().getStringExtra(ORIGIN_FRAGMENT);
    }

    @Override
    protected void onResume() {
        super.onResume();
        retrieveMyFansData();
        retrieveMeFollowingData();
    }

    /***********************************************************************
     ***********************************************************************
     Adding Firebase database Listeners to retrieve the data and update the UI on data change
     ***********************************************************************
     ************************************************************************/

    //This sets reference to posts and lastPosts node in the database
    private void setPostsFirebaseReference() {
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        switch (whichFragment) {
            case LAST_POSTS_NODE: myRef = mDatabase.getReference().child(LAST_POSTS_NODE);
                break;
            case POSTS_NODE: myRef = mDatabase.getReference()
                    .child(thisAppUser.getFireBaseID()).child(POSTS_NODE);
                break;
        }
    }


    private void retrieveMyFansData(){
        setPostsFirebaseReference();
        Query myFansQuery = myRef.child(POST_FAN_NODE).child(postID);
        myFansQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    usersList = new ArrayList<>();
                    for (DataSnapshot dataSnapshotSingle: dataSnapshot.getChildren()){
                        try {
                            Buddy buddy = dataSnapshotSingle.getValue(Buddy.class);
                            buddy.setBuddyID(dataSnapshotSingle.getKey());
                            usersList.add(buddy);
                        } catch (NullPointerException NPE) {
                            FirebaseCrash.log(Plikes_TAG + NPE.getMessage());
                            Log.e(Plikes_TAG, NPE.getMessage());
                        }
                    }
                    postLikesRecyclerViewAdapter.setUserList(usersList);
                }
                Log.v(Plikes_TAG, "Successfully retrieved myFans data for this Post ");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                FirebaseCrash.log(Plikes_TAG + "Error in retrieving myFans data for this Post "
                        + databaseError.getMessage());
                Log.e(Plikes_TAG, "Error in retrieving myFans data for this Post "
                        + databaseError.getMessage());
            }
        });
    }


    private void retrieveMeFollowingData() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference();
        Query queryMeFollowing = databaseReference.child(thisAppUser.getFireBaseID())
                .child(FOLLOWING_NODE);
        queryMeFollowing.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                meFollowingList = new ArrayList<>();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot dataSnapshotSingle: dataSnapshot.getChildren()) {
                        meFollowingList.add(dataSnapshotSingle.getKey());
                    }
                }
                postLikesRecyclerViewAdapter.setMeFollowingList(meFollowingList);
                Log.v(Plikes_TAG, "Successfully retrieved meFollowing data for this Post ");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                FirebaseCrash.log(Plikes_TAG + "Error in retrieving MeFollowing data for this Post "
                        + databaseError.getMessage());
                Log.e(Plikes_TAG, "Error in retrieving MeFollowing data for this Post "
                        + databaseError.getMessage());
            }
        });

    }


}
