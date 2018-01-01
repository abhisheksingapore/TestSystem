package me.veganbuddy.veganbuddy.util;

import java.util.List;

import me.veganbuddy.veganbuddy.actors.Buddy;

/**
 * Created by abhishek on 22/12/17.
 */

public class CommonMethods {

    //Common to both LandingPage.java and FollowActivity.java
    public static boolean containsID(List<Buddy> listFollowing, String buddyID) {
        for (Buddy buddy : listFollowing) {
            if (buddyID.equals(buddy.buddyID)) return true;
        }
        return false;
    }

}
