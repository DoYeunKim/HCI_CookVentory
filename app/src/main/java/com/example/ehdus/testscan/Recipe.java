package com.example.ehdus.testscan;

import android.graphics.drawable.Drawable;

import org.json.JSONException;
import org.json.JSONObject;

class Recipe extends FilterableObject {
    private String name;
    private int rating;

    // parses input JSON object to return values we care about
    Recipe(FilterAdapter a, JSONObject entry) {
        super(a);
        try {
            name = entry.getString("recipeName");
            rating = entry.getInt("rating");
            new ImageGetter().execute(entry.getJSONArray("smallImageUrls"));
        } catch (JSONException e) {
            // TODO: smarter exceptions
            name = "Import failed";
        }
    }

    public String getName() {
        return name;
    }

    public int getRating() {
        return rating;
    }

    public Drawable getPic() {
        return pic;
    }

    @Override
    public String getFilterable() {
        return name;
    }
}
