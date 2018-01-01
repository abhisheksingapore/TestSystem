package me.veganbuddy.veganbuddy.util;


import com.google.android.gms.common.api.GoogleApiClient;

import java.util.List;

import me.veganbuddy.veganbuddy.actors.Buddy;
import me.veganbuddy.veganbuddy.actors.Dashboard;
import me.veganbuddy.veganbuddy.actors.User;

/**
 * Created by abhishek on 28/9/17.
 */

public class GlobalVariables {
    public static Dashboard myDashboard;
    public static User thisAppUser;
    public static GoogleApiClient googleApiClient;
    public static List<Buddy> listFollowing;

}
