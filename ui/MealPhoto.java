package me.veganbuddy.veganbuddy.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import me.veganbuddy.veganbuddy.R;
import me.veganbuddy.veganbuddy.services.FetchAddressIntentService;
import me.veganbuddy.veganbuddy.util.BitmapUtils;
import me.veganbuddy.veganbuddy.util.Constants;

import static me.veganbuddy.veganbuddy.services.FetchAddressIntentService.addressList;
import static me.veganbuddy.veganbuddy.util.Constants.FIRST_PIC_NAME;
import static me.veganbuddy.veganbuddy.util.Constants.HIDE_EDIT_TEXT;
import static me.veganbuddy.veganbuddy.util.Constants.MP_TAG;
import static me.veganbuddy.veganbuddy.util.Constants.SELECTED_LOCATION;
import static me.veganbuddy.veganbuddy.util.Constants.SHARETOFACEBOOK;
import static me.veganbuddy.veganbuddy.util.Constants.SHARETOPINTEREST;
import static me.veganbuddy.veganbuddy.util.Constants.SHARETOTWITTER;
import static me.veganbuddy.veganbuddy.util.Constants.SHOW_EDIT_TEXT;
import static me.veganbuddy.veganbuddy.util.Constants.VEGANPHILOSOPHY;

import static me.veganbuddy.veganbuddy.util.FirebaseStorageUtils.getAppMessage;
import static me.veganbuddy.veganbuddy.util.FirebaseStorageUtils.getNextPicName;
import static me.veganbuddy.veganbuddy.util.FirebaseStorageUtils.retrieveMessageForTheDay;
import static me.veganbuddy.veganbuddy.util.FirebaseStorageUtils.setNextPicName;
import static me.veganbuddy.veganbuddy.util.SocialMediaUtils.initializeTwitter;
import static me.veganbuddy.veganbuddy.util.SocialMediaUtils.loginToFaceBook;
import static me.veganbuddy.veganbuddy.util.SocialMediaUtils.loginToPinterest;
import static me.veganbuddy.veganbuddy.util.SocialMediaUtils.loginToTwitter;


//Todo: Convert to Tabbed Activity for the options of  - "Choose from gallery" and "Video"
public class MealPhoto extends AppCompatActivity  implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    ImageView mealImageView;
    Boolean repeatClick = false;
    public FusedLocationProviderClient mFusedLocationProviderClient;
    protected Location mLastLocation;

    private AddressResultReceiver addressResultReceiver;
    ArrayList <String> locationList;
    LocationRecyclerViewAdapter locationRecyclerViewAdapter;

    LinearLayout linearLayoutAddLocation;
    LinearLayout linearLayoutSelectedLocation;
    LinearLayout linearLayoutSocialIcons;
    TextView textViewLocationLabel;
    EditText editTextVeganPh;
    ImageButton imageButtonSend;
    TextView textViewLocationSelected;
    Boolean facebook;
    Boolean pinterest;
    Boolean twitter;
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
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationList = new ArrayList<>();

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getLastKnownLocation();
        }
        else {
            Toast.makeText(this, "App needs Location Permission", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions
                    (this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constants.LOCATION_PERMITTED);
        }

        LinearLayoutManager linearLayoutManager
                = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false);
        locationRecyclerViewAdapter = new LocationRecyclerViewAdapter(locationList);
        RecyclerView locationRV = findViewById(R.id.amp_rv_location_list);
        locationRV.setLayoutManager(linearLayoutManager);
        locationRV.setAdapter(locationRecyclerViewAdapter);
        handleSocialMediaSharingOptions();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.LOCATION_PERMITTED: getLastKnownLocation();
            break;
            default: System.exit(0);
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
        String mealPhotoPath;
        int photoLength = -939;
        int photoWidth = - 939;
        boolean rotate = false;

        super.onStart();
        Bundle extras = getIntent().getExtras();
        mealPhotoPath = extras.getString("PhotoFilePath");
        try {
            ExifInterface exifInterface = new ExifInterface(mealPhotoPath);
            photoLength = exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, -939);
            photoWidth = exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, -939);
          } catch (IOException IOE) {
            IOE.printStackTrace();
            Log.i(MP_TAG, "IO error in getting Exif data from camera photo");
        }

        if (photoWidth > photoLength) {
            rotate = true;
        }
        //Create a thumbnail of the received photo and load into Imageview
        File photoThumbnail = BitmapUtils.createThumbnail
                (mealPhotoPath, getExternalFilesDir(Environment.DIRECTORY_PICTURES), rotate);
        //save thumbnail URI in BitmapUtils
        Uri photoThumbnailUri = Uri.fromFile(photoThumbnail);
        BitmapUtils.photoThumbnailUri = photoThumbnailUri;
        mealImageView = findViewById(R.id.amp_image_food);
        Picasso.with(this).load(photoThumbnailUri).into(mealImageView);


        //check App Message to be displayed in the Meal Preview Screen
        // If it is null, then set it to First_PIC
        if (getAppMessage()==null) {
            retrieveMessageForTheDay(FIRST_PIC_NAME);
            setNextPicName(FIRST_PIC_NAME);
            Log.v(MP_TAG, "Vegan Message file for the day is: " + getNextPicName() +
            " \n And Vegan Message for the day is: " + getAppMessage());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (showEditTextFlag) updateUI(SHOW_EDIT_TEXT);
        else updateUI(HIDE_EDIT_TEXT);
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
            Intent previewPhotoIntent = new Intent(this, MealPreviewPhoto.class);
            previewPhotoIntent.putExtra(VEGANPHILOSOPHY, inputText);
            previewPhotoIntent.putExtra(SELECTED_LOCATION, selectedLocation);
            previewPhotoIntent.putExtra(SHARETOFACEBOOK, facebook);
            previewPhotoIntent.putExtra(SHARETOPINTEREST, pinterest);
            previewPhotoIntent.putExtra(SHARETOTWITTER, twitter);
            startActivity(previewPhotoIntent);
            repeatClick = false;
        } else {
            Snackbar noCommentFeedback = Snackbar
                    .make(view, "Any comments about the picture?", Snackbar.LENGTH_SHORT);
            noCommentFeedback.show();
            repeatClick = true;
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

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
        if (view.getId() == R.id.amp_add_location) {
            Toast.makeText(this, view.toString() + " To insert Places search API", Toast.LENGTH_SHORT).show();
        }
    }


    public void locationEditClick(View view) {
        updateUI(Constants.HIDE_EDIT_TEXT);
    }

    public void noLocationClick(View view) {
        textViewLocationSelected.setText(R.string.amp_no_location);
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

        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            switch (resultCode){
                case Constants.SUCCESS_RESULT:
                    if (addressList != null) {
                        for (Address eachAddress: addressList) {
                            String addressName = eachAddress.getPremises();
                            if (addressName == null) {
                                addressName = eachAddress.getAddressLine(0);
                            }
                            locationList.add(addressName);
                            locationRecyclerViewAdapter.notifyDataSetChanged();
                        }
                    }
                    break;
                case Constants.FAILURE_RESULT:
                    break;
            }
        }
    }

}
