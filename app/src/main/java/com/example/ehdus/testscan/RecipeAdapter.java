package com.example.ehdus.testscan;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    private static ArrayList<Recipe> mRecipes;
    private final LayoutInflater mInflater;

    RecipeAdapter(Context fragment, ArrayList<Recipe> recipes) {
        mRecipes = recipes;
        mInflater = LayoutInflater.from(fragment);
    }

    // Create new views (invoked by the layout manager)
    @Override
    @NonNull
    public RecipeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                       int viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.recipe_item, parent, false));
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull RecipeAdapter.ViewHolder holder, int pos) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Recipe r = mRecipes.get(pos);
        holder.mName.setText(r.getName());
        holder.mDesc.setText(r.getDesc());
        holder.mPic.setImageDrawable(r.getPic());

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mRecipes.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView mName, mDesc;
        private final ImageView mPic;

        private ViewHolder(View itemView) {
            super(itemView);
            mName = itemView.findViewById(R.id.r_name);
            mDesc = itemView.findViewById(R.id.r_desc);
            mPic = itemView.findViewById(R.id.r_pic);
        }
    }
}
