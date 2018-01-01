package me.veganbuddy.veganbuddy.ui;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import me.veganbuddy.veganbuddy.R;
import me.veganbuddy.veganbuddy.actors.MyPlace;
import me.veganbuddy.veganbuddy.actors.User;

import static me.veganbuddy.veganbuddy.util.Constants.DISPLAY_DATE_FORMAT;
import static me.veganbuddy.veganbuddy.util.Constants.FIREBASE_USER_PROFILE_PIC_FOLDER;
import static me.veganbuddy.veganbuddy.util.Constants.MYPLACE_ADDRESS;
import static me.veganbuddy.veganbuddy.util.Constants.MYPLACE_ID;
import static me.veganbuddy.veganbuddy.util.Constants.MYPLACE_NAME;
import static me.veganbuddy.veganbuddy.util.Constants.REQUEST_PHOTO_NEW;
import static me.veganbuddy.veganbuddy.util.Constants.SCHEME_FIREBASE_STORAGE;
import static me.veganbuddy.veganbuddy.util.Constants.SCHEME_LOCAL_FILE;
import static me.veganbuddy.veganbuddy.util.Constants.UP_TAG;
import static me.veganbuddy.veganbuddy.util.FirebaseStorageUtils.setMyPlacesData;
import static me.veganbuddy.veganbuddy.util.FirebaseStorageUtils.setUserData;
import static me.veganbuddy.veganbuddy.util.GlobalVariables.thisAppUser;
import static me.veganbuddy.veganbuddy.util.SocialMediaUtils.loginToFaceBook;
import static me.veganbuddy.veganbuddy.util.SocialMediaUtils.loginToPinterest;
import static me.veganbuddy.veganbuddy.util.SocialMediaUtils.loginToTwitter;

public class UserProfile extends AppCompatActivity {

    private static final int PLACE_PICKER_REQUEST = 1;
    private static boolean MADE_CHANGES = false;
    MyPlace myPlaceSelected;
    User tempUser; //A placeholder User object to temporarily store and manipulate the variables
    // of thisAppUser

    EditText textVeganStartDate;

    Toolbar toolbar;

    //Social Media Variables
    SharedPreferences sharedPreferences;
    Boolean facebook;
    Boolean pinterest;
    Boolean twitter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        toolbar = findViewById(R.id.aup_toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.aup_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveAndClose();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tempUser = new User(thisAppUser);
        loadCurrentUserProfile();
        loadSocialMediaPreferences();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_small_activities, menu);
        return true;
    }


    private void loadCurrentUserProfile() {
        ImageView imageViewProfile;
        EditText textName;
        EditText textVeganPhil;
        EditText textWebsite;
        EditText textEmail;
        TextView textCity;

        imageViewProfile = findViewById(R.id.cup_profile_pic);
        textName = findViewById(R.id.cup_profile_name);
        textVeganPhil = findViewById(R.id.cup_vegan_phil);
        textWebsite = findViewById(R.id.cup_website);
        textEmail = findViewById(R.id.cup_email);
        textCity = findViewById(R.id.cup_city);
        textVeganStartDate = findViewById(R.id.cup_vegan_start_date);

        Picasso.with(this).load(tempUser.getPhotoUrl())
                .placeholder(R.drawable.veganbuddylogo_stamp_small).into(imageViewProfile);

        textName.setText(tempUser.getUserName());
        textVeganPhil.setText(tempUser.getVeganphilosophy());
        textWebsite.setText(tempUser.getWebsite());
        textEmail.setText(tempUser.getEmail());
        textCity.setText(tempUser.getCity());
        textVeganStartDate.setText(tempUser.getStartDateOfVegan());

        setEditTextChangeListeners(textName, textVeganPhil, textWebsite, textEmail);

        //if the photoUrl is not pointing to the Firebase storage OR a Local File Storage
        // then update it. This is to catch the event that the user has logged in for the first
        // time and his profile picture is pointing to a Google profile picture
        if ((Uri.parse(tempUser.getPhotoUrl()).getScheme().equals(SCHEME_FIREBASE_STORAGE)) ||
                (Uri.parse(tempUser.getPhotoUrl()).getScheme().equals(SCHEME_LOCAL_FILE))) {
            Log.v(UP_TAG, "GS storage file is available");
        } else {
            editProfilePic(findViewById(R.id.cup_edit_profile_pic));
            Log.e(UP_TAG, "GS storage file is not available. Trying to create it");
        }

        loadGenderRadioValue(tempUser.getGender());
        loadRelationshipStatusValue(tempUser.getRelationShipStatus());

    }

    private void loadRelationshipStatusValue(String relationShipStatus) {
        if (relationShipStatus == null || relationShipStatus.length() == 0) return;

        if (relationShipStatus.equals(getString(R.string.cup_relation_single)))
            ((RadioButton) findViewById(R.id.cup_relationship_status_single)).setChecked(true);

        if (relationShipStatus.equals(getString(R.string.cup_relation_married)))
            ((RadioButton) findViewById(R.id.cup_relationship_status_married)).setChecked(true);

        if (relationShipStatus.equals(getString(R.string.cup_relation_attached_engaged)))
            ((RadioButton) findViewById(R.id.cup_relationship_status_engaged)).setChecked(true);

        if (relationShipStatus.equals(getString(R.string.cup_relation_its_complicated)))
            ((RadioButton) findViewById(R.id.cup_relationship_status_complicated)).setChecked(true);

        if (relationShipStatus.equals(getString(R.string.cup_relation_prefer_not_to_say)))
            ((RadioButton) findViewById(R.id.cup_relationship_status_not_say)).setChecked(true);
    }

    private void loadGenderRadioValue(String value) {
        if (value == null || value.length() == 0) return;

        //All strings are being reused between user profile (cup) and buddy profile (cbp). So
        // prefixes may be used across. Have to mange with caution
        if (value.equals(getString(R.string.cup_male)))
            ((RadioButton) findViewById(R.id.cup_gender_male)).setChecked(true);

        if (value.equals(getString(R.string.cup_female)))
            ((RadioButton) findViewById(R.id.cup_gender_female)).setChecked(true);

        if (value.equals(getString(R.string.cup_gender_other)))
            ((RadioButton) findViewById(R.id.cup_gender_other)).setChecked(true);

        if (value.equals(getString(R.string.cup_relation_prefer_not_to_say)))
            ((RadioButton) findViewById(R.id.cup_gender_not_say)).setChecked(true);

    }

    private void setEditTextChangeListeners(EditText textName, EditText textVeganPhil,
                                            EditText textWebsite, EditText textEmail) {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                MADE_CHANGES = true;
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
        textName.addTextChangedListener(textWatcher);
        textVeganPhil.addTextChangedListener(textWatcher);
        textWebsite.addTextChangedListener(textWatcher);
        textEmail.addTextChangedListener(textWatcher);
    }

    @Override
    public void onBackPressed() {
        if (performSaveCheck()) super.onBackPressed();
    }

    private boolean performSaveCheck() {
        if (MADE_CHANGES) {
            SaveOnExitDialogFragment saveOnExitDialogFragment = new SaveOnExitDialogFragment();
            saveOnExitDialogFragment.show(getSupportFragmentManager(), "SaveOnExitDialogFragment");
            return false;
        } else return true;
    }

    private void saveAndClose() {
        String errorMessage = "";

        //Validate date
        String veganDate = ((EditText) findViewById(R.id.cup_vegan_start_date)).getText().toString();
        if (!TextUtils.isEmpty(veganDate)) {
            if (isValidDate(veganDate)) tempUser.setStartDateOfVegan(veganDate);
            else errorMessage = "Date, ";
        }

        //Validate website
        String websiteAdd = ((EditText) findViewById(R.id.cup_website)).getText().toString();
        if (!TextUtils.isEmpty(websiteAdd)) {
            if (isValidWebsite(websiteAdd)) tempUser.setWebsite(websiteAdd);
            else errorMessage = errorMessage.concat("Website, ");
        }

        //validate email
        String emailAdd = ((EditText) findViewById(R.id.cup_email)).getText().toString();
        if (!TextUtils.isEmpty(emailAdd)) {
            if (isValidEmail(emailAdd)) tempUser.setEmail(emailAdd);
            else errorMessage = errorMessage.concat("Email address, ");
        }

        //validate name not empty and at least 2 characters
        String name = ((TextView) findViewById(R.id.cup_profile_name)).getText().toString();
        if (name.length() < 2) {
            Toast.makeText(this, "Your Display Name must be longer than 2 characters",
                    Toast.LENGTH_LONG).show();
            return;
        }

        if (errorMessage.trim().length() > 1) Toast.makeText(this,
                errorMessage + " not saved. You may retry", Toast.LENGTH_SHORT).show();

        //Upload new profile pic to FirebaseStorage
        if (!((tempUser.getPhotoUrl()).equals(thisAppUser.getPhotoUrl()))) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            StorageReference profilePicReference = storageReference.child(profilePicFullPath());
            final Uri profilePicUriLocal = Uri.parse(tempUser.getPhotoUrl());
            UploadTask uploadTaskProfilePic = profilePicReference.putFile(profilePicUriLocal);

            //Uri from should be a local file
            if (!profilePicUriLocal.getScheme().equals(SCHEME_LOCAL_FILE)) {
                Toast.makeText(this, "Error creating local file", Toast.LENGTH_SHORT).show();
                return;
            }

            uploadTaskProfilePic.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    //Save all datafields of user profile to Firebase database
                    if (tempUser != null) {
                        //first update the photourl in tempuser
                        tempUser.setPhotoUrl(taskSnapshot.getStorage().toString());
                        //update thisAppUser
                        thisAppUser = tempUser;
                        //save data of thisAppUser
                        setUserData();
                        //refresh Picasso
                        Picasso.with(getBaseContext()).invalidate(tempUser.getPhotoUrl());
                        Picasso.with(getBaseContext()).load(tempUser.getPhotoUrl()).fetch();
                        //inform user of success
                        Toast.makeText(UserProfile.this, "User Profile Updated!",
                                Toast.LENGTH_SHORT).show();
                        //Delete local file
                        File fileToDelete = new File(profilePicUriLocal.getPath());
                        Uri uriFileToDelete = Uri.fromFile(fileToDelete);
                        if (fileToDelete.delete()) {
                            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                            mediaScanIntent.setData(uriFileToDelete);
                            getBaseContext().sendBroadcast(mediaScanIntent);
                        }
                        //reload landing page
                        startActivity(new Intent(getBaseContext(), LandingPage.class));
                    }
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UserProfile.this,
                                    "Error in saving user profile picture",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            if (MADE_CHANGES = true) {
                if (tempUser != null) {
                    //update thisAppUser
                    thisAppUser = tempUser;
                    //save data of thisAppUser
                    setUserData();
                    //inform user of success
                    Toast.makeText(UserProfile.this, "User Profile Updated!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }

        //Add selected place to My Places
        if (myPlaceSelected != null) setMyPlacesData(myPlaceSelected);

        //Save social media preferences and location chosen
        writeToSharedPreferences();

        //reset MADE_CHANGES
        MADE_CHANGES = false;

        super.onBackPressed();
    }


    private String profilePicFullPath() {
        return tempUser.getFireBaseID() + FIREBASE_USER_PROFILE_PIC_FOLDER +
                tempUser.getUserName().replaceAll("\\s", "") + "ProfilePic.jpg";
    }

    private void writeToSharedPreferences() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(getResources().getString(R.string.pref_social_facebook), facebook);
        editor.putBoolean(getResources().getString(R.string.pref_social_pinterest), pinterest);
        editor.putBoolean(getResources().getString(R.string.pref_social_twitter), twitter);

        if (myPlaceSelected != null) {
            editor.putString(MYPLACE_ID, myPlaceSelected.getPlaceId());
            editor.putString(MYPLACE_NAME, myPlaceSelected.getPlaceName());
            editor.putString(MYPLACE_ADDRESS, myPlaceSelected.getPlaceAddress());
        }
        editor.apply();
    }

    private boolean isValidEmail(String emailAdd) {
        return (Patterns.EMAIL_ADDRESS.matcher(emailAdd).matches());
    }

    private boolean isValidWebsite(String websiteAdd) {
        return (Patterns.WEB_URL.matcher(websiteAdd).matches());
    }

    private boolean isValidDate(String veganDate) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DISPLAY_DATE_FORMAT);
            Date date = simpleDateFormat.parse(veganDate);
            if (date != null) return true;
        } catch (ParseException PE) {
            FirebaseCrash.log(PE.getMessage());
            Log.e(UP_TAG, PE.getMessage());
        }
        return false;
    }


    public void editProfilePic(View view) {
        MADE_CHANGES = true;
        Intent intentProfilePic = new Intent(this, UserProfilePicture.class);
        startActivityForResult(intentProfilePic, REQUEST_PHOTO_NEW);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (performSaveCheck()) return super.onOptionsItemSelected(item);
            case R.id.fpm_close:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void chooseDate(View view) {
        DialogFragment dialogFragment = new DatePickerDialogFragment();
        dialogFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void setDate(int day, int month, int year) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DISPLAY_DATE_FORMAT);
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        String chosenDate = simpleDateFormat.format(calendar.getTime());
        textVeganStartDate.setText(chosenDate);
        tempUser.setStartDateOfVegan(chosenDate);
        MADE_CHANGES = true;
    }

    public void genderChoiceClick(View view) {
        switch (view.getId()) {
            case R.id.cup_gender_male:
                tempUser.setGender(getString(R.string.cup_male));
                break;
            case R.id.cup_gender_female:
                tempUser.setGender(getString(R.string.cup_female));
                break;
            case R.id.cup_gender_not_say:
                tempUser.setGender(getString(R.string.cup_gender_prefer_not_to_say));
                break;
            case R.id.cup_gender_other:
                tempUser.setGender(getString(R.string.cup_gender_other));
                break;
        }
        MADE_CHANGES = true;
    }

    public void relationshipStatusClick(View view) {
        switch (view.getId()) {
            case R.id.cup_relationship_status_single:
                tempUser.setRelationShipStatus(getString(R.string.cup_relation_single));
                break;
            case R.id.cup_relationship_status_not_say:
                tempUser.setRelationShipStatus(getString(R.string.cup_relation_prefer_not_to_say));
                break;
            case R.id.cup_relationship_status_married:
                tempUser.setRelationShipStatus(getString(R.string.cup_relation_married));
                break;
            case R.id.cup_relationship_status_engaged:
                tempUser.setRelationShipStatus(getString(R.string.cup_relation_attached_engaged));
                break;
            case R.id.cup_relationship_status_complicated:
                tempUser.setRelationShipStatus(getString(R.string.cup_relation_its_complicated));
                break;
        }
        MADE_CHANGES = true;
    }

    public void locationAddClick(View view) {
        Intent intentPlacePicker;
        //Prepare intent for launching PlacePicker
        try {
            PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
            intentPlacePicker = intentBuilder.build(this);
            startActivityForResult(intentPlacePicker, PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            Log.e(UP_TAG, e.getMessage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                TextView textCity = findViewById(R.id.cup_city);
                Place place = PlacePicker.getPlace(this, data);
                textCity.setText(place.getName());
                myPlaceSelected = new MyPlace(place);
                tempUser.setCity(place.getName().toString());
                MADE_CHANGES = true;
            }
        }

        if (requestCode == REQUEST_PHOTO_NEW) {
            if (resultCode == RESULT_OK) {
                Uri resultUri = data.getData();
                ImageView imageView = findViewById(R.id.cup_profile_pic);
                if (resultUri != null) {
                    tempUser.setPhotoUrl(resultUri.toString());
                    Picasso.with(this).load(resultUri)
                            .placeholder(R.drawable.veganbuddylogo_stamp_small).into(imageView);
                    MADE_CHANGES = true;
                }
            }
        }
    }


    public void onDialogPositiveClick() {
        saveAndClose();
    }

    public void onDialogNegativeClick() {
        MADE_CHANGES = false;
        finish();
    }

    private void loadSocialMediaPreferences() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        facebook = sharedPreferences.getBoolean(getResources().getString(R.string.pref_social_facebook), false);
        pinterest = sharedPreferences.getBoolean(getResources().getString(R.string.pref_social_pinterest), false);
        twitter = sharedPreferences.getBoolean(getResources().getString(R.string.pref_social_twitter), false);

        handleSocialMediaSharingOptions();
    }

    private void handleSocialMediaSharingOptions() {

        //Set Icon colors based on current user preferences. Set to coloured versions, if
        // settings are "true" and user is logged in. Set to Black and White for whether the
        // user is not logged in or whether the user has made his setting as "false"
        ImageView facebookIcon = findViewById(R.id.cup_fb);
        ImageView pinterestIcon = findViewById(R.id.cup_pinterest);
        ImageView twitterIcon = findViewById(R.id.cup_twitter);

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

    public void facebookClick(View view) {
        ImageView facebookIcon = (ImageView) view;
        if (!facebook) { //if clicked when facebook is set to 'false'
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

    public void pinterestClick(View view) {
        ImageView pinterestIcon = (ImageView) view;
        if (!pinterest) {//if clicked when Pinterest is set to 'False'
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


    public static class SaveOnExitDialogFragment extends DialogFragment {

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.userprofile_save_message)
                    .setPositiveButton(R.string.userprofile_save_exit, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ((UserProfile) getContext()).onDialogPositiveClick();
                        }
                    })
                    .setNegativeButton(R.string.userprofile_exit_no_save, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ((UserProfile) getContext()).onDialogNegativeClick();
                        }
                    });
            return builder.create();
        }

    }

    public static class DatePickerDialogFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            ((UserProfile) getContext()).setDate(day, month, year);
        }
    }

}
