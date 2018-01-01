package me.veganbuddy.veganbuddy.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jackandphantom.circularimageview.CircleImage;
import com.squareup.picasso.Picasso;

import java.util.List;

import me.veganbuddy.veganbuddy.R;
import me.veganbuddy.veganbuddy.actors.ChatMessage;

import static me.veganbuddy.veganbuddy.util.Constants.IS_TYPING;
import static me.veganbuddy.veganbuddy.util.Constants.MESSAGE_RECEIVED;
import static me.veganbuddy.veganbuddy.util.Constants.MESSAGE_SENT;
import static me.veganbuddy.veganbuddy.util.DateAndTimeUtils.dateStampChat;
import static me.veganbuddy.veganbuddy.util.GlobalVariables.thisAppUser;


/**
 * Created by abhishek on 5/12/17.
 */

class ChatRecyclerViewAdapter extends RecyclerView.Adapter {

    private Context contextCRVA;
    private List<ChatMessage> chatMessageList;

    ChatRecyclerViewAdapter(Context context) {
        contextCRVA = context;
    }

    public void setChatMessageList(List<ChatMessage> chatMessageList) {
        this.chatMessageList = chatMessageList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage chatMessage = chatMessageList.get(position);
        return (chatMessage.getSenderID().equals(thisAppUser.getFireBaseID())) ?
                MESSAGE_SENT : MESSAGE_RECEIVED;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == MESSAGE_SENT) {
            View viewthis = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_message, parent, false);
            return new SendMessageHolder(viewthis);
        }

        if (viewType == MESSAGE_RECEIVED) {
            View viewThis = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_message_received, parent, false);
            return new ReceivedMessageHolder(viewThis);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ChatMessage chatMessage = chatMessageList.get(position);

        switch (holder.getItemViewType()) {
            case MESSAGE_SENT:
                SendMessageHolder sendMessageHolder = (SendMessageHolder) holder;
                sendMessageHolder.bind(chatMessage);
                break;
            case MESSAGE_RECEIVED:
                ReceivedMessageHolder receivedMessageHolder = (ReceivedMessageHolder) holder;
                receivedMessageHolder.bind(chatMessage);
                break;
        }
    }

    @Override
    public int getItemCount() {
        if (chatMessageList == null) {
            return 0;
        } else {
            return chatMessageList.size();
        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        CircleImage imageViewprofile;
        TextView textViewMessage;
        TextView textViewTime;

        ReceivedMessageHolder(View view) {
            super(view);
            imageViewprofile = view.findViewById(R.id.icmr_buddy_pic);
            textViewMessage = view.findViewById(R.id.icmr_buddy_message);
            textViewTime = view.findViewById(R.id.icmr_message_time);
        }

        void bind(ChatMessage chatMessage) {
            textViewMessage.setText(chatMessage.getTextMessage());
            if (chatMessage.getTextMessage().equals(IS_TYPING)) {
                textViewMessage.setTypeface(textViewMessage.getTypeface(), Typeface.ITALIC);
            }
            if (chatMessage.getDateStamp().equals(dateStampChat())) {
                textViewTime.setText(chatMessage.getTimeStamp());
            } else {
                textViewTime.setText(chatMessage.getDateStamp());
            }

            Picasso.with(contextCRVA).load(chatMessage.getSenderPicURL())
                    .placeholder(R.drawable.vegan_buddy_menu_icon).into(imageViewprofile);
        }

    }

    private class SendMessageHolder extends RecyclerView.ViewHolder {
        TextView textViewMessage;
        TextView textViewTime;

        SendMessageHolder(View view) {
            super(view);
            textViewMessage = view.findViewById(R.id.icm_message);
            textViewTime = view.findViewById(R.id.icm_message_time);
        }

        void bind(ChatMessage chatMessage) {
            textViewMessage.setText(chatMessage.getTextMessage());

            if (chatMessage.getDateStamp().equals(dateStampChat())) {
                textViewTime.setText(chatMessage.getTimeStamp());
            } else {
                textViewTime.setText(chatMessage.getDateStamp());
            }
        }

    }

}
