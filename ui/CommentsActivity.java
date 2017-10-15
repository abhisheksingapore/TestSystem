package me.veganbuddy.veganbuddy.ui;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.bumptech.glide.Glide;

import me.veganbuddy.veganbuddy.R;
import static me.veganbuddy.veganbuddy.util.GlobalVariables.thisAppUser;

import static me.veganbuddy.veganbuddy.util.FirebaseStorageUtils.uploadPostCommentsToDatabase;

public class CommentsActivity extends AppCompatActivity {
    public static PlacardCommentsRecyclerViewAdapter placardCommentsRecyclerViewAdapter;
    public Uri profileUri;
    public String nodeID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        RecyclerView commentsRecyclerView = findViewById(R.id.ac_comments_recyclerview);
        ImageView commentPhoto = findViewById(R.id.ac_header_photo);
        profileUri = Uri.parse(thisAppUser.getPhotoUrl());

        nodeID = getIntent().getStringExtra("nodeID");
        //Load the header photo into the ImageView. Comments are associated with this picture
        String photoUriString = getIntent().getStringExtra("ImageURI");
        Uri photoUri = Uri.parse(photoUriString);
        Glide.with(this).load(photoUri).into(commentPhoto);

        //Setup the recyclerView with all the comments for this photo
        LinearLayoutManager commentsLayoutManager = new LinearLayoutManager(this);
        commentsRecyclerView.setLayoutManager(commentsLayoutManager);
        placardCommentsRecyclerViewAdapter = new PlacardCommentsRecyclerViewAdapter(this);
        commentsRecyclerView.setAdapter(placardCommentsRecyclerViewAdapter);
    }

    public void addCommentClick(View view) {
        EditText inputText = findViewById(R.id.ac_editText_food);
        String inputString = inputText.getText().toString();
        if ((inputString.equals("")) || (inputString.trim().length() == 0)) {
            Toast.makeText(this, "Please input some comments", Toast.LENGTH_SHORT).show();
            return;
        }

        inputString = inputString.trim();
        inputText.setText("");
        uploadPostCommentsToDatabase(nodeID,
                thisAppUser.getPhotoUrl(),
                inputString, "noReplyAuthor", "noReplyText");
    }

    public void commentLikeClick(View view) {
        Toast.makeText(this, "LIKE To be implemented", Toast.LENGTH_LONG).show();
    }

    public void commentReplyClick(View view) {
        Toast.makeText(this, "REPLY To be implemented", Toast.LENGTH_LONG).show();
    }

    public void commentDeleteClick(View view) {
        Toast.makeText(this, "DELETE To be implemented", Toast.LENGTH_LONG).show();

        /*LinearLayout ci_icons = (LinearLayout) view.getParent();
        RelativeLayout ci_main_comment = (RelativeLayout) ci_icons.getParent();
        ImageView authorID = (ImageView) ci_main_comment.findViewById(R.id.ci_comment_author);
        */
    }
}
