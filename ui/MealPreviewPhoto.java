package me.veganbuddy.veganbuddy.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;

import me.veganbuddy.veganbuddy.R;
import me.veganbuddy.veganbuddy.actors.User;
import me.veganbuddy.veganbuddy.util.BitmapUtils;
import me.veganbuddy.veganbuddy.util.FirebaseStorageUtils;

import static me.veganbuddy.veganbuddy.util.DateAndTimeUtils.dateStamp;
import static me.veganbuddy.veganbuddy.util.DateAndTimeUtils.dateTimeStamp;

public class MealPreviewPhoto extends AppCompatActivity {

    private String previewImagePath;
    private static String veganPhilosophyText;
    private String imageFileName;
    private Uri imageURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_preview_photo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User.mealsForToday = User.mealsForToday + 1;
                reloadDashBoard();
                UploadPhotoAndDashboardData uploadPhotoAndDashboardData
                        = new UploadPhotoAndDashboardData(imageURI, imageFileName);
                uploadPhotoAndDashboardData.execute();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Bundle extras = getIntent().getExtras();
        veganPhilosophyText = extras.getString("VeganPhilosophy");
        imageURI = BitmapUtils.photoThumbnailUri;
        ImageView imageView = (ImageView) findViewById(R.id.iv_preview_food_image);
        Picasso.with(this).load(imageURI).into(imageView);
        TextView textView = (TextView) findViewById(R.id.tv_preview_comments);
        textView.setText(veganPhilosophyText);
    }

    private void reloadDashBoard() {
        Intent intent = new Intent(this, LandingPage.class);
        startActivity(intent);
    }

    class UploadPhotoAndDashboardData extends AsyncTask <Void, Void, Void> {
        private String imageFileName;
        private Uri imageURI;
        private StorageReference mStorageRef;

        //Todo: also upload the thumbnail for the Placards Display

        UploadPhotoAndDashboardData( Uri path, String name) {
            imageURI = path;
            imageFileName = name;
            mStorageRef = FirebaseStorage.getInstance().getReference();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            uploadPhotoToCloudStorage(); //Both thumbnail and full size
            return null;
        }


        private void uploadPhotoToCloudStorage() {
            StorageReference photoReference = mStorageRef.child(uniqueImagePath());
            photoReference.putFile(imageURI)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(MealPreviewPhoto.this, "Photo Upload Successful!", Toast.LENGTH_SHORT).show();
                            BitmapUtils.setPhotoURL (taskSnapshot.getDownloadUrl().toString());
                            uploadDataToCloudDatabase(); //Including the comments, the meal type, the path etc.
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MealPreviewPhoto.this, "Photo Upload Failed!", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        private void uploadDataToCloudDatabase() {
            FirebaseUser thisUser = User.thisAppUser;
            FirebaseStorageUtils.addNodesToDatabase(thisUser.getEmail(), thisUser, 1,
                    BitmapUtils.getPhotoURL(), veganPhilosophyText);
        }

         private String uniqueImagePath() {
            String path = User.thisAppUser.getEmail().toString()+ "/fullSize/" + imageFileName;
            return path;
         }
    }


}