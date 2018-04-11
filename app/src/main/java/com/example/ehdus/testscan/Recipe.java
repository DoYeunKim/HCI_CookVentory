package com.example.ehdus.testscan;

import android.graphics.drawable.Drawable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;

class Recipe {
    private String name;
    private Drawable pic;
    private int rating;

    // parses input JSON object to return values we care about
    Recipe(JSONObject entry) {
        try {
            name = entry.getString("recipeName");
            rating = entry.getInt("rating");
            JSONArray picArray = entry.getJSONArray("smallImageUrls");
            if (picArray.length() > 0) {
                String picURL = (String) picArray.get(0);
                pic = Drawable.createFromStream(new URL(picURL).openStream(), "src");
            }
        } catch (Exception e) {
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
}
