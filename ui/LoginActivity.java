package me.veganbuddy.veganbuddy.ui;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
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


import me.veganbuddy.veganbuddy.R;
import me.veganbuddy.veganbuddy.actors.User;
import me.veganbuddy.veganbuddy.util.FirebaseStorageUtils;
import me.veganbuddy.veganbuddy.util.MeatMathUtils;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "GoogleSignInActivity";
    private static GoogleApiClient googleApiClient;

    private FirebaseAuth firebaseAuth;

    private static ProgressBar signInProgressBar;
    private static SignInButton signInButton;
    private static FirebaseUser currentUser;
    private static Context thisContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        signInButton = (SignInButton) findViewById(R.id.google_sign_in_button);
        //Set onClickListeners for the Sign In Button
        signInButton.setOnClickListener(this);
        signInProgressBar = (ProgressBar) findViewById(R.id.progressBarSignIn);

    }

    @Override
    protected void onStart() {
        super.onStart();
        showProgressBarAndBackgroundImage();
        prepareFirebaseAuthComponents();
        thisContext = this;
        new CheckUserLoginAndRetrieveDataFromFirebase().execute();
    }

    private void prepareFirebaseAuthComponents() {
        // [START config_signin]
        // Configure Google Sign In
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();
    }

    //Method to update the UI once the currentUser is authenticated either prior to start of
    // the activity or after
    public void updateUI() {
        if (currentUser != null) {
            launchUserLandingPage();
        } else Toast.makeText(this
                , "No user found", Toast.LENGTH_SHORT).show();
    }

    //Once currentUser is authenticated, then Launch the next activity
    public void launchUserLandingPage () {
        Intent intent = new Intent(this, LandingPage.class);
        startActivity(intent);
    }


    //Method called when anything is clicked on the activity
    @Override
    public void onClick(View view) {
        int i = view.getId();
        switch (i) {
            case R.id.google_sign_in_button:
                signInButton.setVisibility(View.GONE);
                googleSignIn(view);
                break;
        }
    }

    //Method called when "Google Sign-in Button" is clicked on this activity
    public void googleSignIn(View view) {
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
            } else {
                // Google Sign In failed, update UI appropriately
                updateUI();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d(TAG, "fireBaseAuthWithGoogle: " + account.getId());

        AuthCredential authCredential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(authCredential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            currentUser = firebaseAuth.getCurrentUser();
                            updateUI();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI();
                        }

                        // [START_EXCLUDE]
                        hideProgressBarAndBackgroundImage();
                        // [END_EXCLUDE]
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //Some error happened causing connection to fail
        Toast.makeText(this, "Google Play Services Error", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Called from OnConnectionFailed: " + connectionResult);
    }

    public void showProgressBarAndBackgroundImage () {
        signInProgressBar.setVisibility(View.VISIBLE);
        signInButton.setVisibility(View.INVISIBLE);
        findViewById(R.id.loginpage).setBackground(getDrawable(R.drawable.green_leaf_background));
    }

    public void hideProgressBarAndBackgroundImage () {
        signInProgressBar.setVisibility(View.INVISIBLE);
        findViewById(R.id.loginpage).setBackground(getDrawable(R.drawable.amazing_tofu2));
    }

    //Todo: to check the user of AsyncTask for a user who is signing in for the first time
    private class CheckUserLoginAndRetrieveDataFromFirebase extends AsyncTask <Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // [START initialize_auth]
            firebaseAuth = FirebaseAuth.getInstance();
            // [END initialize_auth]
            currentUser = firebaseAuth.getCurrentUser();
            User.thisAppUser = currentUser;
        }

        //Todo: Check if the AsyncTask is already running in the background.
        @Override
        protected Void doInBackground(Void... voids) {
            if (currentUser!=null) {
                FirebaseStorageUtils.retrievePostsData();
                FirebaseStorageUtils.setUserMealDataForToday();
                while (User.waitingForData) { }; //Todo explore if there is a better way to attach a listener
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            updateUI(); //update the Login UI
        }
    }

}
