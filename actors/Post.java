package me.veganbuddy.veganbuddy.actors;

import android.net.Uri;

import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

import me.veganbuddy.veganbuddy.util.DateAndTimeUtils;

/**
 * Created by abhishek on 2/9/17.
 */

public class Post {
    private String userPhotoUri; //Todo: Remove Suffix Uri from Strings
    private String userName;
    private String datestamp; // = DATE_STAMP_KEY_NAME in Constants file
    private String mealPhotoUri;
    private String mealPhotoThumbnailUri;
    private String screenShotUri;
    private String veganPhilosophyText;
    private String locationText;
    private int likesCount;
    private Map<String, String> likedMe = new HashMap<>();
    private int commentsCount;
    private boolean iLoveFlag;
    private boolean hasComments;

    public Post() {
        //Empty Default Constructor required for Firebase to function
    }

    public Post (String userPicUri, String username, String mealphoto, String dtStamp,
                 String mealThumb, String screenShot, String veganText, String locationText,
                 int likecount, boolean loveflg, boolean comments, int commentsCount,
                 Map<String, String> whoLikedThisPost) {
        this.userPhotoUri = userPicUri;
        this.veganPhilosophyText = veganText;
        this.datestamp = dtStamp;
        this.mealPhotoUri = mealphoto;
        this.mealPhotoThumbnailUri = mealThumb;
        this.screenShotUri = screenShot;
        this.userName = username;
        this.locationText = locationText;
        this.likesCount = likecount;
        this.iLoveFlag = loveflg;
        this.hasComments = comments;
        this.commentsCount = commentsCount;
        this.likedMe = whoLikedThisPost;
    }

    public String getUserPhotoUri() {
        return userPhotoUri;
    }

    public String getUserName() {
        return userName;
    }

    public String getDatestamp() {
        return datestamp;
    }

    public String getMealPhotoUri() {
        return mealPhotoUri;
    }

    public String getMealPhotoThumbnailUri() {
        return mealPhotoThumbnailUri;
    }

    public String getVeganPhilosophyText() {
        return veganPhilosophyText;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public boolean isiLoveFlag() {
        return iLoveFlag;
    }

    public boolean isHasComments() {
        return hasComments;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public String getScreenShotUri() {
        return screenShotUri;
    }

    public String getLocationText() {
        return locationText;
    }

    public Map<String, String> getLikedMe() {
        return likedMe;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public Post deleteLike(String userFirebaseIDphotoUrl) {
        this.likedMe.remove(userFirebaseIDphotoUrl);
        return this;
    }
}
