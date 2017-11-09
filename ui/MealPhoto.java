package me.veganbuddy.veganbuddy.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.net.Uri;
import android.os.Handler;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import me.veganbuddy.veganbuddy.R;
import me.veganbuddy.veganbuddy.services.FetchAddressIntentService;
import me.veganbuddy.veganbuddy.util.BitmapUtils;
import me.veganbuddy.veganbuddy.util.Constants;

import static me.veganbuddy.veganbuddy.services.FetchAddressIntentService.addressList;
import static me.veganbuddy.veganbuddy.services.FetchAddressIntentService.placesList;
import static me.veganbuddy.veganbuddy.util.Constants.FAILURE_RESULT;
import static me.veganbuddy.veganbuddy.util.Constants.FIRST_PIC_NAME;
import static me.veganbuddy.veganbuddy.util.Constants.HIDE_EDIT_TEXT;
import static me.veganbuddy.veganbuddy.util.Constants.MP_TAG;
import static me.veganbuddy.veganbuddy.util.Constants.SELECTED_LOCATION;
import static me.veganbuddy.veganbuddy.util.Constants.SHARETOFACEBOOK;
import static me.veganbuddy.veganbuddy.util.Constants.SHARETOPINTEREST;
import static me.veganbuddy.veganbuddy.util.Constants.SHARETOTWITTER;
import static me.veganbuddy.veganbuddy.util.Constants.SHOW_EDIT_TEXT;
import static me.veganbuddy.veganbuddy.util.Constants.SHOW_MEAL_PHOTO;
import static me.veganbuddy.veganbuddy.util.Constants.SHOW_PROGRESS_BAR;
import static me.veganbuddy.veganbuddy.util.Constants.STATS_IMAGE_URI;
import static me.veganbuddy.veganbuddy.util.Constants.SUCCESS_RESULT;
import static me.veganbuddy.veganbuddy.util.Constants.VEGANPHILOSOPHY;

import static me.veganbuddy.veganbuddy.util.FirebaseStorageUtils.getAppMessage;
import static me.veganbuddy.veganbuddy.util.FirebaseStorageUtils.getNextPicName;
import static me.veganbuddy.veganbuddy.util.FirebaseStorageUtils.getStatsPicReference;
import static me.veganbuddy.veganbuddy.util.FirebaseStorageUtils.retrieveMessageForTheDay;
import static me.veganbuddy.veganbuddy.util.FirebaseStorageUtils.setNextPicName;
import static me.veganbuddy.veganbuddy.util.GlobalVariables.myDashboard;
import static me.veganbuddy.veganbuddy.util.SocialMediaUtils.loginToFaceBook;
import static me.veganbuddy.veganbuddy.util.SocialMediaUtils.loginToPinterest;
import static me.veganbuddy.veganbuddy.util.SocialMediaUtils.loginToTwitter;


//Todo: Convert to Tabbed Activity for the options of  - "Choose from gallery" and "Video"
public class MealPhoto extends AppCompatActivity {

    final int PLACE_PICKER_REQUEST = 1;

    ImageView mealImageView;
    Boolean repeatClick = false;
    public FusedLocationProviderClient mFusedLocationProviderClient;
    protected Location mLastLocation;

    AddressResultReceiver addressResultReceiver;
    ArrayList <String> locationList;
    LocationRecyclerViewAdapter locationRecyclerViewAdapter;

    LinearLayout linearLayoutAddLocation;
    LinearLayout linearLayoutSelectedLocation;
    LinearLayout linearLayoutSocialIcons;
    TextView textViewLocationLabel;
    EditText editTextVeganPh;
    ImageButton imageButtonSend;
    TextView textViewLocationSelected;
    ProgressBar progressBar;

    Boolean facebook;
    Boolean pinterest;
    Boolean twitter;
    String stringStatsImageUri;

    Boolean showEditTextFlag = false;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_photo);
        linearLayoutAddLocation = findViewById(R.id.amp_location);
        linearLayoutSelectedLocation = findViewById(R.id.amp_location_selected);
        linearLayoutSocialIcons = findViewById(R.id.amp_share_icons);
        textViewLocationLabel = findViewById(R.id.amp_location_label);
        editTextVeganPh = findViewById(R.id.amp_editText_food);
        imageButtonSend = findViewById(R.id.amp_send_button);
        textViewLocationSelected = findViewById(R.id.amp_tv_location_selected);
        progressBar = findViewById(R.id.amp_progress_bar);
        mealImageView = findViewById(R.id.amp_image_food);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationList = new ArrayList<>();

        checkLocationPermission();

        LinearLayoutManager linearLayoutManager
                = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false);
        locationRecyclerViewAdapter = new LocationRecyclerViewAdapter(locationList);
        RecyclerView locationRV = findViewById(R.id.amp_rv_location_list);
        locationRV.setLayoutManager(linearLayoutManager);
        locationRV.setAdapter(locationRecyclerViewAdapter);
        handleSocialMediaSharingOptions();
    }

    private void checkLocationPermission() {
        //Check Location Permission. If it is granted, then get the lastKnownLocation
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getLastKnownLocation();
        } //Else request for the location Permission
        else {
            ActivityCompat.requestPermissions
                    (this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constants.LOCATION_PERMITTED);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            //Once location permission is granted, getLastKnownLocation
            case Constants.LOCATION_PERMITTED: getLastKnownLocation();
            break;
            //Else display a message
            default:
                Toast.makeText(this, "Application will not work fine without location " +
                        "permission", Toast.LENGTH_LONG).show();
                updateUI(SHOW_EDIT_TEXT);
        }
    }

    private void getLastKnownLocation() {
        try {
            mFusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                mLastLocation = location;
                                startIntentService();
                            }
                        }
                    });
        } catch (SecurityException SE) {
            SE.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // check if myDashboard is null, then re-login
        if (myDashboard == null) {
            Intent intentLogin = new Intent (this, LoginActivity.class);
            startActivity(intentLogin);
            finish();
        }
        if (showEditTextFlag) updateUI(SHOW_EDIT_TEXT);
        else updateUI(HIDE_EDIT_TEXT);

        String mealPhotoPath = BitmapUtils.getPhotoPath();
        //If no meal photo received in this activity
        if (mealPhotoPath == null) {
            updateUI(SHOW_PROGRESS_BAR);
            Toast.makeText(this, "No meal photo received. Please try again",
                    Toast.LENGTH_SHORT).show();
        }
        else {
            //Create a thumbnail of the received photo and load into Imageview using Picasso
            BitmapUtils.createThumbnail();
            Picasso.with(this).load(BitmapUtils.getPhotoThumbnailUri()).into(mealImageView);
            //check App Message to be displayed in the Meal Preview Screen
            // If it is null, then set it to First_PIC
            if (getAppMessage()==null) {
                retrieveMessageForTheDay(FIRST_PIC_NAME);
                setNextPicName(FIRST_PIC_NAME);
                myDashboard.setLastPicName(FIRST_PIC_NAME);
                Log.v(MP_TAG, "Vegan Message file for the day is: " + getNextPicName() +
                        " \n And Vegan Message for the day is: " + getAppMessage());
            }
            //load appMessagePic
            retrieveStatsImageUri();
            //show the imageview once meal photo thumbnail and appMessagePic are loaded
            updateUI(SHOW_MEAL_PHOTO);
        }

    }


    @Override
    protected void onStop() {
        super.onStop();
        writeToSharedPreferences(); //Save the shared preferences based on selections made
    }

    public void shareMyPhoto (View view) {
        EditText editText = findViewById(R.id.amp_editText_food);
        String inputText = editText.getText().toString();
        String selectedLocation = textViewLocationSelected.getText().toString();

        if (inputText.trim().length() > 0 || repeatClick) {
            //Create intent for MealPreviewPhoto
            final Intent previewPhotoIntent = new Intent(this, MealPreviewPhoto.class);
            previewPhotoIntent.putExtra(VEGANPHILOSOPHY, inputText);
            previewPhotoIntent.putExtra(SELECTED_LOCATION, selectedLocation);
            previewPhotoIntent.putExtra(SHARETOFACEBOOK, facebook);
            previewPhotoIntent.putExtra(SHARETOPINTEREST, pinterest);
            previewPhotoIntent.putExtra(SHARETOTWITTER, twitter);
            previewPhotoIntent.putExtra(STATS_IMAGE_URI,stringStatsImageUri);
            startActivity(previewPhotoIntent);
            repeatClick = false;
        } else {
            //Todo: insert logic for automatic vegan quotes
            Snackbar noCommentFeedback = Snackbar
                    .make(view, "Any comments about the picture?", Snackbar.LENGTH_SHORT);
            noCommentFeedback.show();
            repeatClick = true;
        }
    }

    private void retrieveStatsImageUri() {
        StorageReference statsPicReference = getStatsPicReference(getNextPicName());
        final Task<Uri> uriTask = statsPicReference.getDownloadUrl();
        uriTask.addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                stringStatsImageUri = task.getResult().toString();
            }
        });
    }


    protected void startIntentService () {
        if (mLastLocation != null) {
            Intent intent = new Intent(this, FetchAddressIntentService.class);
            addressResultReceiver = new AddressResultReceiver(new Handler());
            intent.putExtra(Constants.RECEIVER, addressResultReceiver);
            intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);
            startService(intent);
        }
    }

    public void locationAddClick(View view) {
        Intent intentPlacePicker;
        //Prepare intent for launching PlacePicker
        try {
            PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
            intentPlacePicker = intentBuilder.build(this);
            startActivityForResult(intentPlacePicker, PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            Log.e(MP_TAG, e.getMessage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                textViewLocationSelected.setText(place.getName());
            }
        }
    }

    public void locationEditClick(View view) {
        updateUI(Constants.HIDE_EDIT_TEXT);
    }

    public void noLocationClick(View view) {
        textViewLocationSelected.setText(getString(R.string.amp_no_location));
        updateUI (Constants.SHOW_EDIT_TEXT);
    }

    public void addThisLocationClick(View view) {
        if (view.getId() == R.id.li_location) {
            TextView textViewLocation = (TextView) view;
            String locationSelected = textViewLocation.getText().toString();
            textViewLocationSelected.setText(locationSelected);
            updateUI (Constants.SHOW_EDIT_TEXT);
        }
    }

    private void updateUI(int showEditText) {
        switch (showEditText) {
            case Constants.SHOW_EDIT_TEXT:
                linearLayoutSelectedLocation.setVisibility(View.VISIBLE);
                linearLayoutSocialIcons.setVisibility(View.VISIBLE);
                linearLayoutAddLocation.setVisibility(View.INVISIBLE);
                textViewLocationLabel.setVisibility(View.INVISIBLE);
                editTextVeganPh.setVisibility(View.VISIBLE);
                imageButtonSend.setVisibility(View.VISIBLE);
                showEditTextFlag = true;
                break;
            case Constants.HIDE_EDIT_TEXT:
                linearLayoutSelectedLocation.setVisibility(View.INVISIBLE);
                linearLayoutSocialIcons.setVisibility(View.INVISIBLE);
                linearLayoutAddLocation.setVisibility(View.VISIBLE);
                textViewLocationLabel.setVisibility(View.VISIBLE);
                editTextVeganPh.setVisibility(View.INVISIBLE);
                imageButtonSend.setVisibility(View.INVISIBLE);
                showEditTextFlag = false;
                break;
            case SHOW_MEAL_PHOTO:
                mealImageView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                break;
            case SHOW_PROGRESS_BAR:
                mealImageView.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void handleSocialMediaSharingOptions() {
        //Load current user preferences
        loadSocialMediaSharedPreference();

        //Set Icon colors based on current user preferences. Set to coloured versions, if
        // settings are "true" and user is logged in. Set to Black and White for whether the
        // user is not logged in or whether the user has made his setting as "false"
        ImageView facebookIcon = findViewById(R.id.amp_share_fb);
        ImageView pinterestIcon = findViewById(R.id.amp_share_pinterest);
        ImageView twitterIcon = findViewById(R.id.amp_share_twitter);

        if (facebook && loginToFaceBook()) { //To handle the situation where settings is 'True' but user is not logged in
            facebookIcon.setContentDescription(getResources().getString(R.string.amp_facebook_color));
            facebookIcon.setImageDrawable(getDrawable(R.drawable.facebook_color));
        } else {
            facebookIcon.setContentDescription(getResources().getString(R.string.amp_facebook));
            facebookIcon.setImageDrawable(getDrawable(R.drawable.facebook_bw));
            facebook = false; //To handle the situation where settings is 'True' but user is not logged in
        }

        if (pinterest && loginToPinterest()) {
            pinterestIcon.setContentDescription(getResources().getString(R.string.amp_pinterest_color));
            pinterestIcon.setImageDrawable(getDrawable(R.drawable.pinterest_color));
        } else {
            pinterestIcon.setContentDescription(getResources().getString(R.string.amp_pinterest));
            pinterestIcon.setImageDrawable(getDrawable(R.drawable.pinterest_bw));
            pinterest = false;
        }

        if (twitter && loginToTwitter()) {//To handle the situation where settings is 'True' but user is not logged in
            twitterIcon.setContentDescription(getResources().getString(R.string.amp_twitter_color));
            twitterIcon.setImageDrawable(getDrawable(R.drawable.twitter_color));
        } else {
            twitterIcon.setContentDescription(getResources().getString(R.string.amp_twitter));
            twitterIcon.setImageDrawable(getDrawable(R.drawable.twitter_bw));
            twitter = false;
        }

    }

    public void loadSocialMediaSharedPreference() {
        facebook = sharedPreferences.getBoolean(getResources().getString(R.string.pref_social_facebook), false);
        pinterest = sharedPreferences.getBoolean(getResources().getString(R.string.pref_social_pinterest), false);
        twitter = sharedPreferences.getBoolean(getResources().getString(R.string.pref_social_twitter), false);
    }

    public void writeToSharedPreferences() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(getResources().getString(R.string.pref_social_facebook), facebook);
        editor.putBoolean(getResources().getString(R.string.pref_social_pinterest), pinterest);
        editor.putBoolean(getResources().getString(R.string.pref_social_twitter), twitter);
        editor.apply();
    }


    public void facebookClick(View view) {
        ImageView facebookIcon = (ImageView)view;
        if (!facebook) { //Clicked when facebook is set to 'false'
            if (loginToFaceBook()) {
                facebookIcon.setContentDescription(getResources().getString(R.string.amp_facebook_color));
                facebookIcon.setImageDrawable(getDrawable(R.drawable.facebook_color));
                facebook = true;
            } else {
                Intent faceBookLogin = new Intent(this, SocialLoginsActivity.class);
                startActivity(faceBookLogin);
            }
        } else {
            facebookIcon.setContentDescription(getResources().getString(R.string.amp_facebook));
            facebookIcon.setImageDrawable(getDrawable(R.drawable.facebook_bw));
            facebook = false;
        }
    }


    public void twitterClick(View view) {
        ImageView twitterIcon = (ImageView) view;
        if (!twitter) {//Clicked when Twitter is set to 'False'
            if (loginToTwitter()) {
                twitterIcon.setContentDescription(getResources().getString(R.string.amp_twitter_color));
                twitterIcon.setImageDrawable(getDrawable(R.drawable.twitter_color));
                twitter = true;
            } else {
                Intent twitterLogin = new Intent(this, SocialLoginsActivity.class);
                startActivity(twitterLogin);
            }
        } else {
            twitterIcon.setContentDescription(getResources().getString(R.string.amp_twitter));
            twitterIcon.setImageDrawable(getDrawable(R.drawable.twitter_bw));
            twitter = false;
        }
    }

    public void pinterestClick(View view) {
        ImageView pinterestIcon = (ImageView)view;
        if (!pinterest) {//Clicked when Pinterest is set to 'False'
            if (loginToPinterest()) {
                pinterestIcon.setContentDescription(getResources().getString(R.string.amp_pinterest_color));
                pinterestIcon.setImageDrawable(getDrawable(R.drawable.pinterest_color));
                pinterest = true;
            } else {
                Intent pinterestLogin = new Intent(this, SocialLoginsActivity.class);
                startActivity(pinterestLogin);
            }
        } else {
            pinterestIcon.setContentDescription(getResources().getString(R.string.amp_pinterest));
            pinterestIcon.setImageDrawable(getDrawable(R.drawable.pinterest_bw));
            pinterest = false;
        }

    }

    class AddressResultReceiver extends ResultReceiver {

        AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            switch (resultCode){
                case SUCCESS_RESULT:
                    if (placesList != null) {
                        for (String eachplace: placesList) {
                            locationList.add(eachplace);
                            locationRecyclerViewAdapter.notifyDataSetChanged();
                        }
                    }
                    break;
                case FAILURE_RESULT:
                    break;
            }
        }
    }

}
