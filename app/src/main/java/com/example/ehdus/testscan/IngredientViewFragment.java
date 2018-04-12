package com.example.ehdus.testscan;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class IngredientViewFragment extends FilterFragment {
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        spinner.setVisibility(View.GONE);

        rv.addOnItemTouchListener(new RecyclerTouchListener(this.getContext(), rv, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                //Values are passing to activity & to fragment as well
                Toast.makeText(IngredientViewFragment.this.getContext(), "Single Click on position: " + position,
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {
                Toast.makeText(IngredientViewFragment.this.getContext(), "Long press on position: " + position,
                        Toast.LENGTH_LONG).show();
            }
        }));

        a = new IngredientAdapter();
        rv.setAdapter(a);

        return rootView;
    }

    @Override
    public String getType() {
        return "Ingredient";
    }
}