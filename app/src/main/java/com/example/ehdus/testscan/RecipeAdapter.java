package com.example.ehdus.testscan;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

// Maps ArrayList of recipes into ViewHolders that the RecyclerView can display
//  Also implements filtering features for search
public class RecipeAdapter extends FilterAdapter<Recipe> {

    RecipeAdapter() {
        super();
    }

    // Create new views (invoked by the layout manager)
    @Override
    @NonNull
    public RecipeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                       int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_item, parent, false));
    }

    // Populate the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull FilterAdapter.CustomViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Recipe r = super.get(position);
        View[] views = holder.getViews();
        ((TextView) views[0]).setText(r.getName());
        ((TextView) views[1]).setText(String.format("%d", r.getRating()));
        ((ImageView) views[2]).setImageDrawable(r.getPic());
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
