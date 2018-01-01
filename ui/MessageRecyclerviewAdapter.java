package me.veganbuddy.veganbuddy.ui;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jackandphantom.circularimageview.CircleImage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import me.veganbuddy.veganbuddy.R;
import me.veganbuddy.veganbuddy.actors.Buddy;

import static me.veganbuddy.veganbuddy.util.Constants.CHAT_BUDDY_ID;
import static me.veganbuddy.veganbuddy.util.Constants.CHAT_BUDDY_NAME;
import static me.veganbuddy.veganbuddy.util.Constants.CHAT_BUDDY_PIC;

/**
 * Created by abhishek on 3/12/17.
 */

public class MessageRecyclerviewAdapter
        extends RecyclerView.Adapter<MessageRecyclerviewAdapter.MessageViewHolder> {

    List<Buddy> buddies = new ArrayList<>();
    Context contextMRVA;

    MessageRecyclerviewAdapter(List<Buddy> buddyList) {
        buddies = buddyList;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        contextMRVA = parent.getContext();
        View view = LayoutInflater.from(contextMRVA)
                .inflate(R.layout.item_messages_chat_contact, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        final Buddy buddythis = buddies.get(position);
        Picasso.with(contextMRVA).load(buddythis.getPhotoUrl())
                .placeholder(R.drawable.vegan_buddy_menu_icon).into(holder.userpic);
        holder.username.setText(buddythis.getName());
        holder.viewThis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(contextMRVA, ChatActivity.class);
                intent.putExtra(CHAT_BUDDY_NAME, buddythis.getName());
                intent.putExtra(CHAT_BUDDY_ID, buddythis.buddyID);
                intent.putExtra(CHAT_BUDDY_PIC, buddythis.getPhotoUrl());
                contextMRVA.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return buddies == null ? 0 : buddies.size();
    }


    class MessageViewHolder extends RecyclerView.ViewHolder {
        View viewThis;
        CircleImage userpic;
        TextView username;
        FloatingActionButton messageFab;

        MessageViewHolder(View thisview) {
            super(thisview);
            userpic = thisview.findViewById(R.id.imcc_iv_userpic);
            username = thisview.findViewById(R.id.imcc_tv_username);
            messageFab = thisview.findViewById(R.id.imcc_iv_message);
            viewThis = thisview;
        }
    }
}
