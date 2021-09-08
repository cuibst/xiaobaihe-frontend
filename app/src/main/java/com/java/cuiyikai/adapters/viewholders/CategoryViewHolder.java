package com.java.cuiyikai.adapters.viewholders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.java.cuiyikai.R;

/**
 * {@link RecyclerView.ViewHolder} for the items in {@link RecyclerView} in {@link com.java.cuiyikai.activities.CategoryActivity}
 */
public class CategoryViewHolder extends RecyclerView.ViewHolder {

    private final TextView textView;
    private final ImageView imageView;
    private final View wholeView;

    public TextView getTextView() {
        return textView;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public View getWholeView() {
        return wholeView;
    }

    public CategoryViewHolder(@NonNull View itemView) {
        super(itemView);
        textView = itemView.findViewById(R.id.text_item);
        imageView = itemView.findViewById(R.id.iv_edit);
        this.wholeView = itemView;
    }
}
