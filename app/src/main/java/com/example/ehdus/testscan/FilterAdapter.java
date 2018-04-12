package com.example.ehdus.testscan;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;

// Parent class for adapters; allows filtering to happen in one location
abstract class FilterAdapter<T extends FilterableObject> extends RecyclerView.Adapter<FilterAdapter.CustomViewHolder> implements Filterable {
    private Filter f;
    final ArrayList<T> mItems;
    private final ArrayList<T> mFilteredItems;

    FilterAdapter() {
        mItems = new ArrayList<>();
        mFilteredItems = new ArrayList<>();
    }

    public void add(T item) {
        mItems.add(item);
        this.notifyDataSetChanged();
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public Filter getFilter() {
        if (f == null) {
            mFilteredItems.clear();
            mFilteredItems.addAll(mItems);
            f = new FilterAdapter.customFilter(this, mFilteredItems);
        }
        return f;
    }

    private class customFilter extends Filter {

        private final FilterAdapter adapter;
        private final ArrayList<T> originalList;
        private final ArrayList<T> filteredList;

        private customFilter(FilterAdapter adapter, ArrayList<T> originalList) {
            this.adapter = adapter;
            this.originalList = originalList;
            this.filteredList = new ArrayList();
        }

        // Makes a filtered list of type based on a string sent from the search bar
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            filteredList.clear();
            final FilterResults results = new FilterResults();
            if (charSequence.length() == 0) {
                filteredList.addAll(originalList);
            } else {
                final String filterPattern = charSequence.toString().toLowerCase().trim();
                for (T object : originalList) {
                    if (object.getFilterable().toLowerCase().contains(filterPattern)) {
                        filteredList.add(object);
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

            adapter.mItems.clear();
            adapter.mItems.addAll((ArrayList<T>) filterResults.values);
            adapter.notifyDataSetChanged();

        }
    }

    abstract class CustomViewHolder extends RecyclerView.ViewHolder {
        CustomViewHolder(View itemView) {
            super(itemView);
        }

        abstract View[] getViews();
    }
}
