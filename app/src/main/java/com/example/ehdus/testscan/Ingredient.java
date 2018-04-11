package com.example.ehdus.testscan;

import android.graphics.drawable.Drawable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;

class Ingredient {
    private String name, desc;
    private Drawable pic;

    Ingredient(JSONObject entry) {
        try {
            name = entry.getString("title");
            desc = entry.getString("description");
            JSONArray picArray = entry.getJSONArray("images");
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

    public String getDesc() {
        return desc;
    }

    public Drawable getPic() {
        return pic;
    }
}
