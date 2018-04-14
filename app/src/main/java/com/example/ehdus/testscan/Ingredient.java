package com.example.ehdus.testscan;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

class Ingredient extends FilterableObject implements Parcelable {
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

    public String getName() {
        return mName;
    }

    public String getDesc() {
        return mDesc;
    }

    public Drawable getPic() {
        return mPic;
    }

    @Override
    public String getFilterable() {
        return mName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mName);
        parcel.writeString(mDesc);
        parcel.writeString(mPictureUrl);
    }
}
