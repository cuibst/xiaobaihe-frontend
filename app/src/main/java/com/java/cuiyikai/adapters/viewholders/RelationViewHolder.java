package com.java.cuiyikai.adapters.viewholders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.java.cuiyikai.R;

/**
 * {@link RecyclerView.ViewHolder} for the relations in {@link RecyclerView} in {@link com.java.cuiyikai.activities.EntityActivity}
 */
public class RelationViewHolder extends RecyclerView.ViewHolder {

    private final TextView relationName;
    private final ImageView relationPic;
    private final TextView targetName;
    private final View relationView;

    public RelationViewHolder(@NonNull View itemView) {
        super(itemView);

        relationName = itemView.findViewById(R.id.relationName);
        relationPic = itemView.findViewById(R.id.relationPic);
        targetName = itemView.findViewById(R.id.targetName);
        relationView = itemView;
    }

    public TextView getRelationName() {
        return relationName;
    }

    public ImageView getRelationPic() {
        return relationPic;
    }

    public TextView getTargetName() {
        return targetName;
    }

    public View getRelationView() {
        return relationView;
    }
}
