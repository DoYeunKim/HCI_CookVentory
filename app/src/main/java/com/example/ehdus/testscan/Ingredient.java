package com.example.ehdus.testscan;

import android.graphics.drawable.Drawable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

class Ingredient {
    private String name, desc, url;
    private Drawable pic;

    Ingredient(JSONObject entry) {
        try {
            name = entry.getString("title");
            desc = entry.getString("description");
            JSONArray picArray = entry.getJSONArray("images");
            if (picArray.length() > 0) {
                url = (String) picArray.get(0);
                pic = Drawable.createFromStream(new URL(url).openStream(), "src");
            }
        } catch (Exception e) {
            name = "Import failed";
        }

    }

    public JSONObject write() {
        JSONObject output = new JSONObject();
        try {
            output.put("name", name);
            output.put("desc", desc);
            output.put("url", url);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return output;
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
