package me.veganbuddy.veganbuddy.actors;

import android.support.annotation.NonNull;

/**
 * Created by abhishek on 20/10/17.
 */

//Named as Vnotification to prevent any conflict with Android Notifications or Firebase Notifications
public class Vnotification {
    private String type;
    private String createdAt;
    private String viewedAt;
    private String createdBy;
    private String createdByName;
    private String createdByPic;
    private String createdForPhoto;
    private String numberOfVcoins;


    public Vnotification() {
        //default empty constructor
    }

    public Vnotification (String source) {
        this.createdBy = source;
    }

    public Vnotification(String type, String createdAt, String viewedAt, String createdBy, String createdByName,
                         String createdByPic, String createdForPhoto, String numberOfVcoins) {
        this.type = type;
        this.createdAt = createdAt;
        this.viewedAt =viewedAt;
        this.createdBy = createdBy;
        this.createdByName = createdByName;
        this.createdByPic = createdByPic;
        this.createdForPhoto = createdForPhoto;
        this.numberOfVcoins = numberOfVcoins;
    }

    public String getType() {
        return type;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getViewedAt() {
        return viewedAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getCreatedForPhoto() {
        return createdForPhoto;
    }

    public String getNumberOfVcoins() {
        return numberOfVcoins;
    }

    public String getCreatedByPic() {
        return createdByPic;
    }

    public String getCreatedByName() {
        return createdByName;
    }
}
