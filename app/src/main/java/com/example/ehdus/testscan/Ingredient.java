package com.example.ehdus.testscan;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

class Ingredient extends FilterableObject {
    public static final String STATUS = "status", NAME = "title", DESC = "serving_size", PIC = "images", TYPES = "breadcrumbs";
    private String mDesc;
    private ArrayList<String> mTypes;

    Ingredient(FilterAdapter a, String input) {
        super(a);
        try {
            JSONObject entry = new JSONObject(input);
            if (entry.getString(STATUS).equals("failure"))
                setError("UPC code not recognized", "That ingredient cannot be found in our database");
            mName = entry.getString(NAME);
            mDesc = entry.getString(DESC);
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

    Ingredient(FilterAdapter a, String error, String desc) {
        super(a);
        setError(error, desc);
    }

    private void setError(String error, String desc) {
        mName = "Error: " + error;
        mDesc = desc;
        mTypes = new ArrayList<>();
        mTypes.add("");
        new ImageGetter().execute(new JSONArray().put("https://pbs.twimg.com/profile_images/520273796549189632/d1et-xaU_400x400.png"));
    }

    public void setFields(String name, String desc, ArrayList<String> types) {
        this.mName = name;
        this.mDesc = desc;
        this.mTypes = types;
    }

    public String getDesc() {
        return mDesc;
    }

    // TODO: make this actually work
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
