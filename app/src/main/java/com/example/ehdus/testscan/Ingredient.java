package com.example.ehdus.testscan;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

class Ingredient extends FilterableObject {
    public static final String NAME = "title", DESC = "description", PIC = "images", TYPES = "breadcrumbs";
    private String mDesc;
    private ArrayList<String> mTypes;

    Ingredient(FilterAdapter a, String input) {
        super(a);
        try {
            JSONObject entry = new JSONObject(input);
            mName = entry.getString(NAME);
            mDesc = entry.getString(DESC);
            mTypes = new ArrayList<>();
            JSONArray temp = entry.getJSONArray(TYPES);
            if (temp.length() == 0)
                mTypes.add("cinnamon");
            else
                for (int i = 0; i < temp.length(); i++)
                    mTypes.add(temp.getString(i));
            new ImageGetter().execute(entry.getJSONArray(PIC));
        } catch (JSONException e) {
            setError("Image import failed", "JSON Exception occurred");
        }
    }

    Ingredient(FilterAdapter a, String error, String desc) {
        super(a);
        setError(error, desc);
    }

    private void setError(String error, String desc) {
        mName = "Error: " + error;
        mDesc = desc;
        mTypes = new ArrayList<>();
        mTypes.add("cinnamon");
        new ImageGetter().execute(new JSONArray().put("https://pbs.twimg.com/profile_images/520273796549189632/d1et-xaU_400x400.png"));
    }

    public void setFields(String name, String desc, ArrayList<String> types) {
        this.mName = name;
        this.mDesc = desc;
        this.mTypes = types;
    }

    public String getDesc() {
        return mDesc;
    }

    // TODO: make this actually work
    public ArrayList<String> getQuery() {
        return mTypes;
        /*

        if (mName.equals("Barilla Linguine Fini Pasta, 16 Ounce (Pack of 20)")){
            return "linguine";
        }else if (mName.equals("Morton Sea Salt 4.4OZ (Pack of 24)")){
            return  "salt";
        }else if (mName.equals("Simply Organic Daily Grind Certified Organic Peppercorns -- 6x2.65Oz")){
            return  "pepper";
        }else if (mName.equals("Simply Organic Crushed Red Pepper -- 2.39 oz")){
            return  "pepper";
        }

        return "broccoli";
         */
    }

    @Override
    public String getFilterable() {
        return mName;
    }

    // Use this to store and save
    @Override
    public String write() {
        JSONObject output = new JSONObject();
        try {
            output.put(NAME, mName);
            output.put(DESC, mDesc);
            JSONArray temp = new JSONArray();
            for (String s : mTypes)
                temp.put(s);
            output.put(TYPES, temp);
            output.put(PIC, new JSONArray().put(mPictureUrl));
        } catch (JSONException e) {
            // TODO: smarter exceptions
        }

        return output.toString();
    }
}
