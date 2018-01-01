package me.veganbuddy.veganbuddy.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.crash.FirebaseCrash;

import java.util.ArrayList;
import java.util.List;

import me.veganbuddy.veganbuddy.R;
import me.veganbuddy.veganbuddy.actors.MyPlace;

import static me.veganbuddy.veganbuddy.util.Constants.FPIS_TAG;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class FetchPlacesIntentService extends IntentService {

    private static MyPlace thisPlace;
    private PlaceDetectionClient placeDetectionClient;

    public FetchPlacesIntentService() {
        super("FetchPlacesIntentService");
    }

    public static MyPlace getMyPlace() {
        return thisPlace;
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        //Construct variable clients for Places
        placeDetectionClient = Places.getPlaceDetectionClient(this, null);
        makePlacesList();
    }

    private void makePlacesList() {
        try {
            final Task<PlaceLikelihoodBufferResponse> placesResult = placeDetectionClient
                    .getCurrentPlace(null);
            placesResult.addOnCompleteListener(new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
                @Override
                public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
                    String message = "";
                    List<MyPlace> placesList = new ArrayList<>();

                    if (task.isSuccessful() && task.getResult() != null) {
                        PlaceLikelihoodBufferResponse likelihoods = task.getResult();
                        //Todo: limit the number of places to equal to "NUMBER_OF_ADDRESSES_TO_RETRIEVE"
                        //Iterate through the results to create a list of MyPlace objects
                        for (PlaceLikelihood placeLikelihood : likelihoods) {
                            if (placeLikelihood.getLikelihood() >= 0) {
                                Place placeThis = placeLikelihood.getPlace();
                                Float floatThis = placeLikelihood.getLikelihood();

                                MyPlace myPlace = new MyPlace();
                                myPlace.setPlaceId(placeThis.getId());
                                myPlace.setPlaceAddress(placeThis.getAddress().toString());
                                myPlace.setPlaceName(placeThis.getName().toString());
                                myPlace.setLocation(placeThis.getLatLng());
                                myPlace.setLikelihood(floatThis);

                                placesList.add(myPlace);
                            }
                        }
                        likelihoods.release();
                        //Iterate through the list of MyPlace objects to find the Place with
                        // highest likelihood
                        thisPlace = placesList.get(0);
                        for (int i = 1; i < placesList.size(); i++) {
                            MyPlace currentPlace = placesList.get(i);
                            if (thisPlace.getLikelihood() < currentPlace.getLikelihood()) {
                                thisPlace = currentPlace;
                            }
                        }
                        message = "Places successfully retrieved";
                        Log.v(FPIS_TAG, message);
                    } else {
                        // Handle case where no places were found
                        message = getString(R.string.fpis_no_address_found);
                        Log.e(FPIS_TAG, message);
                    }
                }
            });
        } catch (SecurityException SE){//To catch the SecurityException of "No Permission"
            // for "Location"
            SE.printStackTrace();
            FirebaseCrash.log("SecurityException " + SE.getMessage());
        }
    }
}


