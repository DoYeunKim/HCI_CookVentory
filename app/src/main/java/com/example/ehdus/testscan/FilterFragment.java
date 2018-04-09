package com.example.ehdus.testscan;

import android.support.v4.app.Fragment;

// A very simple parent class used in MainActivity.onCreateOptionsMenu()'s SearchView.OnQueryTextListener() so that filtering can be applied to both Pantry and Ingredient lists
abstract public class FilterFragment extends Fragment {
    abstract public void doFilter(String input);
}
