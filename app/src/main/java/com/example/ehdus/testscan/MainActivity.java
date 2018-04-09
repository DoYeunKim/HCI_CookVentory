package com.example.ehdus.testscan;

import android.Manifest;
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

public class MainActivity extends AppCompatActivity {

    private boolean mPaused = true;
    private final static int CAMERA_PERMISSION_REQUEST = 5;
    private boolean mDeniedCameraAccess = false;
    private SectionsPagerAdapter mSectionsPagerAdapter;

    // INIT: main activity appbar and tab scroller
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        // INIT: tab scroller
        //  returns the right fragment for the tab we're currently on
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        ViewPager mViewPager = findViewById(R.id.container);
        mViewPager.setOffscreenPageLimit(2);
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

    // INIT: appbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // TODO: block users from changing tabs while search is open
        // TODO: close search bar when Submit is pressed
        // INIT: search manager
        //  calls filter object inside search class when text is updated or submitted in searchbar
        SearchManager sm = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final MenuItem mi = menu.findItem(R.id.search);
        SearchView sv = (SearchView) mi.getActionView();
        sv.setSearchableInfo(sm.getSearchableInfo(getComponentName()));
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                ((FilterFragment) mSectionsPagerAdapter.getCurrentFragment()).doFilter(query);
                mi.collapseActionView();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                ((FilterFragment) mSectionsPagerAdapter.getCurrentFragment()).doFilter(query);
                return true;
            }
        });

        return true;
    }

    // CONTROL: determine action to take when user taps menu buttons
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI
                return true;

            case R.id.action_camera:
                // User selected the camera icon, open the scanner
                if (mPaused) {
                    return false;
                }
                startActivity(new Intent(MainActivity.this,
                        BarcodeScanner.class));
                return true;

            default:
                // a built-in function (like Search), that we can ignore
                return super.onOptionsItemSelected(item);

        }
    }

    // INIT: camera
    //  requests camera permissions
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

    // CONTROL: camera
    //  performs camera permissions enabling
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

    // CONTROL: back button
    //  prevents back button from exiting application except on main page
    @Override
    public void onBackPressed() {
        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }
    }

    // INIT: page adapter
    //  Controls fragment handling stuff.  Don't touch it, it's temperamental.
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

