package me.veganbuddy.veganbuddy.util;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;
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
import me.veganbuddy.veganbuddy.actors.Comments;
import me.veganbuddy.veganbuddy.actors.Post;
import me.veganbuddy.veganbuddy.actors.Vcoins;
import me.veganbuddy.veganbuddy.actors.Vnotification;


import static me.veganbuddy.veganbuddy.ui.CommentsActivity.placardCommentsRecyclerViewAdapter;
import static me.veganbuddy.veganbuddy.util.Constants.ACTIONED;
import static me.veganbuddy.veganbuddy.util.Constants.COMMENTS_COUNT_NODE;
import static me.veganbuddy.veganbuddy.util.Constants.COMMENTS_NODE;
import static me.veganbuddy.veganbuddy.util.Constants.DASHBOARD_NODE;
import static me.veganbuddy.veganbuddy.util.Constants.DEFAULT_STATS_PIC_NAME;
import static me.veganbuddy.veganbuddy.util.Constants.FB_ERROR_TAG;
import static me.veganbuddy.veganbuddy.util.Constants.FIRST_PIC_NAME;
import static me.veganbuddy.veganbuddy.util.Constants.INBOUND;
import static me.veganbuddy.veganbuddy.util.Constants.INCREASE_BY_ONE;
import static me.veganbuddy.veganbuddy.util.Constants.LAST_POSTS_NODE;
import static me.veganbuddy.veganbuddy.util.Constants.NOT_VIEWED_YET;
import static me.veganbuddy.veganbuddy.util.Constants.NUMBER_OF_COMMENTS_TO_RETRIEVE;
import static me.veganbuddy.veganbuddy.util.Constants.OUTBOUND;
import static me.veganbuddy.veganbuddy.util.Constants.POSTS_NODE;
import static me.veganbuddy.veganbuddy.util.Constants.PROFILE_NODE;
import static me.veganbuddy.veganbuddy.util.Constants.STATISTICS_IMAGES_FOLDER;
import static me.veganbuddy.veganbuddy.util.Constants.SUPPORTED_FILE_EXTENSION;
import static me.veganbuddy.veganbuddy.util.Constants.VEGAN_PHILOSOPHY_NODE;
import static me.veganbuddy.veganbuddy.util.Constants.VN_UPLOADED_NEW_MEAL_PHOTO;
import static me.veganbuddy.veganbuddy.util.Constants.V_NOTIFICATIONS_NODE;
import static me.veganbuddy.veganbuddy.util.GlobalVariables.myDashboard;
import static me.veganbuddy.veganbuddy.util.GlobalVariables.thisAppUser;
/**
 * Created by abhishek on 1/9/17.
 */

public class FirebaseStorageUtils {

    private static FirebaseDatabase mDatabase;
    private static StorageReference mStorageRef; //Reference for root reference to storage location
    private static StorageReference picStorage;
    private static DatabaseReference myRef; //Reference for nodes in the database
    private static String appMessage;
    private static String nextPicName;

    public static List<Comments> commentsList;
    public static FirebaseAuth mFirebaseAuth;
    public static boolean dashboardDataUpdated;


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

    //This sets reference to to Notifications node in the database
    private static void setNotificationsFirebaseReference() {
        mDatabase = FirebaseDatabase.getInstance();
        myRef = mDatabase.getReference().child(V_NOTIFICATIONS_NODE);
    }

    public static void addNodesToDatabase(String photoURL, String thumbnailURL,
                                          String screenshotURL, String userInputText,
                                          String photoLocation) {

        setProperDatabaseReference();
        //Methods to save relevant Data into the Firebase Database
        Post thisPost = setPostsData(photoURL, thumbnailURL, screenshotURL, userInputText, photoLocation);
        setUserData();
        setDashboardData(INCREASE_BY_ONE); //Trigger Dashboard data change at last
        setVnotificationData(VN_UPLOADED_NEW_MEAL_PHOTO, thisPost, BitmapUtils.getPhotoThumbnailURL(),
                thisAppUser.getFireBaseID(), thisAppUser.getUserName(), thisAppUser.getPhotoUrl(), thisAppUser.getFireBaseID());
    }

    private static void setVnotificationData(String vnotificationType, Post thisPost,
                                             String screenshotThumbURL, String senderID, String senderName,
                                             String senderIDpic, String recipientID) {
        setNotificationsFirebaseReference();
        Vcoins vcoins = new Vcoins(ACTIONED,VN_UPLOADED_NEW_MEAL_PHOTO);
        Vnotification vNotification = new Vnotification(vnotificationType, thisPost.getDatestamp(),
                NOT_VIEWED_YET, senderID, senderName, senderIDpic, screenshotThumbURL, vcoins.vCoinsEarnedString());

        //create the notifications in the Database FOR the given users (sender and recipient)
        // AT the given date_time_stamp
        myRef.child(senderID).child(OUTBOUND).child(thisPost.getDatestamp()).setValue(vNotification);

        //if Sender is different from the recipient, then create the "INBOUND" notification for
        // the recipient
        if (!senderID.equals(recipientID)) {
            myRef.child(recipientID).child(INBOUND).child(thisPost.getDatestamp()).setValue(vNotification);
        }
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
                                     String userInputText, String photoLocation) {
        String dateTime = DateAndTimeUtils.dateTimeStamp();
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
                null);
        thisAppUser.setLastPostID(newPost.getDatestamp());
        myRef.child(POSTS_NODE).child(dateTime).setValue(newPost);

        //Maintain a list of last posts for display to all the other users of the app
        setLastPostsFirebaseReference();
        myRef.child(thisAppUser.getFireBaseID()).setValue(newPost);
        return newPost;
    }

    //Method to store some specific Data about the user profile in the Firebase Database
    private static void setUserData() {
        setProperDatabaseReference(); //This sets reference to user specific nodes in the database
        myRef.child(PROFILE_NODE).setValue(thisAppUser);
    }

    //Method to the update the "iLike" flag and "likesCount" of a given post of the current user
    public static void updateIlike(String dateStampID, boolean newValue, int newLikeCount) {
        setProperDatabaseReference();
        myRef.child(POSTS_NODE).child(dateStampID).child("iLoveFlag").setValue(newValue);
        myRef.child(POSTS_NODE).child(dateStampID).child("likesCount").setValue(newLikeCount);

        //Check if this is the "LastPost" for this user. If yes, then update it as well and
        // also update the "mylikes" flag of thisAppUser
        if (dateStampID.equals(thisAppUser.getLastPostID())) {
            setLastPostsFirebaseReference(); //To update the AllPosts under the "LastPosts" node
            myRef.child(thisAppUser.getFireBaseID()).child("iLoveFlag").setValue(newValue);
            myRef.child(thisAppUser.getFireBaseID()).child("likesCount").setValue(newLikeCount);
            if (newValue) {
                thisAppUser.setMyLikes(thisAppUser.getFireBaseID(), dateStampID);
                setUserData();
            } else {
                //Remove this post from "myLikes' for thisAppUser
                thisAppUser.deleteMyLike(thisAppUser.getFireBaseID());
                setUserData();
            }
        }

    }

    //Method to the update the "iLike" flag and "likesCount" of a given post of ANY user
    public static void updateMyLikes(String dateStampID, boolean newValue, int newLikeCount,
                                     String userFirebaseID) {
        setLikesFirebaseReference(); //To update both "posts" and the "lastPosts" node

        //Update the like count of the Original Post itself
        myRef.child(userFirebaseID).child(POSTS_NODE).child(dateStampID).child("likesCount").setValue(newLikeCount);


        //if the user is not liking his own post, Update the likedMe node of the Original Post itself
        if (!userFirebaseID.equals(thisAppUser.getFireBaseID()))
        myRef.child(userFirebaseID).child(POSTS_NODE).child(dateStampID).child("likedMe")
                .setValue(addNewLikedMe(thisAppUser.getFireBaseID(), thisAppUser.getPhotoUrl()));
        //Else, if the user is liking his own post in the "LASTPosts" fragment, then update
        // the "iLoveFlag" for the Original post of thisAppUser
        else if (userFirebaseID.equals(thisAppUser.getFireBaseID())) {
            myRef.child(userFirebaseID).child(POSTS_NODE).child(dateStampID).child("iLoveFlag")
                    .setValue(newValue);
        }

        //Update the like count of the lastPosts node
        myRef.child(LAST_POSTS_NODE).child(userFirebaseID).child("likesCount").setValue(newLikeCount);


        //if the user is not liking his own post, Update the likedMe node of the lastPosts
        if (!userFirebaseID.equals(thisAppUser.getFireBaseID()))
        myRef.child(LAST_POSTS_NODE).child(userFirebaseID).child("likedMe")
                .setValue(addNewLikedMe(thisAppUser.getFireBaseID(),
                        thisAppUser.getPhotoUrl()));
        //However, if the user is liking his own post in the "ALL Posts" fragment, then update
            // the "iLoveFlag" for the lastPost of thisAppUser
        if (userFirebaseID.equals(thisAppUser.getFireBaseID())) {
            myRef.child(LAST_POSTS_NODE).child(userFirebaseID).child("iLoveFlag").setValue(newValue);
        }

        //Finally, add this post to "myLikes' for thisAppUser
        thisAppUser.setMyLikes(userFirebaseID, dateStampID);
        setUserData();
    }

    private static Map<String, String> addNewLikedMe(String fireBaseID, String photoUrl) {
        Map<String, String> map = new HashMap<>();
        map.put(fireBaseID, photoUrl);
        return map;
    }

    public static void deleteMyLikes(String dateStampID, boolean newValue, int newLikeCount,
                                     String userFirebaseID, Post post) {

        setLikesFirebaseReference(); //To update both "posts" and the "lastPosts" node

        //Update the likesCount of the Original Post itself
        myRef.child(userFirebaseID).child(POSTS_NODE).child(dateStampID).child("likesCount").setValue(newLikeCount);

        //Update the likedMe node of the Original Post itself
        myRef.child(userFirebaseID).child(POSTS_NODE).child(dateStampID).child("likedMe")
                .child(thisAppUser.getFireBaseID()).removeValue();

        //Additionally, if the user is liking his own post in the "ALL Posts" fragment, then update
        // the "iLoveFlag" for the Original post of thisAppUser AS well as in the lastPosts
        if (userFirebaseID.equals(thisAppUser.getFireBaseID())) {
            myRef.child(userFirebaseID).child(POSTS_NODE).child(dateStampID).child("iLoveFlag")
                    .setValue(newValue);
            myRef.child(LAST_POSTS_NODE).child(userFirebaseID).child("iLoveFlag").setValue(newValue);
        }

        //Update the like count of the lastPosts node
        myRef.child(LAST_POSTS_NODE).child(userFirebaseID).child("likesCount").setValue(newLikeCount);

        //Update the likedMe node of the lastPosts as well
        myRef.child(LAST_POSTS_NODE).child(userFirebaseID).child("likedMe")
                .child(thisAppUser.getFireBaseID()).removeValue();

        //Finally, remove this post from "myLikes' for thisAppUser
        thisAppUser.deleteMyLike(userFirebaseID);
        setUserData();

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

}
