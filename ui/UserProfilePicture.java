package me.veganbuddy.veganbuddy.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import me.veganbuddy.veganbuddy.R;
import me.veganbuddy.veganbuddy.util.Constants;

import static me.veganbuddy.veganbuddy.util.BitmapUtils.createProfilePictureFile;
import static me.veganbuddy.veganbuddy.util.Constants.GOOGLE_PHOTOS_PACKAGE_NAME;
import static me.veganbuddy.veganbuddy.util.Constants.MED_QUALITY;
import static me.veganbuddy.veganbuddy.util.Constants.PROFILE_PICTURE_CAMERA;
import static me.veganbuddy.veganbuddy.util.Constants.REQUEST_PHOTO_FROM_CAMERA;
import static me.veganbuddy.veganbuddy.util.Constants.REQUEST_PHOTO_FROM_GALLERY;
import static me.veganbuddy.veganbuddy.util.Constants.REQUEST_PHOTO_FROM_GOOGLE_PHOTOS;
import static me.veganbuddy.veganbuddy.util.Constants.SCHEME_FIREBASE_STORAGE;
import static me.veganbuddy.veganbuddy.util.Constants.SCHEME_LOCAL_FILE;
import static me.veganbuddy.veganbuddy.util.GlobalVariables.thisAppUser;

public class UserProfilePicture extends AppCompatActivity {

    PhotoView photoView;
    Uri finalSelectedUri;

    public static boolean isGooglePhotosInstalled(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            return packageManager.getPackageInfo(GOOGLE_PHOTOS_PACKAGE_NAME,
                    PackageManager.GET_ACTIVITIES) != null;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_picture);
        photoView = findViewById(R.id.aupp_user_pic);

        Picasso.with(this).load(thisAppUser.getPhotoUrl())
                .placeholder(R.drawable.veganbuddylogo_stamp_small)
                .into(photoView);

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
                super.onOptionsItemSelected(item);
                return true;
            case R.id.fpm_close:
                //Catch the odd situation where the user clicks the close button the first time
                // that profile picture is being created for a new user
                if (!(Uri.parse(thisAppUser.getPhotoUrl()).getScheme().equals(SCHEME_FIREBASE_STORAGE))) {
                    Uri uriImageView = createProfilePictureFile(profilePicToBytes());
                    Intent intentresult = new Intent();
                    intentresult.setData(uriImageView);
                    setResult(Activity.RESULT_OK, intentresult);
                }
                finish();
                return true;
            default:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void profilePicSourceClick(android.view.View view) {
        switch (view.getId()) {
            case R.id.aupp_gallery:
                launchGalleryPhotosPicker();
                break;
            case R.id.aupp_google:
                launchGooglePhotosPicker(this);
                break;
            case R.id.aupp_camera:
                Intent intentCamera = new Intent(this, CameraActivityProfilePicture.class);
                startActivityForResult(intentCamera, Constants.REQUEST_PHOTO_FROM_CAMERA);
                break;
        }
    }

    private void launchGalleryPhotosPicker() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),
                REQUEST_PHOTO_FROM_GALLERY);
    }

    public void launchGooglePhotosPicker(Activity callingActivity) {
        if (callingActivity != null && isGooglePhotosInstalled(this)) {
            Intent intentGoogle = new Intent();
            intentGoogle.setAction(Intent.ACTION_PICK);
            intentGoogle.setType("image/*");
            intentGoogle.setPackage(GOOGLE_PHOTOS_PACKAGE_NAME);
            callingActivity.startActivityForResult(intentGoogle, REQUEST_PHOTO_FROM_GOOGLE_PHOTOS);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_PHOTO_FROM_CAMERA) {
            if (resultCode == RESULT_OK) {
                final String resultUri = data.getStringExtra(PROFILE_PICTURE_CAMERA);

                //add Photo to Media Gallery of the phone
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                //Add to gallery
                mediaScanIntent.setData(Uri.parse(resultUri));
                this.sendBroadcast(mediaScanIntent);
                //Inform the user
                Toast.makeText(this,
                        "Photo saved in Gallery", Toast.LENGTH_SHORT).show();

                Picasso.with(this).load(resultUri)
                        .placeholder(R.drawable.veganbuddylogo_stamp_small)
                        .into(photoView, new Callback() {
                            @Override
                            public void onSuccess() {
                                finalSelectedUri = Uri.parse(resultUri);
                            }

                            @Override
                            public void onError() {
                                finalSelectedUri = null;
                            }
                        });
            }
        }

        if (requestCode == REQUEST_PHOTO_FROM_GALLERY) {
            if (resultCode == RESULT_OK) {
                final Uri resultUri = data.getData();
                if (resultUri != null) {
                    Picasso.with(this).load(resultUri)
                            .placeholder(R.drawable.veganbuddylogo_stamp_small)
                            .into(photoView, new Callback() {
                                @Override
                                public void onSuccess() {
                                    finalSelectedUri = resultUri;
                                }

                                @Override
                                public void onError() {
                                    finalSelectedUri = null;
                                }
                            });
                } else {
                    Toast.makeText(this,
                            "Unable to load selected image from Gallery", Toast.LENGTH_SHORT).show();
                }
            }
        }

        if (requestCode == REQUEST_PHOTO_FROM_GOOGLE_PHOTOS) {
            if (resultCode == RESULT_OK) {
                final Uri resultUri = data.getData();
                if (resultUri != null) {
                    Picasso.with(this).load(resultUri)
                            .placeholder(R.drawable.veganbuddylogo_stamp_small)
                            .into(photoView, new Callback() {
                                @Override
                                public void onSuccess() {
                                    finalSelectedUri = resultUri;
                                }

                                @Override
                                public void onError() {
                                    finalSelectedUri = null;
                                }
                            });
                } else {
                    Toast.makeText(this,
                            "Unable to load selected image from Google Photos", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private byte[] profilePicToBytes() {
        ImageView imageView = findViewById(R.id.aupp_user_pic);
        imageView.setForeground(null);
        imageView.setDrawingCacheEnabled(true);
        Bitmap bitmapNew = imageView.getDrawingCache();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmapNew.compress(Bitmap.CompressFormat.JPEG, MED_QUALITY, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public void selectionDoneClick(View view) {
        Intent intentResult = new Intent();

        if (finalSelectedUri != null && finalSelectedUri.getScheme().equals(SCHEME_LOCAL_FILE))
            intentResult.setData(finalSelectedUri);
        else {
            Uri uriImageView = createProfilePictureFile(profilePicToBytes());
            intentResult.setData(uriImageView);
        }

        setResult(Activity.RESULT_OK, intentResult);
        finish();
    }
}
