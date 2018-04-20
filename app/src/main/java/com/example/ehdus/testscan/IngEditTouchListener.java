package com.example.ehdus.testscan;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.scandit.barcodepicker.BarcodePicker;

import java.util.ArrayList;

class IngEditTouchListener implements RecyclerView.OnItemTouchListener {

    private final IngredientAdapter a;
    private ClickListener mClickListener;
    private GestureDetector mGestureDetector;
    private View edit;
    private boolean clickable = true;
    private BarcodePicker mScanner;
    private EditText tName, tDesc, tQuery;

    IngEditTouchListener(final Context context, RecyclerView rv, final IngredientAdapter a, final ConstraintLayout rootView) {
        this(context, rv, a, rootView, null);
    }

    IngEditTouchListener(final Context context, final RecyclerView rv, IngredientAdapter a, final ConstraintLayout rootView, BarcodePicker scanner) {

        this.a = a;
        this.mScanner = scanner;

        this.mClickListener = new IngEditTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                // We don't want this to do anything
            }

            @Override
            public void onLongClick(View view, int position) {
                if (clickable) {
                    clickable = false;
                    if (edit == null)
                        initEditor(context, rootView);
                    else
                        edit.setVisibility(View.VISIBLE);

                    setButtons(position);
                    setStrings(position);
                    if (mScanner != null)
                        mScanner.stopScanning();
                }
            }
        };

        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                View child = rv.findChildViewUnder(e.getX(), e.getY());
                if (child != null && mClickListener != null) {
                    mClickListener.onLongClick(child, rv.getChildAdapterPosition(child));
                }
            }
        });
    }

    private void initEditor(Context context, ConstraintLayout rootView) {
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

    private void setButtons(final int position) {
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
        clickable = true;
        if (mScanner != null)
            mScanner.startScanning();
    }

    private void setStrings(int position) {
        tName.setText(a.get(position).getName());
        tDesc.setText(a.get(position).getDesc());
        tQuery.setText(a.get(position).getQuery().get(0));
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        View child = rv.findChildViewUnder(e.getX(), e.getY());
        if (child != null && mClickListener != null && mGestureDetector.onTouchEvent(e)) {
            mClickListener.onClick(child, rv.getChildAdapterPosition(child));
        }

        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }
}
