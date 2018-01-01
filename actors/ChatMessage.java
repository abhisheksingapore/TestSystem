package me.veganbuddy.veganbuddy.actors;

/**
 * Created by abhishek on 5/12/17.
 */

public class ChatMessage {
    public String textMessage;
    public String dateStamp;
    public String timeStamp;
    public String senderPicURL;
    public String senderID;

    public ChatMessage() {

    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getTextMessage() {
        return textMessage;
    }

    public void setTextMessage(String textMessage) {
        this.textMessage = textMessage;
    }

    public String getDateStamp() {
        return dateStamp;
    }

    public void setDateStamp(String dateStamp) {
        this.dateStamp = dateStamp;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getSenderPicURL() {
        return senderPicURL;
    }

    public void setSenderPicURL(String senderPicURL) {
        this.senderPicURL = senderPicURL;
    }
}
