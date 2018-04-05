package com.example.ehdus.testscan;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

/**
 * A slightly more sophisticated activity illustrating how to embed the
 * Scandit BarcodeScanner SDK into your app.
 *
 * This activity shows 3 different ways to use the Scandit Barcode Picker:
 *
 *   - as a full-screen barcode picker in a separate activity (see
 *     SampleFullScreenBarcodeActivity).
 *   - as a cropped-view picker, only showing a small part of the video
 *     feed running
 *   - as a scaled-view picker showing a down-scaled version of the video
 *     feed.
 */
public class MainActivity extends AppCompatActivity {

    // objects for tab layout
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    // The main object for scanning barcodes.
    private boolean mPaused = true;
    private final static int CAMERA_PERMISSION_REQUEST = 5;
    private boolean mDeniedCameraAccess = false;
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        FloatingActionButton activityConfirmButton = findViewById(R.id.another);
        activityConfirmButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPaused) {
                    return;
                }
                startActivity(new Intent(MainActivity.this,
                        BarcodeScanner.class));
            }
        });
    }

    @Override
    protected void onPause() {
        mPaused = true;
        super.onPause();
    }

    @SuppressLint("ObsoleteSdkInt")
    @Override
    protected void onResume() {
        mPaused = false;
        super.onResume();
        // note: onResume will be called repeatedly if camera access is not
        // granted.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            grantCameraPermissions();
        }

    }

    @TargetApi(Build.VERSION_CODES.M)
    private void grantCameraPermissions() {
        if (this.checkSelfPermission(Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            if (!mDeniedCameraAccess) {
                // it's pretty clear for why the camera is required. We don't need to give a
                // detailed reason.
                this.requestPermissions(new String[]{Manifest.permission.CAMERA},
                        CAMERA_PERMISSION_REQUEST);
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            FloatingActionButton button1 = findViewById(R.id.another);
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                button1.setEnabled(true);
            } else {
                button1.setEnabled(false);
                mDeniedCameraAccess = true;
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onBackPressed() {
        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position < 2) {
                RecipeViewFragment rvf = new RecipeViewFragment();
                rvf.setMode(position);
                return rvf;
            } else {
                return new PantryViewFragment();
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

}

