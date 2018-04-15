package com.example.ehdus.testscan;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

public class SwipeCallback extends ItemTouchHelper.SimpleCallback {

    FilterAdapter a;

    SwipeCallback(FilterAdapter a) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.a = a;
    }

    @Override
    public boolean onMove(RecyclerView rv, RecyclerView.ViewHolder vh, RecyclerView.ViewHolder target) {
        // we don't want this to do anything, so return false
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder vh, int direction) {
        a.remove(vh.getAdapterPosition());
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView,
                            RecyclerView.ViewHolder viewHolder, float dX, float dY,
                            int actionState, boolean isCurrentlyActive) {
        final View foregroundView = ((FilterAdapter.CustomViewHolder) viewHolder).getForeground();

        getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY,
                actionState, isCurrentlyActive);
    }

    @Override
    public void onChildDrawOver(Canvas c, RecyclerView recyclerView,
                                RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                int actionState, boolean isCurrentlyActive) {
        final View foregroundView = ((FilterAdapter.CustomViewHolder) viewHolder).getForeground();
        getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY,
                actionState, isCurrentlyActive);
    }
}
