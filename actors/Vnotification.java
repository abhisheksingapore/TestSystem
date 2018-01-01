package me.veganbuddy.veganbuddy.actors;

/**
 * Created by abhishek on 20/10/17.
 */

//Named as Vnotification to prevent any conflict with Android Notifications or Firebase Notifications
public class Vnotification {
    public String type;
    public String createdAt;
    public String viewedAt;
    public String createdBy;
    public String createdByName;
    public String createdByPic;
    public String createdForPhoto;
    public String createdForPostID;
    public Long numberOfVcoins;

    public Vnotification() {
        //default empty constructor
    }

    public Vnotification (String source) {
        this.createdBy = source;
    }

    public Vnotification(String type, String createdAt, String viewedAt, String createdBy, String createdByName,
                         String createdByPic, String createdForPhoto, Long numberOfVcoins, String postID) {
        this.type = type;
        this.createdAt = createdAt;
        this.viewedAt =viewedAt;
        this.createdBy = createdBy;
        this.createdByName = createdByName;
        this.createdByPic = createdByPic;
        this.createdForPhoto = createdForPhoto;
        this.numberOfVcoins = numberOfVcoins;
        this.createdForPostID = postID;
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

    public Long getNumberOfVcoins() {
        return numberOfVcoins;
    }

    public String getCreatedByPic() {
        return createdByPic;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public String getCreatedForPostID() {
        return createdForPostID;
    }
}
