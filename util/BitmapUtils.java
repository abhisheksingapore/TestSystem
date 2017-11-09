package me.veganbuddy.veganbuddy.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import static me.veganbuddy.veganbuddy.util.Constants.BU_TAG;
import static me.veganbuddy.veganbuddy.util.Constants.MP_TAG;
import static me.veganbuddy.veganbuddy.util.Constants.VEGAN_BUDDY_FOLDER;

/**
 * Created by abhishek on 31/8/17.
 */

public class BitmapUtils {

    //Folder to store all the images of this app
    private static File veganBuddyFolder;

    //Variables for MealPhoto
    private static String photoURL;
    private static Uri photoUri;
    private static String photoPath;
    private static String mealPhotoName;

    //Variables for MealPhoto thumbnail
    private static String photoThumbnailURL;
    private static Uri photoThumbnailUri;

    //Variables for MealPhoto Screenshot
    private static String screenShotURL;
    private static File screenShotFile;

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

    public static Uri getPhotoThumbnailUri() {
        return photoThumbnailUri;
    }

    public static void setPhotoThumbnailUri(Uri photoThumbnailUri) {
        BitmapUtils.photoThumbnailUri = photoThumbnailUri;
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

    public static String getPhotoPath() {
        return photoPath;
    }

    public static Uri getPhotoUri() {
        return photoUri;
    }

    public static void setPhotoUri(Uri mUri) {
        photoUri = mUri;
    }


    public static void setMealPhotoName(String mealPhotoName) {
        BitmapUtils.mealPhotoName = mealPhotoName;
    }

    public static String getMealPhotoName() {
        return mealPhotoName;
    }

    public static File getVeganBuddyFolder() {
        return veganBuddyFolder;
    }

    public static void setVeganBuddyFolder(File veganBuddyFolder) {
        BitmapUtils.veganBuddyFolder = veganBuddyFolder;
    }

    static File getScreenShotFile(){
        return screenShotFile;
    }



    public static Uri createMealPhotoFile(byte[] jpeg) {

        try {
            //First create file in the local drive of the phone
            File photoFile = null;
            String mealType = DateAndTimeUtils.getMealTypeBasedOnTimeOfTheDay();
            String timeStamp = DateAndTimeUtils.dateTimeStamp();
            String photoFileName = mealType + timeStamp;
            photoFile = File.createTempFile(photoFileName, ".jpg", veganBuddyFolder);
            setMealPhotoName(photoFile.getName());

            //get Absolute Path of the created file and save it here
            String currentPhotoPath = photoFile.getAbsolutePath();
            setPhotoPath(currentPhotoPath);

            //then save byte[]jpeg into file
            FileOutputStream fos = new FileOutputStream(currentPhotoPath);
            fos.write(jpeg);
            fos.close();

            //get PhotoURI and save in local app variable
            Uri thisPhotoUri = Uri.fromFile(photoFile);
            BitmapUtils.setPhotoUri(thisPhotoUri);

            return BitmapUtils.getPhotoUri();
        } catch (IOException | NullPointerException | IllegalArgumentException exception) {
            FirebaseCrash.log(BU_TAG + exception.getMessage());
            Log.e(BU_TAG, exception.getMessage());
        }
        return null;
    }

    public static Uri createMealPhotoFileFromUri(InputStream inputStream) {
        if (veganBuddyFolderExists()) {
            try {
                //First create file in the local drive of the phone
                File photoFile = null;
                String mealType = DateAndTimeUtils.getMealTypeBasedOnTimeOfTheDay();
                String timeStamp = DateAndTimeUtils.dateTimeStamp();
                String photoFileName = mealType + timeStamp;
                photoFile = File.createTempFile(photoFileName, ".jpg", veganBuddyFolder);
                setMealPhotoName(photoFile.getName());

                //get Absolute Path of the created file and save it here
                String currentPhotoPath = photoFile.getAbsolutePath();
                setPhotoPath(currentPhotoPath);

                //then save input stream into output stream
                FileOutputStream fos = new FileOutputStream(currentPhotoPath);
                IOUtils.copy(inputStream, fos);
                inputStream.close();
                fos.close();

                //get PhotoURI and save in local app variable
                Uri thisPhotoUri = Uri.fromFile(photoFile);
                BitmapUtils.setPhotoUri(thisPhotoUri);
                return BitmapUtils.getPhotoUri();
            } catch (IOException | NullPointerException | IllegalArgumentException exception) {
                FirebaseCrash.log(BU_TAG + exception.getMessage());
                Log.e(BU_TAG, exception.getMessage());
            }
        }
        return null;
    }

    public static void createThumbnail() {

        try {
            //Create the thumbnail bitmap
            Bitmap bitmap = BitmapFactory.decodeFile(getPhotoPath());
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            Bitmap thumbNail = ThumbnailUtils.extractThumbnail(bitmap, width, height);

            //check if photo needs to rotate for correct alignment with other View Objects
            boolean rotate = BitmapUtils.shouldRotate(getPhotoPath());
            if (rotate) {
                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                thumbNail = Bitmap.createBitmap(thumbNail, 0, 0, width, height, matrix, true);
            }

            //Create a file to save the thumbnail into
            File thumbnailFile = null;

            String photoFileName = "thumbnail";
            //make a JPEG to compress size
            thumbnailFile = File.createTempFile(photoFileName, ".jpg", veganBuddyFolder);
            //Save the created bitmap thumbnail into the created temp file;
            FileOutputStream fos = new FileOutputStream(thumbnailFile);
            //make a low res JPEG to compress size
            thumbNail.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.close();

            //save thumbnail URI in BitmapUtils
            Uri photoThumbnailUri = Uri.fromFile(thumbnailFile);
            setPhotoThumbnailUri(photoThumbnailUri);

        }catch (IOException |NullPointerException | IllegalArgumentException exception) {
            Log.e(BU_TAG, "Error creating Thumbnail file");
            exception.printStackTrace();
        }
    }

    private static boolean shouldRotate(String mealPhotoPath) {
        int photoLength = -939;
        int photoWidth = - 939;
        boolean rotate = false;
        try {
            ExifInterface exifInterface = new ExifInterface(mealPhotoPath);
            photoLength = exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, -939);
            photoWidth = exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, -939);
        } catch (IOException IOE) {
            IOE.printStackTrace();
            Log.i(MP_TAG, "IO error in getting Exif data from camera photo");
        }

        if ( photoLength > photoWidth ) {
            rotate = true;
        }
        return rotate;
    }

    public static File createTempImageFile(Bitmap bitmapSS) {
        //Create a temporary File
        File tempFile = null;
        String tempFileName = "veganBuddyTempSS";

        try {
            tempFile = File.createTempFile(tempFileName, ".jpg", veganBuddyFolder);
            FileOutputStream fileOutputStream =
                    new FileOutputStream(tempFile);
            bitmapSS.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.close();
            } catch (IOException | NullPointerException exception) {
                Log.e(BU_TAG, "Exception while saving Screenshot image to temp file");
                exception.printStackTrace();
            }
        screenShotFile = tempFile; //Saving to a local file name for use in Twitter Upload later

        return tempFile;
    }

    public static File createTempThumbFile(Bitmap bitmapSS) {
        //Create a temporary File
        File tempThumbFile = null;
        String tempThumbFileName = "veganBuddyTempThumbSS";
        Bitmap tempThumbBitmap = ThumbnailUtils.extractThumbnail(bitmapSS,
                bitmapSS.getWidth()/2, bitmapSS.getHeight()/2);
        try {
            tempThumbFile = File.createTempFile(tempThumbFileName, ".jpg", veganBuddyFolder);
            FileOutputStream fileOutputStream =
                        new FileOutputStream(tempThumbFile);
            tempThumbBitmap.compress(Bitmap.CompressFormat.JPEG, 90, fileOutputStream);
            fileOutputStream.close();
        } catch (IOException | NullPointerException exception) {
            Log.e("BitmapUtils Error", "IO Exception happened while saving Screenshot image to temp file");
            exception.printStackTrace();
        }
        return tempThumbFile;
    }


    public static File createTempUploadFile(Bitmap bitmapUpload) {
        //Create a temporary File
        File tempThumbFile = null;
        String tempThumbFileName = "veganBuddyTempUpload";

        try {
            tempThumbFile = File.createTempFile(tempThumbFileName, ".jpg", veganBuddyFolder);
                FileOutputStream fileOutputStream =
                        new FileOutputStream(tempThumbFile);
                bitmapUpload.compress(Bitmap.CompressFormat.JPEG, 90, fileOutputStream);
                fileOutputStream.close();
            } catch (IOException | NullPointerException exception) {
            Log.e("BitmapUtils Error", "IO Exception happened while saving Screenshot image to temp file");
            exception.printStackTrace();
        }
        return tempThumbFile;
    }

    public static void deleteFiles() {
        try {
            if (veganBuddyFolderExists()) FileUtils.cleanDirectory(veganBuddyFolder);
        } catch (IOException IOE){
            FirebaseCrash.log(BU_TAG + "error caught\n\n" + IOE.getMessage());
            IOE.printStackTrace();
        }
    }

    public static boolean veganBuddyFolderExists() {
        veganBuddyFolder = new File(Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                + File.separator
                + VEGAN_BUDDY_FOLDER);

        //Check if VeganBuddy Picture Folder exists inside the default Pictures Directory
        if (veganBuddyFolder.exists()) return true;

        //if not, then  create the VeganBuddy Picture Folder and return the status of mkdirs()
        return veganBuddyFolder.mkdirs();

    }
}
