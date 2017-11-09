package me.veganbuddy.veganbuddy.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;

import me.veganbuddy.veganbuddy.R;
import me.veganbuddy.veganbuddy.util.BitmapUtils;
import me.veganbuddy.veganbuddy.util.FirebaseStorageUtils;

import static me.veganbuddy.veganbuddy.util.BitmapUtils.createTempImageFile;
import static me.veganbuddy.veganbuddy.util.BitmapUtils.createTempThumbFile;
import static me.veganbuddy.veganbuddy.util.Constants.FIREBASE_FULL_IMAGE_FOLDER;
import static me.veganbuddy.veganbuddy.util.Constants.FIREBASE_SCREENSHOT_FOLDER;
import static me.veganbuddy.veganbuddy.util.Constants.FIREBASE_THUMBNAIL_FOLDER;
import static me.veganbuddy.veganbuddy.util.Constants.MP_ASYNC_CLASS_TAG;
import static me.veganbuddy.veganbuddy.util.Constants.MP_CLASS_TAG;
import static me.veganbuddy.veganbuddy.util.Constants.SELECTED_LOCATION;
import static me.veganbuddy.veganbuddy.util.Constants.SHARETOFACEBOOK;
import static me.veganbuddy.veganbuddy.util.Constants.SHARETOPINTEREST;
import static me.veganbuddy.veganbuddy.util.Constants.SHARETOTWITTER;
import static me.veganbuddy.veganbuddy.util.Constants.STATS_IMAGE_URI;
import static me.veganbuddy.veganbuddy.util.Constants.VEGANPHILOSOPHY;
import static me.veganbuddy.veganbuddy.util.FirebaseStorageUtils.getAppMessage;
import static me.veganbuddy.veganbuddy.util.FirebaseStorageUtils.getNextPicName;
import static me.veganbuddy.veganbuddy.util.FirebaseStorageUtils.getStatsPicReference;
import static me.veganbuddy.veganbuddy.util.GlobalVariables.thisAppUser;
import static me.veganbuddy.veganbuddy.util.SocialMediaUtils.uploadToSocialMedia;

public class MealPreviewPhoto extends AppCompatActivity {

    private String veganPhilosophyText;
    private String selectedLocationText;
    private Uri imageURI;
    private Uri screenShotURI;
    private Uri screenshotThumbURI;
    private String mealPhotoName;
    public boolean uploadToFaceBook;
    public boolean tweetThisPic;
    public boolean pinThisPic;


    public TextView textViewAppMessage;
    public ImageView imageViewStatsPic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_preview_photo);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.ampp_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), DataRefreshActivity.class);
                startActivity(intent);

                saveScreenShot ();
                uploadPhotoAndDashboardData();
            }
        });

    }

    private void saveScreenShot() {
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
    protected void onStart() {
        super.onStart();
        ImageView imageView = findViewById(R.id.iv_preview_food_image);
        imageViewStatsPic = findViewById(R.id.cmpp_iv_preview_stats);
        textViewAppMessage = findViewById(R.id.cmpp_tv_app_message);
        Uri uriStatsImage = null;

        Bundle extras = getIntent().getExtras();
        try {
            veganPhilosophyText = extras.getString(VEGANPHILOSOPHY);
            selectedLocationText = extras.getString(SELECTED_LOCATION);
            uploadToFaceBook = extras.getBoolean(SHARETOFACEBOOK);
            tweetThisPic = extras.getBoolean(SHARETOTWITTER);
            pinThisPic = extras.getBoolean(SHARETOPINTEREST);
            uriStatsImage = Uri.parse(extras.getString(STATS_IMAGE_URI));
        } catch (NullPointerException NPE) {
            NPE.printStackTrace();
            FirebaseCrash.log("error in retrieving extras from the intent that created this " +
                    "activity");
            Log.e(MP_CLASS_TAG, "error in retrieving extras from the intent that created this" +
                    " activity");
        }

        String appMessage = getAppMessage();
        if (appMessage!=null && uriStatsImage!=null) {
            textViewAppMessage.setText(appMessage);
            Picasso.with(this)
                    .load(uriStatsImage)
                    .into(imageViewStatsPic);
        }else {
            textViewAppMessage.setText("Vegetarianism stops animal killing for food");
        }

        Uri thumbnailImageURI = BitmapUtils.getPhotoThumbnailUri();
        imageURI = BitmapUtils.getPhotoUri();
        mealPhotoName = BitmapUtils.getMealPhotoName();

        Picasso.with(this).load(thumbnailImageURI).placeholder(R.drawable.progressbar_image)
                .into(imageView);
        TextView textViewComment = findViewById(R.id.tv_preview_comments);
        TextView textViewLocation = findViewById(R.id.cmpp_tv_location);

        textViewComment.setText(veganPhilosophyText);
        textViewLocation.setText(selectedLocationText);
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
                        uploadToSocialMedia(uploadToFaceBook, tweetThisPic,pinThisPic,
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
        FirebaseStorageUtils.addNodesToDatabase(BitmapUtils.getPhotoURL(),
                BitmapUtils.getPhotoThumbnailURL(), BitmapUtils.getScreenShotURL(),
                veganPhilosophyText, selectedLocationText);
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

}
