package me.veganbuddy.veganbuddy.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.wonderkiln.camerakit.CameraKit;
import com.wonderkiln.camerakit.CameraView;

import me.veganbuddy.veganbuddy.R;

/**
 * Created by abhishek on 19/12/17.
 */

public class CameraActivityFoodWisdom extends CameraActivityProfilePicture {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final CameraView cameraView = findViewById(R.id.acpp_camera_cameraview);
        cameraView.setForeground(null);

        TextView textView = findViewById(R.id.acpp_instructions);
        CharSequence sequenceInstructions = "*Click here to toggle front/back camera ";
        textView.setText(sequenceInstructions);
        textView.setCompoundDrawablesWithIntrinsicBounds(null, null, getDrawable(R.drawable.ic_menu_camera), null);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cameraView.isFacingFront())
                    cameraView.setFacing(CameraKit.Constants.FACING_BACK);
                else cameraView.setFacing(CameraKit.Constants.FACING_FRONT);
            }
        });
    }
}
