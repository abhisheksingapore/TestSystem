package me.veganbuddy.veganbuddy.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
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
import com.google.firebase.crash.FirebaseCrash;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import me.veganbuddy.veganbuddy.R;
import me.veganbuddy.veganbuddy.actors.Post;
import me.veganbuddy.veganbuddy.actors.Vnotification;
import me.veganbuddy.veganbuddy.util.BitmapUtils;
import me.veganbuddy.veganbuddy.util.FirebaseStorageUtils;

import static me.veganbuddy.veganbuddy.util.BitmapUtils.createTempUploadFile;
import static me.veganbuddy.veganbuddy.util.Constants.DO_NOT_INCREASE;
import static me.veganbuddy.veganbuddy.util.Constants.FOLLOWERS;
import static me.veganbuddy.veganbuddy.util.Constants.FOLLOWING;
import static me.veganbuddy.veganbuddy.util.Constants.FULL_PHOTO_URI;
import static me.veganbuddy.veganbuddy.util.Constants.HEART_EMPTY;
import static me.veganbuddy.veganbuddy.util.Constants.HEART_FULL;
import static me.veganbuddy.veganbuddy.util.Constants.INBOUND;
import static me.veganbuddy.veganbuddy.util.Constants.LAST_PLACARDS_LAYOUT;
import static me.veganbuddy.veganbuddy.util.Constants.LAST_POSTS_NODE;
import static me.veganbuddy.veganbuddy.util.Constants.LP_TAG;
import static me.veganbuddy.veganbuddy.util.Constants.MY_PLACARDS_LAYOUT;
import static me.veganbuddy.veganbuddy.util.Constants.NOTIFICATIONS_INBOUND_LAYOUT;
import static me.veganbuddy.veganbuddy.util.Constants.NOTIFICATIONS_OUTBOUND_LAYOUT;
import static me.veganbuddy.veganbuddy.util.Constants.NO_LIKES;
import static me.veganbuddy.veganbuddy.util.Constants.ONE_LIKE;
import static me.veganbuddy.veganbuddy.util.Constants.ORIGIN_FRAGMENT;
import static me.veganbuddy.veganbuddy.util.Constants.OUTBOUND;
import static me.veganbuddy.veganbuddy.util.Constants.POSTID;
import static me.veganbuddy.veganbuddy.util.Constants.POSTS_NODE;
import static me.veganbuddy.veganbuddy.util.Constants.RELATION;
import static me.veganbuddy.veganbuddy.util.Constants.UPLOAD_PICTURE_MANUALLY;
import static me.veganbuddy.veganbuddy.util.Constants.app_link_url;
import static me.veganbuddy.veganbuddy.util.Constants.app_logo;
import static me.veganbuddy.veganbuddy.util.FirebaseStorageUtils.mFirebaseAuth;
import static me.veganbuddy.veganbuddy.util.FirebaseStorageUtils.retrieveCommentsData;
import static me.veganbuddy.veganbuddy.util.FirebaseStorageUtils.setDashboardData;
import static me.veganbuddy.veganbuddy.util.GlobalVariables.googleApiClient;
import static me.veganbuddy.veganbuddy.util.GlobalVariables.myDashboard;
import static me.veganbuddy.veganbuddy.util.GlobalVariables.thisAppUser;


public class LandingPage extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        LandingPageFragment.OnFragmentInteractionListener,
        VnotificationFragment.OnListFragmentInteractionListener,
        PlacardsFragment.OnListFragmentInteractionListener{
    //Todo: Implement Landscape view of all the features

    //Todo: Implement Vegan Questions - "How many days vegan?", "Fulltime Vegan/PartTime" -
    // to Calculate the number of animals saved before starting to use the app

    //instances of Local classes to create the sections and fragments for the Landing Page view
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);


            setContentView(R.layout.activity_landing_page);
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");//no Title


            toolbar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    veganBuddyClick(view);
                }
            });

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

            // Create the adapters that will return a fragment for each of the
            // primary sections of the activity.
            mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

            // Set up the ViewPagers with the respective sections adapter.
            mViewPager = findViewById(R.id.container_for_fragments);
            mViewPager.setAdapter(mSectionsPagerAdapter);
        } catch (Exception e) {
            if (myDashboard == null || thisAppUser == null) {
                //This situation will arise if the app has launched this activity without first retrieving
                // the myDashboard Data or it crashed on this activity and Android automatically tried
                // to resume it
                Intent intentLogin = new Intent(this, LoginActivity.class);
                startActivity(intentLogin);
            } else {
                FirebaseCrash.log(LP_TAG + e.getMessage());
                Log.e(LP_TAG, "Exception during loading of LandingPage" + e.getMessage());
            }
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
            case R.id.notifications:
                item.setIcon(R.drawable.ic_notifications_gold_24dp);
                loadNotificationsFragment();
                break;
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

    private void loadNotificationsFragment() {
        mViewPager.setCurrentItem(4);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (myDashboard == null ||  thisAppUser == null) {
            //This situation will arise if the app has launched this activity without first retrieving
            // the myDashboard Data or it crashed on this activity and AndroidOS automatically tried
            // to resume it
            Intent intentLogin = new Intent (this, LoginActivity.class);
            startActivity(intentLogin);
        } else if (!myDashboard.todayExistsInDashboard()) {
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
            case R.id.nav_veganalytics:
                Intent intentFoodWisdom = new Intent(this, FoodWisdom.class);
                startActivity(intentFoodWisdom);
                break;
            case R.id.nav_manual_entry:
                chooseFromPicturesFolder();
                break;
            case R.id.nav_messages:;
                break;
            case R.id.nav_meal_mate:;
                break;
            case R.id.nav_delete:
                deletePhotoFilesFromPhone();
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

    private void deletePhotoFilesFromPhone() {
        BitmapUtils.deleteFiles();

        //Send broadcast intent to Media Scanner to update the gallery
        //Todo: This is not working... Have to make the MediaScanner work better
        Uri veganBuddyFolder = Uri.fromFile(BitmapUtils.getVeganBuddyFolder());
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(veganBuddyFolder);
        this.sendBroadcast(mediaScanIntent);

        Toast.makeText(getBaseContext(), "All Vegan Buddy photos " +
                "deleted from your phone", Toast.LENGTH_SHORT).show();
    }

    private void chooseFromPicturesFolder() {
        Intent intentPicturePicker = new Intent();
        intentPicturePicker.setType("image/*");
        intentPicturePicker.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intentPicturePicker, "Select Picture"),
                UPLOAD_PICTURE_MANUALLY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UPLOAD_PICTURE_MANUALLY) {
            if (resultCode == RESULT_OK) {
                if (data==null)
                Toast.makeText(this, "No Meal Photo chosen", Toast.LENGTH_SHORT).show();
                else{
                    Uri thisPhotoUri = null;
                    try {
                        Uri uriPicture = data.getData();
                        if (uriPicture!=null)
                        //if Uri retrieved successfully, then create new meal photo and
                            // associated files with it
                         thisPhotoUri = BitmapUtils.createMealPhotoFileFromUri(this
                                .getContentResolver().openInputStream(uriPicture));
                        else {
                            //Else give a message to the user and return from this method without doing anything
                            Toast.makeText(this, "No picture chosen", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } catch (NullPointerException | FileNotFoundException e) {
                        FirebaseCrash.log(LP_TAG + e.getMessage());
                        Log.e(LP_TAG, e.getMessage());
                    }

                    if (thisPhotoUri!=null) {
                        //add Photo to Media Gallery of the phone
                        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        //Add to gallery
                        mediaScanIntent.setData(thisPhotoUri);
                        this.sendBroadcast(mediaScanIntent);
                    }

                    //Start the mealPhoto activity
                    Intent intentMealPhoto = new Intent(this, MealPhoto.class);
                    startActivity(intentMealPhoto);
                }
            }
        }
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
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);

        //Set Profile Picture
        ImageView profilePicView = headerView.findViewById(R.id.userProfilePic);
        try {
            String photoUrl = thisAppUser.getPhotoUrl();
            Picasso.with(getBaseContext()).load(photoUrl).into(profilePicView);

            //Set Profile Name
            TextView profileName = headerView.findViewById(R.id.userName);
            profileName.setText(thisAppUser.getUserName());

            //Set Profile Email
            TextView profileEmail = headerView.findViewById(R.id.userEmail);
            profileEmail.setText(thisAppUser.getEmail());

            //Set Followers/Following data
            TextView followers = headerView.findViewById(R.id.nhlp_followers);
            TextView following = headerView.findViewById(R.id.nhlp_following);
            if (thisAppUser.getMeFollowingCount() > 0) {
                String stringFollowing = "Following: " + Integer.toString(thisAppUser.getMeFollowingCount());
                following.setText(stringFollowing);
            } else following.setText("Following: 0 ");
            if (thisAppUser.getMyFollowersCount() > 0) {
                String stringFollowers = "Followers: "
                        + Integer.toString(thisAppUser.getMyFollowersCount());
                followers.setText(stringFollowers);
            } else followers.setText("Followers: 0 ");


        }catch (NullPointerException npe) {
            Log.e(LP_TAG, "Null pointer Exception happened while creating user profile");
            npe.printStackTrace();
        }
    }

    //Method to Invoke Camera Action for taking photo of the food/Meal
    private void takeFoodPhoto() {
        Intent intentCamera = new Intent(this, CameraActivity.class);
        startActivity(intentCamera);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onFragmentInteraction(int position) {
        //Todo: Implement the fragment to activity interaction here

    }

    //FragmentInteraction Method for the notification fragments
    @Override
    public void onListFragmentInteraction(Vnotification item) {
        //Todo: Implement the fragment to activity interaction here

        Toast.makeText(this, "clicked item is: " + item.getCreatedAt(), Toast.LENGTH_SHORT).show();
    }

    //FragmentInteraction Method for the new POSTs fragments
    @Override
    public void onListFragmentInteraction(Post post, View v, int position, String postID) {
        switch (v.getId())
        {
            case R.id.pi_heart_icon: heartClick(v, post);
            break;
            case R.id.pi_likes_count: numberofLikesClick(v, post);
            break;
            case R.id.pi_food_photo: photoClick(v);
            break;
            case R.id.pi_share_placard: shareThisPhoto(v);
            break;
            case R.id.pi_instagram_icon: postOnInstragram(post, v);
            break;
            case R.id.pi_comments_icon:commentsClick(v, postID, post.getMealPhotoThumbnailUri());
            break;
            case R.id.pi_comments_count:commentsClick(v, postID, post.getMealPhotoThumbnailUri());
            break;
        }
    }


    public void heartClick(View view, Post post) {
        boolean newValue = false;
        int newLikeCount = 0;
        int currentLikeCount;

        ImageView heart = view.findViewById(R.id.pi_heart_icon);
        //the content description of the images stores the text describing whether the clicked
        // heart is empty or full. Based on that change status
        String heartStatus = heart.getContentDescription().toString();
        RelativeLayout viewParent = (RelativeLayout) view.getParent();
        CardView viewGrandParent = (CardView) viewParent.getParent().getParent();
        TextView textViewUserName = viewGrandParent.findViewById(R.id.pi_profile_name);

        //The postID  Uid is stored in the content description of the CardView
        String thisPostID = viewGrandParent.getContentDescription().toString();

        TextView likesCount = viewGrandParent.findViewById(R.id.pi_likes_count);
        //The datetime unique PostID is stored in the content description on the CardView
        String dateTimeStampID = viewGrandParent.getContentDescription().toString();

        //Show Like count
        String numberofLikes = likesCount.getText().toString();
        switch (numberofLikes) {
            case NO_LIKES: currentLikeCount = 0;
            break;
            case ONE_LIKE: currentLikeCount = 1;
            break;
            default: currentLikeCount = getLikeCount (numberofLikes);
            break;
        }

        //Update Like count based on the click
        if (heartStatus.equals(HEART_FULL)) {
            //implies that currentValue of boolean "iLike" is true;
            newValue = false;
            newLikeCount = currentLikeCount - 1; //Reduce like by 1
        }

        if (heartStatus.equals(HEART_EMPTY)) {
            //implies that currentValue of boolean "iLike" is false;
            newValue = true;
            newLikeCount = currentLikeCount + 1; //Increase like by 1
        }

        //Update Firebase Database
        if (newValue)
        FirebaseStorageUtils.addToMyFans(thisPostID, newLikeCount, post.getDatestamp(),
                thisPostID.equals(post.getDatestamp())? POSTS_NODE:LAST_POSTS_NODE);

        if (!newValue)
            FirebaseStorageUtils.deleteFromMyFans(thisPostID, newLikeCount,post.getDatestamp(),
                    thisPostID.equals(post.getDatestamp())? POSTS_NODE:LAST_POSTS_NODE);

    }


    private int getLikeCount(String numberofLikes) {
        if (numberofLikes !=null && numberofLikes !=""){
            String theDigits = numberofLikes.replaceAll("\\D+","");
            return Integer.parseInt(theDigits);
        } else return 0;
    }

    public void commentsClick(View view, String nodeID, String thisPhotoUri ) {
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
        File tempImageFile = createTempUploadFile(tempImage);
        Uri tempImageFileUri = Uri.fromFile(tempImageFile);

        //TODO: try the ACTION_SENDTO
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, tempImageFileUri);
        startActivity(Intent.createChooser(shareIntent, "Share this photo..."));
    }

    private void postOnInstragram(Post p, View v) {
        Intent shareIntent = new Intent();
        RelativeLayout viewParent = (RelativeLayout) v.getParent();
        CardView viewGrandParent = (CardView) viewParent.getParent().getParent();
        ImageView thisPhoto = viewGrandParent.findViewById(R.id.pi_food_photo);
        Bitmap tempImage = ((BitmapDrawable)thisPhoto.getDrawable()).getBitmap();
        File tempImageFile = createTempUploadFile(tempImage);
        Uri tempImageFileUri = Uri.fromFile(tempImageFile);

        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, tempImageFileUri);
        shareIntent.setPackage("com.instagram.android");
        startActivity(shareIntent);
    }

    public void numberofLikesClick(View view, Post currentPost) {
        RelativeLayout viewParent = (RelativeLayout) view.getParent();
        CardView viewGrandParent = (CardView) viewParent.getParent().getParent();
        String uniquePostID = viewGrandParent.getContentDescription().toString();

        //if uniquePostID is equal to dateStamp of the post, then it belongs to fragment POSTS
        // else it belongs to LASTPOSTS
        String whichFragment = (uniquePostID.equals(currentPost.getDatestamp()))?
                POSTS_NODE:LAST_POSTS_NODE;

        Intent intentPostLikes = new Intent(this, PostLikesActivity.class);
        intentPostLikes.putExtra(POSTID, uniquePostID);
        intentPostLikes.putExtra(ORIGIN_FRAGMENT, whichFragment);
        startActivity(intentPostLikes);
    }

    public void photoClick(View view) {
        ImageView clickedPhoto = view.findViewById(R.id.pi_food_photo);
        String fullsizeImageUriStr = "";

        CharSequence fullsizeImageUriContentDescription = clickedPhoto.getContentDescription();
        if (fullsizeImageUriContentDescription!=null) {
            fullsizeImageUriStr = clickedPhoto.getContentDescription().toString();
            Intent intentFullSizePhoto = new Intent(this, FullSizePhoto.class);
            intentFullSizePhoto.putExtra(FULL_PHOTO_URI, fullsizeImageUriStr);
            startActivity(intentFullSizePhoto);
        }
    }

    private void refreshAllFragments() {
        Intent intentDataRefresh = new Intent(this, DataRefreshActivity.class);
        startActivity(intentDataRefresh);
    }

    public void veganBuddyClick(View view) {
        mViewPager.setCurrentItem(0, true);
    }

    public void followingFollowersClick(View view) {
        Intent intentFollow = new Intent(this, FollowActivity.class);
        int relationship = -939;
        switch (view.getId()){
            case R.id.nhlp_followers: relationship = FOLLOWERS;
            break;
            case R.id.nhlp_following: relationship = FOLLOWING;
            break;
        }
        intentFollow.putExtra(RELATION, relationship);
        startActivity(intentFollow);
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
            switch (position) {
                case LAST_PLACARDS_LAYOUT: return PlacardsFragment.newInstance(LAST_POSTS_NODE);
                case MY_PLACARDS_LAYOUT: return PlacardsFragment.newInstance(POSTS_NODE);
                case NOTIFICATIONS_INBOUND_LAYOUT:
                    return VnotificationFragment.newInstance(INBOUND);
                case NOTIFICATIONS_OUTBOUND_LAYOUT:
                    return VnotificationFragment.newInstance(OUTBOUND);
                default:
                    return LandingPageFragment.newInstance(position);
            }
        }

        @Override
        public int getCount() {
            // Show  total pages.
            return 6;
        }
    }

}
