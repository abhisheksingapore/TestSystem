package me.veganbuddy.veganbuddy.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.share.model.AppInviteContent;
import com.facebook.share.widget.AppInviteDialog;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

import me.veganbuddy.veganbuddy.R;
import me.veganbuddy.veganbuddy.actors.Dashboard;
import me.veganbuddy.veganbuddy.util.BitmapUtils;
import me.veganbuddy.veganbuddy.util.Constants;
import me.veganbuddy.veganbuddy.util.DateAndTimeUtils;
import me.veganbuddy.veganbuddy.util.FirebaseStorageUtils;

import static me.veganbuddy.veganbuddy.util.BitmapUtils.createTempUploadFile;
import static me.veganbuddy.veganbuddy.util.Constants.CURRENT_USER;
import static me.veganbuddy.veganbuddy.util.Constants.DASHBOARD_NODE;
import static me.veganbuddy.veganbuddy.util.Constants.DO_NOT_INCREASE;
import static me.veganbuddy.veganbuddy.util.Constants.FULL_PHOTO_URI;
import static me.veganbuddy.veganbuddy.util.Constants.HEART_EMPTY;
import static me.veganbuddy.veganbuddy.util.Constants.HEART_FULL;
import static me.veganbuddy.veganbuddy.util.Constants.LP_TAG;
import static me.veganbuddy.veganbuddy.util.Constants.NO_LIKES;
import static me.veganbuddy.veganbuddy.util.Constants.ONE_LIKE;
import static me.veganbuddy.veganbuddy.util.Constants.REQUEST_IMAGE_CAPTURE;
import static me.veganbuddy.veganbuddy.util.Constants.app_link_url;
import static me.veganbuddy.veganbuddy.util.Constants.app_logo;
import static me.veganbuddy.veganbuddy.util.FirebaseStorageUtils.mFirebaseAuth;
import static me.veganbuddy.veganbuddy.util.FirebaseStorageUtils.retrieveAllPostsData;
import static me.veganbuddy.veganbuddy.util.FirebaseStorageUtils.retrieveApplicablePicName;
import static me.veganbuddy.veganbuddy.util.FirebaseStorageUtils.retrieveCommentsData;
import static me.veganbuddy.veganbuddy.util.FirebaseStorageUtils.retrieveMessageForTheDay;
import static me.veganbuddy.veganbuddy.util.FirebaseStorageUtils.retrievePostsData;
import static me.veganbuddy.veganbuddy.util.FirebaseStorageUtils.setDashboardData;
import static me.veganbuddy.veganbuddy.util.GlobalVariables.googleApiClient;
import static me.veganbuddy.veganbuddy.util.GlobalVariables.myDashboard;
import static me.veganbuddy.veganbuddy.util.GlobalVariables.thisAppUser;


public class LandingPage extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        LandingPageFragment.OnFragmentInteractionListener{
    //Todo: Implement Landscape view of all the features

    //Todo: Implement Vegan Questions - "How many days vegan?", "Fulltime Vegan/PartTime" -
    // to Calculate the number of animals saved before starting to use the app

    private static String mCurrentPhotoPath;
    private static Uri photoUri;

    //instances of Local classes to create the sections and fragments for this view
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takeFoodPhoto();
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        createUserProfile();

        // Create the adapter that will return a fragment for each of the
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container_for_fragments);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        if (myDashboard != null) {
            //retrieve the app Message that will be displayed along with the meal photos
            retrieveMessageForTheDay(retrieveApplicablePicName());
            //retrieve posts of the user
            retrievePostsData();
            //retrieve posts of the entire "user group"
            retrieveAllPostsData();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Toast.makeText(this, "Back Button Pressed but there is nothing to go back to", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.three_dots_settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {

            case R.id.refresh:
                Log.v(LP_TAG, "Refreshing data on user request - manual click");
                refreshAllFragments();
                break;
            case R.id.action_settings:
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            break;
            case R.id.lp_sign_out:
                signOutThisUser();
                finish();
                break;
            case R.id.lp_swap_user:
                revokeAccess();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!myDashboard.todayExistsInDashboard()) {
            Toast.makeText(this, "DATE CHANGED! Refreshing Dashboard...", Toast.LENGTH_LONG).show();
            //This condition will happen when the app was left in onPause() state across
            // local midnight. So if date changes, the dashboard should be refreshed
            //An entry for todays date will not exist in database, so "mealsForToday" is
            //for last entered Date.
            myDashboard.setMealsForToday(0);
            setDashboardData(DO_NOT_INCREASE);
            refreshAllFragments();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_veganalytics:;
                break;
            case R.id.nav_notifications:;
                break;
            case R.id.nav_manual_entry:;
                break;
            case R.id.nav_messages:;
                break;
            case R.id.nav_meal_mate:;
                break;
            case R.id.nav_share_fb: shareOnFaceBook();
            break;
            case R.id.nav_share_twitter:shareOnTwitter();
            break;
            case R.id.nav_share_whatsapp: shareOnWhatsApp();
            break;
            case R.id.nav_social_logins:
                Intent intentSocialLogins = new Intent(this, SocialLoginsActivity.class);
                startActivity(intentSocialLogins);
                break;
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void revokeAccess() {
        mFirebaseAuth.signOut(); //Signout from Firebase
        thisAppUser = null;
        myDashboard = null;

        try {
            Auth.GoogleSignInApi.revokeAccess(googleApiClient)
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            Toast.makeText(LandingPage.this, "Revoke Successful",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (IllegalStateException ISE) {
            Log.e(LP_TAG, "Error while revoking access for the GoogleApiClient");
            ISE.printStackTrace();
        }

        //Reload the login page
        Intent login = new Intent(this, LoginActivity.class);
        startActivity(login);
    }

    private void signOutThisUser() {
        mFirebaseAuth.signOut(); //Signout from Firebase
        thisAppUser = null;

        //Reload the login page
        Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(loginIntent);
    }

    private void shareOnFaceBook() {
        if (AppInviteDialog.canShow()){
            AppInviteContent content = new AppInviteContent.Builder()
                    .setApplinkUrl(app_link_url)
                    .setPreviewImageUrl(app_logo)
                    .build();
            AppInviteDialog.show(this,content);
        }
    }

    private void shareOnTwitter() {
        Toast.makeText(this, "Twitter App Card Invite - Yet to be implemented", Toast.LENGTH_SHORT).show();
    }

    private void shareOnWhatsApp() {
        Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
        whatsappIntent.setType("text/plain");
        whatsappIntent.setPackage("com.whatsapp");
        whatsappIntent.putExtra(Intent.EXTRA_TEXT, "Please try out this app - VeganBuddy. " +
                "It's a very easy way to record your daily Vegan meals. #EveryMealMatters. "+ app_link_url);
        try {
            startActivity(whatsappIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Whatsapp has not been installed. Please install and " +
                    "try again", Toast.LENGTH_SHORT).show();
        }
    }



    public void createUserProfile () {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);

        //Set Profile Picture
        ImageView profilePicView = (ImageView) headerView.findViewById(R.id.userProfilePic);
        try {
            String photoUrl = thisAppUser.getPhotoUrl();
            Picasso.with(getBaseContext()).load(photoUrl).into(profilePicView);

            //Set Profile Name
            TextView profileName = headerView.findViewById(R.id.userName);
            profileName.setText(thisAppUser.getUserName());

            //Set Profile Email
            TextView profileEmail = headerView.findViewById(R.id.userEmail);
            profileEmail.setText(thisAppUser.getEmail());
        }catch (NullPointerException npe) {
            Log.e(LP_TAG, "Null pointer Exception happened while creating user profile");
            npe.printStackTrace();
        }
    }

    //Method to Invoke Camera Action for taking photo of the food/Meal
    private void takeFoodPhoto() {
        //Create the file to save the photo
        File photoFile = null;
        try {
            photoFile = createPhotoFile();
        } catch (IOException IOE){
            IOE.printStackTrace();
            Log.d(LP_TAG, "IO Exception happened while creating the file for saving the image");
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
        BitmapUtils.setImageFileName(photoFile.getName());
        return photoFile;
    }

    private void startTheCameraAndSavePhoto(File photoFile) {

        try {
             photoUri = FileProvider.getUriForFile(this,
                     Constants.FILE_PROVIDER_AUTHORITY, photoFile);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            Log.v(LP_TAG, e.toString());
        }

        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);

        if (takePhotoIntent.resolveActivity(getPackageManager())!=null) {
            startActivityForResult(takePhotoIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void addPhotoToGallery() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);

        //Todo: Optimize the creation of new file and new Uri
        File file = new File(mCurrentPhotoPath);
        photoUri = Uri.fromFile(file);

        //Save path and Uri for the full Size photo on the phone
        BitmapUtils.setPhotoUri(photoUri);
        BitmapUtils.setPhotoPath(mCurrentPhotoPath);

        //Add to gallery
        mediaScanIntent.setData(photoUri);
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
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onFragmentInteraction(int position) {
        //Todo: Implement the fragment to activity interaction here
    }

    public void heartClick(View view) {
        boolean newValue = false;
        int newLikeCount = 0;
        int currentLikeCount;

        ImageView heart = view.findViewById(R.id.pi_heart_icon);
        String heartStatus = heart.getContentDescription().toString();
        RelativeLayout viewParent = (RelativeLayout) view.getParent();
        CardView viewGrandParent = (CardView) viewParent.getParent().getParent();
        TextView textViewUserName = viewGrandParent.findViewById(R.id.pi_profile_name);
        String userFirebaseID = textViewUserName.getContentDescription().toString();

        TextView likesCount = viewGrandParent.findViewById(R.id.pi_likes_count);
        String dateTimeStampID = viewGrandParent.getContentDescription().toString();

        String numberofLikes = likesCount.getText().toString();
        switch (numberofLikes) {
            case NO_LIKES: currentLikeCount = 0;
            break;
            case ONE_LIKE: currentLikeCount = 1;
            break;
            default: currentLikeCount = getLikeCount (numberofLikes);
            break;
        }

        if (heartStatus == HEART_FULL) {
            //implies that currentValue of boolean "iLike" is true;
            newValue = false;
            newLikeCount = currentLikeCount - 1; //Reduce like by 1
        }

        if (heartStatus == HEART_EMPTY) {
            //implies that currentValue of boolean "iLike" is false;
            newValue = true;
            newLikeCount = currentLikeCount + 1; //Increase like by 1
        }


        if(userFirebaseID.equals(CURRENT_USER)) //If Placard belongs to current user listing
        FirebaseStorageUtils.updateIlike (dateTimeStampID, newValue, newLikeCount);
        else //If placard belongs to ALL POSTS listing
        FirebaseStorageUtils.updateIlike (dateTimeStampID, newValue, newLikeCount, userFirebaseID);
    }


    private int getLikeCount(String numberofLikes) {
        if (numberofLikes !=null && numberofLikes !=""){
            String theDigits = numberofLikes.replaceAll("\\D+","");
            return Integer.parseInt(theDigits);
        } else return 0;
    }

    public void commentsClick(View view) {
        RelativeLayout viewParent = (RelativeLayout) view.getParent();
        CardView viewGrandParent = (CardView) viewParent.getParent().getParent();
        ImageView thisPhoto = viewGrandParent.findViewById(R.id.pi_food_photo);
        String thisPhotoUri = thisPhoto.getContentDescription().toString();
        String nodeID = viewGrandParent.getContentDescription().toString();
        retrieveCommentsData(nodeID);

        Intent commentsIntent = new Intent(this, CommentsActivity.class);
        commentsIntent.putExtra("ImageURI", thisPhotoUri);
        commentsIntent.putExtra("nodeID", nodeID);
        startActivity(commentsIntent);
    }

    public void shareThisPhoto(View view) {
        Intent shareIntent = new Intent();
        RelativeLayout viewParent = (RelativeLayout) view.getParent();
        CardView viewGrandParent = (CardView) viewParent.getParent().getParent();
        ImageView thisPhoto = viewGrandParent.findViewById(R.id.pi_food_photo);
        Bitmap tempImage = ((BitmapDrawable)thisPhoto.getDrawable()).getBitmap();
        File tempImageFile = createTempUploadFile(tempImage,
                getExternalFilesDir(Environment.DIRECTORY_PICTURES));
        Uri tempImageFileUri = Uri.fromFile(tempImageFile);

        //TODO: try the ACTION_SENDTO
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, tempImageFileUri);
        startActivity(Intent.createChooser(shareIntent, "Share this photo..."));
    }

    public void numberofLikesClick(View view) {
        //ToDo: Display List of people who liked this placard
        Toast.makeText(this, "ToDo: Display List of people who liked this placard",
                Toast.LENGTH_SHORT).show();
    }

    public void photoClick(View view) {
        ImageView clickedPhoto = view.findViewById(R.id.pi_food_photo);
        String fullsizeImageUriStr = "";

        CharSequence fullsizeImageUriContentDescription = clickedPhoto.getContentDescription();
        if (fullsizeImageUriContentDescription!=null) {
            fullsizeImageUriStr = clickedPhoto.getContentDescription().toString();
        }
        Intent intentFullSizePhoto = new Intent(this, FullSizePhoto.class);
        intentFullSizePhoto.putExtra(FULL_PHOTO_URI, fullsizeImageUriStr);
        startActivity(intentFullSizePhoto);
    }

    private void refreshAllFragments() {
        Intent intentDataRefresh = new Intent(this, DataRefreshActivity.class);
        startActivity(intentDataRefresh);
    }


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
            return LandingPageFragment.newInstance(position);
        }


        @Override
        public int getCount() {
            // Show  total pages.
            return 4;
        }
    }
}
