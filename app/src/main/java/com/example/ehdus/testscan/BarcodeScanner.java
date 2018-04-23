package com.example.ehdus.testscan;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.scandit.barcodepicker.BarcodePicker;
import com.scandit.barcodepicker.OnScanListener;
import com.scandit.barcodepicker.ScanOverlay;
import com.scandit.barcodepicker.ScanSession;
import com.scandit.barcodepicker.ScanSettings;
import com.scandit.barcodepicker.ScanditLicense;
import com.scandit.recognition.Barcode;
import com.scandit.recognition.SymbologySettings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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

    private static final int REQUEST_BARCODE_PICKER_ACTIVITY = 55;
    private final static int CAMERA_PERMISSION_REQUEST = 5;
    private static boolean DEV;
    // The main object for scanning barcodes.
    private BarcodePicker mScanner;
    private boolean mDeniedCameraAccess = false;
    private IngredientAdapter a;

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.barcode_scanner);
        final ConstraintLayout rootView = findViewById(R.id.constraintLayout);

        DEV = getIntent().getBooleanExtra("DEV", false);

        // Barcode picker init
        mScanner = createScanner();
        ConstraintLayout.LayoutParams rParams;

        rParams = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_CONSTRAINT);
        rootView.addView(mScanner, rParams);

        // Done button init
        Button saveAndQuit = findViewById(R.id.save_and_quit);
        saveAndQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent checkMain = new Intent(BarcodeScanner.this, MainActivity.class);
                checkMain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                ArrayList<String> ingredientStrings = new ArrayList<>();
                for (Ingredient i : a.getList())
                    ingredientStrings.add(i.write());
                checkMain.putStringArrayListExtra("ingredients", ingredientStrings);
                checkMain.putExtra("DEV", DEV);
                startActivity(checkMain);
            }
        });

        // Scroller init
        final RecyclerView rv = findViewById(R.id.item_list);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(this));

        a = new IngredientAdapter(this, rootView, mScanner);
        rv.setAdapter(a);

        new ItemTouchHelper(new SwipeCallback(a)).attachToRecyclerView(rv);

        // Setting constraints for barcode scanner (MUST BE LAST or constraints will be whack)
        ConstraintSet cs = new ConstraintSet();
        cs.clone(rootView);
        cs.connect(mScanner.getId(), ConstraintSet.BOTTOM, R.id.guideline, ConstraintSet.BOTTOM);
        cs.connect(mScanner.getId(), ConstraintSet.TOP, rootView.getId(), ConstraintSet.TOP);

        cs.applyTo(rootView);

        mScanner.startScanning();
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

    private BarcodePicker createScanner() {
        ScanditLicense.setAppKey("WBMbFcD100VJcxQP54tH2O/L65ehgyLAbGzyPFQkI8w");

        // The scanning behavior of the barcode picker is configured through scan
        // settings. We start with empty scan settings and enable a very generous
        // set of symbologies. In your own apps, only enable the symbologies you
        // actually need.
        ScanSettings settings = ScanSettings.create();
        int[] symbologiesToEnable = new int[]{
                Barcode.SYMBOLOGY_EAN13,
                Barcode.SYMBOLOGY_EAN8,
                Barcode.SYMBOLOGY_UPCA,
                Barcode.SYMBOLOGY_UPCE
        };
        for (int sym : symbologiesToEnable) {
            settings.setSymbologyEnabled(sym, true);
        }

        // Some 1d barcode symbologies allow you to encode variable-length data. By default, the
        // Scandit BarcodeScanner SDK only scans barcodes in a certain length range. If your
        // application requires scanning of one of these symbologies, and the length is falling
        // outside the default range, you may need to adjust the "active symbol counts" for this
        // symbology. This is shown in the following few lines of code.

        SymbologySettings symSettings = settings.getSymbologySettings(Barcode.SYMBOLOGY_CODE39);
        short[] activeSymbolCounts = new short[]{
                7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20
        };
        symSettings.setActiveSymbolCounts(activeSymbolCounts);
        // For details on defaults and how to calculate the symbol counts for each symbology, take
        // a look at http://docs.scandit.com/stable/c_api/symbologies.html.

        // Prefer the back-facing camera, is there is any.
        settings.setCameraFacingPreference(ScanSettings.CAMERA_FACING_BACK);

        // the following code caching and duplicate filter values are the
        // defaults, they are nevertheless listed here to introduce them.

        // keep codes forever
        settings.setCodeCachingDuration(-1);
        // classify codes as duplicates if the same data/symbology is scanned
        // within 500ms.
        settings.setCodeDuplicateFilter(500);

        //! [Restrict Area]
        settings.setRestrictedAreaScanningEnabled(true);
        settings.setScanningHotSpotHeight(0.05f);


        BarcodePicker picker = new BarcodePicker(this, settings);

        // Set UI settings according to the settings activity. To get a
        // short overview and explanation of the most used settings please
        // check the SampleFullScreenBarcodeActivity.
        ScanOverlay overlay = picker.getOverlayView();
        overlay.setGuiStyle(ScanOverlay.GUI_STYLE_LASER);

        overlay.setBeepEnabled(false);
        overlay.setVibrateEnabled(false);

        overlay.setTorchEnabled(false);

        // Register listener, in order to be notified whenever a new code is
        // scanned
        picker.setOnScanListener(this);

        picker.setId(R.id.scanner);

        return picker;

    }

    @Override
    protected void onPause() {
        // When the activity is in the background immediately stop the
        // scanning to save resources and free the camera.
        if (mScanner != null) {
            mScanner.stopScanning();
        }

        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // note: onResume will be called repeatedly if camera access is not
        // granted.
        if (Build.VERSION.SDK_INT >= 23) {
            grantCameraPermissions();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            mDeniedCameraAccess = grantResults.length <= 0
                    || grantResults[0] != PackageManager.PERMISSION_GRANTED;
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void grantCameraPermissions() {
        if (this.checkSelfPermission(Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            if (!mDeniedCameraAccess) {
                // it's pretty clear for why the camera is required. We don't need to give a detailed
                // reason.
                this.requestPermissions(new String[]{Manifest.permission.CAMERA},
                        CAMERA_PERMISSION_REQUEST);
            }

        } else {
            // Once the activity is in the foreground again, restart scanning.
            if (mScanner != null) {
                mScanner.startScanning();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mScanner != null) {
            mScanner.stopScanning();
        }
        finish();
    }

    @Override
    public void didScan(ScanSession session) {
        // We let the scanner continuously scan and import any new ingredients.
        String barcode = session.getNewlyRecognizedCodes().get(0).getData();

        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (v != null) {
            // Vibrate for 500 milliseconds
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                //deprecated in API 26
                v.vibrate(300);
            }
        }

        StringBuilder cleanData = new StringBuilder();
        for (int i = 0; i < barcode.length(); ++i) {
            char c = barcode.charAt(i);
            cleanData.append(Character.isISOControl(c) ? '#' : c);
        }
        if (cleanData.length() > 30) {
            cleanData = new StringBuilder(cleanData.substring(0, 25) + "[...]");
        }
        new barcodeImport().execute(cleanData.toString());

        new waitBetweenScans().execute(900);


    }

    private class barcodeImport extends AsyncTask<String, String, Ingredient> {

        @Override
        protected Ingredient doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            Ingredient result;

            try {
                if (!DEV) {
                    URL url = new URL("https://spoonacular-recipe-food-nutrition-v1.p.mashape.com/food/products/upc/" + params[0]);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestProperty("X-Mashape-Key", MainActivity.KEY);
                    connection.connect(); //connects to server and returns data as input stream

                    InputStream stream = connection.getInputStream();

                    reader = new BufferedReader(new InputStreamReader(stream));

                    StringBuilder buffer = new StringBuilder();

                    String line;
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line);
                    }

                    String finalJson = buffer.toString();

                    if (finalJson == null) {
                        result = new Ingredient(a, "No relevant product found", "We can't find this in our database.  Please enter this item manually");
                    } else {
                        result = new Ingredient(a, finalJson);
                    }
                } else {
                    result = new Ingredient(a, "Barcode API turned off", "Barcode value = " + params[0]);
                }

                if (connection != null) {
                    connection.disconnect();
                }
                if (reader != null) {
                    reader.close();
                }

            } catch (IOException e) {
                // TODO: on FileNotFoundExceptions, the API returns a JSON with an error code.  I can't figure out how to get it, because it just triggers this and jumps out without getting access to that code.  I want it to ensure that we are returning the right error.
                result = new Ingredient(a, "Something happened", "Probably an invalid UPC code");
                return result;
            }

            return result;
        }

        @Override
        protected void onPostExecute(Ingredient result) {
            super.onPostExecute(result);
            a.add(result);
        }
    }

    // After each scan, wait some length of time then resume scanning
    private class waitBetweenScans extends AsyncTask<Integer, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mScanner.pauseScanning();
        }

        @Override
        protected Void doInBackground(Integer... params) {
            int time = params.length > 0 ? params[0] : 900;
            try {
                Thread.sleep(time);
            } catch (InterruptedException e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void exit) {
            super.onPostExecute(exit);
            mScanner.resumeScanning();
        }
    }
}
