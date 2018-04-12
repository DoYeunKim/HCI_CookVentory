package com.example.ehdus.testscan;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

import org.json.JSONArray;

import java.net.URL;

abstract class FilterableObject {

    FilterAdapter a;
    Drawable pic;
    String picURL;

    FilterableObject(FilterAdapter a) {
        this.a = a;
    }

    public abstract String getFilterable();

    class ImageGetter extends AsyncTask<JSONArray, Void, Drawable> {

        @Override
        protected Drawable doInBackground(JSONArray... arrays) {
            JSONArray picArray = arrays[0];
            try {
                if (picArray.length() > 0) {
                    picURL = (String) picArray.get(0);
                    pic = Drawable.createFromStream(new URL(picURL).openStream(), "src");
                } else {
                    // TODO: find a default image to use when this fails
                    pic = null;
                    picURL = "null";
                }
            } catch (Exception e) {
                // TODO: smarter exceptions
                pic = null;
                picURL = "null";
            }
            return pic;
        }

        @Override
        protected void onPostExecute(Drawable d) {
            a.notifyDataSetChanged();
        }
    }
}
