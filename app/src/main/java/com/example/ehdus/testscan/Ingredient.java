package com.example.ehdus.testscan;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

class Ingredient extends FilterableObject {
    private String mDesc;
    public static final String NAME = "title", DESC = "description", PIC = "images";

    Ingredient(FilterAdapter a, JSONObject entry) {
        super(a);
        try {
            mName = entry.getString(NAME);
            mDesc = entry.getString(DESC);
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
        new ImageGetter().execute(new JSONArray().put("https://pbs.twimg.com/profile_images/520273796549189632/d1et-xaU_400x400.png"));
    }

    // Use this to write to disk
    public String write() {
        JSONObject output = new JSONObject();
        try {
            output.put(NAME, mName);
            output.put(DESC, mDesc);
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
