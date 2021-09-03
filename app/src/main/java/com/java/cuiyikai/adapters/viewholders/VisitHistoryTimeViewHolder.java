package com.java.cuiyikai.adapters.viewholders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.java.cuiyikai.R;

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
