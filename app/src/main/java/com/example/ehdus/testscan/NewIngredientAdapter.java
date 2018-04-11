package com.example.ehdus.testscan;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class NewIngredientAdapter extends RecyclerView.Adapter<NewIngredientAdapter.ViewHolder> {

    private static ArrayList<Ingredient> mIngredients;

    NewIngredientAdapter() {
        mIngredients = new ArrayList<>();
    }

    public void add(Ingredient i) {
        mIngredients.add(i);
        this.notifyDataSetChanged();
    }

    // Create new views (invoked by the layout manager)
    @Override
    @NonNull
    public NewIngredientAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                              int viewType) {
        return new NewIngredientAdapter.ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pantry_item, parent, false));
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull NewIngredientAdapter.ViewHolder holder, int pos) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Ingredient i = mIngredients.get(pos);
        if (i != null) {
            holder.mName.setText(i.getName());
            holder.mDesc.setText(i.getDesc());
            holder.mPic.setImageDrawable(i.getPic());
        } else {
            holder.mName.setText(R.string.scan);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mIngredients.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView mName, mDesc;
        private final ImageView mPic;

        private ViewHolder(View itemView) {
            super(itemView);
            mName = itemView.findViewById(R.id.p_name);
            mDesc = itemView.findViewById(R.id.p_desc);
            mPic = itemView.findViewById(R.id.p_pic);
        }
    }
}
