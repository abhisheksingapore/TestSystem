package me.veganbuddy.veganbuddy.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;

import me.veganbuddy.veganbuddy.R;
import me.veganbuddy.veganbuddy.actors.MyPlace;
import me.veganbuddy.veganbuddy.util.BitmapUtils;
import me.veganbuddy.veganbuddy.util.DateAndTimeUtils;
import me.veganbuddy.veganbuddy.util.FirebaseStorageUtils;
import pl.droidsonroids.gif.GifImageView;

import static me.veganbuddy.veganbuddy.services.FetchPlacesIntentService.getMyPlace;
import static me.veganbuddy.veganbuddy.util.BitmapUtils.createTempImageFile;
import static me.veganbuddy.veganbuddy.util.BitmapUtils.createTempThumbFile;
import static me.veganbuddy.veganbuddy.util.BitmapUtils.getStringStatsImageUri;
import static me.veganbuddy.veganbuddy.util.Constants.FIREBASE_FULL_IMAGE_FOLDER;
import static me.veganbuddy.veganbuddy.util.Constants.FIREBASE_SCREENSHOT_FOLDER;
import static me.veganbuddy.veganbuddy.util.Constants.FIREBASE_THUMBNAIL_FOLDER;
import static me.veganbuddy.veganbuddy.util.Constants.FIRST_PIC_NAME;
import static me.veganbuddy.veganbuddy.util.Constants.MP_ASYNC_CLASS_TAG;
import static me.veganbuddy.veganbuddy.util.Constants.MP_CLASS_TAG;
import static me.veganbuddy.veganbuddy.util.Constants.MYPLACE_ADDRESS;
import static me.veganbuddy.veganbuddy.util.Constants.MYPLACE_ID;
import static me.veganbuddy.veganbuddy.util.Constants.MYPLACE_NAME;
import static me.veganbuddy.veganbuddy.util.Constants.VEGANPHILOSOPHY_TEXT;
import static me.veganbuddy.veganbuddy.util.DateAndTimeUtils.getMealTypeBasedOnTimeOfTheDay;
import static me.veganbuddy.veganbuddy.util.FirebaseStorageUtils.getAppMessage;
import static me.veganbuddy.veganbuddy.util.FirebaseStorageUtils.getNextPicName;
import static me.veganbuddy.veganbuddy.util.FirebaseStorageUtils.retrieveMessageForTheDay;
import static me.veganbuddy.veganbuddy.util.FirebaseStorageUtils.setNextPicName;
import static me.veganbuddy.veganbuddy.util.GlobalVariables.myDashboard;
import static me.veganbuddy.veganbuddy.util.GlobalVariables.thisAppUser;
import static me.veganbuddy.veganbuddy.util.SocialMediaUtils.loginToFaceBook;
import static me.veganbuddy.veganbuddy.util.SocialMediaUtils.loginToPinterest;
import static me.veganbuddy.veganbuddy.util.SocialMediaUtils.loginToTwitter;
import static me.veganbuddy.veganbuddy.util.SocialMediaUtils.uploadToSocialMedia;

public class MealPreviewPhoto extends AppCompatActivity {

    public TextView textViewAppMessage;
    public ImageView imageViewStatsPic;
    public EditText textComment;
    TextView textViewLocation;
    //Social Media Variables
    SharedPreferences sharedPreferences;
    Boolean facebook;
    Boolean pinterest;
    Boolean twitter;
    private String veganPhilosophyText;
    private String selectedLocationText;
    private Uri imageURI;
    private Uri screenShotURI;
    private Uri screenshotThumbURI;
    private String mealPhotoName;
    private MyPlace lastKnownPlace = new MyPlace();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_preview_photo);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Load current user shared preferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        loadSharedPreferences();

        //Social Media - handle sharing options based on shared preferences
        handleSocialMediaSharingOptions();


        FloatingActionButton fab = findViewById(R.id.ampp_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), DataRefreshActivity.class);
                startActivity(intent);

                saveScreenShot();
                uploadPhotoAndDashboardData();
                writeToSharedPreferences(); //Save the shared preferences based on selections made
            }
        });

    }

    private void saveScreenShot() {

        //Hide the Social Media Share Buttons before creating Screenshot
        View viewToHide = findViewById(R.id.cmpp_share_icons);
        viewToHide.setVisibility(View.GONE);

        //Hide the blinking cursor of the EditText
        textComment.setCursorVisible(false);

        //Create a canvas and draw View as a bitmap on the Canvas
        View viewToSave = findViewById(R.id.cmpp);

        Bitmap bitmapSS = Bitmap.createBitmap(viewToSave.getWidth(), viewToSave.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapSS);
        viewToSave.draw(canvas);

        //Create a temporary file to store the bitmap and retrieve the URI
        File tempImageFile = createTempImageFile(bitmapSS);
        if (tempImageFile!=null) {
            screenShotURI = Uri.fromFile(tempImageFile);
        } else Toast.makeText(this, "Error while creating Screenshot file", Toast.LENGTH_SHORT).show();

        File tempImageThumbFile = createTempThumbFile(bitmapSS);
        if (tempImageThumbFile!=null) {
            screenshotThumbURI = Uri.fromFile(tempImageThumbFile);
        } else Toast.makeText(this, "Error while creating Screenshot Thumbnail file", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        GifImageView imageView = findViewById(R.id.iv_preview_food_image);
        imageViewStatsPic = findViewById(R.id.cmpp_iv_preview_stats);
        textViewAppMessage = findViewById(R.id.cmpp_tv_app_message);

        veganPhilosophyText = VEGANPHILOSOPHY_TEXT + getMealTypeBasedOnTimeOfTheDay()
                + " for #" + DateAndTimeUtils.dateStampHumanReadable();

        //From the MyPlace received in FetchPlacesIntentService, use the name
        MyPlace myPlace = getMyPlace();
        if (myPlace != null) {
            String thisPlaceName = myPlace.getPlaceName();
            String thisPlaceAdd = myPlace.getPlaceAddress();
            selectedLocationText = (thisPlaceName == null) ? thisPlaceAdd : thisPlaceName;
        } else {
            myPlace = lastKnownPlace;
            String thisPlaceName = myPlace.getPlaceName();
            String thisPlaceAdd = myPlace.getPlaceAddress();
            selectedLocationText = (thisPlaceName == null) ? thisPlaceAdd : thisPlaceName;
        }

        Uri uriStatsImage = Uri.parse(getStringStatsImageUri());

        String appMessage = getAppMessage();
        //check App Message to be displayed in the Meal Preview Screen
        // If it is null, then set it to First_PIC
        if (appMessage == null) {
            retrieveMessageForTheDay(FIRST_PIC_NAME);
            setNextPicName(FIRST_PIC_NAME);
            myDashboard.setLastPicName(FIRST_PIC_NAME);
            Log.v(MP_CLASS_TAG, "Vegan Message file for the day is: " + getNextPicName() +
                    " \n And Vegan Message for the day is: " + getAppMessage());
        }

        if (appMessage!=null && uriStatsImage!=null) {
            textViewAppMessage.setText(appMessage);
            Picasso.with(this)
                    .load(uriStatsImage)
                    .placeholder(R.drawable.progressbar_green)
                    .error(R.drawable.animal_stats_default)
                    .into(imageViewStatsPic);
        }else {
            textViewAppMessage.setText("Vegetarianism stops animal killing for food");
            Picasso.with(this).load(R.drawable.animal_stats_default).into(imageViewStatsPic);
        }

        Uri thumbnailImageURI = BitmapUtils.getPhotoThumbnailUri();
        imageURI = BitmapUtils.getPhotoUri();
        mealPhotoName = BitmapUtils.getMealPhotoName();

        Picasso.with(this)
                .load(thumbnailImageURI)
                .placeholder(R.drawable.progressbar_green)
                .error(R.drawable.progressbar_green)
                .into(imageView);
        textComment = findViewById(R.id.tv_preview_comments);
        textViewLocation = findViewById(R.id.cmpp_tv_location);

        textComment.setText(veganPhilosophyText);
        textViewLocation.setText((selectedLocationText == null) ? "No Place found" : selectedLocationText);
    }

    public void locationEditClick(View view) {
        PlaceAutocompleteFragment autocompleteFragment
                = (PlaceAutocompleteFragment) getFragmentManager()
                .findFragmentById(R.id.cmpp_place_autocomplete_fragment);

        if (getMyPlace() != null) {
            //Lat Long of the Current Location
            LatLng latLng = getMyPlace().getLocation();

            //Maximum North East variation allowed
            LatLng latLngNE = new LatLng(latLng.latitude + 1, latLng.longitude + 1);

            //Minimum South West variation allowed
            LatLng latLngSW = new LatLng(latLng.latitude - 1, latLng.longitude - 1);

            autocompleteFragment.setBoundsBias(new LatLngBounds(latLngSW, latLngNE));
        }

        View root = autocompleteFragment.getView();
        root.findViewById(R.id.place_autocomplete_search_input).performClick();

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                //If place has a valid name, then use name
                if (place.getName().toString().trim().length() != 0) {
                    textViewLocation.setText(place.getName());
                    Log.v(MP_CLASS_TAG, "Name of Place selected:" + place.getName());
                } //else use its address
                else {
                    textViewLocation.setText(place.getAddress());
                    Log.v(MP_CLASS_TAG, "Address of Place selected:" + place.getAddress());
                }
            }

            @Override
            public void onError(Status status) {
                Log.e(MP_CLASS_TAG, "error retrieving Places data from PlaceAutoCompleteFragment");
            }
        });
    }

    private void uploadPhotoAndDashboardData () {
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        StorageReference photoReference = mStorageRef.child(uniqueImagePath());
        StorageReference thumbnailReference = mStorageRef.child(uniqueThumbnailPath());
        StorageReference screenShotReference = mStorageRef.child(uniqueScreenShotPath());

        //Uploading Thumbnail before full size photo as thumbnail upload will be faster
        UploadTask thumbnailUpload = thumbnailReference.putFile(screenshotThumbURI);
        UploadTask screenShotUpload = screenShotReference.putFile(screenShotURI);
        UploadTask photoUpload = photoReference.putFile(imageURI);

        thumbnailUpload
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                BitmapUtils.setPhotoThumbnailURL(taskSnapshot.getDownloadUrl().toString());
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MealPreviewPhoto.this, "Photo(thumbnail) Upload Failed!", Toast.LENGTH_SHORT).show();
                        Log.e(MP_ASYNC_CLASS_TAG,"Photo(thumbnail) Upload Failed!" );
                    }
                });

        screenShotUpload
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        BitmapUtils.setScreenShotURL(taskSnapshot.getDownloadUrl().toString());
                        //Once Screenshot is successfully uploaded then upload the image to the
                        //selected social media channels as well
                        uploadToSocialMedia(facebook, twitter, pinterest,
                                screenShotURI, veganPhilosophyText);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MealPreviewPhoto.this, "Photo(Screenshot) Upload Failed!", Toast.LENGTH_SHORT).show();
                        Log.e(MP_ASYNC_CLASS_TAG,"Photo(Screenshot) Upload Failed!" );
                    }
                });

        photoUpload
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        BitmapUtils.setPhotoURL(taskSnapshot.getDownloadUrl().toString());
                        //Once the full photo is uploaded successfully then upload the rest of
                        // the data as well
                        uploadDataToCloudDatabase(); //Including the comments, the meal type, the path for fullSizeImage, etc.
                        Toast.makeText(MealPreviewPhoto.this, "Photo Upload Successful!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MealPreviewPhoto.this, "Photo(FullSize) Upload Failed!", Toast.LENGTH_SHORT).show();
                        Log.e(MP_ASYNC_CLASS_TAG,"Photo(FullSize) Upload Failed!" );
                    }
                });

    }

    private void uploadDataToCloudDatabase() {
        thisAppUser.updateFoodWisdomCounter();
        FirebaseStorageUtils.addNodesToDatabase(BitmapUtils.getPhotoURL(),
                BitmapUtils.getPhotoThumbnailURL(), BitmapUtils.getScreenShotURL(),
                veganPhilosophyText, selectedLocationText, getMyPlace(), facebook, twitter, pinterest);
    }

     private String uniqueImagePath() {
         return thisAppUser.getFireBaseID()+ FIREBASE_FULL_IMAGE_FOLDER + mealPhotoName;
     }

    private String uniqueThumbnailPath() {
        return thisAppUser.getFireBaseID()+ FIREBASE_THUMBNAIL_FOLDER + mealPhotoName;
    }

    private String uniqueScreenShotPath() {
        return thisAppUser.getFireBaseID() + FIREBASE_SCREENSHOT_FOLDER + mealPhotoName;
    }

    /************************* Handling of Social Media ********************************
     ********* Social Media Logins
     ********* Social Media SharedPreferences
     ********* Social Media Sharing Options
     * ****** Social Media Sharing Button ClickHandlers
     * **********************************************************************************/

    private void handleSocialMediaSharingOptions() {

        //Set Icon colors based on current user preferences. Set to coloured versions, if
        // settings are "true" and user is logged in. Set to Black and White for whether the
        // user is not logged in or whether the user has made his setting as "false"
        ImageView facebookIcon = findViewById(R.id.cmpp_share_fb);
        ImageView pinterestIcon = findViewById(R.id.cmpp_share_pinterest);
        ImageView twitterIcon = findViewById(R.id.cmpp_share_twitter);

        if (facebook && loginToFaceBook()) { //To handle the situation where settings is 'True' but user is not logged in
            facebookIcon.setContentDescription(getResources().getString(R.string.ampp_facebook_color));
            facebookIcon.setImageDrawable(getDrawable(R.drawable.facebook_color));
        } else {
            facebookIcon.setContentDescription(getResources().getString(R.string.ampp_facebook));
            facebookIcon.setImageDrawable(getDrawable(R.drawable.facebook_bw));
            facebook = false; //To handle the situation where settings is 'True' but user is not logged in
        }

        if (pinterest && loginToPinterest()) {
            pinterestIcon.setContentDescription(getResources().getString(R.string.ampp_pinterest_color));
            pinterestIcon.setImageDrawable(getDrawable(R.drawable.pinterest_color));
        } else {
            pinterestIcon.setContentDescription(getResources().getString(R.string.ampp_pinterest));
            pinterestIcon.setImageDrawable(getDrawable(R.drawable.pinterest_bw));
            pinterest = false;
        }

        if (twitter && loginToTwitter()) {//To handle the situation where settings is 'True' but user is not logged in
            twitterIcon.setContentDescription(getResources().getString(R.string.ampp_twitter_color));
            twitterIcon.setImageDrawable(getDrawable(R.drawable.twitter_color));
        } else {
            twitterIcon.setContentDescription(getResources().getString(R.string.ampp_twitter));
            twitterIcon.setImageDrawable(getDrawable(R.drawable.twitter_bw));
            twitter = false;
        }

    }

    public void loadSharedPreferences() {
        facebook = sharedPreferences.getBoolean(getResources().getString(R.string.pref_social_facebook), false);
        pinterest = sharedPreferences.getBoolean(getResources().getString(R.string.pref_social_pinterest), false);
        twitter = sharedPreferences.getBoolean(getResources().getString(R.string.pref_social_twitter), false);

        String lastPlaceID = sharedPreferences.getString(MYPLACE_ID, null);
        String lastPlaceName = sharedPreferences.getString(MYPLACE_NAME, null);
        String lastPlaceAddress = sharedPreferences.getString(MYPLACE_ADDRESS, null);
        lastKnownPlace.setPlaceId(lastPlaceID);
        lastKnownPlace.setPlaceName(lastPlaceName);
        lastKnownPlace.setPlaceAddress(lastPlaceAddress);
    }

    public void writeToSharedPreferences() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(getResources().getString(R.string.pref_social_facebook), facebook);
        editor.putBoolean(getResources().getString(R.string.pref_social_pinterest), pinterest);
        editor.putBoolean(getResources().getString(R.string.pref_social_twitter), twitter);

        MyPlace myPlace = getMyPlace();
        if (myPlace != null) {
            editor.putString(MYPLACE_ID, myPlace.getPlaceId());
            editor.putString(MYPLACE_NAME, myPlace.getPlaceName());
            editor.putString(MYPLACE_ADDRESS, myPlace.getPlaceAddress());
        }
        editor.apply();
    }

    public void facebookClick(View view) {
        ImageView facebookIcon = (ImageView) view;
        if (!facebook) { //Clicked when facebook is set to 'false'
            if (loginToFaceBook()) {
                facebookIcon.setContentDescription(getResources().getString(R.string.ampp_facebook_color));
                facebookIcon.setImageDrawable(getDrawable(R.drawable.facebook_color));
                facebook = true;
            } else {
                Intent faceBookLogin = new Intent(this, SocialLoginsActivity.class);
                startActivity(faceBookLogin);
            }
        } else {
            facebookIcon.setContentDescription(getResources().getString(R.string.ampp_facebook));
            facebookIcon.setImageDrawable(getDrawable(R.drawable.facebook_bw));
            facebook = false;
        }
    }

    public void twitterClick(View view) {
        ImageView twitterIcon = (ImageView) view;
        if (!twitter) {//Clicked when Twitter is set to 'False'
            if (loginToTwitter()) {
                twitterIcon.setContentDescription(getResources().getString(R.string.ampp_twitter_color));
                twitterIcon.setImageDrawable(getDrawable(R.drawable.twitter_color));
                twitter = true;
            } else {
                Intent twitterLogin = new Intent(this, SocialLoginsActivity.class);
                startActivity(twitterLogin);
            }
        } else {
            twitterIcon.setContentDescription(getResources().getString(R.string.ampp_twitter));
            twitterIcon.setImageDrawable(getDrawable(R.drawable.twitter_bw));
            twitter = false;
        }
    }

    public void pinterestClick(View view) {
        ImageView pinterestIcon = (ImageView) view;
        if (!pinterest) {//Clicked when Pinterest is set to 'False'
            if (loginToPinterest()) {
                pinterestIcon.setContentDescription(getResources().getString(R.string.ampp_pinterest_color));
                pinterestIcon.setImageDrawable(getDrawable(R.drawable.pinterest_color));
                pinterest = true;
            } else {
                Intent pinterestLogin = new Intent(this, SocialLoginsActivity.class);
                startActivity(pinterestLogin);
            }
        } else {
            pinterestIcon.setContentDescription(getResources().getString(R.string.ampp_pinterest));
            pinterestIcon.setImageDrawable(getDrawable(R.drawable.pinterest_bw));
            pinterest = false;
        }

    }
}
