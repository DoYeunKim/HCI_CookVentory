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
import android.widget.EditText;

import com.scandit.barcodepicker.BarcodePicker;

class IngEditTouchListener implements RecyclerView.OnItemTouchListener {

    private ClickListener mClickListener;
    private GestureDetector mGestureDetector;
    private View edit;
    private EditText tName, tDesc, tQuery;

    IngEditTouchListener(final Context context, final RecyclerView rv, final IngredientAdapter a, final ConstraintLayout rootView) {

        this.mClickListener = new IngEditTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                // We don't want this to do anything
            }

            @Override
            public void onLongClick(View view, int position) {
                if (edit == null)
                    inflateLayout(context, rootView).setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            edit.setVisibility(View.GONE);
                        }
                    });
                else
                    edit.setVisibility(View.VISIBLE);

                setStrings(a, position);
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

    IngEditTouchListener(final Context context, final RecyclerView rv, final IngredientAdapter a, final ConstraintLayout rootView, final BarcodePicker scanner) {
        this(context, rv, a, rootView);

        this.mClickListener = new IngEditTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                // We don't want this to do anything
            }

            @Override
            public void onLongClick(View view, int position) {
                if (edit == null)
                    inflateLayout(context, rootView).setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            edit.setVisibility(View.GONE);
                            scanner.startScanning();
                        }
                    });
                else
                    edit.setVisibility(View.VISIBLE);

                setStrings(a, position);
                scanner.stopScanning();
            }
        };
    }

    private Button inflateLayout(Context context, ConstraintLayout rootView) {
        LayoutInflater inflater = LayoutInflater.from(context);
        edit = inflater.inflate(R.layout.ingredient_edit, null, false);
        tName = edit.findViewById(R.id.editName);
        tDesc = edit.findViewById(R.id.editDesc);
        tQuery = edit.findViewById(R.id.editQuery);
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

        return edit.findViewById(R.id.ing_button);
    }

    private void setStrings(IngredientAdapter a, int position) {
        tName.setText(a.get(position).getName());
        tDesc.setText(a.get(position).getDesc());
        tQuery.setText(a.get(position).getQueryString());
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
