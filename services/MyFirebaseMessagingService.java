package me.veganbuddy.veganbuddy.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import static me.veganbuddy.veganbuddy.util.Constants.MFMS_TAG;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public MyFirebaseMessagingService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.v(MFMS_TAG, "From: " + remoteMessage.getFrom());

        if(remoteMessage.getData().size()>0) {
            Log.v(MFMS_TAG, "Message data payload:" + remoteMessage.getData());
        }

        //handle messages

        if (remoteMessage.getNotification()!=null) {
            Log.v(MFMS_TAG, "Message notification body:"+ remoteMessage.getNotification().getBody());
        }
    }

}
