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

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static me.veganbuddy.veganbuddy.util.Constants.BU_TAG;
import static me.veganbuddy.veganbuddy.util.Constants.HIGH_QUALITY;
import static me.veganbuddy.veganbuddy.util.Constants.MED_QUALITY;
import static me.veganbuddy.veganbuddy.util.Constants.PROFILE_PIC_HEIGHT;
import static me.veganbuddy.veganbuddy.util.Constants.PROFILE_PIC_WIDTH;
import static me.veganbuddy.veganbuddy.util.Constants.VEGAN_BUDDY_FOLDER;
import static me.veganbuddy.veganbuddy.util.GlobalVariables.thisAppUser;

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

    //Variable for StatsImage
    private static String stringStatsImageUri;

    //Variables for MealPhoto thumbnail
    private static String photoThumbnailURL;
    private static Uri photoThumbnailUri;

    //Variables for MealPhoto Screenshot
    private static String screenShotURL;
    private static File screenShotFile;

    //Variables for ProfilePicture
    private static Uri profilePictureUri;
    private static String profilePictureName;
    private static String profilePicturePath;


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

    public static String getPhotoPath() {
        return photoPath;
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

    public static String getMealPhotoName() {
        return mealPhotoName;
    }

    public static void setMealPhotoName(String mealPhotoName) {
        BitmapUtils.mealPhotoName = mealPhotoName;
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

                //create Thumbnail of this image
                createThumbnail();

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
            Log.i(BU_TAG, "IO error in getting Exif data from camera photo");
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
            bitmapSS.compress(Bitmap.CompressFormat.JPEG, HIGH_QUALITY, fileOutputStream);
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
            tempThumbBitmap.compress(Bitmap.CompressFormat.JPEG, MED_QUALITY, fileOutputStream);
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
            bitmapUpload.compress(Bitmap.CompressFormat.JPEG, MED_QUALITY, fileOutputStream);
                fileOutputStream.close();
            } catch (IOException | NullPointerException exception) {
            Log.e("BitmapUtils Error", "IO Exception happened while saving Screenshot image to temp file");
            exception.printStackTrace();
        }
        return tempThumbFile;
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

    public static String getStringStatsImageUri() {
        return stringStatsImageUri;
    }

    public static void setStringStatsImageUri(String imageUri) {
        stringStatsImageUri = imageUri;
    }

    public static Uri createProfilePictureFile(byte[] jpeg) {
        try {
            //decode bitmap from byte array
            Bitmap bitmapProfilePicFull = BitmapFactory.decodeByteArray(jpeg, 0, jpeg.length);

            //convert bitmap to required size
            Bitmap bitmapProfilePic = Bitmap.createScaledBitmap(bitmapProfilePicFull,
                    PROFILE_PIC_WIDTH, PROFILE_PIC_HEIGHT, false);


            if (veganBuddyFolderExists()) {
                //create a relevant filename and set the profile picture name
                String filename = thisAppUser.getUserName().replaceAll("\\s", "");
                File profilePic = File.createTempFile(filename, ".jpg", veganBuddyFolder);
                setProfilePictureName(profilePic.getName());

                //find the relevant filename path and store it
                String profilePicPath = profilePic.getAbsolutePath();
                setProfilePicturePath(profilePicPath);

                //create a new file output stream to save the bitmap
                FileOutputStream fileOutputStream = new FileOutputStream(profilePicPath);

                //compress bitmap to jpeg
                bitmapProfilePic.compress(Bitmap.CompressFormat.JPEG, MED_QUALITY, fileOutputStream);

                fileOutputStream.close();

                //retrieve the Uri of the recently saved bitmap for profilePic and save
                Uri profilePicUri = Uri.fromFile(profilePic);
                setProfilePictureUri(profilePicUri);
            }

        } catch (IOException ioe) {
            FirebaseCrash.log(BU_TAG + ioe.getMessage());
            Log.e(BU_TAG, ioe.getMessage());
        }

        //return the saved Uri
        return profilePictureUri;
    }

    public static String getProfilePictureName() {
        return profilePictureName;
    }

    public static void setProfilePictureName(String profilePictureName) {
        BitmapUtils.profilePictureName = profilePictureName;
    }

    public static Uri getProfilePictureUri() {
        return profilePictureUri;
    }

    public static void setProfilePictureUri(Uri profilePictureUri) {
        BitmapUtils.profilePictureUri = profilePictureUri;
    }

    public static String getProfilePicturePath() {
        return profilePicturePath;
    }

    public static void setProfilePicturePath(String profilePicturePath) {
        BitmapUtils.profilePicturePath = profilePicturePath;
    }
}
