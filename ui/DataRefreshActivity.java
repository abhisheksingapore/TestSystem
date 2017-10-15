package me.veganbuddy.veganbuddy.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import me.veganbuddy.veganbuddy.R;
import me.veganbuddy.veganbuddy.actors.Dashboard;

import static me.veganbuddy.veganbuddy.util.Constants.DASHBOARD_NODE;
import static me.veganbuddy.veganbuddy.util.Constants.LP_TAG;
import static me.veganbuddy.veganbuddy.util.Constants.MAX_WAIT_TIME;
import static me.veganbuddy.veganbuddy.util.FirebaseStorageUtils.dashboardDataUpdated;
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
        addDashboardListener();
    }

    public void reloadLandingPage(boolean success){
        if (!success) Toast.makeText(this,
                "Data refresh error. Please try manually later", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, LandingPage.class);
        startActivity(intent);
    }


    //========== Attaching Firebase Listeners ============
    private  void setProperDatabaseReference(){
        String username = thisAppUser.getFireBaseID();
        mDatabase = FirebaseDatabase.getInstance();
        myRef = mDatabase.getReference(username);
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
            }
        });
    }
}
