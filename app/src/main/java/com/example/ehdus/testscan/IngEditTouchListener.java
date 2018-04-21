package com.example.ehdus.testscan;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.scandit.barcodepicker.BarcodePicker;

class IngEditTouchListener implements RecyclerView.OnItemTouchListener {

    private ClickListener mClickListener;
    private GestureDetector mGestureDetector;
    private boolean clickable = true;
    private BarcodePicker mScanner;
    private Editor editor;

    IngEditTouchListener(final Context context, RecyclerView rv, final IngredientAdapter a, final ConstraintLayout rootView) {
        this(context, rv, a, rootView, null);
    }

    IngEditTouchListener(final Context context, final RecyclerView rv, final IngredientAdapter a, final ConstraintLayout rootView, BarcodePicker scanner) {

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
                    if (editor == null)
                        editor = new Editor(context, rootView, a, IngEditTouchListener.this);
                    editor.setVisible();

                    editor.setButtons(position);
                    editor.setStrings(position);
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

    public void cleanup() {
        clickable = true;
        if (mScanner != null)
            mScanner.startScanning();
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public void onBackPressed() {
        this.cleanup();
    }
}
