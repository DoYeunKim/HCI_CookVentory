package com.example.ehdus.testscan;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

class Ingredient extends FilterableObject {
    private String mDesc;

    Ingredient(FilterAdapter a, JSONObject entry) {
        super(a);
        try {
            mName = entry.getString("title");
            mDesc = entry.getString("description");
            new ImageGetter().execute(entry.getJSONArray("images"));
        } catch (JSONException e) {
            mName = "Import failed";
        }
    }

    // Use this to write to disk
    public String write() {
        JSONObject output = new JSONObject();
        try {
            output.put("title", mName);
            output.put("description", mDesc);
            output.put("images", new JSONArray().put(mPictureUrl));
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
