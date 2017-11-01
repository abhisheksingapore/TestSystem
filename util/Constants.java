package me.veganbuddy.veganbuddy.util;

import android.os.Build;

import java.security.PublicKey;

/**
 * Created by abhishek on 21/9/17.
 */

public final class Constants {

    public static final String app_logo = "https://firebasestorage.googleapis.com/v0/b/veganbuddy-55a61.appspot.com/o/AppFiles%2FVeganBuddyFBSquareLogo.png?alt=media&token=352e9feb-ca17-4cb3-ac44-1c63dec72273";
    public static final String app_link_url = "https://fb.me/144721999467838";

    //Constants for LoginActivity.java
    public static final String LOGIN_TAG = "LoginActivity.java";
    public static final String NOTIFICATION_CHANNEL_ID = "VeganBuddy_Notifications";


    //Constants for Dashboard.java and FirbaseStorageUtils.java
    public static final String DEFAULT_STATS_PIC_NAME = "99999";
    public static final String FIRST_PIC_NAME = "00001";

    //Constants for BitmapUtils.Java
    public static final String FILE_PROVIDER_AUTHORITY = "me.veganbuddy.veganbuddy.fileprovider";
    public static final String BU_TAG = "BitmapUtils.java";

    //Constants for Landingpage.Java
    public static final int UPLOAD_PICTURE_MANUALLY = 1;
    public static final String LP_TAG = "LandingPage.Java";
    public static final String CURRENT_USER = "currentUserPost";
    public static final String HEART_FULL = "heart full";
    public static final String HEART_EMPTY = "heart empty";

    public static final String NO_LIKES = " No likes";
    public static final String ONE_LIKE = " 1 like";

    public static final String NO_COMMENT = " No comment";
    public static final String ONE_COMMENT = " 1 comment";


    //Constants for Landingpagefragment.Java
    //Static variables to decide which Fragment Layout is to be loaded
    public static final int ANIMALS_DASHBOARD_LAYOUT = 0;
    public static final int VEGAN_DASHBOARD_LAYOUT = 1;
    public static final int PLACARD_LAYOUT = 2;
    public static final int MY_PLACARD_LAYOUT = 3;
    public static final int NOTIFICATIONS_INBOUND_LAYOUT = 4;
    public static final int NOTIFICATIONS_OUTBOUND_LAYOUT = 5;

    //Constants for Landingpage.Java and FullSizePhoto.java
    public static final String FULL_PHOTO_URI = "FullPhotoUri";

    //Constants for MealPhoto.java
    public static final String MP_TAG = "MealPhoto.Java";
    public static final int LOCATION_PERMITTED = 0;
    public static final int SHOW_EDIT_TEXT = 0;
    public static final int HIDE_EDIT_TEXT = 1;
    public static final int SHOW_PROGRESS_BAR = 2;
    public static final int SHOW_MEAL_PHOTO = 3;


    //Constants for MealPreviewPhoto.Java
    public static final String MP_CLASS_TAG = "MealPreviewPhoto";
    public static final String MP_ASYNC_CLASS_TAG = "UploadPhotoAnd..";
    public static final String FIREBASE_FULL_IMAGE_FOLDER = "/fullSize/";
    public static final String FIREBASE_THUMBNAIL_FOLDER = "/thumbnail/";
    public static final String FIREBASE_SCREENSHOT_FOLDER = "/Screenshot/";

    //Constants for both MealPhoto.Java and MealPreviewPhoto.Java
    public static final String SELECTED_LOCATION="Location";
    public static final String VEGANPHILOSOPHY="Philosophy";
    public static final String SHARETOFACEBOOK = "Facebook";
    public static final String SHARETOPINTEREST = "Pin";
    public static final String SHARETOTWITTER = "Twitter";
    public static final String STATS_IMAGE_URI = "StatsImageUri";

    //Constants for FetchAddressIntentService.java
    public static final int SUCCESS_RESULT = 0;
    public static final int FAILURE_RESULT = 1;
    public static final int NUMBER_OF_ADDRESSES_TO_RETRIEVE = 10;
    public static final String PACKAGE_NAME = "me.veganbuddy.veganbuddy.services";
    public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME + ".LOCATION_DATA_EXTRA";
    public static final String RESULT_DATA_KEY = PACKAGE_NAME + ".RESULT_DATA_KEY";
    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
    public static final String FAIS_TAG = "FetchAddressIntent";

    //Constants for SocialMediaUtils.java
    static final String SMU_TAG = "SocialMediaUtils.Java";
    static final String VEGAN_BUDDY_WEBSITE = "http://www.veganbuddy.me/";

    //Constants for FirebaseStorageUtils.java
    final static String FB_ERROR_TAG = "FIREBASESTORAGEUTILS";
    final static String STATISTICS_IMAGES_FOLDER = "/animal_statistics/";
    final static String SUPPORTED_FILE_EXTENSION = ".png";

    final static int NUMBER_OF_COMMENTS_TO_RETRIEVE = 10;
    final static String VEGAN_PHILOSOPHY_NODE = "VeganPhilosophy";
    final static boolean INCREASE_BY_ONE = true ;
    final static String COMMENTS_NODE = "comments";
    final static String COMMENTS_COUNT_NODE = "commentsCount";
    final static String POSTS_NODE = "posts";
    final static String LAST_POSTS_NODE = "lastPosts";
    final static String V_COINS_NODE = "vCoins";
    public final static String V_NOTIFICATIONS_NODE = "vNotifications";
    public final static String PROFILE_NODE = "profile";
    public final static String DASHBOARD_NODE = "dashboard";

    public final static boolean DO_NOT_INCREASE = false;
    public final static String DEFAULT_VEGAN_DATE = "start";

    //Constants for PinterestLoginActivity.java
    public final static String PLA_TAG = "PinterestLoginActivity:";
    public final static int PINTEREST_LOGIN_SUCCESS = 3333;
    public final static String PINTEREST_KEY = "Pinterest_key";

    //Constants for SocialLoginsActivity.java;
    public final static int PINTEREST_LOGIN = 333 ;
    public final static String SLA_TAG = "SocialLoginsActivity:";
    public final static String VEGAN_BUDDY_PIN_BOARD = "VeganBuddy";

    //Constants for DataRefreshActivity.java
    public static final String DRA_TAG = "DataRefreshActivity";
    public static final int VN_NODES_TO_RETRIEVE = 20;
    public static final String DATE_TIME_NODE = "createdAt";//Check against the variable declaration
                                                            // of Vnotification.java
    //Constants for LandingPageFragment.java
    public final static String LPF_TAG = "LandingPageFragment:";

    //Constants for MyFirebaseInstanceIDService.java
    public static final String MFIIS_TAG = "Firebase ID Service";

    //Constants for MyFirebaseMessagingService.java
    public static final String MFMS_TAG = "Firebase Msging Svc";

    //Constants for VnotificationFragment.java
    public static final String VNF_TAG = "VnotificationFragment";

    // Constants for Vnotification.java
    public static final String INBOUND = "INBOUND";
    public static final String OUTBOUND = "OUTBOUND";
    public static final String VN_UPLOADED_NEW_MEAL_PHOTO = "UPLOADED";
    public static final String VN_LIKED_PHOTO = "LIKE";
    public static final String VN_COMMENT_PHOTO = "COMMENT";
    public static final String VN_DIRECT_MESSAGE = "MESSAGE";
    public static final String VN_PHOTO_SHARE = "SHARE";
    public static final String NOT_VIEWED_YET = "NEVER";

    //Constants for Vcoins.Java
    public static final double NEW_PHOTO_CREATED = 100;

    public static final String ACTIONED = "ACTIONED";
    public static final String RECEIVED = "RECEIVED";

    public static final double ACTIONED_FACTOR = 1.0;
    public static final double RECEIVED_FACTOR = 0.8;
    public static final double LIKE_FACTOR = 0.1;
    public static final double COMMENT_FACTOR = 0.2;
    public static final double DIRECT_MESSAGE_FACTOR = 0.3;
    public static final double SHARE_FACTOR = 0.5;

    //Constants for PlacardsFragment.java
    public static final String PF_TAG = "PlacardsFragment.java";
    public static final String NODE_FOR_ALL_POSTS = "lastPosts";
    public static final String NODE_FOR_MY_POSTS = "posts";
    public final static int NUMBER_OF_POSTS_TO_RETRIEVE = 20;//Todo: implement 'Pull Down' 'refresh' and 'add-more'
    public final static String DATE_STAMP_KEY_NAME = "datestamp";

    //Constants for CameraActivity.java
    public static final String CA_TAG = "CameraActivity";

    public static final int PICTURES_DIRECTORY_PERMISSION = 939;
    public static final String VEGAN_BUDDY_FOLDER = "VeganBuddy";

    public static final String FLASH_SETTING = "Flash Setting";
    public static final String FLASH_ON_MSG = "flashOn" ;
    public static final String FLASH_OFF_MSG = "flashOff";
    public static final String SHUTTER_SOUND_SETTING = "Sound Setting";
    public static final String SHUTTER_SOUND_ON_MSG = "SoundOn" ;
    public static final String SHUTTER_SOUND_OFF_MSG = "SoundOff";
    public static final int HIGH_QUALITY = 100;
    public static final int LOW_QUALITY = 50;

    public static final int SHOW_CAMERA = 0;
    public static final int SHOW_PROGRESS = 1;
}
