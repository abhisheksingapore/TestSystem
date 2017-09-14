package me.veganbuddy.veganbuddy.actors;

import android.net.Uri;

import java.util.HashMap;
import java.util.Map;

import me.veganbuddy.veganbuddy.util.DateAndTimeUtils;

/**
 * Created by abhishek on 2/9/17.
 */

public class Post {
    private String userPhotoUri; //Todo: Remove Suffix Uri from Strings
    private String userName;
    private String datestamp;
    private String mealPhotoUri;
    private String mealPhotoThumbnailUri;
    private String veganPhilosophyText;
    private int likesCount;
    private boolean iLoveFlag;
    private boolean hasComments; //Comments class for recording all comments is yet to be implemented


    public Post() {
        //Empty Default Constructor required for Firebase to function
    }

    public Post (String userPicUri, String username, String mealphoto, String dtStamp,
                 String mealThumb, String veganText, int likecount, boolean loveflg, boolean comments ) {
        this.userPhotoUri = userPicUri;
        this.veganPhilosophyText = veganText;
        this.datestamp = dtStamp;
        this.mealPhotoUri = mealphoto;
        this.mealPhotoThumbnailUri = mealThumb;
        this.userName = username;
        this.likesCount = likecount;
        this.iLoveFlag = loveflg;
        this.hasComments = comments;
    }

    public String getUserPhotoUri() {
        return userPhotoUri;
    }

    public void setUserPhotoUri(String userPhotoUri) {
        this.userPhotoUri = userPhotoUri;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDatestamp() {
        return datestamp;
    }

    public void setDatestamp(String datestamp) {
        this.datestamp = datestamp;
    }

    public String getMealPhotoUri() {
        return mealPhotoUri;
    }

    public void setMealPhotoUri(String mealPhotoUri) {
        this.mealPhotoUri = mealPhotoUri;
    }

    public String getMealPhotoThumbnailUri() {
        return mealPhotoThumbnailUri;
    }

    public void setMealPhotoThumbnailUri(String mealPhotoThumbnailUri) {
        this.mealPhotoThumbnailUri = mealPhotoThumbnailUri;
    }

    public String getVeganPhilosophyText() {
        return veganPhilosophyText;
    }

    public void setVeganPhilosophyText(String veganPhilosophyText) {
        this.veganPhilosophyText = veganPhilosophyText;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public boolean isiLoveFlag() {
        return iLoveFlag;
    }

    public void setiLoveFlag(boolean iLoveFlag) {
        this.iLoveFlag = iLoveFlag;
    }

    public boolean isHasComments() {
        return hasComments;
    }

    public void setHasComments(boolean hasComments) {
        this.hasComments = hasComments;
    }
}
