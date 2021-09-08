package com.java.cuiyikai.adapters.viewholders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.java.cuiyikai.R;

/**
 * {@link RecyclerView.ViewHolder} for the properties in {@link RecyclerView} in {@link com.java.cuiyikai.activities.EntityActivity}
 */
public class PropertyViewHolder extends RecyclerView.ViewHolder {

    private final TextView propertyText;

    public PropertyViewHolder(@NonNull View itemView) {
        super(itemView);

        propertyText = itemView.findViewById(R.id.propertyItemName);
    }

    public TextView getPropertyText() {
        return propertyText;
    }
}
