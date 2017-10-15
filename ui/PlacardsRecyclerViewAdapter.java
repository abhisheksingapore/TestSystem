package me.veganbuddy.veganbuddy.ui;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.crash.FirebaseCrash;
import com.jackandphantom.circularimageview.CircleImage;

import java.util.List;

import me.veganbuddy.veganbuddy.R;
import me.veganbuddy.veganbuddy.actors.Post;
import me.veganbuddy.veganbuddy.util.DateAndTimeUtils;

import static me.veganbuddy.veganbuddy.util.Constants.CURRENT_USER;
import static me.veganbuddy.veganbuddy.util.Constants.HEART_EMPTY;
import static me.veganbuddy.veganbuddy.util.Constants.HEART_FULL;
import static me.veganbuddy.veganbuddy.util.Constants.NO_COMMENT;
import static me.veganbuddy.veganbuddy.util.Constants.NO_LIKES;
import static me.veganbuddy.veganbuddy.util.Constants.ONE_COMMENT;
import static me.veganbuddy.veganbuddy.util.Constants.ONE_LIKE;
import static me.veganbuddy.veganbuddy.util.Constants.PRVA_TAG;
import static me.veganbuddy.veganbuddy.util.GlobalVariables.thisAppUser;

/**
 * Created by abhishek on 9/9/17.
 */

public class PlacardsRecyclerViewAdapter extends RecyclerView.Adapter<PlacardsRecyclerViewAdapter.PlacardHolder> {

    private List<Post> listofPosts;
    private List<String> listOfUsers;
    Context thisContext;


    public PlacardsRecyclerViewAdapter(List <Post> list, Context context) {
        listofPosts = list;
        thisContext = context;
        listOfUsers = null;
    }

    public PlacardsRecyclerViewAdapter(List <Post> list, List<String> userFirebaseIDs, Context context) {
        listofPosts = list;
        thisContext = context;
        listOfUsers = userFirebaseIDs;
    }

    @Override
    public PlacardHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context recyclerViewContext = parent.getContext();
        View view = LayoutInflater.from(recyclerViewContext)
                .inflate(R.layout.placard_item, parent, false);
        return new PlacardHolder(view);
    }

    @Override
    public void onBindViewHolder(PlacardHolder placard, int position) {
        // To present the data in reverse order than it is inserted we insert the last Post first
        int positionOfPost = listofPosts.size() - position - 1;
        Post currentPost = listofPosts.get(positionOfPost);

        if (currentPost.getUserName()==null) {
            FirebaseCrash.log("User name not found in the database for this person");
            return;
        }

        placard.userName.setText(currentPost.getUserName());

        //Check listOfUsers to see if this RecyclerViewAdapter is for this users POSTS or for ALL POSTS
        if (listOfUsers==null) placard.userName.setContentDescription(CURRENT_USER);
        else {
            placard.userName.setContentDescription(listOfUsers.get(positionOfPost));
        }
        placard.thisPlacard.setContentDescription(currentPost.getDatestamp());

        //Retrieve the URI of the Profile picture and then insert it into the imageView using Picasso
        Uri profilePicUri = Uri.parse(currentPost.getUserPhotoUri());
        Glide.with(thisContext).load(profilePicUri).into(placard.profilePic);

        String timediff = DateAndTimeUtils.timeDifference(currentPost.getDatestamp());
        placard.timeDifference.setText(timediff);

        //Retrieve the URI of the thumbnail of the meal Screenshot and...
        // ...then insert it into the imageView using Glide
        //Retrieve the URI of the fullsize of the meal Screenshot and...
        //...insert into the content description of the picture. This URI will be used to display
        // the "full image" on click of the thumbnail.
        Uri mealphotouri = Uri.parse(currentPost.getMealPhotoThumbnailUri());
        placard.mealPhoto.setContentDescription(currentPost.getScreenShotUri());
        Glide.with(thisContext).load(mealphotouri).into(placard.mealPhoto);

        int likeCount = currentPost.getLikesCount();
        switch (likeCount) {
            case 0: placard.likes.setText(NO_LIKES);
            break;
            case 1: placard.likes.setText(ONE_LIKE);
            break;
            default: placard.likes.setText(" " + Integer.toString(likeCount) + " likes");
        }

        if (currentPost.isiLoveFlag()){
            placard.iLove.setImageDrawable(thisContext.getDrawable(R.drawable.heart_full));
            placard.iLove.setContentDescription(HEART_FULL);
        } else {
            placard.iLove.setImageDrawable(thisContext.getDrawable(R.drawable.heart_empty));
            placard.iLove.setContentDescription(HEART_EMPTY);
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
    }

    public void setListofPosts(List<Post> listofPosts) {
        this.listofPosts = listofPosts;
    }


    @Override
    public int getItemCount() {
        if (listofPosts == null) {
            return 0;
        } else {
            return listofPosts.size();
        }
    }


    //Inner Viewholder class for showing the posts
    public class PlacardHolder extends RecyclerView.ViewHolder {
        private static final String TAG = "PlacardsHolder";

        public CardView thisPlacard;
        public TextView userName;
        public CircleImage profilePic;
        public TextView timeDifference;
        public ImageView mealPhoto;
        public TextView likes;
        public ImageView iLove;
        public ImageView myComments;
        public TextView commentsCount;
        public ImageView shareImage;
        public Toolbar placardMenu;

        public PlacardHolder(View view){
            super(view);
            this.thisPlacard = view.findViewById(R.id.pi_card_placard);
            this.userName = view.findViewById(R.id.pi_profile_name);
            this.profilePic = view.findViewById(R.id.pi_profile_picture);
            this.timeDifference = view.findViewById(R.id.pi_timestamp);
            this.mealPhoto = view.findViewById(R.id.pi_food_photo);
            this.likes = view.findViewById(R.id.pi_likes_count);
            this.iLove = view.findViewById(R.id.pi_heart_icon);
            this.myComments = view.findViewById(R.id.pi_comments_icon);
            this.commentsCount = view.findViewById(R.id.pi_comments_count);
            this.shareImage = view.findViewById(R.id.pi_share_placard);
            this.placardMenu = view.findViewById(R.id.pi_card_menu_toolbar);
        }

    }


}
