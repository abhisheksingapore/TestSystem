package me.veganbuddy.veganbuddy.services;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.crash.FirebaseCrash;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import me.veganbuddy.veganbuddy.R;
import me.veganbuddy.veganbuddy.util.Constants;

import static me.veganbuddy.veganbuddy.util.Constants.FAILURE_RESULT;
import static me.veganbuddy.veganbuddy.util.Constants.FAIS_TAG;
import static me.veganbuddy.veganbuddy.util.Constants.NUMBER_OF_ADDRESSES_TO_RETRIEVE;
import static me.veganbuddy.veganbuddy.util.Constants.RESULT_DATA_KEY;
import static me.veganbuddy.veganbuddy.util.Constants.SUCCESS_RESULT;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class FetchAddressIntentService extends IntentService {

    protected ResultReceiver mResultReceiver;
    public static List<Address> addressList;

    private PlaceDetectionClient placeDetectionClient;
    public static List<String> placesList = new ArrayList<>();

    public FetchAddressIntentService() {
        super("FetchAddressIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mResultReceiver = intent.getParcelableExtra(Constants.RECEIVER);

        //Construct variable clients for Places
        placeDetectionClient = Places.getPlaceDetectionClient(this, null);
        makePlacesList();
    }

    private void makePlacesList() {
        try {
            Task<PlaceLikelihoodBufferResponse> placesResult = placeDetectionClient
                    .getCurrentPlace(null);
            placesResult.addOnCompleteListener(new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
                @Override
                public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
                    String message = "";
                    if (task.isSuccessful() && task.getResult() != null) {
                        PlaceLikelihoodBufferResponse likelihoods = task.getResult();
                        //Todo: limit the number of places to equal to "NUMBER_OF_ADDRESSES_TO_RETRIEVE"
                        for (PlaceLikelihood placeLikelihood : likelihoods) {
                            if (placeLikelihood.getLikelihood() >= 0) {
                                placesList.add(placeLikelihood.getPlace().getName().toString());
                                //Todo: If address needs to be added - +"\n" + placeLikelihood.getPlace().getAddress().toString()
                            }
                        }
                        likelihoods.release();
                        message = "Places successfully retrieved";
                        deliverResultToReceiver (SUCCESS_RESULT, message);
                    } else if (placesList == null || placesList.size() ==0 ) {
                        // Handle case where no places were found
                        message = getString(R.string.fais_no_address_found);
                        Log.e(FAIS_TAG, message);
                        deliverResultToReceiver (FAILURE_RESULT, message);
                    }
                }
            });
        } catch (SecurityException SE){//To catch the SecurityException of "No Permission"
            // for "Location"
            SE.printStackTrace();
            FirebaseCrash.log("SecurityException " + SE.getMessage());
        }

    }

    private void deliverResultToReceiver(int result, String message) {
        Bundle bundle = new Bundle();
        bundle.putString(RESULT_DATA_KEY, message);
        mResultReceiver.send(result, bundle);
    }

}


