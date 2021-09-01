package com.java.cuiyikai.adapters.viewholders;


import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.java.cuiyikai.R;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class VisitHistoryViewHolder extends RecyclerView.ViewHolder  {
    public ImageView img;
    public TextView label,category;
    public View view;
    public VisitHistoryViewHolder(@NonNull View itemView) {
        super(itemView);
        img=itemView.findViewById(R.id.img);
        label=itemView.findViewById(R.id.label);
        category=itemView.findViewById(R.id.category);
        view=itemView.findViewById(R.id.search_line);
    }
}
