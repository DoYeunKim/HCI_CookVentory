package com.example.ehdus.testscan;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

class YumlySearch {


    private RequestQueue requestQueue;
    private String data;



    public YumlySearch(Context context) {
        this.requestQueue = Volley.newRequestQueue(context);
    }

    public void query() {
        String URL = "http://api.yummly.com/v1/api/recipes?_app_id=c69b9d36&_app_key=03634fefafae018b30371ba8d00ec23f&q=onion+soup";

        // constructing the actual request
        JsonObjectRequest objectRequest = new JsonObjectRequest(
                Request.Method.GET, URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            ArrayList<Recipe> recipeList = new ArrayList<>();

                            Gson gson = new Gson();

                            JSONArray matches = response.getJSONArray("matches");

                            System.out.println(matches.length());

                            for(int i = 0; i < matches.length(); i++){
                                JSONObject entry = matches.getJSONObject(i);

                                Recipe recipe = new Recipe(entry.getString("recipeName"));

                                recipeList.add(recipe);


                            }
                            data = gson.toJson(recipeList);
                            System.out.println(data);


                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }


        );

        requestQueue.add(objectRequest);

    }

    public String getData(){
        return data;
    }


}
