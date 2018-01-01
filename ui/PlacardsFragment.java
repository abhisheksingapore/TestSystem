package me.veganbuddy.veganbuddy.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import me.veganbuddy.veganbuddy.R;
import me.veganbuddy.veganbuddy.actors.Post;

import static me.veganbuddy.veganbuddy.util.Constants.DATE_STAMP_KEY_NAME;
import static me.veganbuddy.veganbuddy.util.Constants.LAST_POSTS_NODE;
import static me.veganbuddy.veganbuddy.util.Constants.NUMBER_OF_POSTS_TO_RETRIEVE;
import static me.veganbuddy.veganbuddy.util.Constants.PF_TAG;
import static me.veganbuddy.veganbuddy.util.Constants.POSTS_NODE;
import static me.veganbuddy.veganbuddy.util.Constants.POST_FAN_NODE;
import static me.veganbuddy.veganbuddy.util.GlobalVariables.thisAppUser;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class PlacardsFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_POSTS_NODE = "posts_node";
    private static DatabaseReference myRef; //Reference for nodes in the database
    String mPostsNode;
    PlacardsRecyclerViewAdapter placardsRecyclerViewAdapter;
    // TODO: Customize parameters
    private OnListFragmentInteractionListener mListener;
    private List<Post> postsList = new ArrayList<>();
    private List<String> postIDList = new ArrayList<>();

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PlacardsFragment() {
    }

    public static PlacardsFragment newInstance( String postsNode) {
        PlacardsFragment fragment = new PlacardsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_POSTS_NODE, postsNode);
        fragment.setArguments(args);
        return fragment;
    }

    /***********************************************************************
     ***********************************************************************
     Adding Firebase database Listeners to retrieve the data and update the UI on data change
     ***********************************************************************
     ************************************************************************/

    //This sets reference to posts and lastPosts node in the database
    private static void setPostsFirebaseReference(String mPostsNode) {
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        switch (mPostsNode) {
            case LAST_POSTS_NODE:
                myRef = mDatabase.getReference().child(LAST_POSTS_NODE);
                break;
            case POSTS_NODE:
                myRef = mDatabase.getReference()
                        .child(thisAppUser.getFireBaseID()).child(POSTS_NODE);
                break;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mPostsNode = getArguments().getString(ARG_POSTS_NODE);
            retrievePostsData();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         View fragmentview = inflater.inflate(R.layout.fragment_placards_list, container, false);

        // Set the adapter
        if (fragmentview instanceof RecyclerView) {
            Context context = fragmentview.getContext();
            RecyclerView recyclerView = (RecyclerView) fragmentview;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            placardsRecyclerViewAdapter = new PlacardsRecyclerViewAdapter(postsList, postIDList,
                    mListener);
            recyclerView.setAdapter(placardsRecyclerViewAdapter);
        }
        return fragmentview;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void retrievePostsData() {
        setPostsFirebaseReference(mPostsNode); //This sets reference to user specific nodes in the database
        myRef.limitToLast(NUMBER_OF_POSTS_TO_RETRIEVE)
                .orderByChild(DATE_STAMP_KEY_NAME)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                postsList = new ArrayList<>(); //reinitialize as the DataSnapshot will return the entire list
                postIDList = new ArrayList<>(); //reinitialize as the DataSnapshot will return the entire list

                if (dataSnapshot.getChildrenCount() > 0) {
                    //create array of Placards from Firebase Posts
                    createArrayFromFirebasePosts(dataSnapshot, mPostsNode);
                }
                else {
                    Log.v(PF_TAG, "No children found for POSTS. Creating a new node");
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.v(PF_TAG, "error found" + databaseError);
            }
        });
    }

    private void createArrayFromFirebasePosts(DataSnapshot dataSnapshot, String mPostsNode) {
        String postID="";
        int itemIndex = 0;

        for (DataSnapshot singleSnapShot: dataSnapshot.getChildren()) {
            Post thisPost = singleSnapShot.getValue(Post.class);
            postsList.add(thisPost);
            postID = singleSnapShot.getKey();
            postIDList.add(postID);

            //retrieve fans for each post to update thisPost iLoveFlag
            retrieveMeInMyFansData(singleSnapShot.getKey(), thisPost, mPostsNode, itemIndex);
            itemIndex++;
        }
        updatePlacardsRecyclerView();
    }

    private void retrieveMeInMyFansData(final String postID, final Post thisPost,
                                        final String mPostsNode, final int itemIndex){
        setPostsFirebaseReference(mPostsNode);
        if (thisAppUser == null) return;
        Query myFansQuery = myRef.child(POST_FAN_NODE).child(postID).child(thisAppUser.getFireBaseID());

        myFansQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //if this fan exists, then
                if (dataSnapshot.exists()) {
                    //update the iLoveFlag
                    thisPost.setiLoveFlag(true);
                    //Todo: to find a more efficient way of updating the recyclerview data
                    placardsRecyclerViewAdapter.notifyDataSetChanged();
                }

                Log.v(PF_TAG, "Successfully retrieved myFans data for iLove flag ");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                FirebaseCrash.log(PF_TAG + "Error in retrieving myFans data for iLove flag "
                + databaseError.getMessage());
                Log.e(PF_TAG, "Error in retrieving myFans data for iLove flag "
                        + databaseError.getMessage());
            }
        });
    }

    private void updatePlacardsRecyclerView() {
        placardsRecyclerViewAdapter.updateLists(postsList, postIDList);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Post item, View v, int position, String postID);
    }

}
