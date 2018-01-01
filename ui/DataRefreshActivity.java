package me.veganbuddy.veganbuddy.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import me.veganbuddy.veganbuddy.R;
import me.veganbuddy.veganbuddy.actors.Dashboard;
import me.veganbuddy.veganbuddy.actors.User;

import static me.veganbuddy.veganbuddy.util.Constants.DASHBOARD_NODE;
import static me.veganbuddy.veganbuddy.util.Constants.LP_TAG;
import static me.veganbuddy.veganbuddy.util.Constants.PROFILE_NODE;
import static me.veganbuddy.veganbuddy.util.FirebaseStorageUtils.getFoodWisdomThreshold;
import static me.veganbuddy.veganbuddy.util.FirebaseStorageUtils.retrieveApplicablePicName;
import static me.veganbuddy.veganbuddy.util.FirebaseStorageUtils.retrieveFoodWisdomThreshold;
import static me.veganbuddy.veganbuddy.util.FirebaseStorageUtils.retrieveMessageForTheDay;
import static me.veganbuddy.veganbuddy.util.GlobalVariables.myDashboard;
import static me.veganbuddy.veganbuddy.util.GlobalVariables.thisAppUser;

public class DataRefreshActivity extends AppCompatActivity {

    private static FirebaseDatabase mDatabase;
    private static DatabaseReference myRef; //Reference for nodes in the database


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_refresh);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (myDashboard != null) {
            addDashboardListener();
            addProfileListener();
            //retrieve the app Message that will be displayed along with the meal photos
            retrieveMessageForTheDay(retrieveApplicablePicName());
            //retrieve foodWisdomThreshold value
            if (getFoodWisdomThreshold() == -939) retrieveFoodWisdomThreshold();
        } else {
            //This situation will arise if the app has launched this activity without first retrieving
            // the myDashboard Data
            Intent intentLogin = new Intent (this, LoginActivity.class);
            startActivity(intentLogin);
        }
    }

    public void reloadLandingPage(boolean success){
        if (!success) Toast.makeText(this,
                "Data refresh error. Please try manually later", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, LandingPage.class);
        startActivity(intent);
    }


    //========== Attaching Firebase Listeners ============
    private  void setProperDatabaseReference(){
        if (thisAppUser!=null) {
            String username = thisAppUser.getFireBaseID();
            mDatabase = FirebaseDatabase.getInstance();
            myRef = mDatabase.getReference(username);
        } else {
            //This situation will arise if the app has launched this activity without first retrieving
            // thisAppUser Data or it crashed on this activity and AndroidOS automatically tried
            // to resume it
            Intent intentLogin = new Intent (this, LoginActivity.class);
            startActivity(intentLogin);
        }
    }

    private void addDashboardListener() {
        setProperDatabaseReference();
        myRef.child(DASHBOARD_NODE).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot!=null) {
                    myDashboard = dataSnapshot.getValue(Dashboard.class);
                    Log.v(LP_TAG, "myDashboard data fetched from Firebase");
                    reloadLandingPage(true);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(LP_TAG, "Error in retrieving myDashboard data from Firebase" +
                        databaseError.getDetails());
                reloadLandingPage(false);
            }
        });
    }

    private void addProfileListener() {
        setProperDatabaseReference();
        myRef.child(PROFILE_NODE).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot!=null) {
                    thisAppUser = dataSnapshot.getValue(User.class);
                    Log.v(LP_TAG, "thisAppUser data fetched from Firebase");
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(LP_TAG, "Error in retrieving thisAppUser data from Firebase" +
                        databaseError.getDetails());
                reloadLandingPage(false);
            }
        });
    }

}
