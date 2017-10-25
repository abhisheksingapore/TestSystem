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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import me.veganbuddy.veganbuddy.R;
import me.veganbuddy.veganbuddy.actors.Vnotification;

import static me.veganbuddy.veganbuddy.util.Constants.DATE_TIME_NODE;
import static me.veganbuddy.veganbuddy.util.Constants.VNF_TAG;
import static me.veganbuddy.veganbuddy.util.Constants.VN_NODES_TO_RETRIEVE;
import static me.veganbuddy.veganbuddy.util.Constants.V_NOTIFICATIONS_NODE;
import static me.veganbuddy.veganbuddy.util.GlobalVariables.thisAppUser;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class VnotificationFragment extends Fragment {

    private static final String ARG_LIST_SOURCE = "source";
    private String LIST_SOURCE = "";

    private static FirebaseDatabase mDatabase;
    private static DatabaseReference myRef; //Reference for nodes in the database

    private List<Vnotification> vnotificationList  = new ArrayList<>();
    private VnotificationRecyclerViewAdapter vnotificationRVA;

    private OnListFragmentInteractionListener mListener;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public VnotificationFragment() {

    }

    public static VnotificationFragment newInstance(String source) {
        VnotificationFragment fragment = new VnotificationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_LIST_SOURCE, source);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            LIST_SOURCE = getArguments().getString(ARG_LIST_SOURCE);
        }
        try {
            retrieveVvotificationsList();
        } catch (NullPointerException NPE) {
            FirebaseCrash.log("NullpointerException in Vnotification array initialization");
            Log.e(VNF_TAG, "Nullpointerexception in Vnotification array initialization " + NPE.toString());
            NPE.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vnotification_list, container, false);
        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            vnotificationRVA = new VnotificationRecyclerViewAdapter(
                    vnotificationList, LIST_SOURCE, mListener);
            recyclerView.setAdapter(vnotificationRVA);
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
        void onListFragmentInteraction(Vnotification item);
    }

    /***********************************************************************
     ***********************************************************************
     Adding Firebase database Listeners to retrieve the data and update the UI on data change
     ***********************************************************************
     ************************************************************************/
    private void setNotificationsDatabaseReference() {
        mDatabase = FirebaseDatabase.getInstance();
        myRef = mDatabase.getReference(V_NOTIFICATIONS_NODE);
    }

    private void retrieveVvotificationsList() {
        setNotificationsDatabaseReference();
        String username = thisAppUser.getFireBaseID();

        myRef.child(username)
                .child(LIST_SOURCE).limitToLast(VN_NODES_TO_RETRIEVE).orderByChild(DATE_TIME_NODE)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot!=null && dataSnapshot.getChildrenCount()>0) {
                            vnotificationList = createArrayofVnotifications(dataSnapshot);
                            Log.v(VNF_TAG, LIST_SOURCE + " Vnotification data retrieved from Firebase");
                            vnotificationRVA.updateList(vnotificationList);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(VNF_TAG, "Error in retrieving " + LIST_SOURCE +
                                " Vnotification data from Firebase" + databaseError.getDetails());
                        FirebaseCrash.log("Error in retrieving " + LIST_SOURCE +
                                " Vnotification data from Firebase" + databaseError.getDetails());
                    }
                });
    }

    private List<Vnotification> createArrayofVnotifications(DataSnapshot dataSnapshot) {
        List <Vnotification> thisList = new ArrayList<>();
        for (DataSnapshot dataSnapshotSingle : dataSnapshot.getChildren()) {
            Vnotification vnotification = dataSnapshotSingle.getValue(Vnotification.class);
            thisList.add(vnotification);
        }
        //Finally, add the List Header item - either INBOUND or OUTBOUND
        thisList.add(new Vnotification(LIST_SOURCE));
        return thisList;
    }

}
