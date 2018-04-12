package com.example.ehdus.testscan;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

class Ingredient extends FilterableObject implements Parcelable {
    private String name, desc;

    Ingredient(FilterAdapter a, JSONObject entry) {
        super(a);
        try {
            name = entry.getString("title");
            desc = entry.getString("description");
            new ImageGetter().execute(entry.getJSONArray("images"));
        } catch (JSONException e) {
            name = "Import failed";
        }
    }

    Ingredient(FilterAdapter a, Parcel parcel) {
        super(a);
        name = parcel.readString();
        desc = parcel.readString();
        picURL = parcel.readString();
    }

    final Creator<Ingredient> CREATOR = new Creator<Ingredient>() {
        @Override
        public Ingredient createFromParcel(Parcel in) {
            return new Ingredient(a, in);
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
            output.put("url", new JSONArray().put(picURL));
        } catch (JSONException e) {
            // TODO: smarter exceptions
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
        parcel.writeString(picURL);
    }
}
