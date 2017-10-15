package me.veganbuddy.veganbuddy.ui;

import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import me.veganbuddy.veganbuddy.R;

import static me.veganbuddy.veganbuddy.util.Constants.FULL_PHOTO_URI;


public class FullSizePhoto extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_size_photo);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        String photoUriStr = getIntent().getStringExtra(FULL_PHOTO_URI);

        if (photoUriStr != null || photoUriStr.length()==0) {
        Uri photoURI = Uri.parse(photoUriStr);
        ImageView imageView = findViewById(R.id.afsp_iv_full_photo);
            Picasso.with(this).load(photoURI).into(imageView);
        }
    }

    public void fullPhotoClick(View view) {
        finish();
    }
}
