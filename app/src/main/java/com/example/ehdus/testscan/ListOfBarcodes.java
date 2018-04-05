package com.example.ehdus.testscan;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ListOfBarcodes extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        final String barcodeReceived = bundle.getString("barcode");

        setContentView(R.layout.activity_list_of_barcodes);

        FloatingActionButton activityConfirmButton = findViewById(R.id.showBarcode);
        activityConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar snackbar = Snackbar
                        .make(findViewById(R.id.showBarcode), barcodeReceived, Snackbar.LENGTH_SHORT);

                snackbar.show();
            }
        });

        Button saveAndQuit = (Button)findViewById(R.id.backToMain);
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

}
