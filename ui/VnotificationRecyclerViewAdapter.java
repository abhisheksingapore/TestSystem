package me.veganbuddy.veganbuddy.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jackandphantom.circularimageview.CircleImage;
import com.squareup.picasso.Picasso;

import me.veganbuddy.veganbuddy.R;
import me.veganbuddy.veganbuddy.actors.Vnotification;
import me.veganbuddy.veganbuddy.ui.VnotificationFragment.OnListFragmentInteractionListener;

import java.util.List;

import static me.veganbuddy.veganbuddy.util.Constants.VN_COMMENT_PHOTO;
import static me.veganbuddy.veganbuddy.util.Constants.VN_DIRECT_MESSAGE;
import static me.veganbuddy.veganbuddy.util.Constants.VN_LIKED_PHOTO;
import static me.veganbuddy.veganbuddy.util.Constants.VN_PHOTO_SHARE;
import static me.veganbuddy.veganbuddy.util.Constants.VN_UPLOADED_NEW_MEAL_PHOTO;
import static me.veganbuddy.veganbuddy.util.DateAndTimeUtils.timeDifference;
import static me.veganbuddy.veganbuddy.util.GlobalVariables.thisAppUser;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Vnotification} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class VnotificationRecyclerViewAdapter extends RecyclerView.Adapter<VnotificationRecyclerViewAdapter.VnotificationHolder> {

    private final OnListFragmentInteractionListener mListener;
    Context thisContext;
    List<Vnotification> thisList;
    String Source;

    public VnotificationRecyclerViewAdapter(List<Vnotification> items, String source,
                                            OnListFragmentInteractionListener listener) {
        thisList = items;
        mListener = listener;
        Source = source;
    }

    @Override
    public VnotificationHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        thisContext = parent.getContext();
        View view = LayoutInflater.from(thisContext)
                .inflate(R.layout.fragment_vnotification, parent, false);
        return new VnotificationHolder(view);
    }

    @Override
    public void onBindViewHolder(final VnotificationHolder vNholder, int position) {
        Drawable vNtypeIcon = thisContext.getDrawable(R.drawable.ic_info_black_24dp);
        String vNotificationMessage;

        int positionOfVnotification = thisList.size() - position - 1; //To display the latest Vnotification first
        Vnotification vnotificationCurrent = thisList.get(positionOfVnotification);
        vNholder.vNotification = vnotificationCurrent;

        //Check if it is the List Header item - either INBOUND or OUTBOUND
        if (vnotificationCurrent.getCreatedBy().equals(Source)) {
            vNotificationMessage = vnotificationCurrent.getCreatedBy() + " Notifications for you";
            vNholder.notificationMessage.setText(vNotificationMessage);
            vNholder.dateTimeStamp.setText("");
            vNholder.notificationType.setImageDrawable(null);
            vNholder.userPic.setImageDrawable(null);
            vNholder.vCoins.setText("");
            return;
        }

        vNotificationMessage = constructVnotificationMessage(vnotificationCurrent);

        //add user profile picture
        Uri userPic = Uri.parse(vnotificationCurrent.getCreatedByPic());
        Picasso.with(thisContext).load(userPic).into(vNholder.userPic);

        //Show the amount of vCoins earned with this action
        vNholder.vCoins.setText(vnotificationCurrent.getNumberOfVcoins());

        //choose the icon for the vNotification type based on the text
        switch (vnotificationCurrent.getType()) {
            case VN_UPLOADED_NEW_MEAL_PHOTO:
                vNtypeIcon = thisContext.getDrawable(R.drawable.ic_add_circle_green_24dp);
                break;
            case VN_LIKED_PHOTO:
                break;
            case VN_COMMENT_PHOTO:
                break;
            case VN_DIRECT_MESSAGE:
                break;
            case VN_PHOTO_SHARE:
                break;
        }
        vNholder.notificationType.setImageDrawable(vNtypeIcon);


        vNholder.notificationMessage.setText(vNotificationMessage);

        //contextualize DateTimestamp with reference to current time and then display the message
        vNholder.dateTimeStamp.setText(timeDifference(vnotificationCurrent.getCreatedAt()));

        vNholder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null!=mListener) {
                    mListener.onListFragmentInteraction(vNholder.vNotification);
                }
            }
        });
    }

    private String constructVnotificationMessage(Vnotification vN) {
        String vNmessage = "";

        //Get name of creator of the Notification
        if (vN.getCreatedBy().equals(thisAppUser.getFireBaseID())) {
            vNmessage = "You have ";
        } else {
            vNmessage = vN.getCreatedByName() + " has ";
        }

        //Add action of the Notification
        switch (vN.getType()) {
            case VN_UPLOADED_NEW_MEAL_PHOTO:
                vNmessage = vNmessage + "uploaded a new meal photo";
                break;
            case VN_LIKED_PHOTO:
                vNmessage = vNmessage + "liked your meal photo";
                break;
            case VN_COMMENT_PHOTO:
                vNmessage = vNmessage + "commented on your meal photo";
                break;
            case VN_DIRECT_MESSAGE:
                vNmessage = vNmessage + "messaged you";
                break;
            case VN_PHOTO_SHARE:
                vNmessage = vNmessage + "shared your meal photo";
                break;
        }

        //Add text about vCoins earned
        vNmessage = vNmessage + " to earn $" + vN.getNumberOfVcoins() + " vCoins";

        return vNmessage;
    }

    @Override
    public int getItemCount() {
        if (thisList == null) return  0;
        else return thisList.size();
    }

    public void updateList(List <Vnotification> list) {
        thisList = list;
        notifyDataSetChanged();
    }

    class VnotificationHolder extends RecyclerView.ViewHolder{
        final View mView;
        Vnotification vNotification;

        private CircleImage userPic;
        private TextView vCoins;
        private TextView notificationMessage;
        private ImageView notificationType;
        private TextView dateTimeStamp;

        VnotificationHolder(View itemView) {
            super(itemView);
            mView = itemView;
            userPic = itemView.findViewById(R.id.ni_user_pic);
            vCoins = itemView.findViewById(R.id.ni_vcoin);
            notificationMessage = itemView.findViewById(R.id.ni_notification_message);
            notificationType = itemView.findViewById(R.id.ni_notification_type_icon);
            dateTimeStamp = itemView.findViewById(R.id.ni_date_time);
        }
    }
}
