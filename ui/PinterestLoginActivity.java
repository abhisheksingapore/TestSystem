package me.veganbuddy.veganbuddy.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.pinterest.android.pdk.PDKCallback;
import com.pinterest.android.pdk.PDKClient;
import com.pinterest.android.pdk.PDKException;
import com.pinterest.android.pdk.PDKResponse;

import java.util.ArrayList;
import java.util.List;

import me.veganbuddy.veganbuddy.R;

import static me.veganbuddy.veganbuddy.util.Constants.PINTEREST_KEY;
import static me.veganbuddy.veganbuddy.util.Constants.PINTEREST_LOGIN_SUCCESS;
import static me.veganbuddy.veganbuddy.util.Constants.PLA_TAG;

public class PinterestLoginActivity extends AppCompatActivity {
    private PDKClient pdkClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pinterest_login);

        pdkClient = PDKClient.configureInstance(this, getString(R.string.pinterest_app_id));
        pdkClient.onConnect(this);
        pdkClient.setDebugMode(true);
    }

    @Override
    protected void onStart() {
        super.onStart();

        List scopes = new ArrayList<String>();
        scopes.add(PDKClient.PDKCLIENT_PERMISSION_WRITE_PUBLIC);
        scopes.add(PDKClient.PDKCLIENT_PERMISSION_READ_PUBLIC);

        pdkClient.login(this, scopes, new PDKCallback(){
            @Override
            public void onSuccess(PDKResponse response) {
                Log.v(PLA_TAG,"Login to Pinterest successful" + response.getData().toString());
            }

            @Override
            public void onFailure(PDKException exception) {
                Log.e(PLA_TAG, "Error in login for Pinterest: " + exception.getDetailMessage());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        pdkClient.onOauthResponse(requestCode,resultCode,data);
        if (resultCode==RESULT_OK) {
            setResult(PINTEREST_LOGIN_SUCCESS);
            finish();
        }
    }
}
