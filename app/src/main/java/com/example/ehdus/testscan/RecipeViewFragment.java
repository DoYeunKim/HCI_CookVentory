package com.example.ehdus.testscan;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class RecipeViewFragment extends FilterFragment {

    private int mode; // this will be used to determine where to draw recipes from
    private static final String url = "http://api.yummly.com/v1/api/recipes?_app_id=c69b9d36&_app_key=03634fefafae018b30371ba8d00ec23f&q=";
    // TODO: make this customizable
    private String query = "onion+soup";
    private static final boolean DEV = false; // set this to FALSE to allow recipe lookup to work

    // INIT: busy spinner, recipe list import and display
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        spinner.setVisibility(View.VISIBLE);

        a = new RecipeAdapter();
        rv.setAdapter(a);

        // INIT: fetch recipes conforming to query
        //  recipeImport task populates the list on completion
        if (DEV) {
            try {
                a.add(new Recipe(a, new JSONObject(
                        "{\"recipeName\":\"Error: Unable to access API\"," +
                                "\"rating\":0," +
                                "\"smallImageUrls\":[\"https://pbs.twimg.com/profile_images/520273796549189632/d1et-xaU_400x400.png\"]}"
                )));
                spinner.setVisibility(View.GONE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            new recipeImport().execute(url + query, "3");
        }

        return rootView;
    }

    // INIT: recipe list
    //  recursively attempts to solve until whiler runs out
    //  initializes adapter and displays list of recipes
    //  stops spinner and populates list
    private void populateList(ArrayList<Recipe> recipeList, int whiler) {
        if (whiler > 0 && recipeList == null) {
            new recipeImport().execute(url + query, Integer.toString(whiler - 1));
            return;
        } else if (recipeList == null) {
            try {
                a.add(new Recipe(a, new JSONObject(
                        "{\"recipeName\":\"Error: Unable to access API\"," +
                                "\"rating\":0," +
                                "\"smallImageUrls\":[\"https://pbs.twimg.com/profile_images/520273796549189632/d1et-xaU_400x400.png\"]}"
                )));
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                return;
            }
        }
        for (Recipe r : recipeList) {
            a.add(r);
        }
        spinner.setVisibility(View.GONE);
    }

    @Override
    public String getType() {
        return "Recipe";
    }

    // Sets mode; 0 for Top Picks and 1 for Favorites
    public void setMode(int inMode) {
        mode = inMode;
    }

    // INIT: gets list of recipes from Yummly
    private class recipeImport extends AsyncTask<String, String, ArrayList<Recipe>> {

        private int whiler;

        // Makes query to get recipe list
        @Override
        protected ArrayList<Recipe> doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                whiler = Integer.valueOf(params[1]);
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect(); //connects to server and returns data as input stream

                InputStream stream = connection.getInputStream();

                // Parse input
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuilder buffer = new StringBuilder();

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                ArrayList<Recipe> recipeList = new ArrayList<>();
                String finalJson = buffer.toString();

                JSONObject parentObject = new JSONObject(finalJson);
                JSONArray parentArray = parentObject.getJSONArray("matches");

                // Create list of recipes (recipe handles JSON parsing)
                for (int i = 0; i < parentArray.length(); i++)
                    recipeList.add(new Recipe(a, parentArray.getJSONObject(i)));

                return recipeList;

            } catch (Exception e) {
                e.printStackTrace();
            } finally {

                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            return null;

        }

        // publishes results to the RecyclerView after search ends
        @Override
        protected void onPostExecute(ArrayList<Recipe> result) {
            super.onPostExecute(result);
            populateList(result, whiler);
        }
    }

}