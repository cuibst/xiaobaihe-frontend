package com.java.cuiyikai.adapters.viewholders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.java.cuiyikai.R;

public class ItemViewHolder extends RecyclerView.ViewHolder {
    private final TextView labelTextView;
    private final TextView categoryTextView;
    private final View searchLine;
    private final ImageView img;

    public TextView getLabelTextView() {
        return labelTextView;
    }

    public TextView getCategoryTextView() {
        return categoryTextView;
    }

    public View getSearchLine() {
        return searchLine;
    }

    public ImageView getImg() {
        return img;
    }

    public ItemViewHolder(View view) {
        super(view);
        labelTextView = view.findViewById(R.id.label);
        img = view.findViewById(R.id.img);
        categoryTextView = view.findViewById(R.id.category);
        searchLine = view.findViewById(R.id.search_line);
    }
}
