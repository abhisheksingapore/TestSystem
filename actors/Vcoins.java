package me.veganbuddy.veganbuddy.actors;

import static me.veganbuddy.veganbuddy.util.Constants.ACTIONED;
import static me.veganbuddy.veganbuddy.util.Constants.ACTIONED_FACTOR;
import static me.veganbuddy.veganbuddy.util.Constants.COMMENT_FACTOR;
import static me.veganbuddy.veganbuddy.util.Constants.DIRECT_MESSAGE_FACTOR;
import static me.veganbuddy.veganbuddy.util.Constants.LIKE_FACTOR;
import static me.veganbuddy.veganbuddy.util.Constants.NEW_PHOTO_CREATED;
import static me.veganbuddy.veganbuddy.util.Constants.RECEIVED;
import static me.veganbuddy.veganbuddy.util.Constants.RECEIVED_FACTOR;
import static me.veganbuddy.veganbuddy.util.Constants.SHARE_FACTOR;
import static me.veganbuddy.veganbuddy.util.Constants.VN_COMMENT_PHOTO;
import static me.veganbuddy.veganbuddy.util.Constants.VN_DIRECT_MESSAGE;
import static me.veganbuddy.veganbuddy.util.Constants.VN_LIKED_PHOTO;
import static me.veganbuddy.veganbuddy.util.Constants.VN_PHOTO_SHARE;

/**
 * Created by abhishek on 20/10/17.
 */

public class Vcoins {

    public Long vCoinsEarned;
    public Long vCoinsNewPost;
    public Long vCoinsIlike;
    public Long vCoinsLikeMe;
    public Long vCoinsCommentIgive;
    public Long vCoinsCommentIget;
    public Long vCoinsIFollow;
    public Long vCoinsFollowMe;
    public Long vCoinsIMessage;
    public Long vCoinsMessageMe;
    public Long vCoinsIshareScreenshotFB;
    public Long vCoinsIshareScreenshotTw;
    public Long vCoinsIshareScreenshotPin;
    public Long vCoinsIshareScreenshotIG;
    public Long vCoinsIshareOtherScreenshotFbTwPinIG;
    public Long vCoinsIshareBenefitsBoardFB;
    public Long vCoinsIshareBenefitsBoardTw;
    public Long vCoinsIshareBenefitsBoardPin;
    public Long vCoinsIshareBenefitsBoardIG;
    public Long vCoinsSharedMyScreenshotFbTwPinIG;
    public Long vCoinsIshareAppLink;
    public Long vCoinsIshareFoodWisdom;

    public Long numberOfNewPost;
    public Long numberofIlikes;
    public Long numberOfLikeMe;
    public Long numberOfCommentIgive;
    public Long numberOfCommentIget;
    public Long numberOfIFollow;
    public Long numberOfFollowMe;
    public Long numberOfIMessage;
    public Long numberOfMessageMe;
    public Long numberOfMyScreenshotFbTwPinIG;
    public Long numberOfIshareScreenshotFB;
    public Long numberOfIshareScreenshotTw;
    public Long numberOfIshareScreenshotPin;
    public Long numberOfIshareScreenshotIG;
    public Long numberOfIshareOtherScreenshotFbTwPinIG;
    public Long numberOfIshareBenefitsBoardFB;
    public Long numberOfIshareBenefitsBoardTw;
    public Long numberOfIshareBenefitsBoardPin;
    public Long numberOfIshareBenefitsBoardIG;
    public Long numberOfIshareAppLink;
    public Long numberOfIshareFoodWisdom;

    public Vcoins() {
        //empty default constructor
    }

    public Vcoins(String direction, String vNtype) {
        double vCoinsforThis = getDirectionFactor(direction) * getVnTypeFactor(vNtype) * NEW_PHOTO_CREATED;
        vCoinsEarned = Math.round(vCoinsforThis);
    }

    private double getVnTypeFactor(String VnotificationType) {
        double multiplierFactor = 0.0;
        switch (VnotificationType){
            case VN_LIKED_PHOTO: multiplierFactor = LIKE_FACTOR;
            break;
            case VN_COMMENT_PHOTO: multiplierFactor = COMMENT_FACTOR;
            break;
            case VN_DIRECT_MESSAGE: multiplierFactor = DIRECT_MESSAGE_FACTOR;
            break;
            case VN_PHOTO_SHARE: multiplierFactor = SHARE_FACTOR;
            break;
        }
        return multiplierFactor;
    }

    private double getDirectionFactor(String direction) {
        double multiplierFactor = 0.0;
        switch (direction){
            case ACTIONED: multiplierFactor = ACTIONED_FACTOR;
            break;
            case RECEIVED: multiplierFactor = RECEIVED_FACTOR;
        }
        return multiplierFactor;
    }


    public Long getvCoinsEarned() {
        return vCoinsEarned == null ? 0 : vCoinsEarned;
    }

    public Long getvCoinsIlike() {
        return vCoinsIlike == null ? 0 : vCoinsIlike;
    }

    public Long getvCoinsLikeMe() {
        return vCoinsLikeMe == null ? 0 : vCoinsLikeMe;
    }

    public Long getvCoinsCommentIgive() {
        return vCoinsCommentIgive == null ? 0 : vCoinsCommentIgive;
    }

    public Long getvCoinsCommentIget() {
        return vCoinsCommentIget == null ? 0 : vCoinsCommentIget;
    }

    public Long getvCoinsIFollow() {
        return vCoinsIFollow == null ? 0 : vCoinsIFollow;
    }

    public Long getvCoinsFollowMe() {
        return vCoinsFollowMe == null ? 0 : vCoinsFollowMe;
    }

    public Long getvCoinsIshareAppLink() {
        return vCoinsIshareAppLink == null ? 0 : vCoinsIshareAppLink;
    }

    public Long getvCoinsIshareFoodWisdom() {
        return vCoinsIshareFoodWisdom == null ? 0 : vCoinsIshareFoodWisdom;
    }

    public Long getvCoinsNewPost() {
        return vCoinsNewPost == null ? 0 : vCoinsNewPost;
    }

    public Long getNumberOfNewPost() {
        return numberOfNewPost == null ? 0 : numberOfNewPost;
    }

    public Long getNumberofIlikes() {
        return numberofIlikes == null ? 0 : numberofIlikes;
    }

    public Long getNumberOfLikeMe() {
        return numberOfLikeMe == null ? 0 : numberOfLikeMe;
    }

    public Long getNumberOfCommentIgive() {
        return numberOfCommentIgive == null ? 0 : numberOfCommentIgive;
    }

    public Long getNumberOfCommentIget() {
        return numberOfCommentIget == null ? 0 : numberOfCommentIget;
    }

    public Long getNumberOfIFollow() {
        return numberOfIFollow == null ? 0 : numberOfIFollow;
    }

    public Long getNumberOfFollowMe() {
        return numberOfFollowMe == null ? 0 : numberOfFollowMe;
    }

    public Long getNumberOfIshareAppLink() {
        return numberOfIshareAppLink == null ? 0 : numberOfIshareAppLink;
    }

    public Long getNumberOfIshareFoodWisdom() {
        return numberOfIshareFoodWisdom == null ? 0 : numberOfIshareFoodWisdom;
    }

    public Long getvCoinsIMessage() {
        return vCoinsIMessage == null ? 0 : vCoinsIMessage;
    }

    public Long getvCoinsMessageMe() {
        return vCoinsMessageMe == null ? 0 : vCoinsMessageMe;
    }

    public Long getNumberOfIMessage() {
        return numberOfIMessage == null ? 0 : numberOfIMessage;
    }

    public Long getNumberOfMessageMe() {
        return numberOfMessageMe == null ? 0 : numberOfMessageMe;
    }

    public Long getvCoinsIshareScreenshotFB() {
        return vCoinsIshareScreenshotFB == null ? 0 : vCoinsIshareScreenshotFB;
    }

    public Long getvCoinsIshareScreenshotTw() {
        return vCoinsIshareScreenshotTw == null ? 0 : vCoinsIshareScreenshotTw;
    }

    public Long getvCoinsIshareScreenshotPin() {
        return vCoinsIshareScreenshotPin == null ? 0 : vCoinsIshareScreenshotPin;
    }

    public Long getvCoinsIshareScreenshotIG() {
        return vCoinsIshareScreenshotIG == null ? 0 : vCoinsIshareScreenshotIG;
    }

    public Long getvCoinsIshareOtherScreenshotFbTwPinIG() {
        return vCoinsIshareOtherScreenshotFbTwPinIG == null ? 0 : vCoinsIshareOtherScreenshotFbTwPinIG;
    }

    public Long getvCoinsIshareBenefitsBoardFB() {
        return vCoinsIshareBenefitsBoardFB == null ? 0 : vCoinsIshareBenefitsBoardFB;
    }

    public Long getvCoinsIshareBenefitsBoardTw() {
        return vCoinsIshareBenefitsBoardTw == null ? 0 : vCoinsIshareBenefitsBoardTw;
    }

    public Long getvCoinsIshareBenefitsBoardPin() {
        return vCoinsIshareBenefitsBoardPin == null ? 0 : vCoinsIshareBenefitsBoardPin;
    }

    public Long getvCoinsIshareBenefitsBoardIG() {
        return vCoinsIshareBenefitsBoardIG == null ? 0 : vCoinsIshareBenefitsBoardIG;
    }

    public Long getvCoinsSharedMyScreenshotFbTwPinIG() {
        return vCoinsSharedMyScreenshotFbTwPinIG == null ? 0 : vCoinsSharedMyScreenshotFbTwPinIG;
    }

    public Long getNumberOfMyScreenshotFbTwPinIG() {
        return numberOfMyScreenshotFbTwPinIG == null ? 0 : numberOfMyScreenshotFbTwPinIG;
    }

    public Long getNumberOfIshareScreenshotFB() {
        return numberOfIshareScreenshotFB == null ? 0 : numberOfIshareScreenshotFB;
    }

    public Long getNumberOfIshareScreenshotTw() {
        return numberOfIshareScreenshotTw == null ? 0 : numberOfIshareScreenshotTw;
    }

    public Long getNumberOfIshareScreenshotPin() {
        return numberOfIshareScreenshotPin == null ? 0 : numberOfIshareScreenshotPin;
    }

    public Long getNumberOfIshareScreenshotIG() {
        return numberOfIshareScreenshotIG == null ? 0 : numberOfIshareScreenshotIG;
    }

    public Long getNumberOfIshareOtherScreenshotFbTwPinIG() {
        return numberOfIshareOtherScreenshotFbTwPinIG == null ? 0 : numberOfIshareOtherScreenshotFbTwPinIG;
    }

    public Long getNumberOfIshareBenefitsBoardFB() {
        return numberOfIshareBenefitsBoardFB == null ? 0 : numberOfIshareBenefitsBoardFB;
    }

    public Long getNumberOfIshareBenefitsBoardTw() {
        return numberOfIshareBenefitsBoardTw == null ? 0 : numberOfIshareBenefitsBoardTw;
    }

    public Long getNumberOfIshareBenefitsBoardPin() {
        return numberOfIshareBenefitsBoardPin == null ? 0 : numberOfIshareBenefitsBoardPin;
    }

    public Long getNumberOfIshareBenefitsBoardIG() {
        return numberOfIshareBenefitsBoardIG == null ? 0 : numberOfIshareBenefitsBoardIG;
    }
}
