package com.example.ehdus.testscan;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListOfNewIngredients extends AppCompatActivity {

    private ArrayList<Ingredient> ingredients = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        final String barcodeReceived;
        boolean DEV = false;

        if (bundle != null)
            barcodeReceived = bundle.getString("barcode");
        else if (DEV == true)
            barcodeReceived = "031155205002,885909950805";
        else
            barcodeReceived = "Error";

        System.out.println(barcodeReceived);

        String barcodeString = "031155205002,885909950805,";
        barcodeString = barcodeString.substring(0, barcodeString.length() - 1);



        List<String> barcodelist = new ArrayList<String>(Arrays.asList(barcodeString.split(",")));

        for(int i = 0; i < barcodelist.size(); i++){
            String URL = "https://api.upcitemdb.com/prod/trial/lookup?upc=" + barcodelist.get(i);

            new barcodeImport().execute(URL);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    //TODO: ingredient import from barcodes
    private ArrayList<Ingredient> barcodeImport() {

        ArrayList<Ingredient> newIngredients = new ArrayList<>();

        for (int i = 1; i <= 10; i++) {
            newIngredients.add(new Ingredient("New ingredient " + i, "This is ingredient #" + i + "'s default description, which can be multiline and very long!  It should wrap, but not indefinitely.", R.drawable.temp));
        }
        return newIngredients;
    }


    private class barcodeImport extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String...params){
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect(); //connects to server and returns data as input stream

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));


                StringBuffer buffer = new StringBuffer();

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                ingredients = new ArrayList<>();
                String finalJson = buffer.toString();

                JSONObject parentObject = new JSONObject(finalJson);
                JSONArray parentArray = parentObject.getJSONArray("items");

                Log.d("Reponse", parentArray.toString());
                return parentArray.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e){
                e.printStackTrace();
            }
            finally {

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
        protected void onPostExecute (String result){
            super.onPostExecute(result);

        }
    }


}