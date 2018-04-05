package com.example.ehdus.testscan;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class PantryAdapter extends BaseExpandableListAdapter {

    private Context mContext;
    private List<String> mGroups; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<Ingredient>> mIngredients;

    PantryAdapter(Context context, List<String> groups,
                  HashMap<String, List<Ingredient>> ingredients) {
        this.mContext = context;
        this.mGroups = groups;
        this.mIngredients = ingredients;
    }

    @Override
    public Object getChild(int gPos, int cPos) {
        return getIngredient(gPos, cPos);
    }

    @Override
    public long getChildId(int gPos, int cPos) {
        return cPos;
    }

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

    @Override
    public int getChildrenCount(int gPos) {
        return this.mIngredients.get(this.mGroups.get(gPos))
                .size();
    }

    @Override
    public Object getGroup(int gPos) {
        return this.mGroups.get(gPos);
    }

    @Override
    public int getGroupCount() {
        return this.mGroups.size();
    }

    @Override
    public long getGroupId(int gPos) {
        return gPos;
    }

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
        return mIngredients.get(mGroups.get(gPos)).get(cPos);
    }
}