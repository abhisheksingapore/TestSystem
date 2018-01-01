package me.veganbuddy.veganbuddy.util;

import android.graphics.BitmapFactory;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StreamDownloadTask;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Request;
import com.squareup.picasso.RequestHandler;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;

import static me.veganbuddy.veganbuddy.util.Constants.SCHEME_FIREBASE_STORAGE;

/**
 * Created by abhishek on 17/12/17.
 */

public class PicassoGSrequestHandler extends RequestHandler {

    @Override
    public boolean canHandleRequest(Request data) {
        String scheme = data.uri.getScheme();
        return (SCHEME_FIREBASE_STORAGE.equals(scheme));
    }

    @Override
    public Result load(Request request, int networkPolicy) throws IOException {

        StorageReference storageReference = FirebaseStorage.getInstance()
                .getReferenceFromUrl(request.uri.toString());
        StreamDownloadTask streamDownloadTask;
        InputStream inputStream;
        streamDownloadTask = storageReference.getStream();

        try {
            inputStream = Tasks.await(streamDownloadTask).getStream();
            return new Result(BitmapFactory.decodeStream(inputStream), Picasso.LoadedFrom.NETWORK);
        } catch (ExecutionException | InterruptedException e) {
            throw new IOException();
        }

    }
}
