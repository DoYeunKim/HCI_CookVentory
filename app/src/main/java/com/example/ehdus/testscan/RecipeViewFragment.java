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
    private final String url = "http://api.yummly.com/v1/api/recipes?_app_id=c69b9d36&_app_key=03634fefafae018b30371ba8d00ec23f&q=";
    // TODO: make this customizable
    private String query = "onion+soup";

    // INIT: busy spinner, recipe list import and display
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recipe, container, false);

        spinner = rootView.findViewById(R.id.spinner);
        spinner.setVisibility(View.VISIBLE);

        rv = rootView.findViewById(R.id.recipe_list);
        rv.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        // INIT: fetch recipes conforming to query
        //  This class also populates the list on completion
        // TODO: remove comment (temporary to avoid clogging up API)
        //new recipeImport().execute(url + query, "3");
        populateList(null, 0);

        return rootView;
    }

    // INIT: recipe list
    //  recursively attempts to solve until whiler runs out
    //  initializes adapter and displays list of recipes
    //  stops spinner and populates list
    private void populateList(ArrayList<Recipe> recipeList, int whiler) {
        /* to cut down on API calls while testing other things TODO: remove the comments
        if (whiler > 0 && recipeList == null) {
            new recipeImport().execute(url + query, Integer.toString(whiler - 1));
            return;
        } else if (recipeList == null) {
            try {
                recipeList = new ArrayList<>();
                recipeList.add(new Recipe(new JSONObject(
                        "{\"recipeName\":\"Error: Unable to access API\"," +
                                "\"rating\":0," +
                                "\"smallimageUrls\":[\"https://pbs.twimg.com/profile_images/520273796549189632/d1et-xaU_400x400.png\"]}"
                )));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        */
        try {
            recipeList = new ArrayList<>();
            recipeList.add(new Recipe(new JSONObject(
                    "{\"recipeName\":\"Error: Unable to access API\"," +
                            "\"rating\":0," +
                            "\"smallimageUrls\":[\"https://pbs.twimg.com/profile_images/520273796549189632/d1et-xaU_400x400.png\"]}"
            )));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        spinner.setVisibility(View.GONE);
        ra = new RecipeAdapter(this.getContext(), recipeList);
        rv.setAdapter(ra);
    }

    // Accessor for list filtering from main activity search
    @Override
    public void doFilter(String input) {
        ra.getFilter().filter(input);
    }

    @Override
    public String getType() {
        return "Recipe";
    }

    // Sets mode; 0 for Top Pics and 1 for Favorites
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
                whiler = new Integer(params[1]);
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

        // publishes results to the RecyclerView after search ends
        @Override
        protected void onPostExecute(ArrayList<Recipe> result) {
            super.onPostExecute(result);
            populateList(result, whiler);
        }
    }

}