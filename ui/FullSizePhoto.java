package me.veganbuddy.veganbuddy.ui;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.github.chrisbanes.photoview.PhotoView;
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
            actionBar.setDisplayHomeAsUpEnabled(false);
        }

        String photoUriStr = getIntent().getStringExtra(FULL_PHOTO_URI);
        if (photoUriStr != null || photoUriStr.length()==0) {
            Uri photoURI = Uri.parse(photoUriStr);
            final PhotoView photoView = findViewById(R.id.afsp_iv_full_photo);
            Picasso.with(this).load(photoURI).into(photoView);
        }
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
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.fpm_close:
                finish();
                return true;
             default: finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void fullPhotoClick(View view) {
        finish();
    }
}
