package me.veganbuddy.veganbuddy.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaActionSound;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.wonderkiln.camerakit.CameraKitEventCallback;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraView;

import me.veganbuddy.veganbuddy.R;

import static me.veganbuddy.veganbuddy.util.BitmapUtils.createProfilePictureFile;
import static me.veganbuddy.veganbuddy.util.Constants.PERMISSIONS;
import static me.veganbuddy.veganbuddy.util.Constants.PROFILE_PICTURE_CAMERA;

public class CameraActivityProfilePicture extends AppCompatActivity {

    boolean storagePermissionGranted = false;
    CameraView cameraView;
    String profilePicUriString;


    @Override
    protected void onResume() {
        super.onResume();
        cameraView.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraView.stop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_profile_picture);
        cameraView = findViewById(R.id.acpp_camera_cameraview);

        checkPermissions();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_small_activities, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            case R.id.fpm_close:
                finish();
                return true;
            default:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }


    private void setResult() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(PROFILE_PICTURE_CAMERA, profilePicUriString);
        setResult(Activity.RESULT_OK, resultIntent);
    }

    private boolean createFileAndSaveImage(byte[] jpeg) {
        profilePicUriString = createProfilePictureFile(jpeg).toString();
        return profilePicUriString != null;
    }


    private void checkPermissions() {
        //Check if the user has granted Permission to create the directory
        int permissionCheckStorage = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionCheckStorage == PackageManager.PERMISSION_GRANTED) {
            storagePermissionGranted = true;
        }

        if (!storagePermissionGranted) {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS);
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
            }
        }

        // if any of the permission is  not granted
        if (!storagePermissionGranted) {
            Toast.makeText(this, "App cannot take a picture without required permissions",
                    Toast.LENGTH_LONG).show();
            //exit the activity after a message
            finish();
        }
    }

    public void cameraProfileClick(View view) {
        //Play the Shutter Click Sound
        new MediaActionSound().play(MediaActionSound.SHUTTER_CLICK);
        //Capture the photo in the cameraView
        cameraView.captureImage(new CameraKitEventCallback<CameraKitImage>() {
            @Override
            public void callback(CameraKitImage cameraKitImage) {
                if (createFileAndSaveImage(cameraKitImage.getJpeg())) {
                    //start the next activity
                    setResult();
                }
                finish();
            }
        });
    }
}
