package com.example.ehdus.testscan;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

// Maps ArrayList of recipes into ViewHolders that the RecyclerView can display
//  Also implements filtering features for search
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> implements Filterable {

    private ArrayList<Recipe> mRecipes;
    private ArrayList<Recipe> mFilteredRecipes;
    private RecipeFilter mFilter;
    private final LayoutInflater mInflater;

    RecipeAdapter(Context fragment, ArrayList<Recipe> recipes) {
        mRecipes = recipes;
        mInflater = LayoutInflater.from(fragment);
        mFilteredRecipes = new ArrayList<>();
    }

    // Create new views (invoked by the layout manager)
    @Override
    @NonNull
    public RecipeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                       int viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.recipe_item, parent, false));
    }

    // Populate the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull RecipeAdapter.ViewHolder holder, int pos) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Recipe r = mRecipes.get(pos);
        holder.mName.setText(r.getName());
        holder.mDesc.setText(Integer.toString(r.getRating()));
        holder.mPic.setImageDrawable(r.getPic());

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mRecipes.size();
    }

    // Contains a set of views so the RecyclerView knows how to map input to display
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

    // Creates a search filter and clones recipe list for non-destructive filtering
    @Override
    public Filter getFilter() {

        if (mFilter == null) {
            mFilteredRecipes.clear();
            mFilteredRecipes.addAll(this.mRecipes);
            mFilter = new RecipeAdapter.RecipeFilter(this, mFilteredRecipes);
        }
        return mFilter;

    }

    private static class RecipeFilter extends Filter {

        private final RecipeAdapter recipeAdapter;
        private final ArrayList<Recipe> originalList;
        private final ArrayList<Recipe> filteredList;

        private RecipeFilter(RecipeAdapter RecipeAdapter, ArrayList<Recipe> originalList) {
            this.recipeAdapter = RecipeAdapter;
            this.originalList = originalList;
            this.filteredList = new ArrayList();
        }

        // Makes a filtered list of recipes based on a string sent from the search bar
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            filteredList.clear();
            final FilterResults results = new FilterResults();
            if (charSequence.length() == 0) {
                filteredList.addAll(originalList);
            } else {
                final String filterPattern = charSequence.toString().toLowerCase().trim();
                for (Recipe recipe : originalList) {
                    if (recipe.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(recipe);
                    }
                }
            }

            results.values = filteredList;
            results.count = filteredList.size();
            return results;

        }

        // Publishes filtered results and refreshes RecyclerView
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

            recipeAdapter.mRecipes.clear();
            recipeAdapter.mRecipes.addAll((ArrayList<Recipe>) filterResults.values);
            recipeAdapter.notifyDataSetChanged();

        }
    }
}
