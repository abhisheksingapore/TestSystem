package me.veganbuddy.veganbuddy.ui;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import me.veganbuddy.veganbuddy.R;
import me.veganbuddy.veganbuddy.actors.Comments;

import static me.veganbuddy.veganbuddy.actors.Comments.NO_REPLY_AUTHOR;
import static me.veganbuddy.veganbuddy.actors.Comments.NO_REPLY_TEXT;
import static me.veganbuddy.veganbuddy.util.FirebaseStorageUtils.commentsList;

/**
 * Created by abhishek on 15/9/17.
 */

public class PlacardCommentsRecyclerViewAdapter
        extends RecyclerView.Adapter <PlacardCommentsRecyclerViewAdapter.PlacardCommentsHolder> {
    private List<Comments>listofComments;
    Context thisContext;

    PlacardCommentsRecyclerViewAdapter(Context context) {
        super();
        thisContext = context;
        setListofComments();
    }

    @Override
    public PlacardCommentsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(thisContext)
                .inflate(R.layout.comment_item, parent, false);
        return new PlacardCommentsRecyclerViewAdapter.PlacardCommentsHolder(view);
    }

    @Override
    public void onBindViewHolder(PlacardCommentsHolder commentsHolder, int position) {
        Comments eachComment = listofComments.get(listofComments.size() - position - 1);

        String commentAuthorStr = eachComment.getCommentAuthor();
        Uri authorUri = Uri.parse(commentAuthorStr);
        Picasso.with(thisContext).load(authorUri).into(commentsHolder.commentAuthor);
        commentsHolder.commentAuthor.setContentDescription(eachComment.getNodeID());
        commentsHolder.commentText.setText(eachComment.getCommentText());

        if (!eachComment.getReplyAuthorUri().equals(NO_REPLY_AUTHOR)) {
            Uri replyUri = Uri.parse(eachComment.getReplyAuthorUri());
            Picasso.with(thisContext).load(replyUri).into(commentsHolder.replyAuthor);
        } else {
            commentsHolder.replyAuthor.setVisibility(View.GONE);
        }

        if (!eachComment.getReplyText().equals(NO_REPLY_TEXT)) {
            commentsHolder.replyText.setText(eachComment.getReplyText());
        } else {
            commentsHolder.replyText.setVisibility(View.GONE);
            commentsHolder.repliesLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        if (listofComments != null) {
            return listofComments.size();
        } else return 0;
    }

    public void setListofComments() {
        if (commentsList != null) {
            this.listofComments = commentsList;
        }
    }

    //Inner class to define the Viewholder for comments
    public class PlacardCommentsHolder extends RecyclerView.ViewHolder{
        private static final String TAG = "PlacardCommentsHolder";

        private ImageView commentAuthor;
        private TextView commentText;
        private ImageView replyAuthor;
        private TextView replyText;
        private LinearLayout repliesLayout;

        public PlacardCommentsHolder(View itemView) {
            super(itemView);
            this.commentAuthor = itemView.findViewById(R.id.ci_comment_author);
            this.commentText = itemView.findViewById(R.id.ci_comment_text);
            this.replyAuthor = itemView.findViewById(R.id.ci_comment_reply_author);
            this.replyText = itemView.findViewById(R.id.ci_comment_reply_text);
            this.repliesLayout = itemView.findViewById(R.id.ci_replies);
        }
    }
}
