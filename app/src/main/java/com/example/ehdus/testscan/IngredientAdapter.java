package com.example.ehdus.testscan;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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

        store();
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
