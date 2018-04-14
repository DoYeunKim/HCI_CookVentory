package com.example.ehdus.testscan;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

import org.json.JSONArray;

import java.net.URL;

abstract class FilterableObject {

    FilterAdapter a;
    Drawable mPic;
    String mName, mPictureUrl;

    FilterableObject(FilterAdapter a) {
        this.a = a;
    }

    public void setAdapter(FilterAdapter a) {
        this.a = a;
    }

    public abstract String getName();

    public abstract String getFilterable();

    class ImageGetter extends AsyncTask<JSONArray, Void, Drawable> {

        @Override
        protected Drawable doInBackground(JSONArray... arrays) {
            JSONArray picArray = arrays[0];
            try {
                if (picArray.length() > 0) {
                    mPictureUrl = (String) picArray.get(0);
                    mPic = Drawable.createFromStream(new URL(mPictureUrl).openStream(), "src");
                } else {
                    // TODO: find a default image to use when this fails
                    mPic = null;
                    mPictureUrl = "null";
                }
            } catch (Exception e) {
                // TODO: smarter exceptions
                mPic = null;
                mPictureUrl = "null";
            }
            return mPic;
        }

        @Override
        protected void onPostExecute(Drawable d) {
            a.notifyDataSetChanged();
        }
    }
}
