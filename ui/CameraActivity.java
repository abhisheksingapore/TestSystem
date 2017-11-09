package me.veganbuddy.veganbuddy.ui;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaActionSound;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.crash.FirebaseCrash;
import com.wonderkiln.camerakit.CameraListener;
import com.wonderkiln.camerakit.CameraView;

import me.veganbuddy.veganbuddy.R;
import me.veganbuddy.veganbuddy.util.BitmapUtils;

import static com.wonderkiln.camerakit.CameraKit.Constants.FLASH_OFF;
import static com.wonderkiln.camerakit.CameraKit.Constants.FLASH_ON;
import static me.veganbuddy.veganbuddy.util.Constants.CA_TAG;
import static me.veganbuddy.veganbuddy.util.Constants.FLASH_OFF_MSG;
import static me.veganbuddy.veganbuddy.util.Constants.FLASH_ON_MSG;
import static me.veganbuddy.veganbuddy.util.Constants.FLASH_SETTING;
import static me.veganbuddy.veganbuddy.util.Constants.PICTURES_DIRECTORY_PERMISSION;
import static me.veganbuddy.veganbuddy.util.Constants.SHOW_CAMERA;
import static me.veganbuddy.veganbuddy.util.Constants.SHOW_PROGRESS;
import static me.veganbuddy.veganbuddy.util.Constants.SHUTTER_SOUND_OFF_MSG;
import static me.veganbuddy.veganbuddy.util.Constants.SHUTTER_SOUND_ON_MSG;
import static me.veganbuddy.veganbuddy.util.Constants.SHUTTER_SOUND_SETTING;

public class CameraActivity extends AppCompatActivity {

    CameraView cameraView;
    ProgressBar progressBar;
    ImageButton imageButtonClick;
    ImageButton imageButtonflash;
    ImageButton imageButtonShutterSound;

    SharedPreferences sharedPreferences;
    boolean permissionGranted = false;
    boolean flash;
    boolean shutterSound;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        cameraView = findViewById(R.id.act_camera_cameraview);
        progressBar = findViewById(R.id.act_camera_progress_bar);
        imageButtonClick = findViewById(R.id.act_camera_imageoverlay);
        imageButtonflash = findViewById(R.id.act_camera_flash);
        imageButtonShutterSound = findViewById(R.id.act_camera_sound);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        loadSharedPreferences();


            cameraView.setCameraListener( new CameraListener() {
                @Override
                public void onPictureTaken(byte[] picture) {
                        super.onPictureTaken(picture);
                         if (createFileAndSaveImage(picture)) startMealPhotoActivity();
                         finish();
                }
            });
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (permissionGranted) {
            //Start the Camera
            cameraView.start();

            //Update the UI to show the camera
            updateUI(SHOW_CAMERA);
        } else {
            checkPermissions();
            Toast.makeText(this, "Cannot start camera, as no permission to save pictures",
                    Toast.LENGTH_SHORT).show();
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
        cameraView.captureImage();
        //update UI to stop camera and show the ProgressBar
        updateUI(SHOW_PROGRESS);
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
            case SHOW_PROGRESS:
                cameraView.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                imageButtonClick.setBackgroundColor(getColor(R.color.colorFABpressed));
                imageButtonClick.setClickable(false);
                break;
            case SHOW_CAMERA:
                cameraView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                imageButtonClick.setBackgroundColor(getColor(R.color.colorBackground));
                imageButtonClick.setClickable(true);
                break;
        }
    }

    private void startMealPhotoActivity() {
        Intent intentMealPhoto = new Intent(this, MealPhoto.class);
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
        //If VeganBuddy Picture Folder does not exist then check if the user has
        //granted Permission to create the directory
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        //Request the user for Permission to create the directory (if applicable)
        if(permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                        {android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.ACCESS_FINE_LOCATION},
                    PICTURES_DIRECTORY_PERMISSION);
        } else permissionGranted = true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //if permission granted
        if (grantResults.length> 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            permissionGranted = true;
        }
        //Else if permission is not not granted
        else{
            Toast.makeText(this, "App cannot take a picture if you don't allow " +
                            "space to save it on your phone and Location permissions",
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
}
