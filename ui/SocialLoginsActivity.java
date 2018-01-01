package me.veganbuddy.veganbuddy.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import me.veganbuddy.veganbuddy.R;

import static me.veganbuddy.veganbuddy.util.Constants.PINTEREST_LOGIN;
import static me.veganbuddy.veganbuddy.util.Constants.PINTEREST_LOGIN_SUCCESS;
import static me.veganbuddy.veganbuddy.util.Constants.SLA_TAG;
import static me.veganbuddy.veganbuddy.util.SocialMediaUtils.loginToPinterest;
import static me.veganbuddy.veganbuddy.util.SocialMediaUtils.loginToTwitter;

public class SocialLoginsActivity extends AppCompatActivity {

    CallbackManager fbCallbackManager = CallbackManager.Factory.create();
    TextView textView;
    ImageButton imageButton;
    TwitterLoginButton twitterLoginButton;
    Button buttonPinterest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_logins);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        textView = findViewById(R.id.csl_text_message);
        imageButton = findViewById(R.id.csl_continue_button);
        buttonPinterest = findViewById(R.id.csl_pinteret_login_button);

        //Setting up initial data for Twitter
        setupTwitterLogin();

        //Setting up initial data for Facebook Login
        setupFacebookLogin();

        //Setting up initial data for Pinterest Login
        setupPinterestLogin();
    }

    private void setupPinterestLogin() {
        if (loginToPinterest()) {
            buttonPinterest.setText(R.string.sla_pinterest_login_success);
            imageButton.setVisibility(View.VISIBLE);
        }
    }

    public void setupTwitterLogin() {
        twitterLoginButton = findViewById(R.id.csl_twitter_login_button);

        if (loginToTwitter()) {
            twitterLoginButton.setText(getResources().getString(R.string.sla_twitter_alreadylogin));
        }

        twitterLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                Toast.makeText(getBaseContext(), "Twitter login successful!", Toast.LENGTH_SHORT).show();
                twitterLoginButton.setText(getResources().getString(R.string.sla_twitter_alreadylogin));
                twitterLoginButton.setClickable(false);
                textView.setText("Twitter login done");
                textView.setVisibility(View.VISIBLE);
                imageButton.setVisibility(View.VISIBLE);
                Log.v("SocialLoginsActivity", "Twitter Login Successful");
            }

            @Override
            public void failure(TwitterException exception) {
                String errorText = exception.toString() + "\n\n" + "Good Luck";
                textView.setText(errorText);
                textView.setVisibility(View.VISIBLE);
                imageButton.setVisibility(View.VISIBLE);
                Log.e("SocialLoginsActivity", "Error logging into Twitter " + exception.toString());
            }
        });
    }

    public void setupFacebookLogin(){
        LoginButton fbLogin = findViewById(R.id.csl_fb_login_button);
        fbLogin.setReadPermissions("email, publish_actions, user_friends");

        //If the user is already logged into Facebook, then make the "Continue" button visible
        if (fbLogin.getText().toString().equals("Log out")) {
            imageButton.setVisibility(View.VISIBLE);
        }

        fbLogin.registerCallback(fbCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast.makeText(SocialLoginsActivity.this,
                        "Facebook Login successful", Toast.LENGTH_SHORT).show();
                imageButton.setVisibility(View.VISIBLE);
                finish();
                Log.v(SLA_TAG, "Facebook Login successful");
            }

            @Override
            public void onCancel() {
                Log.v(SLA_TAG, "Cancelled  Facebook Login");
            }

            @Override
            public void onError(FacebookException error) {
                textView.setText(error.toString()
                        + "\n\n" +
                        "Good Luck"
                );
                textView.setVisibility(View.VISIBLE);
                imageButton.setVisibility(View.VISIBLE);

                Log.e(SLA_TAG, "Error in Facebook Login" + "\n\n" + error.toString());
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fbCallbackManager.onActivityResult(requestCode, resultCode, data);
        twitterLoginButton.onActivityResult(requestCode, resultCode, data);
        if (resultCode == PINTEREST_LOGIN_SUCCESS) {
            Toast.makeText(this, "Pinterest login successful", Toast.LENGTH_SHORT).show();
            buttonPinterest.setText(R.string.sla_pinterest_login_success);
            loginToPinterest = true;
            imageButton.setVisibility(View.VISIBLE);
        }
    }

    public void continueClick(View view) {
        finish();
    }

    public void pinterestLoginClick(View view) {
        Intent intentPinterest = new Intent(this, PinterestLoginActivity.class);
        startActivityForResult(intentPinterest, PINTEREST_LOGIN );
    }
}
