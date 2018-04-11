package com.example.ehdus.testscan;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.scandit.barcodepicker.BarcodePicker;
import com.scandit.barcodepicker.OnScanListener;
import com.scandit.barcodepicker.ScanOverlay;
import com.scandit.barcodepicker.ScanSession;
import com.scandit.barcodepicker.ScanditLicense;

import java.util.Locale;

/**
 * A slightly more sophisticated activity illustrating how to embed the
 * Scandit BarcodeScanner SDK into your app.
 * <p>
 * This activity shows 3 different ways to use the Scandit BarcodePicker:
 * <p>
 * - as a full-screen barcode picker in a separate activity (see
 * SampleFullScreenBarcodeActivity).
 * - as a cropped-view picker, only showing a small part of the video
 * feed running
 * - as a scaled-view picker showing a down-scaled version of the video
 * feed.
 * <p>
 * Copyright 2014 Scandit AG
 */

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class BarcodeScanner extends Activity implements OnScanListener {

    static final int REQUEST_BARCODE_PICKER_ACTIVITY = 55;

    // The main object for scanning barcodes.
    private BarcodePicker mBarcodePicker;
    private boolean mPaused = true;
    private boolean mDeniedCameraAccess = false;

    private final static int CAMERA_PERMISSION_REQUEST = 5;


    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_barcode_scanner);
        final RelativeLayout rootView = findViewById(R.id.barcode_detected);

        mBarcodePicker = createPicker();
        RelativeLayout.LayoutParams rParams;

        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        rParams = new RelativeLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, display.getHeight() / 2);
        rParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

        rootView.addView(mBarcodePicker, rParams);

        TextView overlay = new TextView(this);
        rParams = new RelativeLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, display.getHeight() / 2);
        rParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        overlay.setBackgroundColor(0xFF000000);
        rootView.addView(overlay, rParams);
        overlay.setText("Touch to close");
        overlay.setGravity(Gravity.CENTER);
        overlay.setTextColor(0xFFFFFFFF);
        overlay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mBarcodePicker.stopScanning();
                startActivity(new Intent(BarcodeScanner.this, ListOfNewIngredients.class));
            }
        });
        mBarcodePicker.startScanning();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != REQUEST_BARCODE_PICKER_ACTIVITY) {
            return;
        }
        String message = "no code recognized";
        if (data.getBooleanExtra("barcodeRecognized", false)) {
            message = String.format("%s (%s)", data.getStringExtra("barcodeData"),
                    data.getStringExtra("barcodeSymbologyName").toUpperCase(Locale.US));
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private BarcodePicker createPicker() {
        ScanditLicense.setAppKey("WBMbFcD100VJcxQP54tH2O/L65ehgyLAbGzyPFQkI8w");

        BarcodePicker picker = new BarcodePicker(this);

        // Set UI settings according to the settings activity. To get a
        // short overview and explanation of the most used settings please
        // check the SampleFullScreenBarcodeActivity.
        ScanOverlay overlay = picker.getOverlayView();
        overlay.setGuiStyle(ScanOverlay.GUI_STYLE_LASER);
        overlay.setViewfinderDimension(
                0.7f,
                0.3f,
                0.4f,
                0.3f);

        overlay.setBeepEnabled(false);
        overlay.setVibrateEnabled(true);

        overlay.setTorchEnabled(false);
        overlay.setTorchButtonMarginsAndSize(0, 0, 0, 0);

        // Register listener, in order to be notified whenever a new code is
        // scanned
        picker.setOnScanListener(this);
        return picker;

    }

    @Override
    protected void onPause() {
        mPaused = true;
        // When the activity is in the background immediately stop the
        // scanning to save resources and free the camera.
        if (mBarcodePicker != null) {
            mBarcodePicker.stopScanning();
        }

        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPaused = false;
        // note: onResume will be called repeatedly if camera access is not
        // granted.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            grantCameraPermissions();
        } else {

            // Once the activity is in the foreground again, restart scanning.
            if (mBarcodePicker != null) {
                mBarcodePicker.startScanning();
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mDeniedCameraAccess = false;
            } else {
                mDeniedCameraAccess = true;
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void grantCameraPermissions() {
        if (this.checkSelfPermission(Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            if (mDeniedCameraAccess == false) {
                // it's pretty clear for why the camera is required. We don't need to give a detailed
                // reason.
                this.requestPermissions(new String[]{Manifest.permission.CAMERA},
                        CAMERA_PERMISSION_REQUEST);
            }

        } else {
            // Once the activity is in the foreground again, restart scanning.
            if (mBarcodePicker != null) {
                mBarcodePicker.startScanning();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mBarcodePicker != null) {
            mBarcodePicker.stopScanning();
        }
        finish();
    }

    @Override
    public void didScan(ScanSession session) {
        // We let the scanner continuously scan without showing results.
        Log.e("ScanditSDK", session.getNewlyRecognizedCodes().get(0).getData());
    }
}
