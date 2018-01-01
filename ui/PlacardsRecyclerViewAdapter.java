package me.veganbuddy.veganbuddy.ui;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.crash.FirebaseCrash;
import com.jackandphantom.circularimageview.CircleImage;
import com.squareup.picasso.Picasso;

import java.util.List;

import me.veganbuddy.veganbuddy.R;
import me.veganbuddy.veganbuddy.actors.Post;
import me.veganbuddy.veganbuddy.ui.PlacardsFragment.OnListFragmentInteractionListener;
import me.veganbuddy.veganbuddy.util.DateAndTimeUtils;

import static me.veganbuddy.veganbuddy.util.Constants.HEART_EMPTY;
import static me.veganbuddy.veganbuddy.util.Constants.HEART_FULL;
import static me.veganbuddy.veganbuddy.util.Constants.NO_COMMENT;
import static me.veganbuddy.veganbuddy.util.Constants.NO_LIKES;
import static me.veganbuddy.veganbuddy.util.Constants.ONE_COMMENT;
import static me.veganbuddy.veganbuddy.util.Constants.ONE_LIKE;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Post} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class PlacardsRecyclerViewAdapter extends RecyclerView.Adapter<PlacardsRecyclerViewAdapter.PlacardHolder> {

    private final OnListFragmentInteractionListener mListener;
    private List<Post> listofPosts;
    private List<String> listOfPostIDs;
    private Context thisContext;

    PlacardsRecyclerViewAdapter(List<Post> postList, List<String> postIDs, OnListFragmentInteractionListener listener) {
        listofPosts = postList;
        mListener = listener;
        listOfPostIDs = postIDs;
    }

    @Override
    public PlacardHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        thisContext = parent.getContext();
        View view = LayoutInflater.from(thisContext)
                .inflate(R.layout.fragment_placard, parent, false);
        return new PlacardHolder(view);
    }

    @Override
    public void onBindViewHolder(final PlacardHolder placard, final int position) {
        final int positionOfPost = listofPosts.size() - position - 1;
        String postID = listOfPostIDs.get(positionOfPost);

        Post currentPost = listofPosts.get(positionOfPost);
        placard.postCurrent = currentPost;
        if (currentPost == null) return;
        if (currentPost.getUserName()==null) {
            FirebaseCrash.log("User name not found in the database for this person");
            return;
        }

        placard.userName.setText(currentPost.getUserName());
        placard.thisPlacard.setContentDescription(postID);// This is critical as it will be used to
        // identify the post to update later

        if (currentPost.isiLoveFlag()){
            placard.iLove.setImageDrawable(thisContext.getDrawable(R.drawable.heart_full));
            placard.iLove.setContentDescription(HEART_FULL);
        } else {
            placard.iLove.setImageDrawable(thisContext.getDrawable(R.drawable.heart_empty));
            placard.iLove.setContentDescription(HEART_EMPTY);
        }


        //Retrieve the URI of the Profile picture and then insert it into the imageView using Picasso
        Picasso.with(thisContext).load(currentPost.getUserPhotoUri())
                .placeholder(R.drawable.progressbar_green).error(R.drawable.vegan_buddy_menu_icon)
                .into(placard.profilePic);

        String timediff = DateAndTimeUtils.timeDifference(currentPost.getDatestamp());
        placard.timeDifference.setText(timediff);

        //Retrieve the URI of the thumbnail of the meal Screenshot and...
        // ...then insert it into the imageView using Picasso
        //Retrieve the URI of the fullsize of the meal Screenshot and...
        //...insert into the content description of the picture. This URI will be used to display
        // the "full image" on click of the thumbnail.
        Uri mealphotoThumbUri = Uri.parse(currentPost.getMealPhotoThumbnailUri());
        placard.mealScreenShot.setContentDescription(currentPost.getScreenShotUri());
        Picasso.with(thisContext).load(mealphotoThumbUri).into(placard.mealScreenShot);

        int likeCount = currentPost.getLikesCount();
        switch (likeCount) {
            case 0: placard.likes.setText(NO_LIKES);
                break;
            case 1: placard.likes.setText(ONE_LIKE);
                break;
            default: placard.likes.setText(" " + Integer.toString(likeCount) + " likes");
        }

        if (currentPost.isHasComments()) {
            placard.myComments.setImageDrawable(thisContext.getDrawable(R.drawable.ic_chat_24dp_blue));
        } else {
            placard.myComments.setImageDrawable(thisContext.getDrawable(R.drawable.ic_chat_24dp));
        }

        int commentsCount = currentPost.getCommentsCount();
        switch (commentsCount) {
            case 0:placard.commentsCount.setText(NO_COMMENT);
                break;
            case 1:placard.commentsCount.setText(ONE_COMMENT);
                break;
            default:placard.commentsCount.setText("" + Integer.toString(commentsCount) + " comments");
        }

        placard.placardMenu.inflateMenu(R.menu.placard_item_menu);
        placard.placardMenu.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                switch (itemId){
                    case R.id.pi_menu_edit:
                        Toast.makeText(thisContext, "Edit yet to be implemented", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.pi_menu_delete:
                        Toast.makeText(thisContext, "DELETE yet to be implemented", Toast.LENGTH_SHORT).show();
                        break;
                }
                return false;
            }
        });

        View.OnClickListener placardOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onListFragmentInteraction(placard.postCurrent, v, positionOfPost,
                        placard.thisPlacard.getContentDescription().toString());
            }
        };

        //Set an OnClickListener on the 'heart' icon for the change to "like" status of this post
        placard.iLove.setOnClickListener(placardOnClickListener);


        //Set an OnClickListener on the 'likesCount' to see who has liked my post
        placard.likes.setOnClickListener(placardOnClickListener);

        //Set an OnClickListener on the 'mealPhoto' to Zoom into the full picture
        placard.mealScreenShot.setOnClickListener(placardOnClickListener);

        //Set an OnClickListener on the 'share' icon to share this placard on other apps
        placard.shareImage.setOnClickListener(placardOnClickListener);

        //Set an OnClickListener on the 'instagram' icon to share this placard on instagram
        placard.instagramShare.setOnClickListener(placardOnClickListener);

        //Set an OnClickListener on the 'comment' icon and "commentsCount" TextView
        // to add comments about this placard
        placard.commentsCount.setOnClickListener(placardOnClickListener);
        placard.myComments.setOnClickListener(placardOnClickListener);

        //Set an OnClickListener on the 'profile' picture and the profile username
        placard.profilePic.setOnClickListener(placardOnClickListener);
        placard.userName.setOnClickListener(placardOnClickListener);

        //Set a default OnClickListener on the entire 'placard' cardview and
        // the timeDifference textview
        placard.thisPlacard.setOnClickListener(placardOnClickListener);
        placard.timeDifference.setOnClickListener(placardOnClickListener);
    }



    @Override
    public int getItemCount() {
        if(listofPosts==null) return 0;
        else return listofPosts.size();
    }


    void updateLists(List <Post> listofPosts, List <String> listOfPostIds) {
        this.listofPosts = listofPosts;
        if (listOfPostIds != null ) {
            this.listOfPostIDs = listOfPostIds;
        }
        notifyDataSetChanged();
    }

    //Inner Viewholder class for showing the posts
    public class PlacardHolder extends RecyclerView.ViewHolder {
        final View mView;
        Post postCurrent;

        CardView thisPlacard;
        TextView userName;
        CircleImage profilePic;
        TextView timeDifference;
        ImageView mealScreenShot;
        TextView likes;
        ImageView iLove;
        ImageView myComments;
        TextView commentsCount;
        ImageView shareImage;
        Toolbar placardMenu;
        ImageView instagramShare;

        PlacardHolder(View view) {
            super(view);
            mView = view;

            this.thisPlacard = view.findViewById(R.id.pi_card_placard);
            this.userName = view.findViewById(R.id.pi_profile_name);
            this.profilePic = view.findViewById(R.id.pi_profile_picture);
            this.timeDifference = view.findViewById(R.id.pi_timestamp);
            this.mealScreenShot = view.findViewById(R.id.pi_food_photo);
            this.likes = view.findViewById(R.id.pi_likes_count);
            this.iLove = view.findViewById(R.id.pi_heart_icon);
            this.myComments = view.findViewById(R.id.pi_comments_icon);
            this.commentsCount = view.findViewById(R.id.pi_comments_count);
            this.shareImage = view.findViewById(R.id.pi_share_placard);
            this.placardMenu = view.findViewById(R.id.pi_card_menu_toolbar);
            this.instagramShare = view.findViewById(R.id.pi_instagram_icon);

        }

        @Override
        public String toString() {
            return super.toString() + " '" + postCurrent.getDatestamp() + "'";
        }
    }
}
