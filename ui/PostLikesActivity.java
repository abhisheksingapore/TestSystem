package me.veganbuddy.veganbuddy.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import me.veganbuddy.veganbuddy.R;
import me.veganbuddy.veganbuddy.actors.PostFan;

public class PostLikesActivity extends AppCompatActivity {

    private static Map<String, PostFan> usersList;
    List<UserInfo> userInfoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_likes);
        //create a List of Users
        createListOfUsers(usersList);

        RecyclerView recyclerView = findViewById(R.id.apl_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        PostLikesRecyclerViewAdapter postLikesRecyclerViewAdapter
                = new PostLikesRecyclerViewAdapter(userInfoList);
        recyclerView.setAdapter(postLikesRecyclerViewAdapter);
    }

    public static void setListOfUsers(Map<String, PostFan> likedMe) {
        usersList = likedMe;
    }

    private void createListOfUsers(Map<String, PostFan> likedMe) {
        userInfoList = new ArrayList<>();
        for(Map.Entry<String, PostFan> mapItem: likedMe.entrySet()) {
            UserInfo userInfo = new UserInfo (mapItem.getKey(), mapItem.getValue());
            userInfoList.add(userInfo);
        }
    }


    //Inner class to convert the Map of the userInfo who liked this post into a single UserInfo
    // object that can be mapped to each item in the recyclerView
    public class UserInfo {
        String id;
        String name;
        String photoUrl;

        UserInfo (String objID, PostFan postFan) {
            id = objID;
            name = postFan.getName();
            photoUrl = postFan.getPhotoUrl();
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getPhotoUrl() {
            return photoUrl;
        }
    }
}
