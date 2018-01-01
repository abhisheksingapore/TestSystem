package me.veganbuddy.veganbuddy.util;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import me.veganbuddy.veganbuddy.actors.AppMessageForTheDay;
import me.veganbuddy.veganbuddy.actors.Buddy;
import me.veganbuddy.veganbuddy.actors.Comments;
import me.veganbuddy.veganbuddy.actors.FoodWisdom;
import me.veganbuddy.veganbuddy.actors.MyPlace;
import me.veganbuddy.veganbuddy.actors.Post;

import static me.veganbuddy.veganbuddy.ui.CommentsActivity.placardCommentsRecyclerViewAdapter;
import static me.veganbuddy.veganbuddy.util.Constants.COMMENTS_COUNT_NODE;
import static me.veganbuddy.veganbuddy.util.Constants.COMMENTS_NODE;
import static me.veganbuddy.veganbuddy.util.Constants.DASHBOARD_NODE;
import static me.veganbuddy.veganbuddy.util.Constants.DEFAULT_STATS_PIC_NAME;
import static me.veganbuddy.veganbuddy.util.Constants.FB_ERROR_TAG;
import static me.veganbuddy.veganbuddy.util.Constants.FIRST_PIC_NAME;
import static me.veganbuddy.veganbuddy.util.Constants.FOLLOWERS_NODE;
import static me.veganbuddy.veganbuddy.util.Constants.FOLLOWING_NODE;
import static me.veganbuddy.veganbuddy.util.Constants.FOOD_WISDOM_NODE;
import static me.veganbuddy.veganbuddy.util.Constants.FOOD_WISDOM_THRESHOLD_NODE;
import static me.veganbuddy.veganbuddy.util.Constants.INCREASE_BY_ONE;
import static me.veganbuddy.veganbuddy.util.Constants.LAST_POSTS_NODE;
import static me.veganbuddy.veganbuddy.util.Constants.LIKES_COUNT_NODE;
import static me.veganbuddy.veganbuddy.util.Constants.MYPLACES_NODE;
import static me.veganbuddy.veganbuddy.util.Constants.MY_FOOD_WISDOM_CREATED_NODE;
import static me.veganbuddy.veganbuddy.util.Constants.MY_FOOD_WISDOM_NODE;
import static me.veganbuddy.veganbuddy.util.Constants.MY_LIKES_NODE;
import static me.veganbuddy.veganbuddy.util.Constants.NUMBER_OF_COMMENTS_TO_RETRIEVE;
import static me.veganbuddy.veganbuddy.util.Constants.POSTS_NODE;
import static me.veganbuddy.veganbuddy.util.Constants.POST_FAN_NODE;
import static me.veganbuddy.veganbuddy.util.Constants.PROFILE_NODE;
import static me.veganbuddy.veganbuddy.util.Constants.STATISTICS_IMAGES_FOLDER;
import static me.veganbuddy.veganbuddy.util.Constants.SUPPORTED_FILE_EXTENSION;
import static me.veganbuddy.veganbuddy.util.Constants.VEGAN_PHILOSOPHY_NODE;
import static me.veganbuddy.veganbuddy.util.DateAndTimeUtils.dateTimeStamp;
import static me.veganbuddy.veganbuddy.util.GlobalVariables.myDashboard;
import static me.veganbuddy.veganbuddy.util.GlobalVariables.thisAppUser;
/**
 * Created by abhishek on 1/9/17.
 */

public class FirebaseStorageUtils {

    public static List<Comments> commentsList;
    public static FirebaseAuth mFirebaseAuth;
    public static boolean dashboardDataUpdated;
    private static FirebaseDatabase mDatabase;
    private static StorageReference mStorageRef; //Reference for root reference to storage location
    private static StorageReference picStorage;
    private static DatabaseReference myRef; //Reference for nodes in the database
    private static String appMessage;
    private static String nextPicName;
    private static int foodWisdomThreshold = -939;
    private static long numberOfComments = -99; //default value
    private static String commentNodeID = "default"; //default value

    //This sets reference to user specific nodes in the database
    private static void setProperDatabaseReference(){
        String username = thisAppUser.getFireBaseID();
        mDatabase = FirebaseDatabase.getInstance();
        myRef = mDatabase.getReference(username);
    }

    //This sets reference to the global database and storage parameters
    private static void setPicsFirebaseReference() {
        mDatabase = FirebaseDatabase.getInstance();
        myRef = mDatabase.getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();
    }

    //This sets reference to the global database and storage parameters
    private static void setLikesFirebaseReference() {
        mDatabase = FirebaseDatabase.getInstance();
        myRef = mDatabase.getReference();
    }

        //This sets reference to to lastPosts node in the database
    private static void setLastPostsFirebaseReference() {
        mDatabase = FirebaseDatabase.getInstance();
        myRef = mDatabase.getReference().child(LAST_POSTS_NODE);
    }

    //This sets reference to myPlaces node in the database
    private static void setMyPlacesFirebaseReference() {
        mDatabase = FirebaseDatabase.getInstance();
        myRef = mDatabase.getReference().child(thisAppUser.getFireBaseID()).child(MYPLACES_NODE);
    }

    //This sets reference to to Buddy node that thisAppUser is following
    private static void setFollowersFirebaseReference(String leader) {
        mDatabase = FirebaseDatabase.getInstance();
        myRef = mDatabase.getReference().child(leader);
    }

    //This sets reference to Food Wisdom node in the database
    private static void setFoodWisdomFirebaseReference() {
        mDatabase = FirebaseDatabase.getInstance();
        myRef = mDatabase.getReference().child(FOOD_WISDOM_NODE);
    }


    public static void addNodesToDatabase(String photoURL, String thumbnailURL,
                                          String screenshotURL, String userInputText,
                                          String photoLocation, MyPlace myPlace,
                                          boolean isSharedOnFaceBook, boolean isSharedOnTwitter,
                                          boolean isSharedOnPinterest) {

        setProperDatabaseReference();
        //Methods to save relevant Data into the Firebase Database
        Post thisPost = setPostsData(photoURL, thumbnailURL, screenshotURL, userInputText,
                photoLocation, isSharedOnFaceBook, isSharedOnTwitter, isSharedOnPinterest);
        setUserData();
        setDashboardData(INCREASE_BY_ONE); //Trigger Dashboard data change at last
        setMyPlacesData(myPlace);
    }

    public static void setMyPlacesData(MyPlace myPlace) {
        setMyPlacesFirebaseReference();
        myRef.child(myPlace.getPlaceId()).setValue(myPlace);
    }

    public static void setDashboardData(boolean increase) {
        setProperDatabaseReference(); //This sets reference to user specific nodes in the database
        dashboardDataUpdated = false;
        if (increase) {
            myDashboard = myDashboard.incrementDashboardByOne();
        }
        myRef.child(DASHBOARD_NODE).setValue(myDashboard);
    }

    //Method to save the photoURL and text input by user for that particular meal
    private static Post setPostsData(String photoURL, String thumbnailURL, String screenshotURL,
                                     String userInputText, String photoLocation,
                                     boolean isSharedOnFacebook, boolean isSharedOnTwitter,
                                     boolean isSharedOnPinterest) {
        String dateTime = dateTimeStamp();
        setProperDatabaseReference(); //This sets reference to user specific nodes in the database
        Post newPost = new Post(
                thisAppUser.getPhotoUrl(),
                thisAppUser.getUserName(),
                photoURL,
                dateTime,
                thumbnailURL,
                screenshotURL,
                userInputText,
                photoLocation,
                0,
                false,
                false,
                0,
                isSharedOnFacebook, isSharedOnTwitter, isSharedOnPinterest);
        thisAppUser.setLastPostID(newPost.getDatestamp());
        myRef.child(POSTS_NODE).child(dateTime).setValue(newPost);

        //Maintain a list of last posts for display to all the other users of the app
        setLastPostsFirebaseReference();
        myRef.child(thisAppUser.getFireBaseID()).setValue(newPost);
        //Delete myFans of last posts of thisAppUser
        myRef.child(POST_FAN_NODE).child(thisAppUser.getFireBaseID()).removeValue();

        return newPost;
    }

    //Method to store some specific Data about the user profile in the Firebase Database
    public static void setUserData() {
        setProperDatabaseReference(); //This sets reference to user specific nodes in the database
        myRef.child(PROFILE_NODE).setValue(thisAppUser);
    }

    //Method to update the "myFans" nodes and "likesCount" of a lastPost of other users, including
    // thisAppUser
    public static void addToMyFans(String postID, int newLikeCount, String dateStampID,
                                   String postType) {
        setLikesFirebaseReference(); //To update both "posts" and the "lastPosts" node
        String userFirebaseID = thisAppUser.getFireBaseID();
        Buddy buddy = makeMeAFan();

        switch (postType) {
            case POSTS_NODE:
                //if the user is liking his own post in MY POSTS fragment then Update the like count
                // of the Original Post itself
                myRef.child(userFirebaseID).child(POSTS_NODE).child(postID).child(LIKES_COUNT_NODE)
                        .setValue(newLikeCount);

                //And add thisAppUser as a fan of of his own Post
                myRef.child(userFirebaseID).child(POSTS_NODE).child(POST_FAN_NODE).child(postID)
                        .child(userFirebaseID).setValue(buddy);

                //check if this is the "lastPost" of this user. If yes, also add thisAppuser as
                //"myFans" of "lastPost"
                if (thisAppUser.getLastPostID().equals(postID)) { //postId of lastPost is the firebaseID
                    // of thisAppUser and not the postID. So have to set 'child' accordingly
                    myRef.child(LAST_POSTS_NODE).child(POST_FAN_NODE).child(userFirebaseID)
                            .child(userFirebaseID).setValue(buddy);
                    //And update the likeCount
                    myRef.child(LAST_POSTS_NODE).child(userFirebaseID).child(LIKES_COUNT_NODE)
                            .setValue(newLikeCount);
                }
                //Finally, add this post to "myLikes' for thisAppUser
                setMyLike(userFirebaseID, dateStampID);
                break;
            case LAST_POSTS_NODE:
                //if the user is liking a post, update the myFans node of the Original Post
                // as well as the lastPost along with both their "likeCount"s.
                myRef.child(LAST_POSTS_NODE).child(POST_FAN_NODE).child(postID).child(userFirebaseID)
                        .setValue(buddy);
                myRef.child(LAST_POSTS_NODE).child(postID).child(LIKES_COUNT_NODE)
                        .setValue(newLikeCount);
                myRef.child(postID).child(POSTS_NODE).child(dateStampID).child(LIKES_COUNT_NODE)
                        .setValue(newLikeCount);
                myRef.child(postID).child(POSTS_NODE).child(POST_FAN_NODE).child(dateStampID).child(userFirebaseID)
                        .setValue(buddy);

                //Finally, add this post to "myLikes' for thisAppUser
                setMyLike(postID, dateStampID);
                break;
        }

    }

    private static Buddy makeMeAFan() {
        return new Buddy(thisAppUser.getUserName(), thisAppUser.getPhotoUrl());
    }


    public static void deleteFromMyFans(String postID, int newLikeCount,String dateStampID,
                                        String postType) {

        setLikesFirebaseReference(); //To update both "posts" and the "lastPosts" node
        String userFirebaseID = thisAppUser.getFireBaseID();

        switch (postType) {
            case POSTS_NODE:
                //Update the likesCount of the Original Post itself
                myRef.child(userFirebaseID).child(POSTS_NODE).child(dateStampID)
                        .child(LIKES_COUNT_NODE).setValue(newLikeCount);
                //Update the myFans node of the Original Post itself
                myRef.child(userFirebaseID).child(POSTS_NODE).child(POST_FAN_NODE).child(dateStampID)
                        .child(thisAppUser.getFireBaseID()).removeValue();

                //Additionally, check if this is also the lastPost of this user
                // if yes, then reduce myFan for lastPost and update the LikesCount of the lastPost
                if (postID.equals(thisAppUser.getLastPostID())){
                    //Update the like count of the lastPosts node
                    myRef.child(LAST_POSTS_NODE).child(userFirebaseID).child(LIKES_COUNT_NODE)
                            .setValue(newLikeCount);
                    //Update the myFans node of the lastPosts as well
                    myRef.child(LAST_POSTS_NODE).child(POST_FAN_NODE).child(userFirebaseID)
                            .child(thisAppUser.getFireBaseID()).removeValue();
                }

                //Finally, remove this post from "myLikes' for thisAppUser
                deleteMyLike(userFirebaseID, dateStampID);
                break;

            case LAST_POSTS_NODE:
                //Update the like count of the lastPosts node as well as the original
                myRef.child(LAST_POSTS_NODE).child(postID).child(LIKES_COUNT_NODE)
                        .setValue(newLikeCount);
                myRef.child(postID).child(POSTS_NODE).child(dateStampID).child(LIKES_COUNT_NODE)
                        .setValue(newLikeCount);
                //Update the myFans node of the lastPosts as well as the original
                myRef.child(LAST_POSTS_NODE).child(POST_FAN_NODE).child(postID)
                        .child(thisAppUser.getFireBaseID()).removeValue();
                myRef.child(postID).child(POSTS_NODE).child(POST_FAN_NODE).child(dateStampID)
                        .child(thisAppUser.getFireBaseID()).removeValue();

                //Finally, remove this post from "myLikes' for thisAppUser
                deleteMyLike(postID, dateStampID);
                break;
        }

    }

    private static void setMyLike(String creatorID, String postDateStampID){
        setProperDatabaseReference();
        Map <String, Object> mylike = new HashMap<>();
        String value = postDateStampID + "createdAt:" + dateTimeStamp();
        mylike.put(creatorID + postDateStampID, value);
        myRef.child(MY_LIKES_NODE).updateChildren(mylike);
    }

    private static void deleteMyLike(String creatorID, String postDateStampID) {
        setProperDatabaseReference();
        Map <String, Object> mylike = new HashMap<>();
        mylike.put(creatorID+postDateStampID, null);
        myRef.child(MY_LIKES_NODE).updateChildren(mylike);
    }

    public static void retrieveCommentsData(String nodeID) {
        setProperDatabaseReference();//This sets reference to user specific nodes in the database
        Query recentComments = myRef.child(COMMENTS_NODE).child(nodeID).limitToLast(NUMBER_OF_COMMENTS_TO_RETRIEVE);
        recentComments.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                numberOfComments = dataSnapshot.getChildrenCount();
                createArrayFromComments(dataSnapshot);

                //update corresponding Posts Node with number of "comments"
                if (!commentNodeID.equals("default")) {
                    updateCommentsCountforPost(commentNodeID, numberOfComments, true);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.v(FB_ERROR_TAG, "Data processing cancelled in retrieveCommentsData");
            }
        });
    }

    private static void createArrayFromComments(DataSnapshot dataSnapshot) {
        commentsList = new ArrayList<>();

        for (DataSnapshot singleSnapShot: dataSnapshot.getChildren()) {
            Comments oneComment= singleSnapShot.getValue(Comments.class);
            commentsList.add(oneComment);
        }

        if (placardCommentsRecyclerViewAdapter  != null) {
            placardCommentsRecyclerViewAdapter.setListofComments();
            placardCommentsRecyclerViewAdapter.notifyDataSetChanged();
        }
    }

    //Method to add comments to a given post
    public static void uploadPostCommentsToDatabase(String nodeID, String authorUri,
                                        String commentText, String replyAuthor, String replyText){
        setProperDatabaseReference();//This sets reference to user specific nodes in the database
        Comments newComment = new Comments (nodeID, authorUri, commentText, replyAuthor,replyText);
        myRef.child(COMMENTS_NODE).child(nodeID).push().setValue(newComment);
        commentNodeID = nodeID; //Set the flag that a new comment has been inserted
    }

    private static void updateCommentsCountforPost(String nodeID, long numberOfComments,
                                                   boolean addComment) {
        int commentCount = (int) numberOfComments;
        setProperDatabaseReference(); //This sets reference to user specific nodes in the database
        myRef.child(POSTS_NODE).child(nodeID).child(COMMENTS_COUNT_NODE).setValue(numberOfComments);

        if (commentCount>0) myRef.child(POSTS_NODE).child(nodeID).child("hasComments").setValue(true);
        if (commentCount==0) myRef.child(POSTS_NODE).child(nodeID).child("hasComments").setValue(false);

        //restore default values to global variables
        commentNodeID = "default";
        numberOfComments = -99;
    }

    public static StorageReference getStatsPicReference(String picFileName) {
        if (picFileName.length() < 2) picFileName = FIRST_PIC_NAME;
        setPicsFirebaseReference();//This sets reference to the global database and storage parameters
        picStorage = mStorageRef.child(STATISTICS_IMAGES_FOLDER + picFileName + SUPPORTED_FILE_EXTENSION);
        return picStorage;
    }


    //This method finds the most applicable key for the message and sets the picName to that key as well
    public static void retrieveMessageForTheDay(String picName) {
        setPicsFirebaseReference();//This sets reference to the global database and storage parameters
        Query query = myRef.child(VEGAN_PHILOSOPHY_NODE).child(picName);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                AppMessageForTheDay appMessageForTheDay = dataSnapshot.getValue(AppMessageForTheDay.class);
                try {
                    appMessage = appMessageForTheDay.getPicMessage();
                } catch (NullPointerException NPE) {
                    Log.v(FB_ERROR_TAG, "choosePicFileName: " +
                            "Error retrieving \"message for the day from database");
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.v(FB_ERROR_TAG, "Data processing cancelled in retrieve \"message for the day");
            }
        });
    }


    public static String retrieveApplicablePicName() {
        String picName = myDashboard.getLastPicName();
        //First check if it is the first update EVER for this user
        int defaultPicInt = Integer.parseInt(DEFAULT_STATS_PIC_NAME);
        int picNameInt = Integer.parseInt(picName);

        //This condition will be true when the user is using the app for the first time
        // and has no prior data
        if (picNameInt >= defaultPicInt) {
            setNextPicName(FIRST_PIC_NAME);
        } else {
            //If it is not the first update then picName should be updated to a new name on date change
            // but keep the same for the ongoing day
            setNextPicFileName(picNameInt);
        }
        return getNextPicName();
    }

    public static String getAppMessage() {
        return appMessage;
    }

    public static String getNextPicName() {
        return nextPicName;
    }

    public static void setNextPicName(String nextPic) {
        nextPicName = nextPic;
    }

    private static void setNextPicFileName(int oldFileNameInt ) {
        int newFileNameInt;
        String fileName;
        if (myDashboard.getMealsForToday()==0) {
            newFileNameInt = oldFileNameInt + 1; //increment the filename if this is first update for the day
        } else {
            newFileNameInt = oldFileNameInt;//keep it to the same filename if there are previous updates for the day
        }
        fileName = String.format(Locale.ENGLISH,"%05d", newFileNameInt);
        setNextPicName(fileName);
    }

    public static void addMeFollowingData(Map<String, Buddy> meFollowingUser) {
        setProperDatabaseReference();
        myRef.child(FOLLOWING_NODE).setValue(meFollowingUser);
        setUserData();
    }

    public static void addMeAsFollowerData(String leader, Map<String, Buddy> follower) {
        setFollowersFirebaseReference(leader);
        myRef.child(FOLLOWERS_NODE).setValue(follower);
    }

    public static void deleteMeFollowingData(String meFollowingUser) {
        setProperDatabaseReference();
        myRef.child(FOLLOWING_NODE).child(meFollowingUser).removeValue();
        setUserData();
    }

    public static void deleteMeAsFollowerData(String leader, String  meAsFollower) {
        setFollowersFirebaseReference(leader);
        myRef.child(FOLLOWERS_NODE).child(meAsFollower).removeValue();
    }

    public static void retrieveFoodWisdomThreshold() {
        setFoodWisdomFirebaseReference();
        myRef.child(FOOD_WISDOM_THRESHOLD_NODE)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Integer thresholdCurrentValue = dataSnapshot.getValue(Integer.class);
                    setFoodWisdomThreshold(thresholdCurrentValue);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(FB_ERROR_TAG, "Data processing cancelled in addMeAsFollower");
            }
        });
    }

    @org.jetbrains.annotations.Contract(pure = true)
    public static int getFoodWisdomThreshold() {
        return foodWisdomThreshold;
    }

    private static void setFoodWisdomThreshold(Integer value) {
        foodWisdomThreshold = value;
    }

    public static void createNewFoodWisdomForThisAppUser() {
        //increment the foodWisdomKey
        int lastFoodWisdomKey = thisAppUser.getLastFoodWisdom();

        int newMyFoodWisdomKey = 1;
        if (lastFoodWisdomKey > 0) newMyFoodWisdomKey = lastFoodWisdomKey + 1;

        //retrieve the new one
        setFoodWisdomFirebaseReference();
        myRef.child(Integer.toString(newMyFoodWisdomKey))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            FoodWisdom myWisdom = dataSnapshot.getValue(FoodWisdom.class);
                            String key_node = dataSnapshot.getKey();

                            setProperDatabaseReference();
                            //add to the myFoodWisdom node for thisAppUser
                            myRef.child(MY_FOOD_WISDOM_NODE).child(key_node).setValue(myWisdom);

                            //update thisAppUser lastFoodWisdom flag
                            thisAppUser.setLastFoodWisdom(Integer.parseInt(key_node));
                            //Reset the counter of foodWisdom for thisAppuser to 0
                            thisAppUser.setFoodWisdomCounter(0);
                            //update the thisAppUser data
                            setUserData();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }

    public static void setFoodWisdomData(FoodWisdom foodWisdom, String key) {
        setFoodWisdomFirebaseReference();
        myRef.child(key).setValue(foodWisdom);
    }

    public static void setFoodWisdomDataForThisUser(FoodWisdom foodWisdom, String key) {
        setProperDatabaseReference();
        //add to the myFoodWisdomCreated node for thisAppUser
        myRef.child(MY_FOOD_WISDOM_CREATED_NODE).child(key).setValue(foodWisdom);
    }

}
