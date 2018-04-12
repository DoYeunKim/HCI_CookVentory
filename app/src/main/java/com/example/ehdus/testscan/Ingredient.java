package com.example.ehdus.testscan;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

class Ingredient extends FilterableObject implements Parcelable {
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
            } else {
                // TODO: find a default image to use when this fails
                url = "null";
            }
        } catch (Exception e) {
            name = "Import failed";
        }
    }

    private Ingredient(Parcel parcel) {
        name = parcel.readString();
        desc = parcel.readString();
        url = parcel.readString();
    }

    static final Creator<Ingredient> CREATOR = new Creator<Ingredient>() {
        @Override
        public Ingredient createFromParcel(Parcel in) {
            return new Ingredient(in);
        }

        @Override
        public Ingredient[] newArray(int size) {
            return new Ingredient[size];
        }
    };

    // Use this to write to disk
    public JSONObject write() {
        JSONObject output = new JSONObject();
        try {
            output.put("name", name);
            output.put("desc", desc);
            output.put("url", new JSONArray().put(url));
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

    @Override
    public String getFilterable() {
        return name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(desc);
        parcel.writeString(url);
    }
}
