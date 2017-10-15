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
import java.util.List;
import java.util.Locale;

import me.veganbuddy.veganbuddy.actors.AppMessageForTheDay;
import me.veganbuddy.veganbuddy.actors.Comments;
import me.veganbuddy.veganbuddy.actors.Post;


import static me.veganbuddy.veganbuddy.ui.LandingPageFragment.placardsRecyclerViewAdapter;
import static me.veganbuddy.veganbuddy.ui.CommentsActivity.placardCommentsRecyclerViewAdapter;
import static me.veganbuddy.veganbuddy.ui.LandingPageFragment.placardsRecyclerViewAdapterAllPosts;
import static me.veganbuddy.veganbuddy.util.Constants.COMMENTS_COUNT_NODE;
import static me.veganbuddy.veganbuddy.util.Constants.COMMENTS_NODE;
import static me.veganbuddy.veganbuddy.util.Constants.DASHBOARD_NODE;
import static me.veganbuddy.veganbuddy.util.Constants.DATE_STAMP_KEY_NAME;
import static me.veganbuddy.veganbuddy.util.Constants.DEFAULT_STATS_PIC_NAME;
import static me.veganbuddy.veganbuddy.util.Constants.FB_ERROR_TAG;
import static me.veganbuddy.veganbuddy.util.Constants.FIRST_PIC_NAME;
import static me.veganbuddy.veganbuddy.util.Constants.INCREASE_BY_ONE;
import static me.veganbuddy.veganbuddy.util.Constants.LAST_POSTS_NODE;
import static me.veganbuddy.veganbuddy.util.Constants.NUMBER_OF_COMMENTS_TO_RETRIEVE;
import static me.veganbuddy.veganbuddy.util.Constants.NUMBER_OF_POSTS_TO_RETRIEVE;
import static me.veganbuddy.veganbuddy.util.Constants.POSTS_NODE;
import static me.veganbuddy.veganbuddy.util.Constants.PROFILE_NODE;
import static me.veganbuddy.veganbuddy.util.Constants.STATISTICS_IMAGES_FOLDER;
import static me.veganbuddy.veganbuddy.util.Constants.SUPPORTED_FILE_EXTENSION;
import static me.veganbuddy.veganbuddy.util.Constants.VEGAN_PHILOSOPHY_NODE;
import static me.veganbuddy.veganbuddy.util.GlobalVariables.myDashboard;
import static me.veganbuddy.veganbuddy.util.GlobalVariables.thisAppUser;
/**
 * Created by abhishek on 1/9/17.
 */

public class FirebaseStorageUtils {

    static FirebaseDatabase mDatabase;
    static StorageReference mStorageRef; //Reference for root reference to storage location
    static StorageReference picStorage;
    static DatabaseReference myRef; //Reference for nodes in the database
    static String appMessage;
    static String nextPicName;

    private static DataSnapshot postDataSnapshot; //declare as local variable
    private static DataSnapshot allPostDataSnapshot; //declare as local variable
    public static List<Post> postList;
    public static List<Post> allUsersPostsList;
    public static List<String> userFirebaseIDs;
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

    //This sets reference to to lastPosts node in the database
    private static void setAllPostsFirebaseReference() {
        mDatabase = FirebaseDatabase.getInstance();
        myRef = mDatabase.getReference().child(LAST_POSTS_NODE);
    }

    public static void retrievePostsData() {
        setProperDatabaseReference(); //This sets reference to user specific nodes in the database
        Query recentPostsRef = myRef.child(POSTS_NODE).limitToLast(NUMBER_OF_POSTS_TO_RETRIEVE);
        recentPostsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {
                    postDataSnapshot = dataSnapshot;
                    createArrayFromFirebasePosts();
                }
                else {
                    Log.v(FB_ERROR_TAG, "No children found for POSTS. Creating a new node");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.v(FB_ERROR_TAG, "error found");
            }
        });
    }

    public static void retrieveAllPostsData() {
        setAllPostsFirebaseReference();
        Query recentPostsRef = myRef
                .limitToLast(NUMBER_OF_POSTS_TO_RETRIEVE)
                .orderByChild(DATE_STAMP_KEY_NAME);
        recentPostsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {
                    allPostDataSnapshot = dataSnapshot;
                    createArrayFromAllFirebasePosts();
                    Log.v(FB_ERROR_TAG, "Children found in retrieveAllPostsData...");
                }
                else {
                    Log.v(FB_ERROR_TAG, "No children found for POSTS in retrieveAllPostsData...");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.v(FB_ERROR_TAG, "error found");
            }
        });
    }


    //Method to the update the array of all posts for this particular user
    private static void createArrayFromFirebasePosts() {
        postList = new ArrayList<>();

        for (DataSnapshot singleSnapShot: postDataSnapshot.getChildren()) {
            Post thisPost = singleSnapShot.getValue(Post.class);
            postList.add(thisPost);
        }

        if (placardsRecyclerViewAdapter!=null) {
            placardsRecyclerViewAdapter.setListofPosts(postList);
            placardsRecyclerViewAdapter.notifyDataSetChanged();
        }
    }

    //Method to the update the array of last posts of all the users
    private static void createArrayFromAllFirebasePosts() {
        allUsersPostsList = new ArrayList<>();
        userFirebaseIDs = new ArrayList<>();

        for (DataSnapshot singleSnapShot: allPostDataSnapshot.getChildren()) {
            Post thisPost = singleSnapShot.getValue(Post.class);
            allUsersPostsList.add(thisPost);
            userFirebaseIDs.add(singleSnapShot.getKey());
        }

        if (placardsRecyclerViewAdapterAllPosts!=null) {
            placardsRecyclerViewAdapterAllPosts.setListofPosts(allUsersPostsList);
            placardsRecyclerViewAdapterAllPosts.notifyDataSetChanged();
        }
    }



    public static void addNodesToDatabase(String photoURL, String thumbnailURL,
                                          String screenshotURL, String userInputText,
                                          String photoLocation) {

        setProperDatabaseReference();
        //Methods to save relevant Data into the Firebase Database
        setPostsData(photoURL, thumbnailURL, screenshotURL, userInputText, photoLocation);
        setUserData();
        setDashboardData(INCREASE_BY_ONE); //Trigger Dashboard data change at last
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
    private static void setPostsData(String photoURL, String thumbnailURL, String screenshotURL,
                                     String userInputText, String photoLocation) {
        String dateTime = DateAndTimeUtils.dateTimeStamp();
        setProperDatabaseReference(); //This sets reference to user specific nodes in the database
        Post newPost = new Post(
                thisAppUser.getPhotoUrl().toString(),
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
                0);
        thisAppUser.setLastPostID(newPost.getDatestamp());
        myRef.child(POSTS_NODE).child(dateTime).setValue(newPost);

        //Maintain a list of last posts for display to all the other users of the app
        setAllPostsFirebaseReference();
        myRef.child(thisAppUser.getFireBaseID()).setValue(newPost);
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

        //Check if this is the "LastPost" for this user. If yes, then update it as well
        if (dateStampID.equals(thisAppUser.getLastPostID())) {
            setAllPostsFirebaseReference(); //To update the AllPosts under the "LastPosts" node
            myRef.child(thisAppUser.getFireBaseID()).child("iLoveFlag").setValue(newValue);
            myRef.child(thisAppUser.getFireBaseID()).child("likesCount").setValue(newLikeCount);
        }

    }

    //Method to the update the "iLike" flag and "likesCount" of a given post of ANY user
    public static void updateIlike(String dateStampID, boolean newValue, int newLikeCount, String userFirebaseID) {
        setAllPostsFirebaseReference(); //To update the AllPosts under the "LastPosts" node
        myRef.child(userFirebaseID).child("iLoveFlag").setValue(newValue);
        myRef.child(userFirebaseID).child("likesCount").setValue(newLikeCount);

        setPicsFirebaseReference();//To update the original posts in the main key for the respective user
        myRef.child(userFirebaseID).child(POSTS_NODE).child(dateStampID).child("likesCount").setValue(newLikeCount);

        //Additionally, if the user is liking his own post in the "ALL Posts" fragment, then update
        // the "iLoveFlag" for the post
        if (userFirebaseID.equals(thisAppUser.getFireBaseID()))
            myRef.child(userFirebaseID).child(POSTS_NODE).child(dateStampID).child("iLoveFlag").setValue(newValue);
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
        if (picNameInt >= defaultPicInt) {
            nextPicName = FIRST_PIC_NAME;
            return nextPicName;
        }
        //If it is not the first update then picName should be updated to a new name on date change
        // but keep the same for the ongoing day
        nextPicName = nextPicFileName (picNameInt);
        return nextPicName;
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

    private static String nextPicFileName(int oldFileNameInt ) {
        int newFileNameInt;
        if (myDashboard.getMealsForToday()==0) {
            newFileNameInt = oldFileNameInt + 1; //increment the filename if this is first update for the day
        } else {
            newFileNameInt = oldFileNameInt;//keep it to the same filename if there are previous updates for the day
        }
        return String.format(Locale.ENGLISH,"%05d", newFileNameInt);
    }
}
