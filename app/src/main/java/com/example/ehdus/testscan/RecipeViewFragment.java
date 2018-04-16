package com.example.ehdus.testscan;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class RecipeViewFragment extends FilterFragment implements SwipeRefreshLayout.OnRefreshListener, IngredientViewFragment.QuerySetter {

    private static final String url = "http://api.yummly.com/v1/api/recipes?_app_id=c69b9d36&_app_key=03634fefafae018b30371ba8d00ec23f&q=";
    private static final boolean DEV = true; // set this to FALSE to allow recipe lookup to work
    private int mode; // this will be used to determine where to draw recipes from
    // TODO: new default string?
    private String query = "onion+soup";

    // INIT: busy spinner, recipe list import and display
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);

        // TODO: touch listener to get recipe online

        a = new RecipeAdapter();
        rv.setAdapter(a);

        // swipe to refresh implementation
        if (mode == 0) {
            swipe.setEnabled(true);
            swipe.setOnRefreshListener(this);
            // automatically refresh on load
            swipe.post(new Runnable() {
                @Override
                public void run() {
                    swipe.setRefreshing(true);
                }
            });
            onRefresh();
        }

        return rootView;
    }

    // Swipe listener implementation; loads recipes on swipe
    @Override
    public void onRefresh() {
        // INIT: fetch recipes conforming to query
        //  recipeImport task populates the list on completion
        if (DEV) {
            a.clear();
            a.add(new Recipe(a, "Recipe API turned off"));
            swipe.setRefreshing(false);
        } else {
            new recipeImport().execute(url + query);
        }
    }

    @Override
    public String getType() {
        return "Recipe";
    }

    // Sets mode; 0 for Top Picks and 1 for Favorites
    public void setMode(int inMode) {
        mode = inMode;
    }

    @Override
    public void queryListener(String query) {
        this.query = query;
    }

    // INIT: gets list of recipes from Yummly
    //  on completion, stops spinner and populates list
    private class recipeImport extends AsyncTask<String, String, ArrayList<Recipe>> {

        // Makes query to get recipe list
        @Override
        protected ArrayList<Recipe> doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
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
                    // TODO: smarter exceptions
                }

            }
            return null;

        }

        // publishes results to the RecyclerView after search ends
        @Override
        protected void onPostExecute(ArrayList<Recipe> result) {
            super.onPostExecute(result);
            a.clear();
            for (Recipe r : result) {
                a.add(r);
            }
            swipe.setRefreshing(false);
        }
    }
}