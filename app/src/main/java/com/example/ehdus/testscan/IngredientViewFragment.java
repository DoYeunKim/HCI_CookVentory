package com.example.ehdus.testscan;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class IngredientViewFragment extends FilterFragment {
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        spinner.setVisibility(View.GONE);

        a = new IngredientAdapter();
        rv.setAdapter(a);

        return rootView;
    }

    @Override
    public String getType() {
        return "Ingredient";
    }
}