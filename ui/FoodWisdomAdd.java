package me.veganbuddy.veganbuddy.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import me.veganbuddy.veganbuddy.R;
import me.veganbuddy.veganbuddy.actors.FoodWisdom;
import me.veganbuddy.veganbuddy.util.Constants;

import static me.veganbuddy.veganbuddy.ui.FoodWisdomActivity.FOOD_WISDOM_COUNT;
import static me.veganbuddy.veganbuddy.util.Constants.FOOD_WISDOM_USER_FOLDER;
import static me.veganbuddy.veganbuddy.util.Constants.GOOGLE_PHOTOS_PACKAGE_NAME;
import static me.veganbuddy.veganbuddy.util.Constants.MED_QUALITY;
import static me.veganbuddy.veganbuddy.util.Constants.PROFILE_PICTURE_CAMERA;
import static me.veganbuddy.veganbuddy.util.Constants.REQUEST_PHOTO_FROM_CAMERA;
import static me.veganbuddy.veganbuddy.util.Constants.REQUEST_PHOTO_FROM_GALLERY;
import static me.veganbuddy.veganbuddy.util.Constants.REQUEST_PHOTO_FROM_GOOGLE_PHOTOS;
import static me.veganbuddy.veganbuddy.util.FirebaseStorageUtils.setFoodWisdomData;
import static me.veganbuddy.veganbuddy.util.FirebaseStorageUtils.setFoodWisdomDataForThisUser;
import static me.veganbuddy.veganbuddy.util.GlobalVariables.thisAppUser;

public class FoodWisdomAdd extends AppCompatActivity {

    boolean MADE_CHANGES = false;
    PhotoView photoView;
    Uri finalSelectedUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_wisdom_add);
        Toolbar toolbar = findViewById(R.id.afwa_toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.afwa_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        photoView = findViewById(R.id.cfwa_background_pic);
        TextView textViewName = findViewById(R.id.cfwa_creator);
        ImageView imageViewProfilePic = findViewById(R.id.cfwa_creator_profile_pic);

        textViewName.setText(thisAppUser.getUserName());
        Picasso.with(this).load(thisAppUser.getPhotoUrl()).into(imageViewProfilePic);

        final EditText editTextFoodWisdom = findViewById(R.id.cfwa_food_wisdom_text);
        final TextView textViewFoodWisdom = findViewById(R.id.cfwa_food_wisdom_caption);

        editTextFoodWisdom.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                textViewFoodWisdom.setText(editable);
                MADE_CHANGES = true;
            }
        });
    }

    public void foodWisdomPicSourceClick(View view) {
        switch (view.getId()) {
            case R.id.cfwa_gallery:
                launchGalleryPhotosPicker();
                break;
            case R.id.cfwa_google:
                launchGooglePhotosPicker(this);
                break;
            case R.id.cfwa_camera:
                Intent intentCamera = new Intent(this, CameraActivityFoodWisdom.class);
                startActivityForResult(intentCamera, Constants.REQUEST_PHOTO_FROM_CAMERA);
                break;
        }

    }


    private void launchGooglePhotosPicker(Activity callingActivity) {
        if (callingActivity != null && isGooglePhotosInstalled(this)) {
            Intent intentGoogle = new Intent();
            intentGoogle.setAction(Intent.ACTION_PICK);
            intentGoogle.setType("image/*");
            intentGoogle.setPackage(GOOGLE_PHOTOS_PACKAGE_NAME);
            callingActivity.startActivityForResult(intentGoogle, REQUEST_PHOTO_FROM_GOOGLE_PHOTOS);
        }
    }

    private boolean isGooglePhotosInstalled(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            return packageManager.getPackageInfo(GOOGLE_PHOTOS_PACKAGE_NAME,
                    PackageManager.GET_ACTIVITIES) != null;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private void launchGalleryPhotosPicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),
                REQUEST_PHOTO_FROM_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_PHOTO_FROM_CAMERA) {
            if (resultCode == RESULT_OK) {
                final String resultUri = data.getStringExtra(PROFILE_PICTURE_CAMERA);
                finalSelectedUri = Uri.parse(resultUri);

                //add Photo to Media Gallery of the phone
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                //Add to gallery
                mediaScanIntent.setData(finalSelectedUri);
                this.sendBroadcast(mediaScanIntent);
                //Inform the user
                Toast.makeText(this,
                        "Photo saved in Gallery", Toast.LENGTH_SHORT).show();

                Picasso.with(this).load(finalSelectedUri)
                        .placeholder(R.drawable.veganbuddylogo_stamp_small).fit()
                        .into(photoView);
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

    @Override
    public void onBackPressed() {
        if (!MADE_CHANGES) super.onBackPressed();
        else performSaveCheck();
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
                if (MADE_CHANGES) {
                    MADE_CHANGES = false;
                    performSaveCheck();
                } else super.onBackPressed();
                return true;
            case R.id.fpm_close:
                if (MADE_CHANGES) {
                    performSaveCheck();
                    MADE_CHANGES = false;
                } else finish();
                return true;
            default:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void selectionDoneClick(View view) {
        if (!MADE_CHANGES) finish();
        else saveAndClose();
    }

    private void saveAndClose() {
        if (finalSelectedUri != null) {
            //create a new image
            byte[] foodWisdomImage = foodWisdomToBytes();

            //upload to Firebase
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            StorageReference foodWisdomPicReference = storageReference.child(foodWisdomFullPath());
            UploadTask uploadTaskFoodWisdom = foodWisdomPicReference.putBytes(foodWisdomImage);

            uploadTaskFoodWisdom.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //create new foodWisdom data
                    FoodWisdom foodWisdomthis = new FoodWisdom(taskSnapshot.getStorage().toString());

                    //add to the foodWisdom node of Firebase
                    setFoodWisdomData(foodWisdomthis, Long.toString(FOOD_WISDOM_COUNT));

                    //add to myFoodWisdom node for thisAppUser
                    setFoodWisdomDataForThisUser(foodWisdomthis, Long.toString(FOOD_WISDOM_COUNT));

                    Toast.makeText(FoodWisdomAdd.this, "Food Wisdom uploaded",
                            Toast.LENGTH_SHORT).show();
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            FirebaseCrash.log("Upload FoodWisdom failure " + e.getMessage());
                        }
                    });

        } else Toast.makeText(this, "Error uploading Food Wisdom. Please retry!!",
                Toast.LENGTH_SHORT).show();
        finish();
    }

    private byte[] foodWisdomToBytes() {
        ConstraintLayout foodWisdom = findViewById(R.id.cfwa_food_wisdom);
        foodWisdom.setDrawingCacheEnabled(true);
        Bitmap bitmapNew = foodWisdom.getDrawingCache();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmapNew.compress(Bitmap.CompressFormat.JPEG, MED_QUALITY, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private String foodWisdomFullPath() {
        if (FOOD_WISDOM_COUNT > 0)
            return FOOD_WISDOM_USER_FOLDER + Long.toString(FOOD_WISDOM_COUNT) + ".jpg";
        else {
            FirebaseCrash.log("Issue getting the Firebase database count of foodWisdom children");
            return FOOD_WISDOM_USER_FOLDER + "939.jpg";
        }
    }

    private boolean performSaveCheck() {
        if (MADE_CHANGES) {
            FoodWisdomAdd.SaveOnExitDialogFragment saveOnExitDialogFragment = new FoodWisdomAdd.SaveOnExitDialogFragment();
            saveOnExitDialogFragment.show(getSupportFragmentManager(), "SaveOnExitDialogFragment");
            return false;
        } else return true;
    }

    public void onDialogPositiveClick() {
        saveAndClose();
    }

    public void onDialogNegativeClick() {
        super.onBackPressed();
        MADE_CHANGES = false;
    }

    public void changeBackground(View view) {
        TextView textViewName = findViewById(R.id.cfwa_creator);
        TextView textViewFoodWisdom = findViewById(R.id.cfwa_food_wisdom_caption);
        ImageView imageViewProfile = findViewById(R.id.cfwa_creator_profile_pic);

        if (view.equals(findViewById(R.id.cfwa_white_font))) {
            textViewName.setBackgroundColor(getColor(R.color.colorBlack));
            textViewName.setTextColor(getColor(R.color.whiteColor));
            textViewFoodWisdom.setTextColor(getColor(R.color.whiteColor));
            imageViewProfile.setForeground(getDrawable(R.drawable.circle_black_background));
        }
        if (view.equals(findViewById(R.id.cfwa_black_font))) {
            textViewName.setBackgroundColor(getColor(R.color.whiteColor));
            textViewName.setTextColor(getColor(R.color.colorBlack));
            textViewFoodWisdom.setTextColor(getColor(R.color.colorBlack));
            imageViewProfile.setForeground(getDrawable(R.drawable.circle_white_background));
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
                            ((FoodWisdomAdd) getContext()).onDialogPositiveClick();
                        }
                    })
                    .setNegativeButton(R.string.userprofile_exit_no_save, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ((FoodWisdomAdd) getContext()).onDialogNegativeClick();
                        }
                    });
            return builder.create();
        }

    }
}
