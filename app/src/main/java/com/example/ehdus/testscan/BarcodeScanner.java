package com.example.ehdus.testscan;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.scandit.barcodepicker.BarcodePicker;
import com.scandit.barcodepicker.OnScanListener;
import com.scandit.barcodepicker.ScanOverlay;
import com.scandit.barcodepicker.ScanSession;
import com.scandit.barcodepicker.ScanditLicense;

import java.util.ArrayList;
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
    private boolean mDeniedCameraAccess = false;

    private final static int CAMERA_PERMISSION_REQUEST = 5;


    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.barcode_scanner);
        final ConstraintLayout rootView = findViewById(R.id.constraintLayout);

        mBarcodePicker = createPicker();
        ConstraintLayout.LayoutParams rParams;

        rParams = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_CONSTRAINT);
        rootView.addView(mBarcodePicker, rParams);

        RecyclerView list = findViewById(R.id.new_ingredients);
        list.setHasFixedSize(true);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(new NewIngredientAdapter(barcodeImport()));

        Button saveAndQuit = findViewById(R.id.save_and_quit);
        saveAndQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent checkMain = new Intent(BarcodeScanner.this, MainActivity.class);
                checkMain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                checkMain.putExtra("EXIT", true);
                startActivity(checkMain);
            }
        });

        ConstraintSet cs = new ConstraintSet();
        cs.clone(rootView);
        cs.connect(mBarcodePicker.getId(), ConstraintSet.BOTTOM, R.id.guideline, ConstraintSet.BOTTOM);
        cs.connect(mBarcodePicker.getId(), ConstraintSet.TOP, rootView.getId(), ConstraintSet.TOP);

        cs.applyTo(rootView);

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

        overlay.setBeepEnabled(false);
        overlay.setVibrateEnabled(true);

        overlay.setTorchEnabled(false);

        // Register listener, in order to be notified whenever a new code is
        // scanned
        picker.setOnScanListener(this);
        return picker;

    }

    @Override
    protected void onPause() {
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

    //TODO: ingredient import from barcodes
    private ArrayList<Ingredient> barcodeImport() {

        ArrayList<Ingredient> newIngredients = new ArrayList<>();

        for (int i = 1; i <= 10; i++) {
            newIngredients.add(new Ingredient("New ingredient " + i, "This is ingredient #" + i + "'s default description, which can be multiline and very long!  It should wrap, but not indefinitely.", R.drawable.temp));
        }
        return newIngredients;
    }
}
