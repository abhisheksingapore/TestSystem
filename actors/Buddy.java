package me.veganbuddy.veganbuddy.actors;

import static me.veganbuddy.veganbuddy.util.DateAndTimeUtils.dateTimeStampMilliSeconds;

/**
 * Created by abhishek on 31/10/17.
 */

public class Buddy extends Object {

    public String name;
    public String photoUrl;
    public String buddyID;
    public String datetimestamp;

    public Buddy() {
        //default empty constructor for the class
    }

    public Buddy(String fanName, String url) {
        name = fanName;
        photoUrl = url;
        datetimestamp = dateTimeStampMilliSeconds();
    }

    public String getName() {
        return name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setBuddyID(String buddyId) {
        this.buddyID = buddyId;
    }

    public String getDatetimestamp() {
        return datetimestamp;
    }
}
