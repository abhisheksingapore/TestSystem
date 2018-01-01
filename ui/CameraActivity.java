package me.veganbuddy.veganbuddy.ui;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaActionSound;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.wonderkiln.camerakit.CameraKitEventCallback;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraView;

import me.veganbuddy.veganbuddy.R;
import me.veganbuddy.veganbuddy.services.FetchPlacesIntentService;
import me.veganbuddy.veganbuddy.util.BitmapUtils;

import static com.wonderkiln.camerakit.CameraKit.Constants.FLASH_OFF;
import static com.wonderkiln.camerakit.CameraKit.Constants.FLASH_ON;
import static me.veganbuddy.veganbuddy.util.BitmapUtils.getStringStatsImageUri;
import static me.veganbuddy.veganbuddy.util.BitmapUtils.setStringStatsImageUri;
import static me.veganbuddy.veganbuddy.util.Constants.CA_TAG;
import static me.veganbuddy.veganbuddy.util.Constants.FIRST_PIC_NAME;
import static me.veganbuddy.veganbuddy.util.Constants.FLASH_OFF_MSG;
import static me.veganbuddy.veganbuddy.util.Constants.FLASH_ON_MSG;
import static me.veganbuddy.veganbuddy.util.Constants.FLASH_SETTING;
import static me.veganbuddy.veganbuddy.util.Constants.MP_CLASS_TAG;
import static me.veganbuddy.veganbuddy.util.Constants.PERMISSIONS;
import static me.veganbuddy.veganbuddy.util.Constants.SHOW_CAMERA;
import static me.veganbuddy.veganbuddy.util.Constants.SHUTTER_SOUND_OFF_MSG;
import static me.veganbuddy.veganbuddy.util.Constants.SHUTTER_SOUND_ON_MSG;
import static me.veganbuddy.veganbuddy.util.Constants.SHUTTER_SOUND_SETTING;
import static me.veganbuddy.veganbuddy.util.Constants.STOP_CLICK;
import static me.veganbuddy.veganbuddy.util.FirebaseStorageUtils.getAppMessage;
import static me.veganbuddy.veganbuddy.util.FirebaseStorageUtils.getNextPicName;
import static me.veganbuddy.veganbuddy.util.FirebaseStorageUtils.getStatsPicReference;
import static me.veganbuddy.veganbuddy.util.FirebaseStorageUtils.retrieveMessageForTheDay;
import static me.veganbuddy.veganbuddy.util.FirebaseStorageUtils.setNextPicName;
import static me.veganbuddy.veganbuddy.util.GlobalVariables.myDashboard;

public class CameraActivity extends AppCompatActivity {

    CameraView cameraView;
    ImageButton imageButtonClick;
    ImageButton imageButtonflash;
    ImageButton imageButtonShutterSound;

    SharedPreferences sharedPreferences;

    boolean storagePermissionGranted = false;
    boolean locationPermissionGranted = false;

    boolean flash;
    boolean shutterSound;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        cameraView = findViewById(R.id.act_camera_cameraview);
        imageButtonClick = findViewById(R.id.act_camera_imageoverlay);
        imageButtonflash = findViewById(R.id.act_camera_flash);
        imageButtonShutterSound = findViewById(R.id.act_camera_sound);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        loadSharedPreferences();

        //check App Message to be displayed in the Meal Preview Screen
        // If it is null, then set it to First_PIC
        if (getAppMessage() == null) {
            retrieveMessageForTheDay(FIRST_PIC_NAME);
            setNextPicName(FIRST_PIC_NAME);
            myDashboard.setLastPicName(FIRST_PIC_NAME);
            Log.v(MP_CLASS_TAG, "Vegan Message file for the day is: " + getNextPicName() +
                    " \n And Vegan Message for the day is: " + getAppMessage());
        }

        //load appMessagePic
        retrieveStatsImageUri();
    }

    private void retrieveStatsImageUri() {
        StorageReference statsPicReference = getStatsPicReference(getNextPicName());
        final Task<Uri> uriTask = statsPicReference.getDownloadUrl();
        uriTask.addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                setStringStatsImageUri(task.getResult().toString());
                Picasso.with(getBaseContext()).load(getStringStatsImageUri()).fetch();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_small_activities, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.fpm_close:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void loadSharedPreferences() {
        flash = sharedPreferences.getBoolean(FLASH_SETTING, false);
        shutterSound = sharedPreferences.getBoolean(SHUTTER_SOUND_SETTING, true);
        setFlashAndShutterClick();
    }

    private void setFlashAndShutterClick() {
        if (flash) {
            cameraView.setFlash(FLASH_ON);
            imageButtonflash.setContentDescription(FLASH_ON_MSG);
            imageButtonflash.setImageDrawable(getDrawable(R.drawable.ic_flash_on_24dp));
        } else {
            cameraView.setFlash(FLASH_OFF);
            imageButtonflash.setContentDescription(FLASH_OFF_MSG);
            imageButtonflash.setImageDrawable(getDrawable(R.drawable.ic_flash_off_24dp));
        }

        if (shutterSound) {
            imageButtonShutterSound.setContentDescription(SHUTTER_SOUND_ON_MSG);
            imageButtonShutterSound.setImageDrawable(getDrawable(R.drawable.ic_volume_black_24dp));
        } else {
            imageButtonShutterSound.setContentDescription(SHUTTER_SOUND_OFF_MSG);
            imageButtonShutterSound.setImageDrawable(getDrawable(R.drawable.ic_volume_off_black_24dp));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkPermissions();
        if (locationPermissionGranted) startPlacesIntentService();
        else checkPermissions();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (storagePermissionGranted) {
            //Start the Camera
            cameraView.start();

            //Update the UI to show the camera
            updateUI(SHOW_CAMERA);
        } else {
            checkPermissions();
        }
    }

    @Override
    protected void onPause() {
        cameraView.stop();
        saveSharedPreferences();
        super.onPause();
    }

    public void cameraClick(View view) {
        //Play the Shutter Click Sound, if NOT mute
        if (shutterSound) new MediaActionSound().play(MediaActionSound.SHUTTER_CLICK);
        //Capture the photo in the cameraView
        cameraView.captureImage(new CameraKitEventCallback<CameraKitImage>() {
            @Override
            public void callback(CameraKitImage cameraKitImage) {
                if (createFileAndSaveImage(cameraKitImage.getJpeg())) {
                    //start the next activity
                    startMealPreviewPhotoActivity();
                    BitmapUtils.createThumbnail();
                }
            }
        });
        //update UI to prevent multiple image clicks
        updateUI(STOP_CLICK);

    }

    private void saveSharedPreferences() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(FLASH_SETTING, flash);
        editor.putBoolean(SHUTTER_SOUND_SETTING,shutterSound);
        editor.apply();
        editor.commit();
    }

    private void updateUI(int setView) {
        switch (setView){
            case STOP_CLICK:
                imageButtonClick.setBackgroundColor(getColor(R.color.colorFABpressed));
                imageButtonClick.setClickable(false);
                break;
            case SHOW_CAMERA:
                imageButtonClick.setBackgroundColor(getColor(R.color.colorBackground));
                imageButtonClick.setClickable(true);
                break;
        }
    }

    private void startMealPreviewPhotoActivity() {
        Intent intentMealPhoto = new Intent(this, MealPreviewPhoto.class);
        startActivity(intentMealPhoto);
    }

    private boolean createFileAndSaveImage(byte[] jpeg) {
        Uri thisPhotoUri = null;

        //if VeganBuddy Picture Folder exists then create the Meal Photo. veganBuddyFolderExists
        // will create the folder if it does not find one
        if (BitmapUtils.veganBuddyFolderExists()) thisPhotoUri = BitmapUtils.createMealPhotoFile(jpeg);
        else {
            //if failed to create VeganBuddy Picture Folder then log appropriate messages and
            // return false to exit from this activity without moving forward
            Toast.makeText(this, "Unable to create Pictures directory",
                    Toast.LENGTH_SHORT).show();
            FirebaseCrash.log("Issue with creating/accessing pictures directory on this " +
                    "phone. mkdirs() is returning FALSE");
            Log.v(CA_TAG, "Issue with creating/accessing pictures directory on this phone." +
                    " mkdirs() is returning FALSE.");
            return false;
        }

        if (thisPhotoUri!=null) {
            //add Photo to Media Gallery of the phone
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            //Add to gallery
            mediaScanIntent.setData(thisPhotoUri);
            this.sendBroadcast(mediaScanIntent);
        }

        return true;
    }

    private void checkPermissions() {
        //Check if the user has granted Permission to create the directory
        int permissionCheckStorage = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        //Check for Location Permission
        int permissionCheckLocation = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheckStorage == PackageManager.PERMISSION_GRANTED) {
            storagePermissionGranted = true;
        }

        if (permissionCheckLocation == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        }

        if (!storagePermissionGranted || !locationPermissionGranted) {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS);
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //if permission granted store the permission in relevant variables
        if (requestCode == PERMISSIONS) {
            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    storagePermissionGranted = true;
                if (grantResults[1] == PackageManager.PERMISSION_GRANTED)
                    locationPermissionGranted = true;
            }
        }

        // if any of the permission is  not granted
        if (!storagePermissionGranted || !locationPermissionGranted) {
            Toast.makeText(this, "App cannot take a picture without required permissions",
                    Toast.LENGTH_LONG).show();
            //exit the activity after a message
            finish();
        }
    }

    public void flashClick(View view) {
        //If the Flash is set to "flashOff"
        if (view.getContentDescription().equals(FLASH_OFF_MSG)) {
            cameraView.setFlash(FLASH_ON);
            view.setContentDescription(FLASH_ON_MSG);
            ((ImageButton) view).setImageDrawable(getDrawable(R.drawable.ic_flash_on_24dp));
            flash = true;
        } else {
            cameraView.setFlash(FLASH_OFF);
            view.setContentDescription(FLASH_OFF_MSG);
            ((ImageButton) view).setImageDrawable(getDrawable(R.drawable.ic_flash_off_24dp));
            flash = false;
        }
    }

    public void soundClick(View view) {
        if (view.getContentDescription().equals(SHUTTER_SOUND_OFF_MSG)) {
            imageButtonShutterSound.setContentDescription(SHUTTER_SOUND_ON_MSG);
            imageButtonShutterSound.setImageDrawable(getDrawable(R.drawable.ic_volume_black_24dp));
            shutterSound = true;
        } else {
            imageButtonShutterSound.setContentDescription(SHUTTER_SOUND_OFF_MSG);
            imageButtonShutterSound.setImageDrawable(getDrawable(R.drawable.ic_volume_off_black_24dp));
            shutterSound = false;
        }
    }

    protected void startPlacesIntentService() {
        Intent intent = new Intent(this, FetchPlacesIntentService.class);
        startService(intent);
    }
}
