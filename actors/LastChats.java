package me.veganbuddy.veganbuddy.actors;

/**
 * Created by abhishek on 7/12/17.
 */

public class LastChats extends ChatMessage {
    public String buddyName;
    public String buddyurl;
    public String buddyID;
    public Boolean unRead;

    LastChats() {
        super();
    }

    public String getBuddyName() {
        return buddyName;
    }

    public String getBuddyurl() {
        return buddyurl;
    }

    public Boolean getUnRead() {
        return unRead;
    }
}
