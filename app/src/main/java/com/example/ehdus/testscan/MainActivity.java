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
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;

import java.util.Set;

public class MainActivity extends AppCompatActivity implements IngredientViewFragment.QuerySetter {

    public final static String KEY = "xNULsM3QVWmshXRTDrPZwNmCvwxap1kfmOujsnK5HvF0n7Oa2l";
    private final static int CAMERA_PERMISSION_REQUEST = 5;
    private static boolean DEV;
    private boolean mPaused = true;
    private boolean mDeniedCameraAccess = false;
    private SectionsPagerAdapter mSPA;
    private SearchView sv;

    // INIT: main activity appbar and tab scroller
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DEV = getIntent().getBooleanExtra("DEV", false);

        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        // INIT: tab scroller
        //  returns the right fragment for the tab we're currently on
        mSPA = new SectionsPagerAdapter(getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.container);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(mSPA);
        if (getIntent().getStringArrayListExtra("ingredients") != null) // if we just came from the barcode activity
            viewPager.setCurrentItem(2);
        TabLayout tabLayout = findViewById(R.id.tabs);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));

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
        // note: onResume will be called repeatedly if camera access is not granted.
        if (Build.VERSION.SDK_INT >= 23) {
            grantCameraPermissions();
        }

    }

    // INIT: appbar
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.app_bar, menu);

        // INIT: search manager
        //  calls filter object inside search class when text is updated or submitted in searchbar
        SearchManager sm = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        sv = (SearchView) menu.findItem(R.id.search).getActionView();

        if (sm != null)
            sv.setSearchableInfo(sm.getSearchableInfo(getComponentName()));

        sv.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                sv.requestFocus();
                if(hasFocus) {
                    showInputMethod(v.findFocus());
                    menu.findItem(R.id.action_camera).setVisible(false);
                } else {
                    menu.findItem(R.id.action_camera).setVisible(true);
                }
            }
        });
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mSPA.getCurrentFragment().doFilter(query);
                sv.setIconified(true);
                sv.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                mSPA.getCurrentFragment().doFilter(query);
                return true;
            }
        });
        sv.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                return false;
            }
        });

        return true;

    }

    private void showInputMethod(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(view, 0);
        }

    }

    // CONTROL: determine action to take when user taps menu buttons
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i;
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI
                i = new Intent(MainActivity.this,
                        SettingsActivity.class);
                i.putExtra("DEV", DEV);
                startActivity(i);
                return true;

            case R.id.action_camera:
                // User selected the camera icon, open the scanner
                if (mPaused) {
                    return false;
                }
                i = new Intent(MainActivity.this,
                        BarcodeScanner.class);
                i.putExtra("DEV", DEV);
                startActivity(i);
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
                // it's pretty clear for why the camera is required. We don't need to give a detailed reason.
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
        int count = getFragmentManager().getBackStackEntryCount();

        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }

        if (count == 0) {
            super.onBackPressed();
        } else {
            getFragmentManager().popBackStack();
        }
    }

    @Override
    public void queryListener(Set<String> query) {
        if (mSPA != null)
            mSPA.queryListener(query);
    }

    @Override
    public void toFavorites(String faveRecipe){
        if(mSPA != null)
            mSPA.toFavorites(faveRecipe);
    }


    // INIT: page adapter
    //  Controls fragment handling stuff.  Don't touch it, it's temperamental.
    class SectionsPagerAdapter extends FragmentPagerAdapter implements IngredientViewFragment.QuerySetter {

        private RecipeViewFragment rvfTop;
        private FavoriteViewFragment rvfFav;
        private IngredientViewFragment ivf;
        private FilterFragment mCurrentFragment;

        private SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        FilterFragment getCurrentFragment() {
            return mCurrentFragment;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            if (getCurrentFragment() != object) {
                mCurrentFragment = ((FilterFragment) object);
                if (sv != null) {
                    sv.setQueryHint("Search in " + mCurrentFragment.getType() + "s");
                    sv.setQuery("", false);
                }
            }
            super.setPrimaryItem(container, position, object);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    rvfTop = new RecipeViewFragment();
                    rvfTop.setMode(position);
                    rvfTop.setDev(DEV);
                    return rvfTop;
                case 1:
                    // NOW using Favorite View Fragment
                    Bundle bFav = new Bundle();
                    bFav.putStringArrayList("faveRecipes", getIntent().getStringArrayListExtra("faveRecipes"));
                    rvfFav = new FavoriteViewFragment();
                    rvfFav.setArguments(bFav);
                    return rvfFav;

                case 2:
                    Bundle b = new Bundle();
                    b.putStringArrayList("ingredients", getIntent().getStringArrayListExtra("ingredients"));
                    ivf = new IngredientViewFragment();
                    ivf.setArguments(b);
                    return ivf;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public void queryListener(Set<String> query) {
            if (rvfTop != null)
                rvfTop.queryListener(query);
        }

        @Override
        public void toFavorites(String faveRecipe){
            if(rvfFav != null)
                rvfFav.toFavorites(faveRecipe);
        }
    }
}

