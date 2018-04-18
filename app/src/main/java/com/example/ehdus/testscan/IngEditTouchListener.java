package com.example.ehdus.testscan;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.scandit.barcodepicker.BarcodePicker;

class IngEditTouchListener implements RecyclerView.OnItemTouchListener {

    private ClickListener mClickListener;
    private GestureDetector mGestureDetector;
    private View edit;

    IngEditTouchListener(final Context context, final RecyclerView rv, final FilterAdapter a, final ConstraintLayout rootView) {

        this.mClickListener = new IngEditTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                // We don't want this to do anything
            }

            @Override
            public void onLongClick(View view, int position) {
                inflateLayout(context, a, rootView, position);
                Button button = edit.findViewById(R.id.ing_button);
                button.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        edit.setVisibility(View.GONE);
                    }
                });
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

    IngEditTouchListener(final Context context, final RecyclerView rv, final FilterAdapter a, final ConstraintLayout rootView, final BarcodePicker scanner) {
        this(context, rv, a, rootView);

        this.mClickListener = new IngEditTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                // We don't want this to do anything
            }

            @Override
            public void onLongClick(View view, int position) {
                inflateLayout(context, a, rootView, position);
                Button button = edit.findViewById(R.id.ing_button);
                button.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        edit.setVisibility(View.GONE);
                        scanner.startScanning();
                    }
                });

                scanner.stopScanning();
            }
        };
    }

    private void inflateLayout(Context context, FilterAdapter a, ConstraintLayout rootView, int position) {
        LayoutInflater inflater = LayoutInflater.from(context);
        edit = inflater.inflate(R.layout.ingredient_edit, null, false);
        TextView t = edit.findViewById(R.id.ing_title);
        t.setText("Pressed item with name " + a.get(position).getName());
        rootView.addView(edit, new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT));

        ConstraintSet cs = new ConstraintSet();
        cs.clone(rootView);
        cs.connect(edit.getId(), ConstraintSet.BOTTOM, rootView.getId(), ConstraintSet.BOTTOM);
        cs.connect(edit.getId(), ConstraintSet.TOP, rootView.getId(), ConstraintSet.TOP);
        cs.connect(edit.getId(), ConstraintSet.RIGHT, rootView.getId(), ConstraintSet.RIGHT);
        cs.connect(edit.getId(), ConstraintSet.LEFT, rootView.getId(), ConstraintSet.LEFT);

        cs.applyTo(rootView);
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
