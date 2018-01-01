package me.veganbuddy.veganbuddy.actors;


import static me.veganbuddy.veganbuddy.util.GlobalVariables.thisAppUser;

/**
 * Created by abhishek on 11/11/17.
 */

public class FoodWisdom {
    String general;
    String health;
    String humour;
    String quotes;
    String recipes;
    String stats;
    String user_contributions;
    String userID;
    String photoGSuri;


    public FoodWisdom() {
        //default empty constructor
    }

    public FoodWisdom(String storageLocation) {
        user_contributions = storageLocation;
        userID = thisAppUser.getFireBaseID();
        photoGSuri = thisAppUser.getPhotoUrl();
    }

    public String getGeneral() {
        return general;
    }

    public String getHealth() {
        return health;
    }

    public String getHumour() {
        return humour;
    }

    public String getQuotes() {
        return quotes;
    }

    public String getRecipes() {
        return recipes;
    }

    public String getStats() {
        return stats;
    }

    public String getUser_contributions() {
        return user_contributions;
    }

    public String getUserID() {
        return userID;
    }

    public String getPhotoGSuri() {
        return photoGSuri;
    }
}
