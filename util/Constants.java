package me.veganbuddy.veganbuddy.util;

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
    public static final String HEART_FULL = "heart full";
    public static final String HEART_EMPTY = "heart empty";
    public static final String NO_LIKES = " No likes";
    public static final String ONE_LIKE = " 1 like";
    public static final String NO_COMMENT = " No comment";
    public static final String ONE_COMMENT = " 1 comment";
    //Constants for Landingpagefragment.Java
    //Static variables to decide which Fragment Layout is to be loaded
    public static final int ANIMALS_DASHBOARD_LAYOUT = 0;
    //Todo: TBD public static final int VEGAN_DASHBOARD_LAYOUT = 1;
    public static final int LAST_PLACARDS_LAYOUT = 1;
    public static final int MY_PLACARDS_LAYOUT = 2;
    public static final int NOTIFICATIONS_INBOUND_LAYOUT = 3;
    public static final int NOTIFICATIONS_OUTBOUND_LAYOUT = 4;
    //Constants for Landingpage.Java and FullSizePhoto.java
    public static final String FULL_PHOTO_URI = "FullPhotoUri";
    //Constants for MealPreviewPhoto.Java
    public static final String MP_CLASS_TAG = "MealPreviewPhoto";
    public static final String MP_ASYNC_CLASS_TAG = "UploadPhotoAnd..";
    public static final String FIREBASE_FULL_IMAGE_FOLDER = "/fullSize/";
    public static final String FIREBASE_THUMBNAIL_FOLDER = "/thumbnail/";
    public static final String FIREBASE_SCREENSHOT_FOLDER = "/Screenshot/";
    public static final String MYPLACE_NAME = "myPlaceName";
    public static final String MYPLACE_ADDRESS = "myPlaceAdd";
    public static final String MYPLACE_ID = "myPlaceID";
    //Constants for both MealPhoto.Java and MealPreviewPhoto.Java
    public static final String VEGANPHILOSOPHY_TEXT = "#myVegan";
    //Constants for FetchPlacesIntentService.java
    public static final String FPIS_TAG = "FetchAddressIntent";
    public final static String VCOINS_NODE = "vCoins";
    public final static String POSTS_NODE = "posts";
    public final static String LAST_POSTS_NODE = "lastPosts";
    public final static String V_NOTIFICATIONS_NODE = "vNotifications";
    public final static String MYPLACES_NODE = "myPlaces";
    public final static String PROFILE_NODE = "profile";
    public final static String DASHBOARD_NODE = "dashboard";
    public static final String POST_FAN_NODE = "myPostFans";
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
    // Constants for Vnotification.java. These values are used in Firebase Functions
    public static final String INBOUND = "INBOUND";
    public static final String OUTBOUND = "OUTBOUND";
    public static final String VN_UPLOADED_NEW_MEAL_PHOTO = "NEWPOST";
    public static final String VN_LIKED_PHOTO = "LIKE";
    public static final String VN_COMMENT_PHOTO = "COMMENT";
    public static final String VN_DIRECT_MESSAGE = "MESSAGE";
    public static final String VN_PHOTO_SHARE = "SHARE";
    public static final String VN_FOLLOW = "FOLLOW";
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
    public static final double SHARE_FACTOR = 2;
    //Constants for PlacardsFragment.java
    public static final String PF_TAG = "PlacardsFragment.java";
    public final static int NUMBER_OF_POSTS_TO_RETRIEVE = 20;//Todo: implement 'Pull Down' 'refresh' and 'add-more'
    public final static String DATE_STAMP_KEY_NAME = "datestamp";
    //Constants for CameraActivity.java
    public static final String CA_TAG = "CameraActivity";
    public static final int PERMISSIONS = 939;
    public static final String VEGAN_BUDDY_FOLDER = "VeganBuddy";
    public static final String FLASH_SETTING = "Flash Setting";
    public static final String FLASH_ON_MSG = "flashOn" ;
    public static final String FLASH_OFF_MSG = "flashOff";
    public static final String SHUTTER_SOUND_SETTING = "Sound Setting";
    public static final String SHUTTER_SOUND_ON_MSG = "SoundOn" ;
    public static final String SHUTTER_SOUND_OFF_MSG = "SoundOff";
    public static final int HIGH_QUALITY = 100;
    public static final int MED_QUALITY = 90;
    public static final int LOW_QUALITY = 50;
    public static final int SHOW_CAMERA = 0;
    public static final int STOP_CLICK = 1;
    //Constants for Buddy.java
    public static final String PFan_TAG = "Buddy.java";
    public static final String LIKED = "LIKED";
    //Constants for PostLikesActivity.java
    public static final String Plikes_TAG = "PostLikesActivity.java";
    public static final String POSTID = "postID";
    public static final String ORIGIN_FRAGMENT = "originFragment";
    public static final String FOLLOWING_NODE = "meFollowing";
    public static final String FOLLOWERS_NODE = "myFollowers";
    //Constants for FollowActivity.java
    public static final int FOLLOWING = 1;
    public static final int FOLLOWERS = 0;
    public static final String FOLLOWERS_COUNT_NODE = "myFollowersCount";
    public static final String RELATION = "Relation";
    public static final String FA_TAG = "FollowActivity";
    //Constants for FoodWisdom
    public static final String FOOD_WISDOM_NODE = "foodWisdom";
    public static final String MY_FOOD_WISDOM_NODE = "myFoodWisdom";
    public static final String MY_FOOD_WISDOM_CREATED_NODE = "myFoodWisdomCreated";
    public static final String FOOD_WISDOM_THRESHOLD_NODE = "threshold/currentValue";
    public static final String FOOD_WISDOM_USER_FOLDER = "foodwisdom/user_contributions/";
    public static final String ALL_FW = "Others Food Wisdom";
    public static final String MY_FW = "My Food Wisdom";
    public static final int MY_FOODWISDOM = 0;
    public static final int ALL_FOODWISDOM = 1;
    public static final String MY_FOODWISDOM_TAG = "MYFOODWISOME";
    public static final String ALL_FOODWISDOM_TAG = "ALLFOODWISDOM";
    //Constants for Benefits Activity
    public static final String BA_TAG = "BenefitsActivity";
    public static final int SUMMARY_DASHBOARD = 1;
    public static final int ANIMALS_DASHBOARD = 2;
    public static final int MEAL_MATHS_DASHBOARD = 3;
    public static final int ENVIRONMENT_DASHBOARD = 4;
    public static final int HEALTH_DASHBOARD = 5;
    public static final int HUNGER_DASHBOARD = 6;
    public static final String SUMMARY_TITLE = "Summary";
    public static final String ANIMALS_TITLE = "Animals Saved";
    public static final String MEALS_TITLE = "My Meals";
    public static final String ENVIRONMENT_TITLE = "Environment";
    public static final String HEALTH_TITLE = "My health";
    public static final String HUNGER_TITLE = "World Hunger";
    public static final int NUMBER_OF_DAYS_TO_PLOT = 7;
    public static final int NUMBER_OF_WEEKS_TO_PLOT = 7;
    public static final int DAILY_GRAPH = 0;
    public static final int WEEKLY_GRAPH = 1;
    //constants for BountyActivity.java
    public static final int VCOINS_ACCOUNTS = 1;
    public static final int BOUNTY_TARGETS = 2;
    public static final int BOUNTY_LEADERBOARD = 3;
    public static final String BOUNTY_TAG = "BountyActivity";
    //constants for Messages.java
    public static final int FRAG_CHATS = 1;
    public static final int FRAG_CONTACTS = 2;
    public static final String M_TAG = "Messages.java";
    public static final String CHAT_BUDDY_NAME = "ChatBuddyName";
    public static final String CHAT_BUDDY_ID = "ChatBuddyID";
    public static final String CHAT_BUDDY_PIC = "ChatBuddyPic";
    //constants for ChatActivity and ChatRecyclerViewAdapter.java
    public static final int MESSAGE_SENT = 1;
    public static final int MESSAGE_RECEIVED = 2;
    public static final String LAST_CHAT_MESSAGES_NODE = "myLastChatMessages";
    public static final String BUDDY_NAME = "buddyName";
    public static final String BUDDY_URL = "buddyurl";
    public static final String BUDDY_UNREAD = "unRead";
    public static final String CHAT_MESSAGES_NODE = "myChatMessages";
    public static final String CHAT_TAG = "ChatActivity.java";
    public static final String IS_TYPING = "...is typing";
    //constants for UserProfile.java
    public static final String UP_TAG = "UserProfile.Java";
    public static final String GOOGLE_PHOTOS_PACKAGE_NAME = "com.google.android.apps.photos";
    public static final int REQUEST_PHOTO_NEW = 20;
    public static final int REQUEST_PHOTO_FROM_GOOGLE_PHOTOS = 23;
    public static final int REQUEST_PHOTO_FROM_GALLERY = 24;
    public static final int REQUEST_PHOTO_FROM_CAMERA = 25;
    public static final String PROFILE_PICTURE_CAMERA = "profilePictureCamera";
    public static final String FIREBASE_USER_PROFILE_PIC_FOLDER = "/userProfilePics/";
    public static final int PROFILE_PIC_WIDTH = 350;
    public static final int PROFILE_PIC_HEIGHT = 350;
    public static final String SCHEME_LOCAL_FILE = "file";
    //constants for PicassoGSRequestHandler
    public static final String SCHEME_FIREBASE_STORAGE = "gs";
    //constants for BuddyProfile.java
    public static final String BUDDY_FIREBASE_ID = "firebaseID";
    //Constants for SocialMediaUtils.java
    static final String SMU_TAG = "SocialMediaUtils.Java";
    static final String VEGAN_BUDDY_WEBSITE = "http://www.veganbuddy.me/";
    static final String VEGAN_BUDDY_HASHTAG = "#everymealmatters ";
    //Constants for FirebaseStorageUtils.java and Firebase Database calls
    final static String FB_ERROR_TAG = "FIREBASESTORAGEUTILS";
    final static String STATISTICS_IMAGES_FOLDER = "/animal_statistics/";
    final static String SUPPORTED_FILE_EXTENSION = ".png";
    final static int NUMBER_OF_COMMENTS_TO_RETRIEVE = 10;
    final static String VEGAN_PHILOSOPHY_NODE = "VeganPhilosophy";
    final static boolean INCREASE_BY_ONE = true;
    final static String COMMENTS_NODE = "comments";
    final static String COMMENTS_COUNT_NODE = "commentsCount";
    final static String MY_LIKES_NODE = "myLikes";
    final static String LIKES_COUNT_NODE = "likesCount";
    public static String DISPLAY_TIME_FORMAT = "HH:mm";
    public static String DISPLAY_DATE_FORMAT = "yyyy-MMM-dd";


}
