package me.veganbuddy.veganbuddy.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by abhishek on 31/8/17.
 */

public class BitmapUtils {

    public static String photoURL;
    public static Uri photoUri;
    public static String photoPath;

    public static String imageFileName;

    public static String photoThumbnailURL;
    public static String photoThumbnailPath;
    public static Uri photoThumbnailUri;

    public static String screenShotURL;
    public static File screenShotFile;

    public static String getPhotoURL() {
        return photoURL;
    }

    public static void setPhotoURL(String mURL) {
        photoURL = mURL;
    }

    public static String getPhotoThumbnailURL() {
        return photoThumbnailURL;
    }

    public static void setPhotoThumbnailURL(String photoThumbnailURL) {
        BitmapUtils.photoThumbnailURL = photoThumbnailURL;
    }

    public static String getScreenShotURL() {
        return screenShotURL;
    }

    public static void setScreenShotURL(String screenShotURL) {
        BitmapUtils.screenShotURL = screenShotURL;
    }

    public static void setPhotoPath(String photoPath) {
        BitmapUtils.photoPath = photoPath;
    }

    public static Uri getPhotoUri() {
        return photoUri;
    }

    public static void setPhotoUri(Uri mUri) {
        photoUri = mUri;
    }

    public static String getImageFileName() {
        return imageFileName;
    }

    public static void setImageFileName(String imageFileName) {
        BitmapUtils.imageFileName = imageFileName;
    }

    public static File createThumbnail(String filePath, File fileDirectory, boolean rotate) {

        //Create the thumbnail
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        int width = bitmap.getWidth() / 5; //Thumbnail is 20% of the original image size
        int height = bitmap.getHeight() / 5;
        Bitmap thumbNail = ThumbnailUtils.extractThumbnail(bitmap, width, height);

        if (rotate) {
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            thumbNail = Bitmap.createBitmap(thumbNail,0,0,width,height,matrix,true);
        }

        //Create a file to save the thumbnail into
        File thumbnailFile = null;
        try {
            thumbnailFile = createThumbnailFile(fileDirectory);
        } catch (IOException ioe) {
            Log.e("BitmapUtils Error", "IO Exception happened while creating the file for saving the image");
            ioe.printStackTrace();
        }
        if (thumbnailFile != null) {
            try {
                //Save the created bitmap thumbnail into the created temp file;
                FileOutputStream fos = new FileOutputStream(thumbnailFile);
                thumbNail.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                fos.close();
            } catch (FileNotFoundException fnfe) {
                Log.e("BitmapUtils Error", "Output Thumbnail file not found");
                fnfe.printStackTrace();
            } catch (IOException ioe) {
                Log.e("BitmapUtils Error", "IO Exception happened while saving thumbnail to temp file");
                ioe.printStackTrace();
            }
            photoThumbnailPath = thumbnailFile.getAbsolutePath();
        }
        return thumbnailFile;
    }

    public static File createThumbnailFile(File fileDirectory) throws IOException {
        File photoFile = null;
        String photoFileName = "thumbnail";
        photoFile = File.createTempFile(photoFileName, ".jpg", fileDirectory);
        return photoFile;
    }

    public static File createTempImageFile(Bitmap bitmapSS, File fileDirectory) {
        //Create a temporary File
        File tempFile = null;
        String tempFileName = "veganBuddyTempSS";

        try {
            tempFile = File.createTempFile(tempFileName, ".png", fileDirectory);
        } catch (IOException IOE) {
            Log.e("BitmapUtils Error", "IO Exception happened while creating the file for saving the Screenshot image");
            IOE.printStackTrace();
        }

        if(tempFile!=null) {
            try {
                FileOutputStream fileOutputStream =
                        new FileOutputStream(tempFile);
                bitmapSS.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                fileOutputStream.close();
            } catch (IOException IOE) {
                Log.e("BitmapUtils Error", "IO Exception happened while saving Screenshot image to temp file");
                IOE.printStackTrace();
            } catch (NullPointerException NPE) {
                NPE.printStackTrace();
            }
        }
        screenShotFile = tempFile; //Saving to a local file name for use in Twitter Upload later

        return tempFile;
    }

    public static File createTempThumbFile(Bitmap bitmapSS, File fileDirectory) {
        //Create a temporary File
        File tempThumbFile = null;
        String tempThumbFileName = "veganBuddyTempThumbSS";
        Bitmap tempThumbBitmap = ThumbnailUtils.extractThumbnail(bitmapSS,
                bitmapSS.getWidth()/2, bitmapSS.getHeight()/2);

        try {
            tempThumbFile = File.createTempFile(tempThumbFileName, ".jpg", fileDirectory);
        } catch (IOException IOE) {
            Log.e("BitmapUtils Error", "IO Exception happened while creating the file for saving the Screenshot image");
            IOE.printStackTrace();
        }

        if(tempThumbFile!=null) {
            try {
                FileOutputStream fileOutputStream =
                        new FileOutputStream(tempThumbFile);
                tempThumbBitmap.compress(Bitmap.CompressFormat.JPEG, 90, fileOutputStream);
                fileOutputStream.close();
            } catch (IOException IOE) {
                Log.e("BitmapUtils Error", "IO Exception happened while saving Screenshot image to temp file");
                IOE.printStackTrace();
            } catch (NullPointerException NPE) {
                NPE.printStackTrace();
            }
        }
        return tempThumbFile;
    }


    public static File createTempUploadFile(Bitmap bitmapUpload, File fileDirectory) {
        //Create a temporary File
        File tempThumbFile = null;
        String tempThumbFileName = "veganBuddyTempUpload";

        try {
            tempThumbFile = File.createTempFile(tempThumbFileName, ".jpg", fileDirectory);
        } catch (IOException IOE) {
            Log.e("BitmapUtils Error", "IO Exception happened while creating the file for saving the Screenshot image");
            IOE.printStackTrace();
        }

        if(tempThumbFile!=null) {
            try {
                FileOutputStream fileOutputStream =
                        new FileOutputStream(tempThumbFile);
                bitmapUpload.compress(Bitmap.CompressFormat.JPEG, 90, fileOutputStream);
                fileOutputStream.close();
            } catch (IOException IOE) {
                Log.e("BitmapUtils Error", "IO Exception happened while saving Screenshot image to temp file");
                IOE.printStackTrace();
            } catch (NullPointerException NPE) {
                NPE.printStackTrace();
            }
        }
        return tempThumbFile;
    }

    public static File getScreenShotFile(){
        return screenShotFile;
    }

    public static String getFileNameSansExtension(String fileNameWithExtension) {
         return fileNameWithExtension.substring(0, fileNameWithExtension.lastIndexOf("."));
    }


}
