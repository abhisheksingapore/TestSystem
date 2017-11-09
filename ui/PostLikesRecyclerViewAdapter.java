package me.veganbuddy.veganbuddy.ui;

import android.content.Context;
import android.net.Uri;
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

import static me.veganbuddy.veganbuddy.util.FirebaseStorageUtils.addMeAsFollowerData;
import static me.veganbuddy.veganbuddy.util.FirebaseStorageUtils.addMeFollowingData;
import static me.veganbuddy.veganbuddy.util.FirebaseStorageUtils.deleteMeAsFollowerData;
import static me.veganbuddy.veganbuddy.util.FirebaseStorageUtils.deleteMeFollowingData;
import static me.veganbuddy.veganbuddy.util.GlobalVariables.thisAppUser;

/**
 * Created by abhishek on 25/10/17.
 */

public class PostLikesRecyclerViewAdapter extends RecyclerView.Adapter<PostLikesRecyclerViewAdapter.PostLikesItem> {

    private List<Buddy> userList;
    Context contextThisRV;
    private List<String> meFollowing;

    PostLikesRecyclerViewAdapter(List<Buddy> userInfoList){
        userList = userInfoList;
    }

    @Override
    public PostLikesItem onCreateViewHolder(ViewGroup parent, int viewType) {
        contextThisRV = parent.getContext();
        View view = LayoutInflater.from(contextThisRV)
                .inflate(R.layout.item_buddy_list,parent,false);
        return new PostLikesItem(view);
    }

    @Override
    public void onBindViewHolder(PostLikesItem postLike, int position) {
        if (userList == null || userList.size() == 0) {
            postLike.userName.setText(R.string.plrva_no_fans);
            postLike.buttonFollow.setVisibility(View.INVISIBLE);
            return;
        }

        Buddy likeUser = userList.get(position);
        postLike.userID = likeUser.buddyID;
        postLike.userName.setText(likeUser.getName());
        if (postLike.userID.equals(thisAppUser.getFireBaseID())) {
            postLike.buttonFollow.setVisibility(View.INVISIBLE);
        } else {
            postLike.buttonFollow.setVisibility(View.VISIBLE);

            //this ContentDescription will be used to identify the "MeFollowing" user onClick
            postLike.buttonFollow.setContentDescription(postLike.userID);

            //if the Buddy is found in the meFollowingList then change the "Follow" button
            // to "Unfollow"
            if (meFollowing != null && meFollowing.contains(postLike.userID)) {
                postLike.buttonFollow.setText(R.string.ip_unfollow_button);
                postLike.buttonFollow.setBackgroundColor(ContextCompat
                        .getColor(contextThisRV,R.color.whiteColor));
            } else {
                //if the Buddy is not found in the meFollowingList then revert to original
                // "Follow" button style
                postLike.buttonFollow.setText(R.string.ip_follow_button);
                postLike.buttonFollow.setBackgroundColor(ContextCompat
                        .getColor(contextThisRV, R.color.colorBackground));
            }
        }

        Uri userUri = Uri.parse(likeUser.getPhotoUrl());
        Picasso.with(contextThisRV).load(userUri).into(postLike.userPhoto);
        //This content Description will be used to retrieve the URL to be stored as a follower
        // if the Follow Button is clicked
        postLike.userPhoto.setContentDescription(likeUser.getPhotoUrl());

        //setOnClickListener to the Follow button
        postLike.buttonFollow.setOnClickListener(new View.OnClickListener() {
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

                if (followAction.equals(contextThisRV.getString(R.string.ip_follow_button))) {
                    Map<String, Buddy> meFollowingUser = new HashMap<>();
                    meFollowingUser.put(buttonClicked.getContentDescription().toString(),buddyLeader);
                    //add to database under thisAppUser "Following"
                    addMeFollowingData(meFollowingUser);

                    //add thisAppUser to database as a "Follower"
                    Map<String, Buddy> meAsFollower = new HashMap<>();
                    meAsFollower.put(thisAppUser.getFireBaseID(), buddyMe);
                    addMeAsFollowerData(buttonClicked.getContentDescription().toString(), meAsFollower);

                    //Todo: create vNotification for "the Follower" and "the Followed"
                }

                if (followAction.equals(contextThisRV.getString(R.string.ip_unfollow_button))) {
                    String meFollowingUser = buttonClicked.getContentDescription().toString();
                    //remove from database under thisAppUser "Following"
                    deleteMeFollowingData(meFollowingUser);

                    //remove thisAppUser from database as a "Follower"
                    deleteMeAsFollowerData(meFollowingUser, thisAppUser.getFireBaseID());

                    //Todo: delete vNotification for "the Follower" and "the Followed"
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (userList == null)return 1;
        else return userList.size();
    }

    public void setUserList(List<Buddy> userList) {
        this.userList = userList;
        notifyDataSetChanged();
    }

    public void setMeFollowingList(List<String> meFollowingList) {
        this.meFollowing = meFollowingList;
        notifyDataSetChanged();
    }

    class PostLikesItem extends RecyclerView.ViewHolder{
        CircleImage userPhoto;
        TextView userName;
        String userID;
        Button buttonFollow;


        PostLikesItem (View view){
            super(view);
            this.userPhoto = view.findViewById(R.id.ip_userpic);
            this.userName = view.findViewById(R.id.ip_name);
            this.buttonFollow = view.findViewById(R.id.ip_follow);
        }
    }
}
