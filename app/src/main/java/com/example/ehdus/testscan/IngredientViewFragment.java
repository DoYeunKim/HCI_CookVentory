package com.example.ehdus.testscan;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class IngredientViewFragment extends FilterFragment {

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

        new ItemTouchHelper(new SwipeCallback(a)).attachToRecyclerView(rv);
        a.store();

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