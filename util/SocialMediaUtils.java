package me.veganbuddy.veganbuddy.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.pinterest.android.pdk.PDKBoard;
import com.pinterest.android.pdk.PDKCallback;
import com.pinterest.android.pdk.PDKClient;
import com.pinterest.android.pdk.PDKException;
import com.pinterest.android.pdk.PDKResponse;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.Media;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.MediaService;
import com.twitter.sdk.android.core.services.StatusesService;

import java.io.File;
import java.net.URL;
import java.util.List;

import me.veganbuddy.veganbuddy.R;
import me.veganbuddy.veganbuddy.ui.PinterestLoginActivity;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


import static me.veganbuddy.veganbuddy.util.Constants.SMU_TAG;
import static me.veganbuddy.veganbuddy.util.Constants.VEGAN_BUDDY_PIN_BOARD;
import static me.veganbuddy.veganbuddy.util.Constants.VEGAN_BUDDY_WEBSITE;

/**
 * Created by abhishek on 26/9/17.
 */

public class SocialMediaUtils {
    public static String twitterToken="";
    public static String twitterSecret="";
    public static TwitterAuthToken twitterAuthToken;
    public static TwitterSession twitterSession;
    public static long mediaIDforTwitter;

    public static PDKClient pdkClient;
    public static PDKBoard pdkBoardVegan;
    public static boolean loginToPinterest = false;
    public static String pinterestUserID;
    static String veganText;

    public static void uploadToSocialMedia(boolean uploadToFaceBook, boolean tweetThisPic,
                                           boolean pinThisPic, Uri screenShotURI,
                                           String veganPhilosophyText) {
        veganText = veganPhilosophyText;

        if (uploadToFaceBook)
            if (!loginToFaceBook()) {
                Log.e (SMU_TAG,"Photo(Screenshot) Upload to Facebook Failed!" );
            } else {
                uplodPhotoToFaceBook(screenShotURI,
                        veganPhilosophyText); //upload screenshot to Facebook
            }

        if (tweetThisPic) {
            if (!loginToTwitter()){
                Log.e (SMU_TAG,"Photo(Screenshot) Upload to Twitter Failed! Login to Twitter" );
            } else {
                uploadToTwitter();
                Log.v (SMU_TAG,"Photo(Screenshot) - preparing upload to Twitter" );
            }
        }

        if (pinThisPic) {
            if (!loginToPinterest()){
                Log.e (SMU_TAG,"Photo(Screenshot) Upload to Pinterest Failed! Login to Twitter" );
            } else {
                uploadToPinterest();
                Log.v (SMU_TAG,"Photo(Screenshot) - preparing upload to Twitter" );
            }
        }
    }


    public static boolean loginToFaceBook() {
        AccessToken token = AccessToken.getCurrentAccessToken();
        if((token != null) && !(token.isExpired())) return true;  //checking both for login status and expired status of the token
        else return false;
    }

    private static void uplodPhotoToFaceBook(Uri screenShotURI, String userCaption) {
        SharePhoto photo = new SharePhoto.Builder()
                .setImageUrl(screenShotURI)
                .setCaption(userCaption)
                .setUserGenerated(true)
                .build();

        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .build(); //Todo:Add a hashtag

        ShareApi.share(content, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                Log.v(SMU_TAG, "Successfully uploaded the photo to Facebook");
            }

            @Override
            public void onCancel() {
                Log.v(SMU_TAG, "Cancelled uploading the photo to Facebook");
            }

            @Override
            public void onError(FacebookException error) {
                Log.e(SMU_TAG, "Error in uploading the photo to Facebook" + "\n\n" + error.toString());
            }
        });
    }

    public static void initializeTwitter(Context context) {
        //Twitter is initialized at the start of the application in LoginActivity
        Twitter.initialize(context);
    }

    public static boolean loginToTwitter() {
        boolean loginToTwitter = false;

        twitterSession = TwitterCore.getInstance().getSessionManager().getActiveSession();
        if(twitterSession != null) {
            if((twitterAuthToken != null)) loginToTwitter = true;
            else {
                twitterAuthToken = twitterSession.getAuthToken();
                twitterToken = twitterAuthToken.token;
                twitterSecret = twitterAuthToken.secret;
                loginToTwitter = true;
            }
        }
        return loginToTwitter;
    }


    public static void uploadToTwitter(){
        File uploadScreenShotFile = BitmapUtils.getScreenShotFile();
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), uploadScreenShotFile);
        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
        final StatusesService statusesService = twitterApiClient.getStatusesService();
        MediaService mediaService = twitterApiClient.getMediaService();
        Call <Media> mediaCall = mediaService.upload(requestBody, null,null);
        mediaCall.enqueue(new com.twitter.sdk.android.core.Callback<Media>() {
            @Override
            public void success(Result<Media> result) {
               setMediaID (result.data.mediaId);
                Log.v (SMU_TAG,"Screenshot Uploaded to Twitter! Now composing Tweet" );
                createTweetFromMedia(veganText, statusesService);
            }

            @Override
            public void failure(TwitterException exception) {

            }
        });
    }

    private static void createTweetFromMedia(String veganPhilosophyText, StatusesService statusesService) {
        Call<Tweet> call = statusesService.update(" #everyMealCounts #vegan " + veganPhilosophyText
                        + " @theVeganBuddy", null, null,
                null, null, null, null,
                null, getMediaIDforTwitter());
        call.enqueue(new Callback<Tweet>() {
            @Override
            public void onResponse(Call<Tweet> call, Response<Tweet> response) {
                Log.v(SMU_TAG, "Tweet Successful" +response.message());
            }

            @Override
            public void onFailure(Call<Tweet> call, Throwable t) {
                Log.e(SMU_TAG, "Posting the tweet failed \n" + call.toString());
            }
        });
    }

    private static String getMediaIDforTwitter() {
        return Long.toString(mediaIDforTwitter);
    }

    private static void setMediaID(long mediaId) {
        mediaIDforTwitter = mediaId;
    }

    public static void initializePinterest(Context context, String pinterestAppID){
        pdkClient = PDKClient.configureInstance(context, pinterestAppID);
        pdkClient.onConnect(context);
        pdkClient.setDebugMode(true);
        pdkClient.getMe(new PDKCallback() {
            @Override
            public void onSuccess(PDKResponse response) {
                loginToPinterest = true;
                pinterestUserID = response.getUser().getUid();
                Log.v(SMU_TAG,"Login to Pinterest successful using accessToken" + response.getData().toString());
            }

            @Override
            public void onFailure(PDKException exception) {
                loginToPinterest = false;
                Log.e(SMU_TAG, "Error in receiving data from Pinterest: " + exception.getDetailMessage());
            }
        });
    }

    public static boolean loginToPinterest() {
        return loginToPinterest;
    }

    private static void uploadToPinterest() {
        //Get list of ALL Boards for this user
        pdkClient.getMyBoards("name", new PDKCallback(){
            @Override
            public void onSuccess(PDKResponse response) {
                super.onSuccess(response);
                Log.v(SMU_TAG, response.getData().toString());
                List<PDKBoard> boardsList = response.getBoardList();

                //Check if Board "VeganBuddy" exists
                for (int i = 0; i < boardsList.size(); i++) {
                    PDKBoard pdkBoard = boardsList.get(i);
                    if (pdkBoard.getName().equals(VEGAN_BUDDY_PIN_BOARD)) {
                        pdkBoardVegan = pdkBoard;
                        Log.v(SMU_TAG, "Found VeganBuddy Board. BoardID: " + pdkBoardVegan.getUid());
                    }
                    else {
                        Log.v(SMU_TAG, "Looking for VeganBuddy Board");
                    }
                }
                //If the Board "VeganBuddy" is found, then createPin
                if (pdkBoardVegan!=null) createPin(pdkBoardVegan.getUid());
                else {//else create the Board and use that BoardID to create Pin
                    createPinterestBoardAndPin();
                    Log.v(SMU_TAG, "VeganBuddy board not found. Creating one for this user");
                }
            }
            @Override
            public void onFailure(PDKException exception) {
                super.onFailure(exception);
                Log.e(SMU_TAG, "Could not retrieve User Pin Boards.." +
                        exception.getDetailMessage());
            }


        });
    }

    private static void createPinterestBoardAndPin() {
        pdkClient.createBoard("VeganBuddy", "Board for storing Pins from app VeganBuddy"
                , new PDKCallback(){
                    @Override
                    public void onSuccess(PDKResponse response) {
                        Log.v(SMU_TAG, "VeganBuddy board created successfully" + response.getData().toString());
                        String boardID = response.getBoard().getUid();
                        createPin(boardID );
                    }

                    @Override
                    public void onFailure(PDKException exception) {
                        Log.e(SMU_TAG, "Error in  creating VeganBuddy Board " + exception.getDetailMessage());
                    }
                });
    }

    private static void createPin(String boardID) {
        pdkClient.createPin (veganText,boardID,
                BitmapUtils.getScreenShotURL(),
                VEGAN_BUDDY_WEBSITE,
                new PDKCallback() {
                    @Override
                    public void onSuccess(PDKResponse response) {
                        Log.v(SMU_TAG, response.getData().toString());
                    }

                    @Override
                    public void onFailure(PDKException exception) {
                        Log.e(SMU_TAG, "Error in uploading image to Pinterest: " + exception.getDetailMessage());
                    }
                });
    }
}
