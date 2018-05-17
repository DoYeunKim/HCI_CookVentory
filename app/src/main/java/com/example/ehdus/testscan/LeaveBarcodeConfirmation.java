package com.example.ehdus.testscan;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import java.util.ArrayList;

class LeaveBarcodeConfirmation {

    LeaveBarcodeConfirmation(final Context context, ConstraintLayout rootView, final IngredientAdapter a, final boolean DEV) {

        if (a.getList().isEmpty()) {
            leave(context, DEV);
            return;
        }

        LayoutInflater inflater = LayoutInflater.from(context);
        View confirm = inflater.inflate(R.layout.confirm_scan, null, false);

        rootView.addView(confirm, new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        ConstraintSet cs = new ConstraintSet();
        cs.clone(rootView);
        cs.connect(confirm.getId(), ConstraintSet.BOTTOM, rootView.getId(), ConstraintSet.BOTTOM);
        cs.connect(confirm.getId(), ConstraintSet.TOP, rootView.getId(), ConstraintSet.TOP);
        cs.connect(confirm.getId(), ConstraintSet.RIGHT, rootView.getId(), ConstraintSet.RIGHT);
        cs.connect(confirm.getId(), ConstraintSet.LEFT, rootView.getId(), ConstraintSet.LEFT);

        cs.applyTo(rootView);

        ImageButton confirmSave = ((Activity) context).findViewById(R.id.confirm_save);
        confirmSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> ingredientStrings = new ArrayList<>();
                for (Ingredient i : a.getList())
                    ingredientStrings.add(i.write());
                leave(context, DEV, ingredientStrings);
            }
        });

        ImageButton cancelSave = ((Activity) context).findViewById(R.id.cancel_save);
        cancelSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leave(context, DEV);
            }
        });
    }

    private void leave(Context context, boolean DEV, ArrayList<String> ingredientStrings) {
        Intent checkMain = new Intent(context, MainActivity.class);
        checkMain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (ingredientStrings != null)
            checkMain.putStringArrayListExtra("ingredients", ingredientStrings);
        checkMain.putExtra("DEV", DEV);
        context.startActivity(checkMain);
    }

    private void leave(Context context, boolean DEV) {
        this.leave(context, DEV, null);
    }
}
