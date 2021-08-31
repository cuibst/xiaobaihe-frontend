package com.java.cuiyikai.adapters.viewholders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.java.cuiyikai.R;

public class VisitHistoryTimeViewHolder extends RecyclerView.ViewHolder  {
    public TextView timetext;
    public VisitHistoryTimeViewHolder(@NonNull View itemView) {
        super(itemView);
        timetext=itemView.findViewById(R.id.timetext);
    }
}
