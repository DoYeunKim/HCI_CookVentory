package com.example.ehdus.testscan;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.SearchView;

/**
 * A slightly more sophisticated activity illustrating how to embed the
 * Scandit BarcodeScanner SDK into your app.
 * <p>
 * This activity shows 3 different ways to use the Scandit Barcode Picker:
 * <p>
 * - as a full-screen barcode picker in a separate activity (see
 * SampleFullScreenBarcodeActivity).
 * - as a cropped-view picker, only showing a small part of the video
 * feed running
 * - as a scaled-view picker showing a down-scaled version of the video
 * feed.
 */
public class MainActivity extends AppCompatActivity {

    // The main object for scanning barcodes.
    private boolean mPaused = true;
    private final static int CAMERA_PERMISSION_REQUEST = 5;
    private boolean mDeniedCameraAccess = false;
    private SectionsPagerAdapter mSectionsPagerAdapter;

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        ViewPager mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        SearchManager sm = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView sv = (SearchView) menu.findItem(R.id.search).getActionView();
        sv.setSearchableInfo(sm.getSearchableInfo(getComponentName()));
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                RecipeViewFragment rvf = (RecipeViewFragment) mSectionsPagerAdapter.getCurrentFragment();
                rvf.getAdapter().getFilter().filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                RecipeViewFragment rvf = (RecipeViewFragment) mSectionsPagerAdapter.getCurrentFragment();
                rvf.getAdapter().getFilter().filter(newText);
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                return true;

            case R.id.action_camera:
                if (mPaused) {
                    return false;
                }
                startActivity(new Intent(MainActivity.this,
                        BarcodeScanner.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);

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
            ActionMenuItemView button1 = findViewById(R.id.action_camera);
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
    class SectionsPagerAdapter extends FragmentPagerAdapter {

        private Fragment mCurrentFragment;

        public Fragment getCurrentFragment() {
            return mCurrentFragment;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            if (getCurrentFragment() != object) {
                mCurrentFragment = ((Fragment) object);
            }
            super.setPrimaryItem(container, position, object);
        }

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

