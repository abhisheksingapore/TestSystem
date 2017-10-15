package me.veganbuddy.veganbuddy.actors;

import android.support.annotation.Nullable;

import java.util.Map;

/**
 * Created by abhishek on 15/9/17.
 */

public class Comments {

    public static String NO_REPLY_AUTHOR = "noReplyAuthor";
    public static String NO_REPLY_TEXT = "noReplyText";

    private String nodeID;
    private String commentAuthor;
    private String commentText;

    private String replyAuthorUri;
    private String replyText;

    public Comments(){
        //Default empty constructor
    }

    public Comments(String node, String author, String text, String replyAuthor, String replyTxt) {
        this.nodeID = node;
        this.commentAuthor = author;
        this.commentText = text;
        this.replyAuthorUri = replyAuthor;
        this.replyText = replyTxt;
    }

    public String getNodeID() {
        return nodeID;
    }

    public String getCommentAuthor() {
        return commentAuthor;
    }

    public String getCommentText() {
        return commentText;
    }

    public String getReplyAuthorUri() {
        return replyAuthorUri;
    }

    public String getReplyText() {
        return replyText;
    }
}
