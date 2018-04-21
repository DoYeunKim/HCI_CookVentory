package com.example.ehdus.testscan;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Set;

public class IngredientViewFragment extends FilterFragment{

    public static final String NAME = "title", DESC = "serving_size", PIC = "images", TYPES = "breadcrumbs";
    private ArrayList<String> mTypes;
    QuerySetter mQueryGetter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ConstraintLayout rootView = (ConstraintLayout) super.onCreateView(inflater, container, savedInstanceState);

        a = new IngredientAdapter(mQueryGetter, this.getContext());
        rv.setAdapter(a);

        ArrayList<String> ingredients = getArguments().getStringArrayList("ingredients");

        ingredients = a.retrieveStored(ingredients);
        if (ingredients != null)
            for (String s : ingredients)
                a.add(new Ingredient(a, s));

        rv.addOnItemTouchListener(new IngEditTouchListener(this.getContext(), rv, (IngredientAdapter) a, rootView));

        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        a.store();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof QuerySetter)
            mQueryGetter = (QuerySetter) context;
    }

    @Override
    public String getType() {
        return "Ingredient";
    }

    interface QuerySetter {
        void queryListener(Set<String> query);
    }
}