package me.veganbuddy.veganbuddy.actors;

import java.util.HashMap;
import java.util.Map;

import me.veganbuddy.veganbuddy.util.DateAndTimeUtils;

/**
 * Created by abhishek on 2/9/17.
 */

public class Post {
    private Map <String, String> posts = new HashMap<>();

    public Post (String url, String mText) {
        String dateofToday = DateAndTimeUtils.dateStamp();
        posts.put(dateofToday + "_mealPhotoUrl", url);
        posts.put(dateofToday + "_veganPhilosophyText", mText);
        }

    public Map<String, String> getPosts() {
        return posts;
    }

}
