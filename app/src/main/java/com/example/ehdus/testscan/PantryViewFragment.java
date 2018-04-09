package com.example.ehdus.testscan;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;

public class PantryViewFragment extends FilterFragment {

    private ExpandableListView elv;
    private PantryAdapter pa;
    private String[] typeList = {"Pie", "Cookies", "Ice Cream"}; //TODO: determine real food categories to use

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pantry, container, false);
        elv = rootView.findViewById(R.id.pantry_list);

        populateList(pantryImport());

        return rootView;
    }

    private void populateList(HashMap<String, ArrayList<Ingredient>> pantry) {
        pa = new PantryAdapter(this.getContext(), typeList, pantry);
        elv.setAdapter(pa);
    }

    @Override
    public void doFilter(String input) {
        pa.getFilter().filter(input);
    }

    //TODO: pantry import
    private HashMap<String, ArrayList<Ingredient>> pantryImport() {

        HashMap<String, ArrayList<Ingredient>> pantry = new HashMap<>();

        for (String type : typeList) {
            pantry.put(type, new ArrayList<Ingredient>());
            for (int i = 1; i <= 10; i++) {
                pantry.get(type).add(new Ingredient("Ingredient " + i, "This is ingredient #" + i + "'s default description, which can be multiline and very long!  It should wrap, but not indefinitely.", R.drawable.temp));
            }
        }

        return pantry;
    }
}