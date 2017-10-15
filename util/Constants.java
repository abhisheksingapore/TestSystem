package me.veganbuddy.veganbuddy.util;

import java.security.PublicKey;

/**
 * Created by abhishek on 21/9/17.
 */

public final class Constants {

    public static final String app_logo = "https://firebasestorage.googleapis.com/v0/b/veganbuddy-55a61.appspot.com/o/AppFiles%2FVeganBuddyFBSquareLogo.png?alt=media&token=352e9feb-ca17-4cb3-ac44-1c63dec72273";
    public static final String app_link_url = "https://fb.me/144721999467838";

    //Constants for LoginActivity.java
    public static final String LOGIN_TAG = "LoginActivity.java";
    public static final String DOWNLOAD_PICS = "DOWNLOAD_PICS";
    public static final String DELETE_PICS = "DELETE_PICS";


    //Constants for Dashboard.java and FirbaseStorageUtils.java
    public static final String DEFAULT_STATS_PIC_NAME = "99999";
    public static final String FIRST_PIC_NAME = "00001";

    //Constants for BitmapUtils.Java
    public static final String FILE_PROVIDER_AUTHORITY = "me.veganbuddy.veganbuddy.fileprovider";

    //Constants for Landingpage.Java
    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final String LP_TAG = "LandingPage.Java";
    public static final int DASHBOARD_ANIMAL_FRAGMENT_ID = 0;
    public static final int DASHBOARD_VEGAN_FRAGMENT_ID = 0;

    //Constants for Landingpage.Java and FullSizePhoto.java
    public static final String FULL_PHOTO_URI = "FullPhotoUri";

    //Constants for MealPhoto.java
    public static final String MP_TAG = "MealPhoto.Java";
    public static final int LOCATION_PERMITTED = 0;
    public static final int SHOW_EDIT_TEXT = 0;
    public static final int HIDE_EDIT_TEXT = 1;

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

    //Constants for FetchAddressIntentService.java
    public static final int SUCCESS_RESULT = 0;
    public static final int FAILURE_RESULT = 1;
    public static final int NUMBER_OF_ADDRESSES_TO_RETRIEVE = 5;
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

    final static int NUMBER_OF_POSTS_TO_RETRIEVE = 20;//Todo: implement 'Pull Down' 'refresh' and 'add-more'
    final static int NUMBER_OF_COMMENTS_TO_RETRIEVE = 10;
    final static String VEGAN_PHILOSOPHY_NODE = "VeganPhilosophy";
    final static String COMMENTS_NODE = "comments";
    final static String COMMENTS_COUNT_NODE = "commentsCount";
    final static String POSTS_NODE = "posts";
    final static String LAST_POSTS_NODE = "lastPosts";
    final static boolean INCREASE_BY_ONE = true ;
    final static String DATE_STAMP_KEY_NAME = "datestamp";
    public final static boolean DO_NOT_INCREASE = false;
    public final static String PROFILE_NODE = "profile";
    public final static String DASHBOARD_NODE = "dashboard";
    public final static String DEFAULT_VEGAN_DATE = "start";

    //Constants for PlacardsRecyclerViewAdapter.java
    public static final String PRVA_TAG = "PlacardsViewAdapter";
    public static final String CURRENT_USER = "currentUserPost";
    public static final String HEART_FULL = "heart full";
    public static final String HEART_EMPTY = "heart empty";

    public static final String NO_LIKES = " No likes";
    public static final String ONE_LIKE = " 1 like";

    public static final String NO_COMMENT = " No comment";
    public static final String ONE_COMMENT = " 1 comment";

    //Constants for PinterestLoginActivity.java
    public final static String PLA_TAG = "PinterestLoginActivity:";
    public final static int PINTEREST_LOGIN_SUCCESS = 3333;
    public final static String PINTEREST_KEY = "Pinterest_key";

    //Constants for SocialLoginsActivity.java;
    public final static int PINTEREST_LOGIN = 333 ;
    public final static String SLA_TAG = "SocialLoginsActivity:";
    public final static String VEGAN_BUDDY_PIN_BOARD = "VeganBuddy";

    //Constants for DataRefresActivity.java
    public static final int MAX_WAIT_TIME = 5; //equivalent to 5 seconds

    //Constants for LandingPageFragment.java
    public final static String LPF_TAG = "LandingPageFragment:";

    //Constants for MyFirebaseInstanceIDService.java
    public static final String MFIIS_TAG = "Firebase ID Service";

    //Constants for MyFirebaseMessagingService.java
    public static final String MFMS_TAG = "Firebase Msging Svc";

}
