package me.veganbuddy.veganbuddy.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.security.auth.login.LoginException;

import me.veganbuddy.veganbuddy.R;
import me.veganbuddy.veganbuddy.actors.Dashboard;
import me.veganbuddy.veganbuddy.util.BitmapUtils;
import me.veganbuddy.veganbuddy.util.DateAndTimeUtils;
import me.veganbuddy.veganbuddy.actors.User;
import me.veganbuddy.veganbuddy.util.FirebaseStorageUtils;
import me.veganbuddy.veganbuddy.util.MeatMathUtils;

import static me.veganbuddy.veganbuddy.util.MeatMathUtils.GREEN_COLOR;
import static me.veganbuddy.veganbuddy.util.MeatMathUtils.RED_COLOR;
import static me.veganbuddy.veganbuddy.util.MeatMathUtils.YELLOW_COLOR;

public class LandingPage extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        LandingPageFragment.OnFragmentInteractionListener{
    //Todo: Implement Landscape view of all the features

    //Todo: Implement Vegan Questions - "How many days vegan?", "Fulltime Vegan/PartTime" -
    // to Calculate the number of animals saved before starting to use the app

    private FirebaseUser currentUser;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static String TAG = "LandingPage.Java";
    private static String mCurrentPhotoPath;

    //instances of Local classes to create the sections and fragments for this view
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takeFoodPhoto();
            }
        });
        //Todo: Retrieve and set the user profile

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        createUserProfile();

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container_for_fragments);
        mViewPager.setAdapter(mSectionsPagerAdapter);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.landing_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void createUserProfile () {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        currentUser = User.thisAppUser;


        //Set Profile Picture
        ImageView profilePicView = (ImageView) headerView.findViewById(R.id.userProfilePic);
        try {
            String photoUrl = currentUser.getPhotoUrl().toString();
            Picasso.with(getBaseContext()).load(photoUrl).into(profilePicView);
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }

        //Set Profile Name
        TextView profileName = (TextView) headerView.findViewById(R.id.userName);
        profileName.setText(currentUser.getDisplayName());

        //Set Profile Email
        TextView profileEmail = (TextView) headerView.findViewById(R.id.userEmail);
        profileEmail.setText(currentUser.getEmail());

    }

    //Method to Invoke Camera Action for taking photo of the food/Meal
    private void takeFoodPhoto() {
        //Create the file to save the photo
        File photoFile = null;
        try {
            photoFile = createPhotoFile();
        } catch (IOException IOE){
            IOE.printStackTrace();
            Log.d(TAG, "IO Exception happened while creating the file for saving the image");
        }

        //Start the camera to take the photo and save it to the created file once clicked
        if (photoFile !=null) {
            startTheCameraAndSavePhoto (photoFile);
            //Add Photo to the Gallery
            addPhotoToGallery();
        }

    }

    private File createPhotoFile() throws IOException {
        File photoFile = null;
        String mealType = DateAndTimeUtils.getMealTypeBasedOnTimeOfTheDay();
        String timeStamp = DateAndTimeUtils.dateTimeStamp();
        String photoFileName = mealType + timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        photoFile = File.createTempFile(photoFileName, ".jpg", storageDir);
        mCurrentPhotoPath = photoFile.getAbsolutePath();
        return photoFile;
    }

    private void startTheCameraAndSavePhoto(File photoFile) {
        Uri photoUri = null;
        try {
             photoUri = FileProvider.getUriForFile(this,
                     BitmapUtils.FILE_PROVIDER_AUTHORITY, photoFile);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            Log.v(TAG, e.toString());
        }

        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);

        if (takePhotoIntent.resolveActivity(getPackageManager())!=null) {
            startActivityForResult(takePhotoIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void addPhotoToGallery() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File file = new File(mCurrentPhotoPath);
        Uri uri = Uri.fromFile(file);
        mediaScanIntent.setData(uri);
        this.sendBroadcast(mediaScanIntent);
    }

    //Check the photo taken and open the MealPhoto Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent takePhotoIntent) {
        if ((requestCode==REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK)) {

            Intent mealPhotoIntent = new Intent(this, MealPhoto.class);
            //put the extras Bundle into intent for the new activity

            mealPhotoIntent.putExtra("PhotoFilePath", mCurrentPhotoPath );
            startActivity(mealPhotoIntent);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    public void onFragmentInteraction(Uri uri) {
        Toast.makeText(this, "Testing Context", Toast.LENGTH_SHORT).show();
    }


    /***
     *
     * Private classes of Sections and Fragments
     *
     */

    /**
     * A placeholder fragment containing a simple view.
     */
    /*public static class PlaceholderFragment extends Fragment {
        *//**
         * The fragment argument representing the section number for this
         * fragment.
         *//*
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        *//**
         * Returns a new instance of this fragment for the given section
         * number.
         *//*
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.content_landing_page, container, false);
            return rootView;
        }
    }*/

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).

            return LandingPageFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            // Show  total pages.
            return 4;
        }
    }

}
