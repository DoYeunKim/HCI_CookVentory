package com.example.ehdus.testscan;

import android.graphics.drawable.Drawable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

class Recipe {
    private String name;
    private Drawable pic;
    private int rating;

    Recipe(JSONObject entry) {
        //TODO: populate recipe object
        try {
            name = entry.getString("recipeName");
            rating = entry.getInt("rating");
            String picURL = (String) entry.getJSONArray("smallImageUrls").get(0);
            pic = Drawable.createFromStream(new URL(picURL).openStream(), "src");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
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
