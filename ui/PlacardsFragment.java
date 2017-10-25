package me.veganbuddy.veganbuddy.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import me.veganbuddy.veganbuddy.R;
import me.veganbuddy.veganbuddy.actors.Post;
import me.veganbuddy.veganbuddy.actors.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.veganbuddy.veganbuddy.util.Constants.DATE_STAMP_KEY_NAME;
import static me.veganbuddy.veganbuddy.util.Constants.NODE_FOR_ALL_POSTS;
import static me.veganbuddy.veganbuddy.util.Constants.NODE_FOR_MY_POSTS;
import static me.veganbuddy.veganbuddy.util.Constants.NUMBER_OF_POSTS_TO_RETRIEVE;
import static me.veganbuddy.veganbuddy.util.Constants.PF_TAG;
import static me.veganbuddy.veganbuddy.util.Constants.PROFILE_NODE;
import static me.veganbuddy.veganbuddy.util.GlobalVariables.thisAppUser;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class PlacardsFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String ARG_POSTS_NODE = "posts_node";

    // TODO: Customize parameters
    private int mColumnCount = 1;
    private String mPostsNode = "posts";
    private OnListFragmentInteractionListener mListener;

    private static DatabaseReference myRef; //Reference for nodes in the database
    private List<Post> postsList = new ArrayList<>();
    private List<String> userFirebaseIDs = new ArrayList<>();
    private Map<String, String> myLikes = new HashMap<>();

    PlacardsRecyclerViewAdapter placardsRecyclerViewAdapter;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PlacardsFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static PlacardsFragment newInstance(int columnCount, String postsNode) {
        PlacardsFragment fragment = new PlacardsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putString(ARG_POSTS_NODE, postsNode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            mPostsNode = getArguments().getString(ARG_POSTS_NODE);

            //Set Listener to thisAppUser data for the fragment of lastPosts. This will be used to
            // retrieve myLikes
            if (mPostsNode.equals(NODE_FOR_ALL_POSTS)) {
                retrieveUserData();
            }
            retrievePostsData(mPostsNode);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_placards_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            placardsRecyclerViewAdapter = new PlacardsRecyclerViewAdapter(postsList,
                                                        userFirebaseIDs, mListener);
            recyclerView.setAdapter(placardsRecyclerViewAdapter);
        }
        return view;
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
        void onListFragmentInteraction(Post item, View v, int position);
    }

    /***********************************************************************
     ***********************************************************************
     Adding Firebase database Listeners to retrieve the data and update the UI on data change
     ***********************************************************************
     ************************************************************************/

    //This sets reference to to lastPosts node in the database
    private static void setPostsFirebaseReference(String mPostsNode) {
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        switch (mPostsNode) {
            case NODE_FOR_ALL_POSTS: myRef = mDatabase.getReference().child(NODE_FOR_ALL_POSTS);
            break;
            case NODE_FOR_MY_POSTS: myRef = mDatabase.getReference()
                    .child(thisAppUser.getFireBaseID()).child(NODE_FOR_MY_POSTS);
            break;
        }
    }

    private void retrievePostsData(final String mPostsNode) {
        setPostsFirebaseReference(mPostsNode); //This sets reference to user specific nodes in the database
        myRef.limitToLast(NUMBER_OF_POSTS_TO_RETRIEVE)
                .orderByChild(DATE_STAMP_KEY_NAME)
                .addValueEventListener(new ValueEventListener() {//Todo:optimize addValueEventListeners to childEventListeners
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                postsList = new ArrayList<>(); //reinitialize as the DataSnapshot will return the entire list
                userFirebaseIDs = new ArrayList<>(); //reinitialize as the DataSnapshot will return the entire list

                if (dataSnapshot.getChildrenCount() > 0) {
                    createArrayFromFirebasePosts(dataSnapshot, mPostsNode);
                    switch (mPostsNode) {
                        case NODE_FOR_ALL_POSTS: placardsRecyclerViewAdapter.updateLists(postsList, userFirebaseIDs);
                            break;
                        case NODE_FOR_MY_POSTS: placardsRecyclerViewAdapter.updateLists(postsList, null);
                            break;
                    }
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
        for (DataSnapshot singleSnapShot: dataSnapshot.getChildren()) {
            Post thisPost = singleSnapShot.getValue(Post.class);
            postsList.add(thisPost);
            if (mPostsNode.equals(NODE_FOR_ALL_POSTS)) {
                userFirebaseIDs.add(singleSnapShot.getKey());
            }
        }
    }

    private void retrieveUserData() {
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = mDatabase.getReference();

        myRef.child(thisAppUser.getFireBaseID()).child(PROFILE_NODE).addValueEventListener(
                new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                myLikes = new HashMap<>(); //reinitialize as the DataSnapshot will return the entire list
                thisAppUser = dataSnapshot.getValue(User.class);
                myLikes = thisAppUser.getMyLikes();
                placardsRecyclerViewAdapter.updateMyLikes(myLikes);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.v(PF_TAG, "Data request cancelled due to some error" + databaseError);
            }
        });

    }

}
