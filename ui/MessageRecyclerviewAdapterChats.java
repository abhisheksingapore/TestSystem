package me.veganbuddy.veganbuddy.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jackandphantom.circularimageview.CircleImage;
import com.squareup.picasso.Picasso;

import java.util.List;

import me.veganbuddy.veganbuddy.R;
import me.veganbuddy.veganbuddy.actors.LastChats;

import static me.veganbuddy.veganbuddy.util.Constants.CHAT_BUDDY_ID;
import static me.veganbuddy.veganbuddy.util.Constants.CHAT_BUDDY_NAME;
import static me.veganbuddy.veganbuddy.util.Constants.CHAT_BUDDY_PIC;
import static me.veganbuddy.veganbuddy.util.DateAndTimeUtils.dateStampChat;

/**
 * Created by abhishek on 7/12/17.
 */

public class MessageRecyclerviewAdapterChats
        extends RecyclerView.Adapter<MessageRecyclerviewAdapterChats.MessageChatViewHolder> {

    Context contextMRVAC;
    List<LastChats> lastChatsList;

    MessageRecyclerviewAdapterChats(List<LastChats> list) {
        lastChatsList = list;
    }

    @Override
    public MessageChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        contextMRVAC = parent.getContext();
        View view = LayoutInflater.from(contextMRVAC)
                .inflate(R.layout.item_messages_chat_message, parent, false);
        return new MessageChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MessageChatViewHolder holder, int position) {
        final LastChats lastChat = lastChatsList.get(position);

        if (lastChat == null) return;

        holder.textViewBuddyName.setText(lastChat.getBuddyName());
        holder.textViewChatMessage.setText(lastChat.getTextMessage());

        if (lastChat.getDateStamp() == null) return;
        if (lastChat.getDateStamp().equals(dateStampChat()))
            holder.textViewMessageTime.setText(lastChat.getTimeStamp());
        else holder.textViewMessageTime.setText(lastChat.getDateStamp());

        if (lastChat.getUnRead() != null && lastChat.getUnRead())
            holder.textViewMessageTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.ic_new_message_chat_24dp);
        else
            holder.textViewMessageTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);


        Picasso.with(contextMRVAC).load(lastChat.getBuddyurl())
                .placeholder(R.drawable.veganbuddylogo_stamp_small).into(holder.circleImageBuddy);

        holder.viewThis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(contextMRVAC, ChatActivity.class);
                intent.putExtra(CHAT_BUDDY_NAME, lastChat.getBuddyName());
                intent.putExtra(CHAT_BUDDY_ID, lastChat.buddyID);
                intent.putExtra(CHAT_BUDDY_PIC, lastChat.getBuddyurl());
                contextMRVAC.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (lastChatsList != null) ? lastChatsList.size() : 0;
    }

    public class MessageChatViewHolder extends RecyclerView.ViewHolder {

        CircleImage circleImageBuddy;
        TextView textViewBuddyName;
        TextView textViewChatMessage;
        TextView textViewMessageTime;
        View viewThis;

        MessageChatViewHolder(View view) {
            super(view);
            viewThis = view;
            circleImageBuddy = view.findViewById(R.id.imcm_iv_userpic);
            textViewBuddyName = view.findViewById(R.id.imcm_tv_username);
            textViewChatMessage = view.findViewById(R.id.imcm_tv_lastmessage);
            textViewMessageTime = view.findViewById(R.id.imcm_tv_timestamp);
        }

    }
}
