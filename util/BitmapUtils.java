package me.veganbuddy.veganbuddy.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by abhishek on 31/8/17.
 */

public class BitmapUtils {

    public static final String FILE_PROVIDER_AUTHORITY = "me.veganbuddy.veganbuddy.fileprovider";
    public static String photoURL;
    public static String photoUri;
    public static String photoThumbnailPath;
    public static Uri photoThumbnailUri;

    public static String getPhotoURL() {
        return photoURL;
    }

    public static void setPhotoURL(String mURL) {
        photoURL = mURL;
    }

    public static String getPhotoUri() {
        return photoUri;
    }

    public static void setPhotoUri(String mUri) {
        photoUri = mUri;
    }

    public static File createThumbnail (String filePath, File fileDirectory) {

        //Create the thumbnail
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        int width = bitmap.getWidth()/5; //Thumbnail is 20% of the original image size
        int height = bitmap.getHeight()/5;
        Bitmap thumbNail = ThumbnailUtils.extractThumbnail(bitmap,width, height);

        //Create a file to save the thumbnail into
        File thumbnailFile = null;
        try {
             thumbnailFile = createThumbnailFile(fileDirectory);
        } catch (IOException ioe) {
            Log.d("BitmapUtils Error", "IO Exception happened while creating the file for saving the image");
            ioe.printStackTrace();
        }
        if (thumbnailFile != null) {
            try {
                //Save the created bitmap thumbnail into the created temp file;
                FileOutputStream fos = new FileOutputStream(thumbnailFile);
                thumbNail.compress(Bitmap.CompressFormat.JPEG,90,fos);
                fos.close();
            } catch (FileNotFoundException fnfe) {
                Log.d("BitmapUtils Error", "Output Thumbnail file not found");
                fnfe.printStackTrace();
            } catch (IOException ioe) {
                Log.d("BitmapUtils Error", "IO Exception happened while saving thumbnail to temp file");
                ioe.printStackTrace();
            }
            photoThumbnailPath = thumbnailFile.getAbsolutePath();
        }
        return thumbnailFile;
    }

    private static File createThumbnailFile(File fileDirectory) throws IOException {
        File photoFile = null;
        String photoFileName = "thumbnail";
        photoFile = File.createTempFile(photoFileName, ".jpg", fileDirectory);
        return photoFile;
    }

}
