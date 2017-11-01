package me.veganbuddy.veganbuddy.ui;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.jackandphantom.circularimageview.CircleImage;
import com.squareup.picasso.Picasso;

import java.util.List;

import me.veganbuddy.veganbuddy.R;
import me.veganbuddy.veganbuddy.util.GlobalVariables;

import static me.veganbuddy.veganbuddy.util.GlobalVariables.thisAppUser;

/**
 * Created by abhishek on 25/10/17.
 */

public class PostLikesRecyclerViewAdapter extends RecyclerView.Adapter<PostLikesRecyclerViewAdapter.PostLikesItem> {

    private List<PostLikesActivity.UserInfo> userList;
    Context contextThisRV;

    PostLikesRecyclerViewAdapter(List<PostLikesActivity.UserInfo> userInfoList){
        userList = userInfoList;
    }

    @Override
    public PostLikesItem onCreateViewHolder(ViewGroup parent, int viewType) {
        contextThisRV = parent.getContext();
        View view = LayoutInflater.from(contextThisRV)
                .inflate(R.layout.item_post_likes,parent,false);
        return new PostLikesItem(view);
    }

    @Override
    public void onBindViewHolder(PostLikesItem postLike, int position) {
        PostLikesActivity.UserInfo likeUser = userList.get(position);

        postLike.userID = likeUser.getId();
        postLike.userName.setText(likeUser.getName());
        if (postLike.userID.equals(thisAppUser.getFireBaseID())) {
            postLike.buttonFollow.setVisibility(View.INVISIBLE);
        }

        Uri userUri = Uri.parse(likeUser.getPhotoUrl());
        Picasso.with(contextThisRV).load(userUri).into(postLike.userPhoto);
    }

    @Override
    public int getItemCount() {
        if (userList == null)return 0;
        else return userList.size();
    }

    class PostLikesItem extends RecyclerView.ViewHolder{
        CircleImage userPhoto; //Try to replace this CircleImage with the android default cirleImage
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
