package com.example.ehdus.testscan;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

// Maps HashMap of ingredients into ViewHolders that the ExpandableListView can display
//  Also implements filtering features for search
class PantryAdapter extends BaseExpandableListAdapter implements Filterable {

    private final Context mContext;
    private final String[] mGroups;
    private final HashMap<String, ArrayList<Ingredient>> mIngredients;
    private final HashMap<String, ArrayList<Ingredient>> mFilteredIngredients;
    private IngredientFilter mFilter;

    PantryAdapter(Context context, String[] groups,
                  HashMap<String, ArrayList<Ingredient>> ingredients) {
        mContext = context;
        mGroups = groups;
        mIngredients = ingredients;
        mFilteredIngredients = new HashMap<>();
    }

    @Override
    public Object getChild(int gPos, int cPos) {
        return getIngredient(gPos, cPos);
    }

    @Override
    public long getChildId(int gPos, int cPos) {
        return cPos;
    }

    // Populate the contents of a child view (invoked by the layout manager)
    @Override
    public View getChildView(int gPos, final int cPos,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (inflater != null) {
                convertView = inflater.inflate(R.layout.pantry_item, parent, false);
            }
        }

        TextView name = Objects.requireNonNull(convertView)
                .findViewById(R.id.p_name);

        TextView desc = convertView
                .findViewById(R.id.p_desc);

        ImageView pic = convertView
                .findViewById(R.id.p_pic);

        Ingredient i = getIngredient(gPos, cPos);

        name.setText(i.getName());
        desc.setText(i.getDesc());
        pic.setImageResource(i.getPic());

        return convertView;
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getChildrenCount(int gPos) {
        return this.mIngredients.get(this.mGroups[gPos])
                .size();
    }

    @Override
    public Object getGroup(int gPos) {
        return this.mGroups[gPos];
    }

    @Override
    public int getGroupCount() {
        return this.mGroups.length;
    }

    @Override
    public long getGroupId(int gPos) {
        return gPos;
    }

    // Populate the contents of a category view (invoked by the layout manager)
    @Override
    public View getGroupView(int gPos, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(gPos);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (inflater != null) {
                convertView = inflater.inflate(R.layout.pantry_group, parent, false);
            }
        }

        TextView lblListHeader = Objects.requireNonNull(convertView)
                .findViewById(R.id.p_group);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int gPos, int cPos) {
        return true;
    }

    private Ingredient getIngredient(int gPos, int cPos) {
        return mIngredients.get(mGroups[gPos]).get(cPos);
    }

    // Creates a search filter and clones recipe list for non-destructive filtering
    @Override
    public Filter getFilter() {

        if (mFilter == null) {
            mFilteredIngredients.clear();
            mFilteredIngredients.putAll(this.mIngredients);
            mFilter = new IngredientFilter(this, mGroups, mFilteredIngredients);
        }
        return mFilter;

    }

    private static class IngredientFilter extends Filter {

        private final PantryAdapter pantryAdapter;
        private final String[] originalTypes;
        private final HashMap<String, ArrayList<Ingredient>> originalList;
        private final HashMap<String, ArrayList<Ingredient>> filteredList;

        private IngredientFilter(PantryAdapter p,
                                 String[] originalTypes,
                                 HashMap<String, ArrayList<Ingredient>> originalList) {
            this.pantryAdapter = p;
            this.originalTypes = originalTypes;
            this.originalList = originalList;
            this.filteredList = new HashMap<>();
        }

        // Makes a filtered list of ingredients based on a string sent from the search bar
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            filteredList.clear();
            final FilterResults results = new FilterResults();
            if (charSequence.length() == 0) {
                filteredList.putAll(originalList);
            } else {
                //TODO: make search open all dropdowns
                final String filterPattern = charSequence.toString().toLowerCase().trim();
                for (String type : originalTypes) {
                    filteredList.put(type, new ArrayList<Ingredient>());
                    for (Ingredient ing : originalList.get(type)) {
                        if (ing.getName().toLowerCase().contains(filterPattern)) {
                            filteredList.get(type).add(ing);
                        }
                    }
                }
            }

            results.values = filteredList;
            results.count = filteredList.size();
            return results;

        }

        // Publishes filtered results and refreshes ExpandableListView
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

            pantryAdapter.mIngredients.clear();
            pantryAdapter.mIngredients.putAll((HashMap<String, ArrayList<Ingredient>>) filterResults.values);
            pantryAdapter.notifyDataSetChanged();

        }
    }
}