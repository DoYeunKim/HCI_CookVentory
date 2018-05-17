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

public class FavoriteViewFragment extends FilterFragment implements IngredientViewFragment.FragPass {

    IngredientViewFragment.FragPass mQueryGetter;

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
        if (context instanceof IngredientViewFragment.FragPass)
            mQueryGetter = (IngredientViewFragment.FragPass) context;
    }


    @Override
    public void toFavorites(String faveRecipe){
        //TODO Check duplicate
        a.add(new Recipe(a, faveRecipe));
    }

    @Override
    public void queryListener(Set<String> query){
        return;
    }

    @Override
    public boolean isFavorite(Recipe checkFav){
        return a.contains(checkFav);
    }

    @Override
    public void updateRecipe() {
    }
}
