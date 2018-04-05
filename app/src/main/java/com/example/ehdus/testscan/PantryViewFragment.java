package com.example.ehdus.testscan;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PantryViewFragment extends Fragment {

    private List<String> typeList;
    private HashMap<String, List<Ingredient>> pantry;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pantry, container, false);

        ExpandableListView rv = rootView.findViewById(R.id.pantry_list);
        pantryImport();
        rv.setAdapter(new PantryAdapter(this.getContext(), typeList, pantry));

        return rootView;
    }

    //TODO: pantry import
    private void pantryImport() {
        typeList = new ArrayList<>();
        pantry = new HashMap<>();

        typeList.add("Fruits");
        typeList.add("Vegetables");
        typeList.add("Meat");
        typeList.add("Cookies");

        for (String type : typeList) {
            pantry.put(type, new ArrayList<Ingredient>());
            for (int i = 1; i <= 10; i++) {
                pantry.get(type).add(new Ingredient("Ingredient " + i, "This is ingredient #" + i + "\nDescriptions can be multiline up to 2 lines\nBut not more", R.drawable.temp));
            }
        }
    }
}