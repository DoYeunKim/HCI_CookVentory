package com.example.ehdus.testscan;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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
import java.util.*;

public class ListOfBarcodes extends AppCompatActivity {

    private ArrayList<Ingredient> ingredients = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        final String barcodeReceived;

        if (bundle != null)
            barcodeReceived = bundle.getString("barcode");
        else
            barcodeReceived = "Error";

        setContentView(R.layout.activity_list_of_barcodes);

        RecyclerView rv = findViewById(R.id.barcode_list);

        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new BarcodeAdapter(barcodeImport()));

        System.out.println(barcodeReceived);

        String barcodeString = "031155205002,885909950805,";
        barcodeString = barcodeString.substring(0, barcodeString.length() - 1);



        List<String> barcodelist = new ArrayList<String>(Arrays.asList(barcodeString.split(",")));

        for(int i = 0; i < barcodelist.size(); i++){
            String URL = "https://api.upcitemdb.com/prod/trial/lookup?upc=" + barcodelist.get(i);

            new barcodeImport().execute(URL);
        }


//        FloatingActionButton activityConfirmButton = findViewById(R.id.showBarcode);
//        activityConfirmButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Snackbar snackbar = null;
//                if (barcodeReceived != null) {
//                    snackbar = Snackbar
//                            .make(findViewById(R.id.showBarcode), barcodeReceived, Snackbar.LENGTH_SHORT);
//                    System.out.println(barcodeReceived);
//                }
//
//                Objects.requireNonNull(snackbar).show();
//            }
//        });

        Button saveAndQuit = findViewById(R.id.backToMain);
        saveAndQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent checkMain = new Intent(ListOfBarcodes.this, MainActivity.class);
                checkMain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                checkMain.putExtra("EXIT", true);
                startActivity(checkMain);
            }
        });


    }

    @Override
    public void onBackPressed() {
        finish();
    }

    //TODO: Barcode import
    private ArrayList<Barcode> barcodeImport() {

        ArrayList<Barcode> barcodes = new ArrayList<>();

        for (int i = 1; i <= 10; i++) {
            barcodes.add(new Barcode("Barcode " + i, "This is barcode #" + i + "'s default description, which can be multiline and very long!  It should wrap, but not indefinitely.", R.drawable.temp));
        }
        return barcodes;
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

                String line = "";
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
