package com.java.cuiyikai.adapters.viewholders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.java.cuiyikai.R;

/**
 * {@link RecyclerView.ViewHolder} for the title bar in {@link RecyclerView} in {@link com.java.cuiyikai.activities.CategoryActivity}
 */
public class CategoryTitleHolder extends RecyclerView.ViewHolder {

    private final TextView textView;

    public CategoryTitleHolder(@NonNull View itemView) {
        super(itemView);
        textView = itemView.findViewById(R.id.channel_title);
    }

    public TextView getTextView() {
        return textView;
    }


}
