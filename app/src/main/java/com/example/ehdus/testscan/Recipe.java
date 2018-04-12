package com.example.ehdus.testscan;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

class Recipe extends FilterableObject {
    private String name, picUrl;
    private Drawable pic;
    private int rating;
    private final FilterAdapter a;

    // parses input JSON object to return values we care about
    Recipe(FilterAdapter a, JSONObject entry) {
        this.a = a;
        try {
            name = entry.getString("recipeName");
            rating = entry.getInt("rating");
            JSONArray picArray = entry.getJSONArray("smallImageUrls");
            if (picArray.length() > 0) {
                picUrl = (String) picArray.get(0);
                new ImageGetter().execute();
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

    @Override
    public String getFilterable() {
        return name;
    }

    class ImageGetter extends AsyncTask<String, Void, Drawable> {

        @Override
        protected Drawable doInBackground(String... strings) {
            try {
                pic = Drawable.createFromStream(new URL(picUrl).openStream(), "src");
            } catch (IOException e) {
                // TODO: handle this
            }
            return null;
        }

        @Override
        protected void onPostExecute(Drawable d) {
            a.notifyDataSetChanged();
        }
    }
}
