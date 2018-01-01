package me.veganbuddy.veganbuddy.actors;

import com.google.firebase.auth.FirebaseUser;

import static me.veganbuddy.veganbuddy.util.DateAndTimeUtils.dateStampHumanReadable;
import static me.veganbuddy.veganbuddy.util.DateAndTimeUtils.dateTimeStamp;

/**
 * Created by abhishek on 1/9/17.
 */

public class User {
    //Todo: move 'start-date-of-vegan and lastPicName from Dashboard to profile
    public String fireBaseID;
    public String userName;
    public String photoUrl;
    public String email;
    public String lastPostID;
    public String city;
    public String website;
    public String gender;
    public String veganphilosophy;
    public String startDateOfVegan;
    public String relationShipStatus;

    public int myFollowersCount;
    public int meFollowingCount;
    public String myFaceBookID;
    public String myTwitterID;
    public String myPinterestID;
    public String myPinterestBoardID;
    private int foodWisdomCounter = 1; //value increases between 1 and foodWisdom/threshold/CurrentValue
    private int lastFoodWisdom = -939;

    public User(){
        //default empty constructor
    }

    public User(User thisUser) {
        userName = thisUser.getUserName();
        photoUrl = thisUser.getPhotoUrl();
        fireBaseID = thisUser.getFireBaseID();
        email = thisUser.getEmail();
        lastPostID = thisUser.getLastPostID();
        meFollowingCount = thisUser.getMeFollowingCount();
        myFollowersCount = thisUser.getMyFollowersCount();
        foodWisdomCounter = thisUser.getFoodWisdomCounter();
        lastFoodWisdom = thisUser.getLastFoodWisdom();
        city = thisUser.getCity();
        website = thisUser.getWebsite();
        gender = thisUser.getGender();
        veganphilosophy = thisUser.getVeganphilosophy();
        startDateOfVegan = thisUser.getStartDateOfVegan();
        relationShipStatus = thisUser.getRelationShipStatus();
        myFaceBookID = thisUser.getMyFaceBookID();
        myTwitterID = thisUser.getMyTwitterID();
        myPinterestID = thisUser.getMyPinterestID();
        myPinterestBoardID = thisUser.getMyPinterestBoardID();
    }

    public User (FirebaseUser thisUser) {
        userName = thisUser.getDisplayName();
        fireBaseID = thisUser.getUid();
        email = thisUser.getEmail();
        photoUrl = thisUser.getPhotoUrl().toString();
        lastPostID = dateTimeStamp();
        meFollowingCount = 0;
        myFollowersCount = 0;
        foodWisdomCounter = 1;
        lastFoodWisdom = -939;

        startDateOfVegan = dateStampHumanReadable();
    }

    public void updateFoodWisdomCounter() {
        if (foodWisdomCounter == 0) {
            foodWisdomCounter = 1;
            return;
        }

        if (foodWisdomCounter > 0) {
            foodWisdomCounter++;
        }
    }

    public String createMyLikesKey(String userId, String postID) {
        return userId+postID;
    }

    public int getMyFollowersCount() {
        return myFollowersCount;
    }

    public void setMyFollowersCount(int myFollowersCount) {
        this.myFollowersCount = myFollowersCount;
    }

    public int getMeFollowingCount() {
        return meFollowingCount;
    }

    public String getMyPinterestID() {
        return myPinterestID;
    }

    public void setMyPinterestID(String myPinterestID) {
        this.myPinterestID = myPinterestID;
    }

    public String getMyPinterestBoardID() {
        return myPinterestBoardID;
    }

    public void setMyPinterestBoardID(String myPinterestBoardID) {
        this.myPinterestBoardID = myPinterestBoardID;
    }

    public String getFireBaseID() {
        return fireBaseID;
    }

    public void setFireBaseID(String fireBaseID) {
        this.fireBaseID = fireBaseID;
    }

    public String getMyFaceBookID() {
        return myFaceBookID;
    }

    public void setMyFaceBookID(String myFaceBookID) {
        this.myFaceBookID = myFaceBookID;
    }

    public String getMyTwitterID() {
        return myTwitterID;
    }

    public void setMyTwitterID(String myTwitterID) {
        this.myTwitterID = myTwitterID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLastPostID() {
        return lastPostID;
    }

    public void setLastPostID(String lastPostID) {
        this.lastPostID = lastPostID;
    }

    public int getFoodWisdomCounter() {
        return foodWisdomCounter;
    }

    public void setFoodWisdomCounter(int v) {
        this.foodWisdomCounter = v;
    }

    public int getLastFoodWisdom() {
        return lastFoodWisdom;
    }

    public void setLastFoodWisdom(int l) {
        this.lastFoodWisdom = l;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getVeganphilosophy() {
        return veganphilosophy;
    }

    public void setVeganphilosophy(String veganphilosophy) {
        this.veganphilosophy = veganphilosophy;
    }

    public String getStartDateOfVegan() {
        if (startDateOfVegan == null) startDateOfVegan = dateStampHumanReadable();
        return startDateOfVegan;
    }

    public void setStartDateOfVegan(String startDateOfVegan) {
        this.startDateOfVegan = startDateOfVegan;
    }

    public String getRelationShipStatus() {
        return relationShipStatus;
    }

    public void setRelationShipStatus(String relationShipStatus) {
        this.relationShipStatus = relationShipStatus;
    }

}
