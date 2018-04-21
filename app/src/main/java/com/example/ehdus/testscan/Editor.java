package com.example.ehdus.testscan;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.util.ArrayList;

class Editor {

    private EditText tName, tDesc, tQuery;
    private View edit;
    private IngredientAdapter a;
    private IngEditTouchListener mTouchListener;

    Editor(Context context, ConstraintLayout rootView, IngredientAdapter a, IngEditTouchListener touchListener) {
        this.a = a;
        this.mTouchListener = touchListener;
        LayoutInflater inflater = LayoutInflater.from(context);
        edit = inflater.inflate(R.layout.ingredient_edit, null, false);
        tName = edit.findViewById(R.id.editName);
        tDesc = edit.findViewById(R.id.editDesc);
        tQuery = edit.findViewById(R.id.editQuery);

        rootView.addView(edit, new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        ConstraintSet cs = new ConstraintSet();
        cs.clone(rootView);
        cs.connect(edit.getId(), ConstraintSet.BOTTOM, rootView.getId(), ConstraintSet.BOTTOM);
        cs.connect(edit.getId(), ConstraintSet.TOP, rootView.getId(), ConstraintSet.TOP);
        cs.connect(edit.getId(), ConstraintSet.RIGHT, rootView.getId(), ConstraintSet.RIGHT);
        cs.connect(edit.getId(), ConstraintSet.LEFT, rootView.getId(), ConstraintSet.LEFT);

        cs.applyTo(rootView);
    }

    public void setVisible() {
        edit.setVisibility(View.VISIBLE);
    }

    public void setButtons(final int position) {
        edit.findViewById(R.id.done).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ArrayList<String> out = new ArrayList<>();
                out.add(tQuery.getText().toString());
                a.setFields(position, tName.getText().toString(), tDesc.getText().toString(), out);
                cleanup();
            }
        });

        edit.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                cleanup();
            }
        });

        edit.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                a.remove(position);
                cleanup();
            }
        });
    }

    private void cleanup() {
        tName.clearFocus();
        tDesc.clearFocus();
        tQuery.clearFocus();
        InputMethodManager imm = (InputMethodManager) edit.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edit.getWindowToken(), 0);
        edit.setVisibility(View.GONE);
        mTouchListener.cleanup();
    }

    public void setStrings(int position) {
        tName.setText(a.get(position).getName());
        tDesc.setText(a.get(position).getDesc());
        tQuery.setText(a.get(position).getQuery().get(0));
    }
}
