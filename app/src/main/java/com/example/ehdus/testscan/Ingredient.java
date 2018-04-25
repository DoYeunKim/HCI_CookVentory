package com.example.ehdus.testscan;

import android.content.Context;
import android.graphics.PorterDuff;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

class Ingredient extends FilterableObject {
    public static final String STATUS = "status", NAME = "title", DESC = "serving_size", PIC = "images", TYPES = "breadcrumbs", ADD_FLAG = "ADD_FLAG";
    private String mDesc;
    private ArrayList<String> mTypes;

    // Creation of add ingredient item
    Ingredient(FilterAdapter a, Context context) {
        super(a);
        mName = "New Ingredient";
        mDesc = "Edit to enter new ingredient";
        mTypes = new ArrayList<>();
        mTypes.add(ADD_FLAG);
        mPic = context.getDrawable(R.drawable.ic_add);
        mPic.setColorFilter(context.getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
    }

    // Normal creation with a JSON as string
    Ingredient(FilterAdapter a, String input) {
        super(a);
        try {
            JSONObject entry = new JSONObject(input);
            if (entry.has(STATUS) && entry.getString(STATUS).equals("failure")) {
                setError("UPC code not recognized", "That ingredient cannot be found in our database");
                return;
            }
            mName = entry.getString(NAME);
            mDesc = entry.getString(DESC);
            if (mDesc == "null") {
                mDesc = "No description given";
            }
            mTypes = new ArrayList<>();
            JSONArray temp = entry.getJSONArray(TYPES);
            if (temp.length() == 0)
                mTypes.add("");
            else
                for (int i = 0; i < temp.length(); i++)
                    mTypes.add(temp.getString(i));
            new ImageGetter().execute(entry.getJSONArray(PIC));
        } catch (JSONException e) {
            setError("Image import failed", "JSON Exception occurred");
        }
    }

    // Creation with string inputs for each value (Added ingredient/error item)
    Ingredient(FilterAdapter a, String name, String desc, boolean error) {
        super(a);
        if (error) {
            setError(name, desc);
            return;
        }

        mName = name;
        mDesc = desc;
        // TODO: get the below from the internet
        mTypes = new ArrayList<>();
        mTypes.add("");
        new ImageGetter().execute(new JSONArray().put("https://pbs.twimg.com/profile_images/520273796549189632/d1et-xaU_400x400.png"));
    }

    private void setError(String error, String desc) {
        mName = "Error: " + error;
        mDesc = desc;
        mTypes = new ArrayList<>();
        mTypes.add("");
        new ImageGetter().execute(new JSONArray().put("https://pbs.twimg.com/profile_images/520273796549189632/d1et-xaU_400x400.png"));
    }

    public void setFields(String name, String desc) {
        this.mName = name;
        this.mDesc = desc;
    }

    public String getDesc() {
        return mDesc;
    }

    public ArrayList<String> getQuery() {
        return mTypes;
    }

    @Override
    public String getFilterable() {
        return mName;
    }

    // Use this to store and save
    @Override
    public String write() {

        if (mTypes.get(0) == ADD_FLAG)
            return ADD_FLAG;

        JSONObject output = new JSONObject();
        try {
            output.put(NAME, mName);
            output.put(DESC, mDesc);
            JSONArray temp = new JSONArray();
            for (String s : mTypes)
                temp.put(s);
            output.put(TYPES, temp);
            output.put(PIC, new JSONArray().put(mPictureUrl));
        } catch (JSONException e) {
            // TODO: smarter exceptions
        }

        return output.toString();
    }
}
