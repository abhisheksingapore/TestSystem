package me.veganbuddy.veganbuddy.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mindorks.placeholderview.ExpandablePlaceHolderView;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.expand.ChildPosition;
import com.mindorks.placeholderview.annotations.expand.Collapse;
import com.mindorks.placeholderview.annotations.expand.Expand;
import com.mindorks.placeholderview.annotations.expand.Parent;
import com.mindorks.placeholderview.annotations.expand.ParentPosition;
import com.mindorks.placeholderview.annotations.expand.SingleTop;
import com.mindorks.placeholderview.annotations.expand.Toggle;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import me.veganbuddy.veganbuddy.R;
import me.veganbuddy.veganbuddy.actors.Vcoins;

import static me.veganbuddy.veganbuddy.util.Constants.BOUNTY_LEADERBOARD;
import static me.veganbuddy.veganbuddy.util.Constants.BOUNTY_TAG;
import static me.veganbuddy.veganbuddy.util.Constants.BOUNTY_TARGETS;
import static me.veganbuddy.veganbuddy.util.Constants.VCOINS_ACCOUNTS;
import static me.veganbuddy.veganbuddy.util.Constants.VCOINS_NODE;
import static me.veganbuddy.veganbuddy.util.GlobalVariables.thisAppUser;

public class BountyActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private BountySectionsPagerAdapter mSectionsPagerAdapter;


    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bounty);

        final Toolbar bountytoolbar = findViewById(R.id.act_bounty_toolbar);
        setSupportActionBar(bountytoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.activity_myVaccounts_title);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new BountySectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.act_bounty_container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position + 1) {
                    case VCOINS_ACCOUNTS:
                        bountytoolbar.setTitle(R.string.activity_myVaccounts_title);
                        break;
                    case BOUNTY_TARGETS:
                        bountytoolbar.setTitle(R.string.activity_vCoinsTarget_title);
                        break;
                    case BOUNTY_LEADERBOARD:
                        bountytoolbar.setTitle(R.string.activity_vCoinsLeader_title);
                        break;
                    default:
                        bountytoolbar.setTitle(R.string.activity_myVaccounts_title);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bounty, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A fragment containing a simple view.
     */
    public static class BountyFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_FRAGMENT_NUMBER = "fragment_number";
        public static List<Feed> feedlist = new ArrayList<>();
        private static int FRAG_ID = -939;
        private static int FRAG_NUM = -939;
        private Vcoins vcoins;


        public BountyFragment() {
        }

        public static BountyFragment newInstance(int sectionNumber) {
            BountyFragment fragment = new BountyFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_FRAGMENT_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            if (getArguments() != null) {
                FRAG_NUM = getArguments().getInt(ARG_FRAGMENT_NUMBER);
            }
            switch (FRAG_NUM) {
                case VCOINS_ACCOUNTS:
                    FRAG_ID = R.layout.fragment_bounty_vcoins_accounting;
                    break;
                case BOUNTY_TARGETS:
                    FRAG_ID = R.layout.fragment_bounty_targets;
                    break;
                case BOUNTY_LEADERBOARD:
                    FRAG_ID = R.layout.fragment_bounty;
                    break;
                default:
                    FRAG_ID = R.layout.fragment_bounty;
                    break;
            }
            View rootView = inflater.inflate(FRAG_ID, container, false);

            return rootView;
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            switch (FRAG_NUM) {
                case VCOINS_ACCOUNTS:
                    retrieveMyvCoinsAccounts(view);
                    break;
            }
        }

        void setupVcoinsAccount(View view) {
            ExpandablePlaceHolderView expandablePlaceHolderView
                    = view.findViewById(R.id.fbva_vcoins_accounts_elv);
            expandablePlaceHolderView.computeScroll();
            Context mContext = getActivity().getApplicationContext();

            for (Feed feedThis : feedlist) {
                String feedHeading = feedThis.getHeading();
                expandablePlaceHolderView.addView(new HeadingView(mContext, feedHeading));

                for (VcoinsItem vcoinsItem : feedThis.getVcoinsItemsList()) {
                    VcoinsItemView vcoinsItemView = new VcoinsItemView(mContext, vcoinsItem);
                    expandablePlaceHolderView.addView(vcoinsItemView);
                }
            }
        }

        /***********************************************************************
         ***********************************************************************
         Adding Firebase database Listeners to retrieve the data and update the UI on data change
         ***********************************************************************
         ************************************************************************/

        private void retrieveMyvCoinsAccounts(final View view) {
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = firebaseDatabase.getReference();
            Query queryMyVcoins = databaseReference.child(thisAppUser.getFireBaseID())
                    .child(VCOINS_NODE);

            queryMyVcoins.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        try {
                            vcoins = dataSnapshot.getValue(Vcoins.class);
                            createFeedList(view, vcoins);
                        } catch (NullPointerException NPE) {
                            FirebaseCrash.log(BOUNTY_TAG + NPE.getMessage());
                            Log.e(BOUNTY_TAG, NPE.getMessage());
                        }

                    }
                    Log.v(BOUNTY_TAG, "Successfully retrieved my vCoins data");
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    FirebaseCrash.log(BOUNTY_TAG + "Error retrieving my vCoins data "
                            + databaseError.getMessage());
                    Log.e(BOUNTY_TAG, "Error retrieving my vCoins data"
                            + databaseError.getMessage());
                }
            });
        }

        private void createFeedList(View view, Vcoins vcoins) {
            feedlist = new ArrayList<>();
            List<VcoinsItem> vcoinsItemListTotal = new ArrayList<>();
            List<VcoinsItem> vcoinsItemListNewPost = new ArrayList<>();
            List<VcoinsItem> vcoinsItemsListSocial = new ArrayList<>();
            List<VcoinsItem> vcoinsItemsListShare = new ArrayList<>();

            //For the Total vCoins Earned Section
            Feed feedTotal = new Feed();
            VcoinsItem vcoinsItemTotal = new VcoinsItem();

            vcoinsItemTotal.setAction("Total vCoins I Earned");
            vcoinsItemTotal.setVcoins_earned(
                    String.format(Locale.ENGLISH, "%,1d", vcoins.getvCoinsEarned()));
            vcoinsItemTotal.setAction_count("29");
            vcoinsItemTotal.setVcoins_icon(R.drawable.ic_monetization_green_24dp);
            vcoinsItemListTotal.add(vcoinsItemTotal);

            feedTotal.setHeading("Total vCoins Earned");
            feedTotal.setVcoinsItemsList(vcoinsItemListTotal);


            //For the Total vCoins earned for New Posts
            Feed feedNewPost = new Feed();
            VcoinsItem vcoinsItemNewPost = new VcoinsItem();
            vcoinsItemNewPost.setAction("New Posts Added");
            vcoinsItemNewPost.setVcoins_earned(
                    String.format(Locale.ENGLISH, "%,1d", vcoins.getvCoinsNewPost()));
            vcoinsItemNewPost.setAction_count(
                    String.format(Locale.ENGLISH, "%,1d", vcoins.getNumberOfNewPost()));
            vcoinsItemNewPost.setVcoins_icon(R.drawable.ic_new_post_24dp);
            vcoinsItemListNewPost.add(vcoinsItemNewPost);

            feedNewPost.setHeading("vCoins for my Posts");
            feedNewPost.setVcoinsItemsList(vcoinsItemListNewPost);

            //For the Total vCoins earned for Social Activities - like, comment, message and follow
            Feed feedSocial = new Feed();

            VcoinsItem vcoinsItemCommentIgive = new VcoinsItem();
            vcoinsItemCommentIgive.setAction("Comments I shared");
            vcoinsItemCommentIgive.setVcoins_earned(
                    String.format(Locale.ENGLISH, "%,1d", vcoins.getvCoinsCommentIgive()));
            vcoinsItemCommentIgive.setAction_count(
                    String.format(Locale.ENGLISH, "%,1d", vcoins.getNumberOfCommentIgive()));
            vcoinsItemCommentIgive.setVcoins_icon(R.drawable.ic_comment_black_24dp);
            vcoinsItemsListSocial.add(vcoinsItemCommentIgive);

            VcoinsItem vcoinsItemCommentIget = new VcoinsItem();
            vcoinsItemCommentIget.setAction("Comments I received");
            vcoinsItemCommentIget.setVcoins_earned(
                    String.format(Locale.ENGLISH, "%,1d", vcoins.getvCoinsCommentIget()));
            vcoinsItemCommentIget.setAction_count(
                    String.format(Locale.ENGLISH, "%,1d", vcoins.getNumberOfCommentIget()));
            vcoinsItemCommentIget.setVcoins_icon(R.drawable.ic_comment_black_24dp);
            vcoinsItemsListSocial.add(vcoinsItemCommentIget);


            VcoinsItem vcoinsMessageMe = new VcoinsItem();
            vcoinsMessageMe.setAction("Messages I received");
            vcoinsMessageMe.setVcoins_earned(
                    String.format(Locale.ENGLISH, "%,1d", vcoins.getvCoinsMessageMe()));
            vcoinsMessageMe.setAction_count(
                    String.format(Locale.ENGLISH, "%,1d", vcoins.getNumberOfMessageMe()));
            vcoinsMessageMe.setVcoins_icon(R.drawable.ic_messages_24dp);
            vcoinsItemsListSocial.add(vcoinsMessageMe);

            VcoinsItem vcoinsIMessage = new VcoinsItem();
            vcoinsIMessage.setAction("Messages I sent");
            vcoinsIMessage.setVcoins_earned(
                    String.format(Locale.ENGLISH, "%,1d", vcoins.getvCoinsIMessage()));
            vcoinsIMessage.setAction_count(
                    String.format(Locale.ENGLISH, "%,1d", vcoins.getNumberOfIMessage()));
            vcoinsIMessage.setVcoins_icon(R.drawable.ic_messages_24dp);
            vcoinsItemsListSocial.add(vcoinsIMessage);

            VcoinsItem vcoinsIfollow = new VcoinsItem();
            vcoinsIfollow.setAction("Vegan buddies I follow");
            vcoinsIfollow.setVcoins_earned(
                    String.format(Locale.ENGLISH, "%,1d", vcoins.getvCoinsIFollow()));
            vcoinsIfollow.setAction_count(
                    String.format(Locale.ENGLISH, "%,1d", vcoins.getNumberOfIFollow()));
            vcoinsIfollow.setVcoins_icon(R.drawable.ic_following_black_24dp);
            vcoinsItemsListSocial.add(vcoinsIfollow);

            VcoinsItem vcoinsFollowMe = new VcoinsItem();
            vcoinsFollowMe.setAction("Vegan buddies following me");
            vcoinsFollowMe.setVcoins_earned(
                    String.format(Locale.ENGLISH, "%,1d", vcoins.getvCoinsFollowMe()));
            vcoinsFollowMe.setAction_count(
                    String.format(Locale.ENGLISH, "%,1d", vcoins.getNumberOfFollowMe()));
            vcoinsFollowMe.setVcoins_icon(R.drawable.ic_following_black_24dp);
            vcoinsItemsListSocial.add(vcoinsFollowMe);

            VcoinsItem vcoinsItemIlike = new VcoinsItem();
            vcoinsItemIlike.setAction("Posts I liked");
            vcoinsItemIlike.setVcoins_earned(
                    String.format(Locale.ENGLISH, "%,1d", vcoins.getvCoinsIlike()));
            vcoinsItemIlike.setAction_count(
                    String.format(Locale.ENGLISH, "%,1d", vcoins.getNumberofIlikes()));
            vcoinsItemIlike.setVcoins_icon(R.drawable.heart_full_vcoins_icon);
            vcoinsItemsListSocial.add(vcoinsItemIlike);

            VcoinsItem vcoinsItemLikeMe = new VcoinsItem();
            vcoinsItemLikeMe.setAction("My posts others liked");
            vcoinsItemLikeMe.setVcoins_earned(
                    String.format(Locale.ENGLISH, "%,1d", vcoins.getvCoinsLikeMe()));
            vcoinsItemLikeMe.setAction_count(
                    String.format(Locale.ENGLISH, "%,1d", vcoins.getNumberOfLikeMe()));
            vcoinsItemLikeMe.setVcoins_icon(R.drawable.heart_full_vcoins_icon);
            vcoinsItemsListSocial.add(vcoinsItemLikeMe);


            feedSocial.setHeading("vCoins for my Social Activities");
            feedSocial.setVcoinsItemsList(vcoinsItemsListSocial);

            //For the Total vCoins earned for Sharing Activities - sharing screenshots, benefits,
            // app link and food wisdom
            Feed feedShare = new Feed();
            VcoinsItem vcoinsItemIshareSSfb = new VcoinsItem();
            vcoinsItemIshareSSfb.setAction("Meal Photos I shared on Facebook");
            vcoinsItemIshareSSfb.setVcoins_earned(
                    String.format(Locale.ENGLISH, "%,1d", vcoins.getvCoinsIshareScreenshotFB()));
            vcoinsItemIshareSSfb.setAction_count(
                    String.format(Locale.ENGLISH, "%,1d", vcoins.getNumberOfIshareScreenshotFB()));
            vcoinsItemIshareSSfb.setVcoins_icon(R.drawable.facebook_color_24);
            vcoinsItemsListShare.add(vcoinsItemIshareSSfb);

            VcoinsItem vcoinsItemIshareBBfb = new VcoinsItem();
            vcoinsItemIshareBBfb.setAction("Benefits I shared on Facebook");
            vcoinsItemIshareBBfb.setVcoins_earned(
                    String.format(Locale.ENGLISH, "%,1d", vcoins.getvCoinsIshareBenefitsBoardFB()));
            vcoinsItemIshareBBfb.setAction_count(
                    String.format(Locale.ENGLISH, "%,1d", vcoins.getNumberOfIshareBenefitsBoardFB()));
            vcoinsItemIshareBBfb.setVcoins_icon(R.drawable.facebook_color_24);
            vcoinsItemsListShare.add(vcoinsItemIshareBBfb);

            VcoinsItem vcoinsItemIshareSStw = new VcoinsItem();
            vcoinsItemIshareSStw.setAction("Meal Photos I shared on Twitter");
            vcoinsItemIshareSStw.setVcoins_earned(
                    String.format(Locale.ENGLISH, "%,1d", vcoins.getvCoinsIshareScreenshotTw()));
            vcoinsItemIshareSStw.setAction_count(
                    String.format(Locale.ENGLISH, "%,1d", vcoins.getNumberOfIshareScreenshotTw()));
            vcoinsItemIshareSStw.setVcoins_icon(R.drawable.twitter_color_24);
            vcoinsItemsListShare.add(vcoinsItemIshareSStw);

            VcoinsItem vcoinsItemIshareBBtw = new VcoinsItem();
            vcoinsItemIshareBBtw.setAction("Benefits I shared on Twitter");
            vcoinsItemIshareBBtw.setVcoins_earned(
                    String.format(Locale.ENGLISH, "%,1d", vcoins.getvCoinsIshareBenefitsBoardTw()));
            vcoinsItemIshareBBtw.setAction_count(
                    String.format(Locale.ENGLISH, "%,1d", vcoins.getNumberOfIshareBenefitsBoardTw()));
            vcoinsItemIshareBBtw.setVcoins_icon(R.drawable.twitter_color_24);
            vcoinsItemsListShare.add(vcoinsItemIshareBBtw);

            VcoinsItem vcoinsItemIshareSSPin = new VcoinsItem();
            vcoinsItemIshareSSPin.setAction("Meal Photos I shared on Pinterest");
            vcoinsItemIshareSSPin.setVcoins_earned(
                    String.format(Locale.ENGLISH, "%,1d", vcoins.getvCoinsIshareScreenshotPin()));
            vcoinsItemIshareSSPin.setAction_count(
                    String.format(Locale.ENGLISH, "%,1d", vcoins.getNumberOfIshareScreenshotPin()));
            vcoinsItemIshareSSPin.setVcoins_icon(R.drawable.pinterest_icon_24);
            vcoinsItemsListShare.add(vcoinsItemIshareSSPin);

            VcoinsItem vcoinsItemIshareBBPin = new VcoinsItem();
            vcoinsItemIshareBBPin.setAction("Benefits I shared on Pinterest");
            vcoinsItemIshareBBPin.setVcoins_earned(
                    String.format(Locale.ENGLISH, "%,1d", vcoins.getvCoinsIshareBenefitsBoardPin()));
            vcoinsItemIshareBBPin.setAction_count(
                    String.format(Locale.ENGLISH, "%,1d", vcoins.getNumberOfIshareBenefitsBoardPin()));
            vcoinsItemIshareBBPin.setVcoins_icon(R.drawable.pinterest_icon_24);
            vcoinsItemsListShare.add(vcoinsItemIshareBBPin);

            VcoinsItem vcoinsItemIshareSSig = new VcoinsItem();
            vcoinsItemIshareSSig.setAction("Meal Photos I shared on Instagram");
            vcoinsItemIshareSSig.setVcoins_earned(
                    String.format(Locale.ENGLISH, "%,1d", vcoins.getvCoinsIshareScreenshotIG()));
            vcoinsItemIshareSSig.setAction_count(
                    String.format(Locale.ENGLISH, "%,1d", vcoins.getNumberOfIshareScreenshotIG()));
            vcoinsItemIshareSSig.setVcoins_icon(R.drawable.instagram_color_24);
            vcoinsItemsListShare.add(vcoinsItemIshareSSig);

            VcoinsItem vcoinsItemIshareBBig = new VcoinsItem();
            vcoinsItemIshareBBig.setAction("Benefits I shared on Instagram");
            vcoinsItemIshareBBig.setVcoins_earned(
                    String.format(Locale.ENGLISH, "%,1d", vcoins.getvCoinsIshareBenefitsBoardIG()));
            vcoinsItemIshareBBig.setAction_count(
                    String.format(Locale.ENGLISH, "%,1d", vcoins.getNumberOfIshareBenefitsBoardIG()));
            vcoinsItemIshareBBig.setVcoins_icon(R.drawable.instagram_color_24);
            vcoinsItemsListShare.add(vcoinsItemIshareBBig);

            VcoinsItem vcoinsItemIshareOtherSS = new VcoinsItem();
            vcoinsItemIshareOtherSS.setAction("Photos from others, I shared ");
            vcoinsItemIshareOtherSS.setVcoins_earned(
                    String.format(Locale.ENGLISH, "%,1d", vcoins.getvCoinsIshareOtherScreenshotFbTwPinIG()));
            vcoinsItemIshareOtherSS.setAction_count(
                    String.format(Locale.ENGLISH, "%,1d", vcoins.getNumberOfIshareOtherScreenshotFbTwPinIG()));
            vcoinsItemIshareOtherSS.setVcoins_icon(R.drawable.ic_social_24dp);
            vcoinsItemsListShare.add(vcoinsItemIshareOtherSS);

            VcoinsItem vcoinsItemSharedMe = new VcoinsItem();
            vcoinsItemSharedMe.setAction("My Meal Photos shared by others");
            vcoinsItemSharedMe.setVcoins_earned(
                    String.format(Locale.ENGLISH, "%,1d", vcoins.getvCoinsSharedMyScreenshotFbTwPinIG()));
            vcoinsItemSharedMe.setAction_count(
                    String.format(Locale.ENGLISH, "%,1d", vcoins.getNumberOfMyScreenshotFbTwPinIG()));
            vcoinsItemSharedMe.setVcoins_icon(R.drawable.ic_menu_share);
            vcoinsItemsListShare.add(vcoinsItemSharedMe);

            VcoinsItem vcoinsItemSharedFoodWisdom = new VcoinsItem();
            vcoinsItemSharedFoodWisdom.setAction("Food Wisdom shared by me");
            vcoinsItemSharedFoodWisdom.setVcoins_earned(
                    String.format(Locale.ENGLISH, "%,1d", vcoins.getvCoinsIshareFoodWisdom()));
            vcoinsItemSharedFoodWisdom.setAction_count(
                    String.format(Locale.ENGLISH, "%,1d", vcoins.getNumberOfIshareFoodWisdom()));
            vcoinsItemSharedFoodWisdom.setVcoins_icon(R.drawable.ic_wisdom_24);
            vcoinsItemsListShare.add(vcoinsItemSharedFoodWisdom);

            feedShare.setHeading("vCoins for my Sharing Activities");
            feedShare.setVcoinsItemsList(vcoinsItemsListShare);

            feedlist.add(feedTotal);
            feedlist.add(feedNewPost);
            feedlist.add(feedSocial);
            feedlist.add(feedShare);
            setupVcoinsAccount(view);
        }

        public static class VcoinsItem {
            @SerializedName("action")
            @Expose
            private String action;

            @SerializedName("action_count")
            @Expose
            private String action_count;

            @SerializedName("vcoins_earned")
            @Expose
            private String vcoins_earned;

            @SerializedName("vcoins_earned")
            @Expose
            private int vcoins_icon;

            public VcoinsItem() {

            }

            public int getVcoins_icon() {
                return vcoins_icon;
            }

            public void setVcoins_icon(int vcoins_icon) {
                this.vcoins_icon = vcoins_icon;
            }

            public String getAction() {
                return action;
            }

            public void setAction(String action) {
                this.action = action;
            }

            public String getAction_count() {
                return action_count;
            }

            public void setAction_count(String action_count) {
                this.action_count = action_count;
            }

            public String getVcoins_earned() {
                return vcoins_earned;
            }

            public void setVcoins_earned(String vcoins_earned) {
                this.vcoins_earned = vcoins_earned;
            }
        }

        @Parent
        @SingleTop
        @Layout(R.layout.item_vcoins_accounts_heading)
        public static class HeadingView {

            @com.mindorks.placeholderview.annotations.View(R.id.ivah_heading)
            private TextView headingTxt;

            @com.mindorks.placeholderview.annotations.View(R.id.ivah_toggle_icon)
            private ImageView toggleIcon;

            @Toggle(R.id.ivah_heading_toggleview)
            private ConstraintLayout toggleView;

            @ParentPosition
            private int mParentPosition;

            private Context mContext;
            private String mHeading;

            public HeadingView(Context context, String heading) {
                mContext = context;
                mHeading = heading;
            }

            @Resolve
            private void onResolved() {
                toggleIcon.setImageDrawable(mContext.getDrawable(R.drawable.ic_expand_more_black_36dp));
                headingTxt.setText(mHeading);
            }

            @Expand
            private void onExpand() {
                toggleIcon.setImageDrawable(mContext.getDrawable(R.drawable.ic_expand_less_black_36dp));
            }

            @Collapse
            private void onCollapse() {
                toggleIcon.setImageDrawable(mContext.getDrawable(R.drawable.ic_expand_more_black_36dp));
            }
        }

        @Layout(R.layout.item_vcoins_accounts)
        public static class VcoinsItemView {

            @ParentPosition
            private int mParentPosition;

            @ChildPosition
            private int mChildPosition;

            @com.mindorks.placeholderview.annotations.View(R.id.iva_action_for_earning)
            private TextView actionForEarning;

            @com.mindorks.placeholderview.annotations.View(R.id.iva_number_of_actions)
            private TextView numberOfActions;

            @com.mindorks.placeholderview.annotations.View(R.id.iva_amount_earned)
            private TextView vCoinsEarned;

            @com.mindorks.placeholderview.annotations.View(R.id.iva_icon)
            private ImageView actionIcon;

            private VcoinsItem vcoinsItem;
            private Context mContext;

            public VcoinsItemView(Context context, VcoinsItem vcoinsItem) {
                mContext = context;
                this.vcoinsItem = vcoinsItem;
            }

            @Resolve
            private void onResolved() {
                actionForEarning.setText(vcoinsItem.getAction());
                numberOfActions.setText(vcoinsItem.getAction_count());
                vCoinsEarned.setText(vcoinsItem.getVcoins_earned());
                actionIcon.setImageDrawable(mContext.getDrawable(vcoinsItem.getVcoins_icon()));
            }
        }

        public class Feed {

            private String heading;

            private List<VcoinsItem> vcoinsItemsList;

            public Feed() {
            }

            public String getHeading() {
                return heading;
            }

            public void setHeading(String heading) {
                this.heading = heading;
            }

            public List<VcoinsItem> getVcoinsItemsList() {
                return vcoinsItemsList;
            }

            public void setVcoinsItemsList(List<VcoinsItem> vcoinsItemsList) {
                this.vcoinsItemsList = vcoinsItemsList;
            }
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class BountySectionsPagerAdapter extends FragmentPagerAdapter {

        public BountySectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return BountyFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }
}
