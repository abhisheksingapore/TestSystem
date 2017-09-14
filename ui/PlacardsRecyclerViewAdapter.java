package me.veganbuddy.veganbuddy.ui;

import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jackandphantom.circularimageview.CircleImage;
import com.squareup.picasso.Picasso;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

import me.veganbuddy.veganbuddy.R;
import me.veganbuddy.veganbuddy.actors.Post;
import me.veganbuddy.veganbuddy.util.DateAndTimeUtils;

/**
 * Created by abhishek on 9/9/17.
 */

public class PlacardsRecyclerViewAdapter extends RecyclerView.Adapter<PlacardsRecyclerViewAdapter.PlacardHolder> {
    private static final String TAG = "PlacardsViewAdapter";
    public static final String HEART_FULL = "heart full";
    public static final String HEART_EMPTY = "heart empty";

    public static final String NO_LIKES = " No likes";
    public static final String ONE_LIKE = " 1 like";


    private  List<Post> listofPosts;
    Context thisContext;


    public PlacardsRecyclerViewAdapter(List <Post> list, Context context) {
        listofPosts = list;
        thisContext = context;
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
        Post currentPost = listofPosts.get(listofPosts.size() - position - 1);

        placard.userName.setText(currentPost.getUserName());
        placard.thisPlacard.setContentDescription(currentPost.getDatestamp());

        //Retrieve the URI of the Profile picture and then insert it into the imageView using Picasso
        Uri profilePicUri = Uri.parse(currentPost.getUserPhotoUri());
        Picasso.with(thisContext).load(profilePicUri).into(placard.profilePic);

        String timediff = DateAndTimeUtils.timeDifference(currentPost.getDatestamp());
        placard.timeDifference.setText(timediff);

        //Retrieve the URI of the meal picture and then insert it into the imageView using Picasso
        Uri mealphotouri = Uri.parse(currentPost.getMealPhotoThumbnailUri());
        Picasso.with(thisContext).load(mealphotouri).into(placard.mealPhoto);
        placard.veganText.setText(currentPost.getVeganPhilosophyText());

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
        public TextView veganText;
        public TextView likes;
        public ImageView iLove;
        public ImageView myComments;

        public PlacardHolder(View view){
            super(view);
            this.thisPlacard = view.findViewById(R.id.pi_card_placard);
            this.userName = view.findViewById(R.id.pi_profile_name);
            this.profilePic = view.findViewById(R.id.pi_profile_picture);
            this.timeDifference = view.findViewById(R.id.pi_timestamp);
            this.mealPhoto = view.findViewById(R.id.pi_food_photo);
            this.veganText = view.findViewById(R.id.pi_vegan_philosophytext);
            this.likes = view.findViewById(R.id.pi_likes_count);
            this.iLove = view.findViewById(R.id.pi_heart_icon);
            this.myComments = view.findViewById(R.id.pi_comments_icon);
        }

    }


}
