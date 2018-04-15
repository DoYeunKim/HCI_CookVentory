package com.example.ehdus.testscan;

import org.json.JSONException;
import org.json.JSONObject;

class Recipe extends FilterableObject {
    private int mRating;

    // parses input JSON object to return values we care about
    Recipe(FilterAdapter a, JSONObject entry) {
        super(a);
        try {
            mName = entry.getString("recipeName");
            mRating = entry.getInt("rating");
            new ImageGetter().execute(entry.getJSONArray("smallImageUrls"));
        } catch (JSONException e) {
            // TODO: smarter exceptions
            mName = "Import failed";
        }
    }

    public int getRating() {
        return mRating;
    }

    @Override
    public String getFilterable() {
        return mName;
    }
}
