package me.veganbuddy.veganbuddy.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import me.veganbuddy.veganbuddy.R;
import me.veganbuddy.veganbuddy.util.BitmapUtils;
import me.veganbuddy.veganbuddy.util.DateAndTimeUtils;

import static android.graphics.Bitmap.Config.ARGB_8888;

//Todo: Convert to Tabbed Activity for the options of  - "Choose from gallery" and "Video"
public class MealPhoto extends AppCompatActivity {
    private static String mealPhotoPath;
    private static String appendedAnimalsImagePath;
    ImageView mealImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_photo);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Bundle extras = getIntent().getExtras();
        mealPhotoPath = extras.getString("PhotoFilePath");
        File photoThumbnail = BitmapUtils.createThumbnail
                (mealPhotoPath, getExternalFilesDir(Environment.DIRECTORY_PICTURES));
        Uri photoThumbnailUri = Uri.fromFile(photoThumbnail);
        BitmapUtils.photoThumbnailUri = photoThumbnailUri;
        mealImageView = (ImageView) findViewById(R.id.image_food);
        Picasso.with(this).load(photoThumbnailUri).into(mealImageView);
    }

    public void shareMyPhoto (View view) {
        EditText editText = (EditText) findViewById(R.id.editText_food);
        String inputText = editText.getText().toString();
        Intent previewPhotoIntent = new Intent(this, MealPreviewPhoto.class);
        previewPhotoIntent.putExtra("VeganPhilosophy", inputText);
        startActivity(previewPhotoIntent);
    }

}
