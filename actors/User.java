package me.veganbuddy.veganbuddy.actors;

import android.net.Uri;

import com.google.firebase.auth.FirebaseUser;

import java.net.URL;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by abhishek on 1/9/17.
 */

public class User {
    public static FirebaseUser thisAppUser;
    public static int mealsForToday;
    public static String startDateForVeganism = "start";
    public static boolean waitingForData = true;

    private Map <String, Object> profile = new HashMap<>();

    public User (FirebaseUser thisUser) {
        String userName = thisUser.getDisplayName();
        String userPhoto = thisUser.getPhotoUrl().toString();

        profile.put("User_Name", userName);
        profile.put("User_Profile_Photo", userPhoto);
    }

    public Map<String, Object> getProfile() {
        return profile;
    }
}
