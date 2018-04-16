package com.example.ehdus.testscan;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class IngredientViewFragment extends FilterFragment {
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        mSpinner.setVisibility(View.GONE);

        a = new IngredientAdapter();
        rv.setAdapter(a);

        ArrayList<String> ingredients = getArguments().getStringArrayList("ingredients");

        if (ingredients != null)
            for (String s : ingredients) {
                try {
                    a.add(new Ingredient(a, new JSONObject(s)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        rv.addOnItemTouchListener(new IngEditTouchListener(this.getContext(), rv, a));

        return rootView;
    }

    @Override
    public String getType() {
        return "Ingredient";
    }
}