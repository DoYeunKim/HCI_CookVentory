package com.example.ehdus.testscan;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.scandit.barcodepicker.BarcodePicker;
import com.scandit.barcodepicker.OnScanListener;
import com.scandit.barcodepicker.ScanOverlay;
import com.scandit.barcodepicker.ScanSession;
import com.scandit.barcodepicker.ScanSettings;
import com.scandit.barcodepicker.ScanditLicense;
import com.scandit.recognition.Barcode;
import com.scandit.recognition.SymbologySettings;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Example for a full-screen barcode scanning activity using the Scandit
 * Barcode picker.
 *
 * The activity does the following:
 *
 *  - starting the picker full-screen mode
 *  - configuring the barcode picker with the settings defined in the
 *    SettingsActivity.
 *  - registering a listener to get notified whenever a barcode gets
 *    scanned. Upon a successful scan, the barcode scanner is paused and
 *    the recognized barcode is displayed in an overlay. When the user
 *    taps the screen, barcode recognition is resumed.
 *
 * For non-fullscreen barcode scanning take a look at the BatchModeScanSampleMainActivity
 * class.
 */
public class BarcodeScanner
        extends AppCompatActivity
        implements OnScanListener {

    // The main object for recognizing a displaying barcodes.
    private BarcodePicker mBarcodePicker;
    private UIHandler mHandler = null;
    private Button buttonScan = null;
    private View barcodeView = null;
    private TextView barcodeText = null;
    private Runnable mRunnable = null;
    private Button listScanned = null;

    // Enter your Scandit SDK License key here.
    // Your Scandit SDK License key is available via your Scandit SDK web account.
    public static final String sScanditSdkAppKey = "WBMbFcD100VJcxQP54tH2O/L65ehgyLAbGzyPFQkI8w";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_scanner);
        mHandler = new UIHandler(this);
        // We keep the screen on while the scanner is running.
        getWindow().addFlags(LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Initialize and start the bar code recognition.
        initializeAndStartBarcodeScanning();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // When the activity is in the background immediately stop the
        // scanning to save resources and free the camera.
        mBarcodePicker.stopScanning();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Once the activity is in the foreground again, restart scanning.
        mBarcodePicker.startScanning();
        mBarcodePicker.pauseScanning();
    }

    /**
     * Initializes and starts the bar code scanning.
     */
    public void initializeAndStartBarcodeScanning() {
        ScanditLicense.setAppKey(sScanditSdkAppKey);
        // Switch to full screen.
        getWindow().setFlags(LayoutParams.FLAG_FULLSCREEN,
                LayoutParams.FLAG_FULLSCREEN);

        barcodeView = findViewById(R.id.barcode_detected);
        barcodeText = findViewById(R.id.barcode_text);
        mRunnable = new Runnable() {
            @Override
            public void run() {
                mBarcodePicker.removeView(barcodeView);
            }
        };

        // The scanning behavior of the barcode picker is configured through scan
        // settings. We start with empty scan settings and enable a very generous
        // set of symbologies. In your own apps, only enable the symbologies you
        // actually need.
        ScanSettings settings = ScanSettings.create();
        int[] symbologiesToEnable = new int[] {
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
        short[] activeSymbolCounts = new short[] {
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
        //! [Restrict Area]
        mBarcodePicker = new BarcodePicker(this, settings);

        // Add listeners for scan events and search bar events (only needed if the bar is visible).
        mBarcodePicker.setOnScanListener(this);

        // Add both views to activity, with the scan GUI on top.
        setContentView(mBarcodePicker);

        mBarcodePicker.getOverlayView().setTorchEnabled(false);
        //! [Laser Button Added]
        //Set laser gui and add button
        mBarcodePicker.getOverlayView().setGuiStyle(ScanOverlay.GUI_STYLE_LASER);

        buttonScan = new Button(this);
        buttonScan.setTextColor(Color.WHITE);
        buttonScan.setTextColor(Color.WHITE);
        buttonScan.setTextSize(18);
        buttonScan.setGravity(Gravity.CENTER);
        buttonScan.setText(R.string.scan);
        buttonScan.setBackgroundColor(0xFF39C1CC);
        addScanButton();
        buttonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resumeScanning();
            }
        });

        listScanned = new Button (this);
        listScanned.setTextColor(Color.WHITE);
        listScanned.setTextSize(18);
        listScanned.setGravity(Gravity.CENTER);
        listScanned.setText(R.string.done);
        listScanned.setBackgroundColor(0xFF39C1CC);
        moveToList();
        listScanned.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBarcodePicker.stopScanning();
                Intent theList = new Intent(BarcodeScanner.this, ListOfBarcodes.class);
                theList.putExtra("barcode", "9788679912077");
                startActivity(theList);
            }
        });

        //! [Laser Button Added]

    }

    private void addScanButton() {
        RelativeLayout layout = mBarcodePicker.getOverlayView();
        RelativeLayout.LayoutParams rParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 150);
        rParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        rParams.bottomMargin = 50;
        rParams.leftMargin = 50;
        rParams.rightMargin = 50;
        layout.addView(buttonScan, rParams);
    }

    private void moveToList() {
        RelativeLayout layout = mBarcodePicker.getOverlayView();
        RelativeLayout.LayoutParams rParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, 150);
        rParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        rParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        rParams.topMargin = 50;
        rParams.leftMargin = 50;
        rParams.rightMargin = 50;
        layout.addView(listScanned, rParams);
    }

    @Override
    public void didScan(ScanSession session) {
        List<Barcode> newlyDecoded = session.getNewlyRecognizedCodes();
        /*
        because the callback is invoked inside the thread running the barcode
        recognition, any UI update must be posted to the UI thread.
        In this example, we want to show the first decoded barcode in a
        splash screen covering the full display.
        */
        Message msg = mHandler.obtainMessage(UIHandler.SHOW_BARCODES,
                newlyDecoded);
        mHandler.removeCallbacks(mRunnable);
        mHandler.sendMessage(msg);
        mHandler.postDelayed(mRunnable, 3 * 1000);
        // pause scanning and clear the session. The scanning itself is resumed
        // when the user taps the screen.
        session.pauseScanning();
        session.clear();
    }

    @Override
    public void onBackPressed() {
        mBarcodePicker.stopScanning();
        finish();
    }

    private void resumeScanning() {
        //! [Resume]
        mBarcodePicker.resumeScanning();
        //! [Resume]
        mBarcodePicker.getOverlayView().removeView(buttonScan);
    }

    static private class UIHandler extends Handler {
        private static final int SHOW_BARCODES = 0;
        private WeakReference<BarcodeScanner> mActivity;
        UIHandler(BarcodeScanner activity) {
            mActivity = new WeakReference<BarcodeScanner>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_BARCODES:
                    showSplash(createMessage((List<Barcode>)msg.obj));
                    break;
            }

        }
        private String createMessage(List<Barcode> codes) {
            StringBuilder message = new StringBuilder();
            for (Barcode code : codes) {
                String data = code.getData();
                // cleanup the data somewhat by replacing control characters contained in
                // some of the barcodes with hash signs and truncating long barcodes
                // to reasonable lengths.
                StringBuilder cleanData = new StringBuilder();
                for (int i = 0; i < data.length(); ++i) {
                    char c = data.charAt(i);
                    cleanData.append(Character.isISOControl(c) ? '#' : c);
                }
                if (cleanData.length() > 30) {
                    cleanData = new StringBuilder(cleanData.substring(0, 25) + "[...]");
                }
                message.append("Scanned ").append(code.getSymbologyName().toUpperCase()).append(" Code: \n");
                message.append(cleanData);

            }
            return message.toString();
        }

        private void showSplash(String msg) {
            BarcodeScanner activity = mActivity.get();
            RelativeLayout layout = activity.mBarcodePicker;
            layout.removeView(activity.barcodeView);
            RelativeLayout.LayoutParams rParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 200);
            rParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            layout.addView(activity.barcodeView, rParams);
            activity.barcodeText.setText(msg);
            activity.addScanButton();
        }
    }
}
