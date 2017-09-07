package me.veganbuddy.veganbuddy.util;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import me.veganbuddy.veganbuddy.actors.Dashboard;
import me.veganbuddy.veganbuddy.actors.Post;
import me.veganbuddy.veganbuddy.actors.User;
import me.veganbuddy.veganbuddy.ui.LandingPage;

/**
 * Created by abhishek on 1/9/17.
 */

public class FirebaseStorageUtils {
    static FirebaseDatabase mDatabase;
    static DatabaseReference myRef;

    public static void addNodesToDatabase(String userID, FirebaseUser user, int addMeal,
                                          String photoURL, String userInputText) {
        String username = justGoogleID(userID);
        mDatabase = FirebaseDatabase.getInstance();
        myRef = mDatabase.getReference(username);

        //Methods to save relevant Data into the Firebase Database
        setDashboardData();
        setPostsData(photoURL,  userInputText);
        setUserData (user);
    }

    //Method to store some specific Data about the user profile in the Firebase Database
    private static void setUserData(FirebaseUser user) {
        myRef.child("profile")
                .child("username").setValue(user.getDisplayName());
        myRef.child("profile")
                .child("profilePic").setValue(user.getPhotoUrl().toString());

    }

    //Method to save the photoURL and text input by user for that particular meal
    private static void setPostsData(String photoURL, String userInputText) {
        String key_prefix = DateAndTimeUtils.dateTimeStamp()+"_"+DateAndTimeUtils.getMealTypeBasedOnTimeOfTheDay();

        myRef.child("posts")
                .child(key_prefix + "_mealPhotoURL")
                .setValue(photoURL);
        myRef.child("posts")
                .child(key_prefix+ "_veganPhilosophyText")
                .setValue(userInputText);
    }


    private static void setDashboardData() {
        //Read the existing Dashboard data
        final String node = "dashboard";
        myRef.child(node).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Dashboard value = dataSnapshot.getValue(Dashboard.class);
                if (value == null) {
                    value = new Dashboard(1);
                } else {
                    value = value.incrementDashboardByOne();
                }
                myRef.child(node).setValue(value);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private static String justGoogleID(String userID) {
        //Remove '@gmail.com' from the Google ID of Sender
        return userID.replaceAll("@gmail.com", "");
    }

    public static void setUserMealDataForToday() {
        String username = justGoogleID(User.thisAppUser.getEmail());
        mDatabase = FirebaseDatabase.getInstance();
        myRef = mDatabase.getReference(username);
        myRef.child("dashboard").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Dashboard dashboard = dataSnapshot.getValue(Dashboard.class);
                if (dashboard.checkIfTodaysDateExistsInDatabase ()){
                    //An entry for todays date exists in database, so no data change needed
                    User.mealsForToday = dashboard.getMealsForToday();
                } else {
                    //An entry for todays date does not exist in database, so "mealsForToday" is .
                    //for last entered Date. Data needs to be refreshed
                    User.mealsForToday = 0;
                    myRef.child("dashboard").setValue(dashboard);
                }
                User.waitingForData = false;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.v("FirebaseStorageUtils", "Error in Database Connection");
            }
        });
    }

}
