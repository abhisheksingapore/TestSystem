package me.veganbuddy.veganbuddy.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jackandphantom.circularimageview.CircleImage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import me.veganbuddy.veganbuddy.R;
import me.veganbuddy.veganbuddy.actors.ChatMessage;

import static me.veganbuddy.veganbuddy.util.Constants.BUDDY_NAME;
import static me.veganbuddy.veganbuddy.util.Constants.BUDDY_UNREAD;
import static me.veganbuddy.veganbuddy.util.Constants.BUDDY_URL;
import static me.veganbuddy.veganbuddy.util.Constants.CHAT_BUDDY_ID;
import static me.veganbuddy.veganbuddy.util.Constants.CHAT_BUDDY_NAME;
import static me.veganbuddy.veganbuddy.util.Constants.CHAT_BUDDY_PIC;
import static me.veganbuddy.veganbuddy.util.Constants.CHAT_MESSAGES_NODE;
import static me.veganbuddy.veganbuddy.util.Constants.CHAT_TAG;
import static me.veganbuddy.veganbuddy.util.Constants.IS_TYPING;
import static me.veganbuddy.veganbuddy.util.Constants.LAST_CHAT_MESSAGES_NODE;
import static me.veganbuddy.veganbuddy.util.DateAndTimeUtils.dateStampChat;
import static me.veganbuddy.veganbuddy.util.DateAndTimeUtils.timeStampChat;
import static me.veganbuddy.veganbuddy.util.GlobalVariables.thisAppUser;

public class ChatActivity extends AppCompatActivity {

    private static String chatBuddyID;
    private static String buddyName;
    private static String buddyUrl;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference();
    private RecyclerView recyclerViewChatMessages;
    private int chatMessagesLastPosition = 0;
    private String istypingMessageKey = "";
    private boolean isTyping = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = findViewById(R.id.achat_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("Chat With " + getIntent().getStringExtra(CHAT_BUDDY_NAME));
        CircleImage circleImage = findViewById(R.id.achat_buddy_pic);
        buddyUrl = getIntent().getStringExtra(CHAT_BUDDY_PIC);
        chatBuddyID = getIntent().getStringExtra(CHAT_BUDDY_ID);
        buddyName = getIntent().getStringExtra(CHAT_BUDDY_NAME);

        if (chatBuddyID == null) {
            Intent intentIncompleteDataReceived = new Intent(this, LandingPage.class);
            startActivity(intentIncompleteDataReceived);
            finish();
        }

        Picasso.with(this).load(buddyUrl).placeholder(R.drawable.vegan_buddy_menu_icon)
                .into(circleImage);
    }

    @Override
    protected void onResume() {
        super.onResume();
        recyclerViewChatMessages = findViewById(R.id.cc_recyclerview_message_list);

        ChatRecyclerViewAdapter chatRecyclerViewAdapter =
                new ChatRecyclerViewAdapter(this);
        recyclerViewChatMessages.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewChatMessages.setAdapter(chatRecyclerViewAdapter);
        retrieveMessages(chatRecyclerViewAdapter);
        recyclerViewChatMessages.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int left, int top, int right, int bottom,
                                       int oldleft, int oldTop, int oldRight, int oldBottom) {
                if (bottom < oldBottom) {
                    if (chatMessagesLastPosition > 0)
                        recyclerViewChatMessages.smoothScrollToPosition(chatMessagesLastPosition);
                }
            }
        });

        EditText editText = findViewById(R.id.cc_edittext_chatbox);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!isTyping) userIsTyping();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    private void userIsTyping() {
        //Todo: implement when the user is typing
        isTyping = true;

        ChatMessage chatMessageIsTyping = new ChatMessage();
        chatMessageIsTyping.setSenderID(thisAppUser.getFireBaseID());
        chatMessageIsTyping.setSenderPicURL(thisAppUser.getPhotoUrl());
        chatMessageIsTyping.setTextMessage(IS_TYPING);
        chatMessageIsTyping.setDateStamp(dateStampChat());
        chatMessageIsTyping.setTimeStamp(timeStampChat());

        //Add messages to myBuddy myChatMessages node
        istypingMessageKey = databaseReference.child(chatBuddyID).child(CHAT_MESSAGES_NODE)
                .child(thisAppUser.getFireBaseID()).push().getKey();

        databaseReference.child(chatBuddyID).child(CHAT_MESSAGES_NODE)
                .child(thisAppUser.getFireBaseID()).child(istypingMessageKey).setValue(chatMessageIsTyping);

        Timer timer = new Timer();
        timer.schedule(new UserStoppedTyping(), 6000);

    }

    @Override
    protected void onPause() {
        super.onPause();
        userStoppedTyping();
    }

    private void userStoppedTyping() {
        if (istypingMessageKey.length() > 1) {
            databaseReference.child(chatBuddyID).child(CHAT_MESSAGES_NODE)
                    .child(thisAppUser.getFireBaseID()).child(istypingMessageKey)
                    .removeValue();
            istypingMessageKey = "";
            isTyping = false;
        }
    }

    public void chatSend(View view) {
        EditText editText = findViewById(R.id.cc_edittext_chatbox);
        String userMessage = editText.getText().toString();
        if (userMessage.trim().length() == 0) return;
        editText.getText().clear();
        ChatMessage chatMessagethis = new ChatMessage();
        chatMessagethis.setSenderID(thisAppUser.getFireBaseID());
        chatMessagethis.setSenderPicURL(thisAppUser.getPhotoUrl());
        chatMessagethis.setTextMessage(userMessage);
        chatMessagethis.setDateStamp(dateStampChat());
        chatMessagethis.setTimeStamp(timeStampChat());

        //Add messages to myChatMessages node
        databaseReference.child(thisAppUser.getFireBaseID()).child(CHAT_MESSAGES_NODE)
                .child(chatBuddyID).push().setValue(chatMessagethis);

        //Add messages to myBuddy myChatMessages node
        if (istypingMessageKey.length() > 1) {
            databaseReference.child(chatBuddyID).child(CHAT_MESSAGES_NODE)
                    .child(thisAppUser.getFireBaseID()).child(istypingMessageKey)
                    .setValue(chatMessagethis);
            istypingMessageKey = "";
            isTyping = false;
        } else {
            databaseReference.child(chatBuddyID).child(CHAT_MESSAGES_NODE)
                    .child(thisAppUser.getFireBaseID()).push().setValue(chatMessagethis);
        }

        //Add messages to myLastChatMessages node
        //Todo: use LastChats class instead of multiple writes
        databaseReference.child(thisAppUser.getFireBaseID()).child(LAST_CHAT_MESSAGES_NODE)
                .child(chatBuddyID).setValue(chatMessagethis);
        databaseReference.child(thisAppUser.getFireBaseID()).child(LAST_CHAT_MESSAGES_NODE)
                .child(chatBuddyID).child(BUDDY_NAME).setValue(buddyName);
        databaseReference.child(thisAppUser.getFireBaseID()).child(LAST_CHAT_MESSAGES_NODE)
                .child(chatBuddyID).child(BUDDY_URL).setValue(buddyUrl);


        //Add messages to myBuddy myLastChatMessage node
        //Todo: use LastChats class instead of multiple writes
        databaseReference.child(chatBuddyID).child(LAST_CHAT_MESSAGES_NODE)
                .child(thisAppUser.getFireBaseID()).setValue(chatMessagethis);
        databaseReference.child(chatBuddyID).child(LAST_CHAT_MESSAGES_NODE)
                .child(thisAppUser.getFireBaseID()).child(BUDDY_NAME)
                .setValue(thisAppUser.getUserName());
        databaseReference.child(chatBuddyID).child(LAST_CHAT_MESSAGES_NODE)
                .child(thisAppUser.getFireBaseID()).child(BUDDY_URL)
                .setValue(thisAppUser.getPhotoUrl());
        databaseReference.child(chatBuddyID).child(LAST_CHAT_MESSAGES_NODE)
                .child(thisAppUser.getFireBaseID()).child(BUDDY_UNREAD)
                .setValue(true);

    }

    private void retrieveMessages(final ChatRecyclerViewAdapter chatRecyclerViewAdapter) {

        //Add listener to my Chat node
        Query queryChat = databaseReference.child(thisAppUser.getFireBaseID()).child(CHAT_MESSAGES_NODE)
                .child(chatBuddyID);
        queryChat.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    List<ChatMessage> chatMessages = new ArrayList<>();
                    for (DataSnapshot dataSnapshotSingle : dataSnapshot.getChildren()) {
                        ChatMessage chatMessagethis = dataSnapshotSingle.getValue(ChatMessage.class);
                        chatMessages.add(chatMessagethis);
                    }
                    chatRecyclerViewAdapter.setChatMessageList(chatMessages);
                    chatMessagesLastPosition = chatMessages.size() - 1;
                    recyclerViewChatMessages.scrollToPosition(chatMessagesLastPosition);
                }
                Log.v(CHAT_TAG, "Chat Messages data retrieved successfully");
                //Mark myLastChatMessages "UNREAD = false" as all messages are retrieved now
                if (chatBuddyID != null) {
                    databaseReference.child(thisAppUser.getFireBaseID()).child(LAST_CHAT_MESSAGES_NODE)
                            .child(chatBuddyID).child(BUDDY_UNREAD)
                            .setValue(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(CHAT_TAG, "Failed to retrieve Chat Messages data");
            }
        });
    }

    class UserStoppedTyping extends TimerTask {
        @Override
        public void run() {
            userStoppedTyping();
        }
    }

}
