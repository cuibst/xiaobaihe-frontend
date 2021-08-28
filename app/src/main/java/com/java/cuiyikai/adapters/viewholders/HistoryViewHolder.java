package com.java.cuiyikai.adapters.viewholders;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import com.java.cuiyikai.R;

public class HistoryViewHolder extends RecyclerView.ViewHolder {
    public TextView historyRecord;
    public HistoryViewHolder(View view) {
        super(view);
        historyRecord=view.findViewById(R.id.historyrecord);
    }
}
