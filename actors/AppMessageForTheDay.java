package me.veganbuddy.veganbuddy.actors;

/**
 * Created by abhishek on 4/10/17.
 */

public class AppMessageForTheDay {
    String picMessage;

    AppMessageForTheDay() {
        //Empty constructor
    }

    AppMessageForTheDay (String picMessage){
        this.picMessage = picMessage;
    }

    public String getPicMessage() {
        return picMessage;
    }

    public void setPicMessage(String picMessage) {
        this.picMessage = picMessage;
    }
}
