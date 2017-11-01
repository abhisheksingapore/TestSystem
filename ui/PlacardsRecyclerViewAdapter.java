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

import me.veganbuddy.veganbuddy.R;
import me.veganbuddy.veganbuddy.actors.Post;
import me.veganbuddy.veganbuddy.ui.PlacardsFragment.OnListFragmentInteractionListener;
import me.veganbuddy.veganbuddy.util.DateAndTimeUtils;

import java.util.List;
import java.util.Map;

import static me.veganbuddy.veganbuddy.util.Constants.CURRENT_USER;
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

    private List<Post> listofPosts;
    private List<String> listOfUsers;
    private  Map <String, String> myLikes;

    private final OnListFragmentInteractionListener mListener;
    private Context thisContext;

    PlacardsRecyclerViewAdapter(List<Post> postList, List<String> userFirebaseIDs, OnListFragmentInteractionListener listener) {
        listofPosts = postList;
        mListener = listener;
        listOfUsers = userFirebaseIDs;
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
        Post currentPost = listofPosts.get(positionOfPost);

        placard.postCurrent = currentPost;

        if (currentPost == null) return;
        if (currentPost.getUserName()==null) {
            FirebaseCrash.log("User name not found in the database for this person");
            return;
        }

        placard.userName.setText(currentPost.getUserName());

        //Check listOfUsers to see if this RecyclerViewAdapter is for this users POSTS
        // or for lastPOSTS. if just for users posts then set the placard Content Description and
        // hearts. Else do it as per the requirements of lastPosts....
        if (listOfUsers==null || listOfUsers.size() == 0) {
            placard.userName.setContentDescription(CURRENT_USER);
            if (currentPost.isiLoveFlag()){
                placard.iLove.setImageDrawable(thisContext.getDrawable(R.drawable.heart_full));
                placard.iLove.setContentDescription(HEART_FULL);
            } else {
                placard.iLove.setImageDrawable(thisContext.getDrawable(R.drawable.heart_empty));
                placard.iLove.setContentDescription(HEART_EMPTY);
            }
        }
        else {
            String userFirebaseID = listOfUsers.get(positionOfPost);
            placard.userName.setContentDescription(userFirebaseID);

            //set the value of "iLike" to false with empty heart
            placard.iLove.setImageDrawable(thisContext.getDrawable(R.drawable.heart_empty));
            placard.iLove.setContentDescription(HEART_EMPTY);

            //check if the myLikes Map is null
            if (myLikes!=null){
                //check if the Map myLikes is not empty then if key for the other user is found in
                // the myLikes of thisAppUser
                if (myLikes.size() !=0 || myLikes.containsKey(userFirebaseID)) {
                    //if yes, then check if this post is found in the myLikes of thisAppUser
                    if (myLikes.containsValue(currentPost.getDatestamp())) {
                        //if yes, then change to full heart with appropriate content description
                        placard.iLove.setImageDrawable(thisContext.getDrawable(R.drawable.heart_full));
                        placard.iLove.setContentDescription(HEART_FULL);
                    }
                }
            }
        }
        placard.thisPlacard.setContentDescription(currentPost.getDatestamp());

        //Retrieve the URI of the Profile picture and then insert it into the imageView using Picasso
        Uri profilePicUri = Uri.parse(currentPost.getUserPhotoUri());
        Picasso.with(thisContext).load(profilePicUri).into(placard.profilePic);

        String timediff = DateAndTimeUtils.timeDifference(currentPost.getDatestamp());
        placard.timeDifference.setText(timediff);

        //Retrieve the URI of the thumbnail of the meal Screenshot and...
        // ...then insert it into the imageView using Picasso
        //Retrieve the URI of the fullsize of the meal Screenshot and...
        //...insert into the content description of the picture. This URI will be used to display
        // the "full image" on click of the thumbnail.
        Uri mealphotouri = Uri.parse(currentPost.getMealPhotoThumbnailUri());
        placard.mealPhoto.setContentDescription(currentPost.getScreenShotUri());
        Picasso.with(thisContext).load(mealphotouri).into(placard.mealPhoto);

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


        //Set an OnClickListener on the 'heart' icon for the change to "like" status of this post
        placard.iLove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(placard.postCurrent, v, positionOfPost);
                }
            }
        });

        //Set an OnClickListener on the 'likesCount' to see who has liked my post
        placard.likes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(placard.postCurrent, v, positionOfPost);
                }
            }
        });

        //Set an OnClickListener on the 'mealPhoto' to Zoom into the full picture
        placard.mealPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(placard.postCurrent, v, positionOfPost);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        if(listofPosts==null) return 0;
        else return listofPosts.size();
    }

    public void updateMyLikes(Map<String, String> myLikes) {
        this.myLikes = myLikes;
        notifyDataSetChanged();
    }

    public void updateLists(List <Post> listofPosts, List <String> listOfUsers) {
        this.listofPosts = listofPosts;
        if (listOfUsers != null ) {
            this.listOfUsers = listOfUsers;
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
        ImageView mealPhoto;
        TextView likes;
        ImageView iLove;
        ImageView myComments;
        TextView commentsCount;
        ImageView shareImage;
        Toolbar placardMenu;

        PlacardHolder(View view) {
            super(view);
            mView = view;

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

        @Override
        public String toString() {
            return super.toString() + " '" + postCurrent.getDatestamp() + "'";
        }
    }
}
