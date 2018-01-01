package me.veganbuddy.veganbuddy.ui;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import bolts.AppLinks;
import me.veganbuddy.veganbuddy.R;
import me.veganbuddy.veganbuddy.actors.Dashboard;
import me.veganbuddy.veganbuddy.actors.User;

import static me.veganbuddy.veganbuddy.util.Constants.DASHBOARD_NODE;
import static me.veganbuddy.veganbuddy.util.Constants.LOGIN_TAG;
import static me.veganbuddy.veganbuddy.util.Constants.NOTIFICATION_CHANNEL_ID;
import static me.veganbuddy.veganbuddy.util.Constants.PERMISSIONS;
import static me.veganbuddy.veganbuddy.util.Constants.PROFILE_NODE;
import static me.veganbuddy.veganbuddy.util.FirebaseStorageUtils.mFirebaseAuth;
import static me.veganbuddy.veganbuddy.util.GlobalVariables.googleApiClient;
import static me.veganbuddy.veganbuddy.util.GlobalVariables.myDashboard;
import static me.veganbuddy.veganbuddy.util.GlobalVariables.thisAppUser;
import static me.veganbuddy.veganbuddy.util.NetworkUtils.internetIsAvailable;
import static me.veganbuddy.veganbuddy.util.SocialMediaUtils.initializePinterest;
import static me.veganbuddy.veganbuddy.util.SocialMediaUtils.initializeTwitter;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SIGN_IN = 9001;

    private ProgressBar signInProgressBar;
    private SignInButton signInButton;
    private FirebaseUser mFirebaseUser;
    private boolean internetPermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        //Initialize Twitter
        initializeTwitter(this);

        //Initialize Pinterest
        initializePinterest(this, getString(R.string.pinterest_app_id));

        signInButton = findViewById(R.id.google_sign_in_button);
        //Set onClickListeners for the Sign In Button
        signInButton.setOnClickListener(this);
        signInProgressBar = findViewById(R.id.progressBarSignIn);

        //The below code if to handle "app invites" shared through FaceBook
        Uri targetUrl = AppLinks.getTargetUrlFromInboundIntent(this, getIntent());
        if (targetUrl != null) {
            Toast.makeText(this, "App Link Target URL: " + targetUrl.toString(), Toast.LENGTH_SHORT).show();
        }

        checkPermissions();

        showProgressBarAndBackgroundImage();
        prepareGoogleAuthComponents();
        createNotificationChannel();
    }

    private void checkPermissions() {
        int permissionCheck = ContextCompat
                .checkSelfPermission(this, Manifest.permission.INTERNET);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) internetPermission = true;
        else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.INTERNET}, PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS) {
            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    internetPermission = true;
            }
        } else
            Toast.makeText(this, "App will not work well without Internet", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }



    @TargetApi(26)
    private void createNotificationChannel() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String id = NOTIFICATION_CHANNEL_ID;
            CharSequence name = getString(R.string.la_notification_channel_name);
            String desc = getString(R.string.la_notification_channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notificationChannel = new NotificationChannel(id, name, importance);
            notificationChannel.setDescription(desc);
            try {
                notificationManager.createNotificationChannel(notificationChannel);
            } catch (NullPointerException NPE) {
                FirebaseCrash.log("Null Pointer Exception in createNotificationChannel " + NPE.toString());
                NPE.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Back Button Pressed but there is nothing to go back to", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser!=null) {
            String firebaseUID = mFirebaseUser.getUid();
            Toast.makeText(this, "Welcome! " + mFirebaseUser.getDisplayName(),
                    Toast.LENGTH_SHORT).show();
            checkUserLoginAndRetrieveDataFromFirebase(firebaseUID);
        } else {
            updateUI();
        }
    }

    private void prepareGoogleAuthComponents() {
        // [START config_signin]
        // Configure Google Sign In
        if (googleApiClient==null) {
            GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions
                    .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .build();

            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                    .build();
        }

        googleApiClient.connect();
        // [START initialize_auth]
        mFirebaseAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]
    }

    //Method to update the UI once the currentUser is authenticated either prior to start of
    // the activity or after
    public void updateUI() {
        if (thisAppUser != null) {
            retrieveUserAppData();
        } else {
            hideProgressBarAndBackgroundImage();
            if (!internetIsAvailable(this)) {
                Log.e(LOGIN_TAG, "No Internet Connection");
                Toast.makeText(this
                        , "Please check your Internet connection and retry Sign In",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this
                        , "Please Sign In", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Once currentUser is authenticated, then Launch the next activity to retrieve all the remaining
    // user data
    public void retrieveUserAppData () {
        Intent intent = new Intent(this, DataRefreshActivity.class);
        startActivity(intent);
    }


    //Method called when anything is clicked on the activity
    @Override
    public void onClick(View view) {
        int i = view.getId();
        switch (i) {
            case R.id.google_sign_in_button:
                signInButton.setVisibility(View.GONE);
                googleSignIn();
                break;
        }
    }

    //Method called when "Google Sign-in Button" is clicked on this activity
    public void googleSignIn() {
        showProgressBarAndBackgroundImage();
        //Launch the intent
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {//Result returned from the Google Sign in Activity
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
                Log.v(LOGIN_TAG, "Google Sign in Successful");
            } else {
                // Google Sign In failed, update UI appropriately
                Log.v(LOGIN_TAG, "Google Sign in failed. Please check your internet connection" +
                        " and try again");
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.v(LOGIN_TAG, "fireBaseAuthWithGoogle: " + account.getId());

        AuthCredential authCredential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(authCredential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.v(LOGIN_TAG, "signInWithCredential:success");
                            Toast.makeText(LoginActivity.this, "Successfully Authenticated!",
                                    Toast.LENGTH_SHORT).show();
                            mFirebaseUser = mFirebaseAuth.getCurrentUser();
                            checkUserLoginAndRetrieveDataFromFirebase(mFirebaseUser.getUid());
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(LOGIN_TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //Some error happened causing connection to fail
        Toast.makeText(this, "Google Play Services Error", Toast.LENGTH_SHORT).show();
        Log.d(LOGIN_TAG, "Called from OnConnectionFailed: " + connectionResult);
    }

    public void showProgressBarAndBackgroundImage () {
        signInProgressBar.setVisibility(View.VISIBLE);
        signInButton.setVisibility(View.INVISIBLE);
        findViewById(R.id.loginpage).setBackground(getDrawable(R.drawable.green_leaf_background));
    }

    public void hideProgressBarAndBackgroundImage () {
        signInProgressBar.setVisibility(View.INVISIBLE);
        findViewById(R.id.loginpage).setBackground(getDrawable(R.drawable.amazing_tofu));
        signInButton.setVisibility(View.VISIBLE);
    }

    private void checkUserLoginAndRetrieveDataFromFirebase (String username) {
        if (username != null) {
            FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
            final DatabaseReference myRef = mDatabase.getReference(username);
            retrieveUserProfile(myRef);
            retrieveUserDashboard(myRef);
        }
    }

    private void retrieveUserProfile(final DatabaseReference myRef) {
        myRef.child(PROFILE_NODE).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    thisAppUser = dataSnapshot.getValue(User.class);
                    //to catch the odd condition where some data is present for the user node
                    // except FirebaseID
                    if (thisAppUser.getFireBaseID() == null) {
                        thisAppUser.setFireBaseID(dataSnapshot.getKey());
                    }
                } else {
                    thisAppUser = new User(mFirebaseUser);
                    createUsersNodesForFirstTime(myRef);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(LOGIN_TAG,"Error retrieving user profile from Firebase Database" +
                        databaseError.getDetails());
            }
        });
    }

    public void retrieveUserDashboard(final DatabaseReference myRef) {
        myRef.child(DASHBOARD_NODE).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {
                    Dashboard dashboard = dataSnapshot.getValue(Dashboard.class);
                    if (dashboard.checkIfTodaysDateExistsInDatabase()) {
                        //An entry for todays date exists in database, so no data change needed
                        myDashboard = dashboard;
                    } else {
                        //An entry for todays date does not exist in database, so "mealsForToday" is
                        //for last entered Date. Data needs to be refreshed
                        myDashboard = dashboard;
                        myDashboard.setMealsForToday(0);
                        myRef.child(DASHBOARD_NODE).setValue(myDashboard);
                    }
                } else {
                    Log.v(LOGIN_TAG, "No children found for DASHBOARD. But userName node exists." +
                            " Creating a new Dashboard node");
                    //To catch the event where the username node exists but the dashboard node does not
                    createUsersNodesForFirstTime(myRef);
                }
                updateUI();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(LOGIN_TAG, "Error in Database Connection" + databaseError.getDetails());
                Toast.makeText(LoginActivity.this, "Error connecting to Database." +
                        " Please check your internet connection", Toast.LENGTH_SHORT).show();
                updateUI();
            }
        });
    }

    public void createUsersNodesForFirstTime(DatabaseReference myRef) {
        Dashboard firstDashboard = new Dashboard(0);

        //Create user "profile" node"
        myRef.child(PROFILE_NODE).setValue(thisAppUser);

        //Create "dashboard" node
        myRef.child(DASHBOARD_NODE).setValue(firstDashboard);
    }


}
