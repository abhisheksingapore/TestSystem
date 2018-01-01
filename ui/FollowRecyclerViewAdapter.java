package me.veganbuddy.veganbuddy.ui;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jackandphantom.circularimageview.CircleImage;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.veganbuddy.veganbuddy.R;
import me.veganbuddy.veganbuddy.actors.Buddy;

import static me.veganbuddy.veganbuddy.util.CommonMethods.containsID;
import static me.veganbuddy.veganbuddy.util.Constants.FOLLOWERS;
import static me.veganbuddy.veganbuddy.util.Constants.FOLLOWING;
import static me.veganbuddy.veganbuddy.util.FirebaseStorageUtils.addMeAsFollowerData;
import static me.veganbuddy.veganbuddy.util.FirebaseStorageUtils.addMeFollowingData;
import static me.veganbuddy.veganbuddy.util.FirebaseStorageUtils.deleteMeAsFollowerData;
import static me.veganbuddy.veganbuddy.util.FirebaseStorageUtils.deleteMeFollowingData;
import static me.veganbuddy.veganbuddy.util.GlobalVariables.thisAppUser;

/**
 * Created by abhishek on 7/11/17.
 */

public class FollowRecyclerViewAdapter
        extends RecyclerView.Adapter<FollowRecyclerViewAdapter.BuddyView> {
    List <Buddy> buddyList;
    List <Buddy> following;
    Context contextFRVA;
    int relationship;

    FollowRecyclerViewAdapter(List<Buddy> followerslist, int relation, List<Buddy> followinglist) {
        switch (relation) {
            case FOLLOWERS:
                buddyList = followerslist;
                break;
            case FOLLOWING:
                buddyList = followinglist;
                break;
        }
        relationship = relation;
        following = followinglist;
    }

    void setBuddyList(List<Buddy> list, List<Buddy> meFollowing) {
        switch (relationship) {
            case FOLLOWERS:
                buddyList = list;
                break;
            case FOLLOWING:
                buddyList = meFollowing;
                break;
        }
        following = meFollowing;
        notifyDataSetChanged();
    }

    @Override
    public BuddyView onCreateViewHolder(ViewGroup parent, int viewType) {
        contextFRVA = parent.getContext();
        View view = LayoutInflater.from(contextFRVA)
                .inflate(R.layout.item_buddy_list,parent, false);
        return new BuddyView(view);
    }

    @Override
    public void onBindViewHolder(final BuddyView fellowBuddy, int position) {
        if (buddyList == null || buddyList.size()==0){
            switch (relationship) {
                case FOLLOWERS: fellowBuddy.userName.setText(R.string.frva_no_followers);
                break;
                case FOLLOWING: fellowBuddy.userName.setText(R.string.frva_no_following);
                break;
            }
            fellowBuddy.userPhoto
                    .setImageDrawable(contextFRVA.getDrawable(R.drawable.vegan_buddy_menu_icon));
            fellowBuddy.buttonFollow.setVisibility(View.INVISIBLE);
            return;
        }
        //Show in reverse order. Latest one first.
        int listSize = buddyList.size();
        Buddy buddyThis = buddyList.get(listSize - position - 1);

        fellowBuddy.userName.setText(buddyThis.getName());

        Picasso.with(contextFRVA).load(buddyThis.getPhotoUrl())
                .placeholder(R.drawable.vegan_buddy_menu_icon).error(R.drawable.ic_info_black_24dp)
                .into(fellowBuddy.userPhoto);
        fellowBuddy.userPhoto.setContentDescription(buddyThis.getPhotoUrl());

        switch (relationship) {
            case FOLLOWERS:
                if (following != null && containsID(following, buddyThis.buddyID)) {
                    fellowBuddy.buttonFollow.setVisibility(View.VISIBLE);
                    fellowBuddy.buttonFollow.setText(R.string.ip_unfollow_button);
                    fellowBuddy.buttonFollow.setBackgroundColor(ContextCompat
                            .getColor(contextFRVA,R.color.whiteColor));
                } else {
                    fellowBuddy.buttonFollow.setVisibility(View.VISIBLE);
                    fellowBuddy.buttonFollow.setText(R.string.ip_follow_button);
                    fellowBuddy.buttonFollow.setBackgroundColor(ContextCompat
                            .getColor(contextFRVA, R.color.colorBackground));
                }
                break;
            case FOLLOWING:
                fellowBuddy.buttonFollow.setVisibility(View.VISIBLE);
                fellowBuddy.buttonFollow.setText(R.string.ip_unfollow_button);
                fellowBuddy.buttonFollow.setBackgroundColor(ContextCompat
                        .getColor(contextFRVA,R.color.whiteColor));
                break;
        }

        fellowBuddy.userID = buddyThis.buddyID;
        fellowBuddy.buttonFollow.setContentDescription(fellowBuddy.userID);

        //setOnClickListener to the Follow button
        fellowBuddy.buttonFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button buttonClicked = (Button) view;
                String followAction = buttonClicked.getText().toString();
                LinearLayout linearLayoutParent = (LinearLayout) buttonClicked.getParent();
                String buddyPhotoUrl = (linearLayoutParent.findViewById(R.id.ip_userpic))
                        .getContentDescription().toString();
                String buddyName = ((TextView)linearLayoutParent.findViewById(R.id.ip_name))
                        .getText().toString();
                Buddy buddyLeader = new Buddy(buddyName,buddyPhotoUrl);
                Buddy buddyMe = new Buddy(thisAppUser.getUserName(), thisAppUser.getPhotoUrl());

                if (followAction.equals(contextFRVA.getString(R.string.ip_follow_button))) {
                    Map<String, Buddy> meFollowingUser = new HashMap<>();
                    meFollowingUser.put(buttonClicked.getContentDescription().toString(),buddyLeader);
                    //add to database under thisAppUser "meFollowing"
                    addMeFollowingData(meFollowingUser);

                    //add thisAppUser to database as a "myFollower"
                    Map<String, Buddy> meAsFollower = new HashMap<>();
                    meAsFollower.put(thisAppUser.getFireBaseID(), buddyMe);
                    addMeAsFollowerData(fellowBuddy.userID, meAsFollower);

                    //change button view
                    buttonClicked.setText(R.string.ip_unfollow_button);
                    fellowBuddy.buttonFollow.setBackgroundColor(ContextCompat
                            .getColor(contextFRVA, R.color.whiteColor));

                } else

                if (followAction.equals(contextFRVA.getString(R.string.ip_unfollow_button))) {
                    String meFollowingUser = buttonClicked.getContentDescription().toString();
                    //remove from database under thisAppUser "Following"
                    deleteMeFollowingData(meFollowingUser);

                    //remove thisAppUser from database as a "Follower"
                    deleteMeAsFollowerData(meFollowingUser, thisAppUser.getFireBaseID());

                    //change button view
                    buttonClicked.setText(R.string.ip_follow_button);
                    buttonClicked.setBackgroundColor(ContextCompat
                            .getColor(contextFRVA, R.color.colorBackground));
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        if (buddyList == null || buddyList.size()==0) return 1;
        else return buddyList.size();
    }


    class BuddyView extends RecyclerView.ViewHolder {

        CircleImage userPhoto;
        TextView userName;
        String userID;
        Button buttonFollow;

        BuddyView (View itemView) {
            super(itemView);
            this.userPhoto = itemView.findViewById(R.id.ip_userpic);
            this.userName = itemView.findViewById(R.id.ip_name);
            this.buttonFollow = itemView.findViewById(R.id.ip_follow);
        }
    }
}
