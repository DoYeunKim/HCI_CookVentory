package com.example.ehdus.testscan;

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

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.ViewHolder> implements Filterable {

    private ArrayList<Ingredient> mIngredients;
    private ArrayList<Ingredient> mFilteredIngredients;
    private IngredientFilter mFilter;

    IngredientAdapter() {
        mIngredients = new ArrayList<>();
        mFilteredIngredients = new ArrayList<>();
    }

    public void add(Ingredient i) {
        mIngredients.add(i);
        this.notifyDataSetChanged();
    }

    // Create new views (invoked by the layout manager)
    @Override
    @NonNull
    public IngredientAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                           int viewType) {
        return new IngredientAdapter.ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ingredient_item, parent, false));
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull IngredientAdapter.ViewHolder holder, int pos) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Ingredient i = mIngredients.get(pos);
        if (i != null) {
            holder.mName.setText(i.getName());
            holder.mDesc.setText(i.getDesc());
            holder.mPic.setImageDrawable(i.getPic());
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


    // Creates a search filter and clones recipe list for non-destructive filtering
    @Override
    public Filter getFilter() {

        if (mFilter == null) {
            mFilteredIngredients.clear();
            mFilteredIngredients.addAll(this.mIngredients);
            mFilter = new IngredientAdapter.IngredientFilter(this, mFilteredIngredients);
        }
        return mFilter;

    }

    private static class IngredientFilter extends Filter {

        private final IngredientAdapter ingredientAdapter;
        private final ArrayList<Ingredient> originalList;
        private final ArrayList<Ingredient> filteredList;

        private IngredientFilter(IngredientAdapter IngredientAdapter, ArrayList<Ingredient> originalList) {
            this.ingredientAdapter = IngredientAdapter;
            this.originalList = originalList;
            this.filteredList = new ArrayList();
        }

        // Makes a filtered list of ingredients based on a string sent from the search bar
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            filteredList.clear();
            final FilterResults results = new FilterResults();
            if (charSequence.length() == 0) {
                filteredList.addAll(originalList);
            } else {
                final String filterPattern = charSequence.toString().toLowerCase().trim();
                for (Ingredient ingredient : originalList) {
                    if (ingredient.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(ingredient);
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

            ingredientAdapter.mIngredients.clear();
            ingredientAdapter.mIngredients.addAll((ArrayList<Ingredient>) filterResults.values);
            ingredientAdapter.notifyDataSetChanged();

        }
    }
}
