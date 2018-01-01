package me.veganbuddy.veganbuddy.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import com.github.chrisbanes.photoview.PhotoView;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import me.veganbuddy.veganbuddy.R;
import me.veganbuddy.veganbuddy.actors.User;

import static me.veganbuddy.veganbuddy.util.Constants.BUDDY_FIREBASE_ID;
import static me.veganbuddy.veganbuddy.util.Constants.CHAT_BUDDY_ID;
import static me.veganbuddy.veganbuddy.util.Constants.CHAT_BUDDY_NAME;
import static me.veganbuddy.veganbuddy.util.Constants.CHAT_BUDDY_PIC;
import static me.veganbuddy.veganbuddy.util.Constants.PROFILE_NODE;

public class BuddyProfile extends AppCompatActivity {

    User buddy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buddy_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.abf_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (buddy != null) {
                    Intent intent = new Intent(getBaseContext(), ChatActivity.class);
                    intent.putExtra(CHAT_BUDDY_NAME, buddy.getUserName());
                    intent.putExtra(CHAT_BUDDY_ID, buddy.getFireBaseID());
                    intent.putExtra(CHAT_BUDDY_PIC, buddy.getPhotoUrl());
                    startActivity(intent);
                } else {
                    Snackbar.make(findViewById(R.id.abp_fullsizepic), "Buddy not available for Chat", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        String buddyFirebaseID = getIntent().getStringExtra(BUDDY_FIREBASE_ID);
        retrieveBuddyProfile(buddyFirebaseID);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_small_activities, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.fpm_close:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setUserProfileData(User buddy) {
        LinearLayout linearLayoutBuddy = findViewById(R.id.cbp_buddy);
        linearLayoutBuddy.setVisibility(View.VISIBLE);
        ProgressBar progressBarThis = findViewById(R.id.abp_progressbar);
        progressBarThis.setVisibility(View.GONE);

        ImageView imageViewBuddy = findViewById(R.id.cbp_profile_pic);
        PhotoView photoViewBuddy = findViewById(R.id.abp_fullsizepic_photoview);
        TextView textViewName = findViewById(R.id.cbp_profile_name);
        TextView textViewVeganDate = findViewById(R.id.cbp_vegan_start_date);
        TextView textViewVeganPhil = findViewById(R.id.cbp_vegan_phil);
        TextView textViewWebsite = findViewById(R.id.cbp_website);
        TextView textViewEmail = findViewById(R.id.cbp_email);
        TextView textViewCity = findViewById(R.id.cbp_city);

        Picasso.with(this).load(buddy.getPhotoUrl()).into(imageViewBuddy);
        Picasso.with(this).load(buddy.getPhotoUrl()).into(photoViewBuddy);

        textViewCity.setText(buddy.getCity());
        textViewEmail.setText(buddy.getEmail());
        textViewName.setText(buddy.getUserName());
        textViewVeganDate.setText(buddy.getStartDateOfVegan());
        textViewVeganPhil.setText(buddy.getVeganphilosophy());
        textViewWebsite.setText(buddy.getWebsite());

        loadGenderRadioValue(buddy.getGender());
        loadRelationshipStatusValue(buddy.getRelationShipStatus());

    }

    private void loadRelationshipStatusValue(String relationShipStatus) {
        if (relationShipStatus == null || relationShipStatus.length() == 0) return;

        if (relationShipStatus.equals(getString(R.string.cup_relation_single)))
            ((RadioButton) findViewById(R.id.cbp_relationship_status_single)).setChecked(true);

        if (relationShipStatus.equals(getString(R.string.cup_relation_married)))
            ((RadioButton) findViewById(R.id.cbp_relationship_status_married)).setChecked(true);

        if (relationShipStatus.equals(getString(R.string.cup_relation_attached_engaged)))
            ((RadioButton) findViewById(R.id.cbp_relationship_status_engaged)).setChecked(true);

        if (relationShipStatus.equals(getString(R.string.cup_relation_its_complicated)))
            ((RadioButton) findViewById(R.id.cbp_relationship_status_complicated)).setChecked(true);

        if (relationShipStatus.equals(getString(R.string.cup_relation_prefer_not_to_say)))
            ((RadioButton) findViewById(R.id.cbp_relationship_status_not_say)).setChecked(true);
    }

    public void loadGenderRadioValue(String value) {
        if (value == null || value.length() == 0) return;

        //All strings are being reused between user profile (cup) and buddy profile (cbp). So
        // prefixes may be used across. Have to mange with caution
        if (value.equals(getString(R.string.cup_male)))
            ((RadioButton) findViewById(R.id.cbp_gender_male)).setChecked(true);

        if (value.equals(getString(R.string.cup_female)))
            ((RadioButton) findViewById(R.id.cbp_gender_female)).setChecked(true);

        if (value.equals(getString(R.string.cup_gender_other)))
            ((RadioButton) findViewById(R.id.cbp_gender_other)).setChecked(true);

        if (value.equals(getString(R.string.cup_relation_prefer_not_to_say)))
            ((RadioButton) findViewById(R.id.cbp_gender_not_say)).setChecked(true);
    }

    /**
     * **********************************************************************
     * Adding Firebase database Listener to retrieve the "buddy" profile data
     * **********************************************************************
     **/
    private void retrieveBuddyProfile(String buddyFirebaseID) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference();
        Query queryMyBuddy = databaseReference.child(buddyFirebaseID)
                .child(PROFILE_NODE);
        queryMyBuddy.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User buddy = dataSnapshot.getValue(User.class);
                    setUserProfileData(buddy);
                    setBuddy(buddy);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                FirebaseCrash.log("Error loading buddy Profile data from internet");
            }
        });
    }

    public void profilePicClick(View view) {
        findViewById(R.id.abp_fullsizepic).setVisibility(View.VISIBLE);
    }

    public void closeClick(View view) {
        findViewById(R.id.abp_fullsizepic).setVisibility(View.INVISIBLE);
    }

    public void setBuddy(User buddy) {
        this.buddy = buddy;
    }
}
