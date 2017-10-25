package me.veganbuddy.veganbuddy.actors;

import static me.veganbuddy.veganbuddy.util.Constants.ACTIONED;
import static me.veganbuddy.veganbuddy.util.Constants.ACTIONED_FACTOR;
import static me.veganbuddy.veganbuddy.util.Constants.COMMENT_FACTOR;
import static me.veganbuddy.veganbuddy.util.Constants.DIRECT_MESSAGE_FACTOR;
import static me.veganbuddy.veganbuddy.util.Constants.LIKE_FACTOR;
import static me.veganbuddy.veganbuddy.util.Constants.NEW_PHOTO_CREATED;
import static me.veganbuddy.veganbuddy.util.Constants.RECEIVED;
import static me.veganbuddy.veganbuddy.util.Constants.RECEIVED_FACTOR;
import static me.veganbuddy.veganbuddy.util.Constants.VN_PHOTO_SHARE;
import static me.veganbuddy.veganbuddy.util.Constants.SHARE_FACTOR;
import static me.veganbuddy.veganbuddy.util.Constants.VN_COMMENT_PHOTO;
import static me.veganbuddy.veganbuddy.util.Constants.VN_DIRECT_MESSAGE;
import static me.veganbuddy.veganbuddy.util.Constants.VN_LIKED_PHOTO;

/**
 * Created by abhishek on 20/10/17.
 */

public class Vcoins {

    private double vCoinsEarned;

    public Vcoins() {
        //empty default constructor
    }

    public Vcoins(String direction, String vNtype) {
        vCoinsEarned = getDirectionFactor(direction) * getVnTypeFactor(vNtype) * NEW_PHOTO_CREATED;
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

    public String vCoinsEarnedString() {
        return Double.toString(vCoinsEarned);
    }

}
