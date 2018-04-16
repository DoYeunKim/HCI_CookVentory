package com.example.ehdus.testscan;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

class Recipe extends FilterableObject {
    private int mRating;
    public static final String NAME = "recipeName", RATING = "rating", PIC = "smallImageUrls";

    // parses input JSON object to return values we care about
    Recipe(FilterAdapter a, JSONObject entry) {
        super(a);
        try {
            mName = entry.getString(NAME);
            mRating = entry.getInt(RATING);
            new ImageGetter().execute(entry.getJSONArray(PIC));
        } catch (JSONException e) {
            setError("Image import failed");
        }
    }

    Recipe(FilterAdapter a, String error) {
        super(a);
        setError(error);
    }

    private void setError(String error) {
        mName = "Error: " + error;
        mRating = 0;
        new ImageGetter().execute(new JSONArray().put("https://pbs.twimg.com/profile_images/520273796549189632/d1et-xaU_400x400.png"));
    }

    public int getRating() {
        return mRating;
    }

    @Override
    public String getFilterable() {
        return mName;
    }
}
