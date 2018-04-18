package com.example.ehdus.testscan;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


import static android.content.Context.MODE_PRIVATE;

public class IngredientViewFragment extends FilterFragment {

    QuerySetter mQueryGetter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ConstraintLayout rootView = (ConstraintLayout) super.onCreateView(inflater, container, savedInstanceState);


        a = new IngredientAdapter(mQueryGetter, this.getContext());
        rv.setAdapter(a);

        ArrayList<String> ingredients = getArguments().getStringArrayList("ingredients");

        ingredients = ((IngredientAdapter) a).retrieveStoredIng(ingredients);
        if (ingredients != null)
            for (String s : ingredients) {
                try {
                    a.add(new Ingredient(a, new JSONObject(s)));
                } catch (JSONException e) {
                    // TODO: smarter exceptions
                }
            }


        Log.d("What's in the ingredient now", "" + ingredients);

        rv.addOnItemTouchListener(new IngEditTouchListener(this.getContext(), rv, (IngredientAdapter) a, rootView));

        new ItemTouchHelper(new SwipeCallback(a)).attachToRecyclerView(rv);
        ((IngredientAdapter) a).storeIngredients();

        return rootView;
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
        void queryListener(ArrayList<String> query);
    }




}