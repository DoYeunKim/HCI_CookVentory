package com.example.ehdus.testscan;

import android.os.Parcel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

class Ingredient extends FilterableObject {
    private String mDesc;

    Ingredient(FilterAdapter a, JSONObject entry) {
        super(a);
        try {
            mName = entry.getString("title");
            mDesc = entry.getString("description");
            new ImageGetter().execute(entry.getJSONArray("images"));
        } catch (JSONException e) {
            mName = "Import failed";
        }
    }

    Ingredient(FilterAdapter a, Parcel parcel) {
        super(a);
        mName = parcel.readString();
        mDesc = parcel.readString();
        mPictureUrl = parcel.readString();
    }

    // Use this to write to disk
    public JSONObject write() {
        JSONObject output = new JSONObject();
        try {
            output.put("name", mName);
            output.put("desc", mDesc);
            output.put("url", new JSONArray().put(mPictureUrl));
        } catch (JSONException e) {
            // TODO: smarter exceptions
        }

        return output;
    }

    public String getDesc() {
        return mDesc;
    }

    @Override
    public String getFilterable() {
        return mName;
    }
}
