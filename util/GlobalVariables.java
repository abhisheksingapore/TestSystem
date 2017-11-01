package me.veganbuddy.veganbuddy.util;


import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.veganbuddy.veganbuddy.actors.Dashboard;
import me.veganbuddy.veganbuddy.actors.User;
import me.veganbuddy.veganbuddy.actors.Vnotification;

/**
 * Created by abhishek on 28/9/17.
 */

public class GlobalVariables {
    public static Dashboard myDashboard;
    public static User thisAppUser;
    public static GoogleApiClient googleApiClient;
}
