package com.java.cuiyikai.adapters.viewholders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.java.cuiyikai.R;

/**
 * This viewholder is used in {@link com.java.cuiyikai.adapters.VisitHistoryAdapter},it shows the visit time.
 */
public class VisitHistoryTimeViewHolder extends RecyclerView.ViewHolder  {
    private final TextView timeText;

    public TextView getTimeText() {
        return timeText;
    }

    public VisitHistoryTimeViewHolder(@NonNull View itemView) {
        super(itemView);
        timeText =itemView.findViewById(R.id.timetext);
    }
}
