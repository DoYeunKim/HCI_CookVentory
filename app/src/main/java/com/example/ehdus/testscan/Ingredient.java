package com.example.ehdus.testscan;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

class Ingredient extends FilterableObject {
    public static final String NAME = "title", DESC = "serving_size", PIC = "images", TYPES = "breadcrumbs";
    private String mDesc;
    private JSONArray mTypes;

    Ingredient(FilterAdapter a, JSONObject entry) {
        super(a);
        try {
            mName = entry.getString(NAME);
            mDesc = entry.getString(DESC);
            mTypes = entry.getJSONArray(TYPES);
            new ImageGetter().execute(entry.getJSONArray(PIC));
        } catch (JSONException e) {
            setError("Image import failed", "");
        }
    }

    Ingredient(FilterAdapter a, String error, String desc) {
        super(a);
        setError(error, desc);
    }

    private void setError(String error, String desc) {
        mName = "Error: " + error;
        mDesc = desc;
        mTypes = new JSONArray();
        new ImageGetter().execute(new JSONArray().put("https://pbs.twimg.com/profile_images/520273796549189632/d1et-xaU_400x400.png"));
    }

    // Use this to store and save this
    public String write() {
        JSONObject output = new JSONObject();
        try {
            output.put(NAME, mName);
            output.put(DESC, mDesc);
            output.put(TYPES, mTypes);
            output.put(PIC, new JSONArray().put(mPictureUrl));
        } catch (JSONException e) {
            // TODO: smarter exceptions
        }

        return output.toString();
    }

    public String getDesc() {
        return mDesc;
    }

    // TODO: make this actually work
    public String getQueryString() {
        return "cinnamon";
    }

    @Override
    public String getFilterable() {
        return mName;
    }
}
