package com.example.ehdus.testscan;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

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
    private RecyclerView rv;
    private ProgressBar spinner;
    private RecipeAdapter ra;

    // INIT: busy spinner, recipe list import and display
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recipe, container, false);

        spinner = rootView.findViewById(R.id.spinner);

        // TODO: make this customizable
        String query = "onion+soup";
        String URL = "http://api.yummly.com/v1/api/recipes?_app_id=c69b9d36&_app_key=03634fefafae018b30371ba8d00ec23f&q=";

        // INIT: fetch recipes conforming to query
        //  This class also populates the list on completion
        new recipeImport().execute(URL + query);

        rv = rootView.findViewById(R.id.recipe_list);
        rv.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        return rootView;
    }

    // INIT: recipe list
    //  initializes adapter and displays list of recipes
    private void populateList(ArrayList<Recipe> recipeList) {
        ra = new RecipeAdapter(this.getContext(), recipeList);
        rv.setAdapter(ra);
    }

    // Accessor for list filtering from main activity search
    @Override
    public void doFilter(String input) {
        ra.getFilter().filter(input);
    }

    // Sets mode; 0 for Top Pics and 1 for Favorites
    public void setMode(int inMode) {
        mode = inMode;
    }

    // INIT: gets list of recipes from Yummly
    private class recipeImport extends AsyncTask<String, String, ArrayList<Recipe>> {

        // Starts busy spinner immediately before starting the search
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            spinner.setVisibility(View.VISIBLE);
        }

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
                    recipeList.add(new Recipe(parentArray.getJSONObject(i)));

                return recipeList;

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
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

        // Stops busy spinner and publishes results to the RecyclerView after search ends
        @Override
        protected void onPostExecute(ArrayList<Recipe> result) {
            super.onPostExecute(result);
            populateList(result);
            spinner.setVisibility(View.GONE);
        }
    }

}