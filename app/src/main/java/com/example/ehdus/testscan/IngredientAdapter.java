package com.example.ehdus.testscan;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.scandit.barcodepicker.BarcodePicker;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class IngredientAdapter extends FilterAdapter<Ingredient> {

    private Editor mEditor;
    private Context mContext;
    private BarcodePicker mScanner;
    private ConstraintLayout mRootView;

    IngredientAdapter(Context context, ConstraintLayout rootView) {
        this(context, rootView, null);
    }

    IngredientAdapter(Context context, ConstraintLayout rootView, BarcodePicker scanner) {
        super(context);
        mContext = context;
        mRootView = rootView;
        mScanner = scanner;
        add(new Ingredient(this, context));
    }

    @Override
    public void add(Ingredient i) {
        super.add(i);
        sendQuery();
    }

    @Override
    public void remove(int pos) {
        super.remove(pos);
        sendQuery();
    }

    public void setFields(int position, String name, String desc, ArrayList<String> query) {
        Ingredient i = get(position);
        if (i.getQuery().get(0) == "ADD_FLAG")
            // TODO: get ingredient type and pic? from internet in new Ingredient constructor
            add(new Ingredient(this, name, desc));
        else
            i.setFields(name, desc, query);
        notifyItemChanged(position);
        sendQuery();
    }

    private void sendQuery() {
        Set<String> query = new HashSet<>();
        for (Ingredient i : getList())
            query.addAll(i.getQuery());
        if (mContext instanceof IngredientViewFragment.QuerySetter)
            ((IngredientViewFragment.QuerySetter) mContext).queryListener(query);
    }

    // Create new views (invoked by the layout manager)
    @Override
    @NonNull
    public IngredientAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                           int viewType) {
        ViewHolder vh = new IngredientAdapter.ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ingredient_item, parent, false));
        return vh;
    }

    // Populate the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull final FilterAdapter.CustomViewHolder vh, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Ingredient i = super.get(position);
        View[] views = vh.getViews();
        ((TextView) views[0]).setText(i.getName());
        ((TextView) views[1]).setText(i.getDesc());
        ((ImageView) views[2]).setImageDrawable(i.getPic());
        ImageButton delete = (ImageButton) views[3];
        ImageButton edit = (ImageButton) views[4];

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int curPos = vh.getAdapterPosition();
                remove(curPos);
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mEditor == null)
                    mEditor = new Editor(mContext, mRootView, IngredientAdapter.this, mScanner);
                mEditor.setVisible();

                int curPos = vh.getAdapterPosition();
                mEditor.setButtons(curPos);
                mEditor.setStrings(curPos);
                if (mScanner != null)
                    mScanner.stopScanning();
            }
        });

    }

    // Contains a set of views so the RecyclerView knows how to map input to display
    class ViewHolder extends FilterAdapter.CustomViewHolder {
        private final TextView mName, mDesc;
        private final ImageView mPic;
        private final ImageButton mDelete, mEdit;

        private ViewHolder(View itemView) {
            super(itemView);
            mName = itemView.findViewById(R.id.name);
            mDesc = itemView.findViewById(R.id.desc);
            mPic = itemView.findViewById(R.id.pic);
            mDelete = itemView.findViewById(R.id.btnDelete);
            mEdit = itemView.findViewById(R.id.btnEdit);
        }

        @Override
        View[] getViews() {
            return new View[]{mName, mDesc, mPic, mDelete, mEdit};
        }
    }
}
