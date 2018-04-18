package com.example.ehdus.testscan;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class IngredientAdapter extends FilterAdapter<Ingredient> {

    private ArrayList<String> query;
    private IngredientViewFragment.QuerySetter mQuerySetter;

    IngredientAdapter(Context context) {
        super(context);
    }

    IngredientAdapter(IngredientViewFragment.QuerySetter querySetter, Context context) {
        this(context);
        mQuerySetter = querySetter;
    }

    @Override
    public void add(Ingredient i) {
        super.add(i);
        String q = i.getQueryString();
        if (query == null)
            query = new ArrayList<>();
        if (!query.contains(q)) {
            query.add(q);
            if (mQuerySetter != null)
                mQuerySetter.queryListener(query);
        }
    }

    @Override
    public void remove(int i) {
        super.remove(i);

        storeIngredients();
    }

    void storeIngredients(){
        SharedPreferences.Editor editor = mSP.edit();

        JSONArray IngsS = new JSONArray();

        for (Ingredient indIngS: this.getList()) {
            IngsS.put(indIngS.write());
        }

        editor.putString("storedIng", IngsS.toString());
        editor.apply();
        Log.d("Stored the ingredients", "" + IngsS.toString());
    }

    ArrayList<String> retrieveStoredIng(ArrayList<String> ingredients) {
        String indIngR = mSP.getString("storedIng", null);

        if (ingredients == null) {
            ingredients = new ArrayList<>();
        }
        try {
            JSONArray IngsR = new JSONArray(indIngR);
            for (int i = 0; i < IngsR.length(); i++) {
                ingredients.add(IngsR.get(i).toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("Retrieved the stored ingredients", "" + indIngR);
        return ingredients;
    }


    // Create new views (invoked by the layout manager)
    @Override
    @NonNull
    public IngredientAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                           int viewType) {
        return new IngredientAdapter.ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ingredient_item, parent, false));
    }

    // Populate the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull FilterAdapter.CustomViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Ingredient i = super.get(position);
        View[] views = holder.getViews();
        ((TextView) views[0]).setText(i.getName());
        ((TextView) views[1]).setText(i.getDesc());
        ((ImageView) views[2]).setImageDrawable(i.getPic());
    }

    // Contains a set of views so the RecyclerView knows how to map input to display
    class ViewHolder extends FilterAdapter.CustomViewHolder {
        private final TextView mName, mDesc;
        private final ImageView mPic;

        private ViewHolder(View itemView) {
            super(itemView);
            mName = itemView.findViewById(R.id.name);
            mDesc = itemView.findViewById(R.id.desc);
            mPic = itemView.findViewById(R.id.pic);
        }

        @Override
        View[] getViews() {
            return new View[]{mName, mDesc, mPic};
        }
    }
}
