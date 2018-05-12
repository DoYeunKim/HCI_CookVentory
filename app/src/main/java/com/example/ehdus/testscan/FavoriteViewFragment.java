package com.example.ehdus.testscan;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class FavoriteViewFragment extends FilterFragment implements IngredientViewFragment.QuerySetter{

    IngredientViewFragment.QuerySetter mQueryGetter;
    private Set<String> query = new HashSet<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ConstraintLayout rootView = (ConstraintLayout) super.onCreateView(inflater, container, savedInstanceState);

        a = new FavoriteAdapter(this.getContext(), rootView);
        rv.setAdapter(a);

        ArrayList<String> faveRecipes = new ArrayList<>();

        // Copies the recipes that we already favorite-d.
        faveRecipes = a.retrieveStored(faveRecipes);
        if (faveRecipes != null)
            for (String s : faveRecipes)
                a.add(new Recipe(a, s));

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
        if (context instanceof IngredientViewFragment.QuerySetter)
            mQueryGetter = (IngredientViewFragment.QuerySetter) context;
    }

    @Override
    public String getType() {
        return "Recipe";
    }

    @Override
    public void toFavorites(String faveRecipe){
        a.add(new Recipe(a, faveRecipe));
    }

    @Override
    public void queryListener(Set<String> query){
        return;
    }


}
