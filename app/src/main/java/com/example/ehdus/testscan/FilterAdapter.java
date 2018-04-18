package com.example.ehdus.testscan;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Filter;
import android.widget.Filterable;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

// Parent class for adapters; allows filtering to happen in one location
abstract class FilterAdapter<T extends FilterableObject> extends RecyclerView.Adapter<FilterAdapter.CustomViewHolder> implements Filterable {
    private final ArrayList<T> mItems;
    private final ArrayList<T> mFilteredItems;
    SharedPreferences mSP;
    private Filter f;

    FilterAdapter(Context context) {
        mItems = new ArrayList<>();
        mFilteredItems = new ArrayList<>();
        mSP = context.getSharedPreferences("saved", MODE_PRIVATE);
    }

    public void add(T item) {
        mItems.add(item);
        this.notifyDataSetChanged();
    }

    public void remove(int i) {
        mItems.remove(i);
        this.notifyItemRemoved(i);
    }

    public T get(int i) {
        return mItems.get(i);
    }

    public void clear() {
        mItems.clear();
        this.notifyDataSetChanged();
    }

    public ArrayList<T> getList() {
        return mItems;
    }

    void store() {
        SharedPreferences.Editor editor = mSP.edit();

        JSONArray itemArray = new JSONArray();

        for (T item : this.getList())
            itemArray.put(item.write());

        editor.putString("stored", itemArray.toString());
        editor.apply();
    }

    ArrayList<String> retrieveStored(ArrayList<String> items) {
        String stringJSON = mSP.getString("stored", null);

        if (items == null)
            items = new ArrayList<>();

        if (stringJSON == null)
            return items;

        try {
            JSONArray itemArray = new JSONArray(stringJSON);
            for (int i = 0; i < itemArray.length(); i++)
                items.add(itemArray.get(i).toString());
        } catch (JSONException e) {
            // TODO: smarter exceptions
        }

        return items;
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

        private View foreground;

        CustomViewHolder(View itemView) {
            super(itemView);
            foreground = itemView.findViewById(R.id.foreground);
        }

        public View getForeground() {
            return foreground;
        }

        abstract View[] getViews();
    }
}
