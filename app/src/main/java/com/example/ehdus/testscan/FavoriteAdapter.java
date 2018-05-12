package com.example.ehdus.testscan;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.scandit.barcodepicker.BarcodePicker;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class FavoriteAdapter extends FilterAdapter<Recipe>{

    ArrayList<JsonObject> favoriteList = new ArrayList<>();

    private Context mContext;
    private BarcodePicker mScanner;
    private ConstraintLayout mRootView;


    FavoriteAdapter(Context context, ConstraintLayout rootView) {
        this(context, rootView, null);
//        add(new Recipe(this, "TEST RECIPE", 0));
    }

    FavoriteAdapter(Context context, ConstraintLayout rootView, BarcodePicker scanner) {
        super(context);
        mContext = context;
        mRootView = rootView;
        mScanner = scanner;
    }

    @Override
    public void add(Recipe i) {
        super.add(i);
//        sendQuery();
    }

    @Override
    public void remove(int pos) {
        super.remove(pos);
//        sendQuery();
    }

    public void setFields(int position, String name, int rating) {
        Recipe i = get(position);
        i.setFields(name, rating);
        notifyItemChanged(position);
//        sendQuery();
    }

//    private void sendQuery() {
//        Set<String> query = new HashSet<>();
//        if (getList() == null) {
//            Recipe i = new Recipe(this, "TEST RECIPE", 0);
//            query.addAll(new ArrayList<String>());
//        } else {
////            for (Recipe i : getList())
////                query.addAll(new ArrayList<String>());
//        }
//            if (mContext instanceof FavoriteViewFragment.QuerySetter)
//                ((FavoriteViewFragment.QuerySetter) mContext).queryListener(query);


//    }
    // Create new views (invoked by the layout manager)
    @Override
    @NonNull
    public FavoriteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                           int viewType) {
        return new FavoriteAdapter.ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.favorite_item, parent, false));
    }

    // Populate the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull final FilterAdapter.CustomViewHolder vh, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final Recipe r = super.get(position);
        View[] views = vh.getViews();
        ((TextView) views[0]).setText(r.getName());
//        ((TextView) views[1]).setText(r.getRating());
        ((ImageView) views[2]).setImageDrawable(r.getPic());
        ImageButton delete = (ImageButton) views[3];
        ImageButton share = (ImageButton) views[4];

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int curPos = vh.getAdapterPosition();
                remove(curPos);
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, r.getSourceUrl());
                mContext.startActivity(intent);

            }
        });

    }

    // Contains a set of views so the RecyclerView knows how to map input to display
    class ViewHolder extends FilterAdapter.CustomViewHolder {
        private final TextView mName, mRating;
        private final ImageView mPic;
        private final ImageButton mDelete, mShare;

        private ViewHolder(View itemView) {
            super(itemView);
            mName = itemView.findViewById(R.id.name);
            mRating = itemView.findViewById(R.id.desc);
            mPic = itemView.findViewById(R.id.pic);
            mDelete = itemView.findViewById(R.id.btnDelete);
            mShare = itemView.findViewById(R.id.btnShare);
        }

        @Override
        View[] getViews() {
            return new View[]{mName, mRating, mPic, mDelete, mShare};
        }
    }
}
