package com.example.ehdus.testscan;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

// Maps ArrayList of recipes into ViewHolders that the RecyclerView can display
//  Also implements filtering features for search
public class RecipeAdapter extends FilterAdapter<Recipe> {

    Context mContext;

    RecipeAdapter(Context context) {
        super(context);
        mContext = context;
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
        final Recipe r = super.get(position);
        View[] views = holder.getViews();
        ((TextView) views[0]).setText(r.getName());
        ((TextView) views[1]).setText(String.format("Ratings: %d", r.getRating()));
        ((ImageView) views[2]).setImageDrawable(r.getPic());

        ImageButton favorite = (ImageButton) views[3];
        ImageButton share = (ImageButton) views[4];

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TO DO: change str to recipe url
                Intent intent = new Intent(Intent.ACTION_VIEW, r.getSourceUrl());
                mContext.startActivity(intent);

            }
        });

        favorite.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                ArrayList<String> recipeString = new ArrayList<>();
                recipeString.add(r.write());

            }
        });
    }

    // Contains a set of views so the RecyclerView knows how to map input to display
    class ViewHolder extends FilterAdapter.CustomViewHolder {
        private final TextView mName, mDesc;
        private final ImageView mPic;
        private final ImageButton mFav, mShare;

        private ViewHolder(View itemView) {
            super(itemView);
            mName = itemView.findViewById(R.id.name);
            mDesc = itemView.findViewById(R.id.desc);
            mPic = itemView.findViewById(R.id.pic);
            mFav = itemView.findViewById(R.id.btnFav);
            mShare = itemView.findViewById(R.id.btnShare);
        }

        @Override
        View[] getViews() {
            return new View[]{mName, mDesc, mPic, mFav, mShare};
        }
    }
}
