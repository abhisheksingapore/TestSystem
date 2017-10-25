package me.veganbuddy.veganbuddy.actors;

import android.net.Uri;

import com.google.firebase.auth.FirebaseUser;

import org.json.JSONObject;

import java.net.URL;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static me.veganbuddy.veganbuddy.util.DateAndTimeUtils.dateTimeStamp;

/**
 * Created by abhishek on 1/9/17.
 */

public class User {
    String fireBaseID;
    String myFaceBookID;
    String myTwitterID;
    String myPinterestID;
    String myPinterestBoardID;
    String userName;
    String photoUrl;
    String email;
    String lastPostID;
    Map<String, String> myLikes = new HashMap<>();


    public User(){
        //default empty constructor
    }

    public User (FirebaseUser thisUser) {
        userName = thisUser.getDisplayName();
        photoUrl = thisUser.getPhotoUrl().toString();
        fireBaseID = thisUser.getUid();
        email = thisUser.getEmail();
        lastPostID = dateTimeStamp();
    }

    public void setMyFaceBookID(String myFaceBookID) {
        this.myFaceBookID = myFaceBookID;
    }

    public void setMyTwitterID(String myTwitterID) {
        this.myTwitterID = myTwitterID;
    }

    public void setMyPinterestBoardID(String myPinterestBoardID) {
        this.myPinterestBoardID = myPinterestBoardID;
    }

    public void setMyPinterestID(String myPinterestID) {
        this.myPinterestID = myPinterestID;
    }

    public void setLastPostID(String lastPostID) {
        this.lastPostID = lastPostID;
    }

    public void setMyLikes(String userId, String postID) {
        this.myLikes.put(userId, postID);
    }

    public String getMyPinterestID() {
        return myPinterestID;
    }

    public String getMyPinterestBoardID() {
        return myPinterestBoardID;
    }

    public String getFireBaseID() {
        return fireBaseID;
    }

    public String getMyFaceBookID() {
        return myFaceBookID;
    }

    public String getMyTwitterID() {
        return myTwitterID;
    }

    public String getUserName() {
        return userName;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getEmail() {
        return email;
    }

    public String getLastPostID() {
        return lastPostID;
    }

    public Map<String, String> getMyLikes() {
        return myLikes;
    }

    public void deleteMyLike(String userFirebaseID) {
        this.myLikes.remove(userFirebaseID);
    }
}
