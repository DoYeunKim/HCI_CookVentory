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

    private int mode;
    private RecyclerView rv;
    private ProgressBar spinner;
    private RecipeAdapter ra;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recipe, container, false);

        spinner = rootView.findViewById(R.id.spinner);

        String URL = "http://api.yummly.com/v1/api/recipes?_app_id=c69b9d36&_app_key=03634fefafae018b30371ba8d00ec23f&q=onion+soup";

        new recipeImport().execute(URL);

        rv = rootView.findViewById(R.id.recipe_list);
        rv.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        return rootView;
    }

    private void populateList(ArrayList<Recipe> recipeList) {
        ra = new RecipeAdapter(this.getContext(), recipeList);
        rv.setAdapter(ra);
    }

    @Override
    public void doFilter(String input) {
        ra.getFilter().filter(input);
    }

    public void setMode(int inMode) {
        mode = inMode;
    }

    private class recipeImport extends AsyncTask<String, String, ArrayList<Recipe>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            spinner.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<Recipe> doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            int visible = spinner.getVisibility();

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect(); //connects to server and returns data as input stream

                InputStream stream = connection.getInputStream();

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

        @Override
        protected void onPostExecute(ArrayList<Recipe> result) {
            super.onPostExecute(result);
            populateList(result);
            spinner.setVisibility(View.GONE);
        }
    }

}