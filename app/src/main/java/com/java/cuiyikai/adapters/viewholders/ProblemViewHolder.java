package com.java.cuiyikai.adapters.viewholders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.java.cuiyikai.R;

/**
 * {@link RecyclerView.ViewHolder} for the problems in {@link RecyclerView} in {@link com.java.cuiyikai.activities.EntityActivity}
 */
public class ProblemViewHolder extends RecyclerView.ViewHolder {

    private final TextView problemText;
    private final View problemView;

    public ProblemViewHolder(@NonNull View itemView) {
        super(itemView);
        problemText = itemView.findViewById(R.id.problem_description_in_entity);
        problemView = itemView;
    }

    public TextView getProblemText() {
        return problemText;
    }

    public View getProblemView() {
        return problemView;
    }
}
