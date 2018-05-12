package com.example.ehdus.testscan;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

// A very simple parent class used in MainActivity.onCreateOptionsMenu()'s SearchView.OnQueryTextListener() so that filtering can be applied to both Pantry and Ingredient lists
abstract public class FilterFragment extends Fragment {

    FilterAdapter a;
    RecyclerView rv;
    SwipeRefreshLayout swipe;

    // INIT: busy spinner, ingredient list import and display
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);

        swipe = rootView.findViewById(R.id.swipe_refresh);
        swipe.setEnabled(false);

        rv = rootView.findViewById(R.id.item_list);
        rv.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        return rootView;
    }

    // Accessor for list filtering from main activity search
    public void doFilter(String input) {
        if (a != null)
            a.getFilter().filter(input);
    }

    // For display of what type of fragment we're in
    public String getType() {
        return a.getType();
    };
}
