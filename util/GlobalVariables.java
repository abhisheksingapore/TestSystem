package me.veganbuddy.veganbuddy.util;


import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import me.veganbuddy.veganbuddy.actors.Dashboard;
import me.veganbuddy.veganbuddy.actors.User;

/**
 * Created by abhishek on 28/9/17.
 */

public class GlobalVariables {
    public static Dashboard myDashboard;
    public static User thisAppUser;
    public static GoogleApiClient googleApiClient;
}
