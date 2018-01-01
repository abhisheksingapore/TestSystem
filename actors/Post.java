package me.veganbuddy.veganbuddy.actors;

/**
 * Created by abhishek on 2/9/17.
 */

public class Post {
    public String userPhotoUri; //Todo: Remove Suffix Uri from Strings
    public String userName;
    public String datestamp; // = DATE_STAMP_KEY_NAME in Constants file
    public String mealPhotoUri;
    public String mealPhotoThumbnailUri;
    public String screenShotUri;
    public String veganPhilosophyText;
    public String locationText;
    public int likesCount;
    public int commentsCount;
    public boolean iLoveFlag;
    public boolean hasComments;
    public boolean isSharedOnFaceBook;
    public boolean isSharedOnTwitter;
    public boolean isSharedOnPinterest;

    public Post() {
        //Empty Default Constructor required for Firebase to function
    }

    public Post (String userPicUri, String username, String mealphoto, String dtStamp,
                 String mealThumb, String screenShot, String veganText, String locationText,
                 int likecount, boolean loveflg, boolean comments, int commentsCount,
                 boolean facebook, boolean twitter, boolean pinterest) {
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
        this.isSharedOnFaceBook = facebook;
        this.isSharedOnTwitter = twitter;
        this.isSharedOnPinterest = pinterest;
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

    public int getCommentsCount() {
        return commentsCount;
    }

    public String getScreenShotUri() {
        return screenShotUri;
    }

    public String getLocationText() {
        return locationText;
    }

    public boolean isSharedOnFaceBook() {
        return isSharedOnFaceBook;
    }

    public void setSharedOnFaceBook(boolean sharedOnFaceBook) {
        isSharedOnFaceBook = sharedOnFaceBook;
    }

    public boolean isSharedOnTwitter() {
        return isSharedOnTwitter;
    }

    public void setSharedOnTwitter(boolean sharedOnTwitter) {
        isSharedOnTwitter = sharedOnTwitter;
    }

    public boolean isSharedOnPinterest() {
        return isSharedOnPinterest;
    }

    public void setSharedOnPinterest(boolean sharedOnPinterest) {
        isSharedOnPinterest = sharedOnPinterest;
    }
}
