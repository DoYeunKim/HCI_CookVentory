package com.example.ehdus.testscan;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class IngredientViewFragment extends FilterFragment {

    private int mode; // this will be used to determine where to draw ingredients from
    private RecyclerView rv;
    private IngredientAdapter ia;

    // INIT: busy spinner, ingredient list import and display
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pantry, container, false);

        rv = rootView.findViewById(R.id.ingredient_list);
        rv.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        ia = new IngredientAdapter();
        rv.setAdapter(ia);

        return rootView;
    }

    // Accessor for list filtering from main activity search
    @Override
    public void doFilter(String input) {
        ia.getFilter().filter(input);
    }

    @Override
    public String getType() {
        return "Ingredient";
    }
}