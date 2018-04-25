package com.example.ehdus.testscan;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.scandit.barcodepicker.BarcodePicker;

class Editor {

    private EditText tName, tDesc;
    private View mEdit;
    private BarcodePicker mScanner;

    Editor(Context context, ConstraintLayout rootView, final IngredientAdapter a, BarcodePicker scanner, final int position) {
        mScanner = scanner;
        LayoutInflater inflater = LayoutInflater.from(context);
        mEdit = inflater.inflate(R.layout.ingredient_edit, null, false);

        // Setup text edit views
        // TODO: make tName and tDesc grey/otherwise visually indicated that they're unchanged until they're edited?
        tName = mEdit.findViewById(R.id.editName);
        tDesc = mEdit.findViewById(R.id.editDesc);
        tName.setText(a.get(position).getName());
        tDesc.setText(a.get(position).getDesc());

        // Setup buttons
        mEdit.findViewById(R.id.done).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                a.setFields(position, tName.getText().toString(), tDesc.getText().toString());
                cleanup();
            }
        });

        mEdit.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                cleanup();
            }
        });

        rootView.addView(mEdit, new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        ConstraintSet cs = new ConstraintSet();
        cs.clone(rootView);
        cs.connect(mEdit.getId(), ConstraintSet.BOTTOM, rootView.getId(), ConstraintSet.BOTTOM);
        cs.connect(mEdit.getId(), ConstraintSet.TOP, rootView.getId(), ConstraintSet.TOP);
        cs.connect(mEdit.getId(), ConstraintSet.RIGHT, rootView.getId(), ConstraintSet.RIGHT);
        cs.connect(mEdit.getId(), ConstraintSet.LEFT, rootView.getId(), ConstraintSet.LEFT);

        cs.applyTo(rootView);

        mEdit.setVisibility(View.VISIBLE);
    }

    private void cleanup() {
        tName.clearFocus();
        tDesc.clearFocus();
        InputMethodManager imm = (InputMethodManager) mEdit.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEdit.getWindowToken(), 0);
        mEdit.setVisibility(View.GONE);
        if (mScanner != null)
            mScanner.startScanning();
        // TODO: any upper-level cleanup! Clickability?
    }
}
