package me.veganbuddy.veganbuddy.services;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import me.veganbuddy.veganbuddy.R;
import me.veganbuddy.veganbuddy.util.Constants;

import static me.veganbuddy.veganbuddy.util.Constants.FAIS_TAG;
import static me.veganbuddy.veganbuddy.util.Constants.NUMBER_OF_ADDRESSES_TO_RETRIEVE;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class FetchAddressIntentService extends IntentService {

    protected ResultReceiver mResultReceiver;
    public static List<Address> addressList;

    public FetchAddressIntentService() {
        super("FetchAddressIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String errorMessage = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        addressList = null;
        Location location = intent.getParcelableExtra(me.veganbuddy.veganbuddy.util.Constants.LOCATION_DATA_EXTRA);
        mResultReceiver = intent.getParcelableExtra(Constants.RECEIVER);

        try {
            addressList = geocoder.getFromLocation(location.getLatitude(),
                    location.getLongitude(), NUMBER_OF_ADDRESSES_TO_RETRIEVE);
        } catch (IOException ioe) {
            // Catch network or other I/O problems.
            errorMessage = getString(R.string.fais_service_not_available);
            Log.e(FAIS_TAG, errorMessage, ioe);
        } catch (IllegalArgumentException illegalArgumentException)
            {// Catch invalid latitude or longitude values.
                errorMessage = getString(R.string.fais_invalid_lat_long_used);
                Log.e(FAIS_TAG, errorMessage + "." + " Latitude = " + location.getLatitude()
                        + ", Longitude = " + location.getLongitude(), illegalArgumentException);
            }
        // Handle case where no address was found

        if (addressList == null || addressList.size() ==0 ) {
            if (errorMessage.isEmpty()){
                errorMessage = getString(R.string.fais_no_address_found);
                Log.e(FAIS_TAG, errorMessage);
            }
            deliverResultToReceiver (me.veganbuddy.veganbuddy.util.Constants.FAILURE_RESULT, errorMessage);
        } else {
            Log.i(FAIS_TAG, getString(R.string.fais_address_found));
            deliverResultToReceiver (me.veganbuddy.veganbuddy.util.Constants.SUCCESS_RESULT,
                    "Success");
        }
    }

    private void deliverResultToReceiver(int result, String message) {
        Bundle bundle = new Bundle();
        bundle.putString(me.veganbuddy.veganbuddy.util.Constants.RESULT_DATA_KEY, message);
        mResultReceiver.send(result, bundle);
    }

}
