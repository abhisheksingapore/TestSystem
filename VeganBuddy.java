package me.veganbuddy.veganbuddy;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import me.veganbuddy.veganbuddy.util.PicassoGSrequestHandler;

/**
 * Created by abhishek on 11/12/17.
 */

public class VeganBuddy extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        //set Firebase persistence
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);


        //Create and set Picasso Singleton
        Picasso picasso = new Picasso.Builder(getApplicationContext())
                .addRequestHandler(new PicassoGSrequestHandler())
                .build();

        Picasso.setSingletonInstance(picasso);

    }
}
