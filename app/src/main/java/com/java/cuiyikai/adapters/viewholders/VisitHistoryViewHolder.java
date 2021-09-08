package com.java.cuiyikai.adapters.viewholders;


import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.java.cuiyikai.R;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * This viewholder is used in {@link com.java.cuiyikai.adapters.VisitHistoryAdapter} .
 * it shows the visit history item.
 */
public class VisitHistoryViewHolder extends RecyclerView.ViewHolder  {
    public ImageView img;
    public TextView label;
    public TextView category;
    public View view;
    public VisitHistoryViewHolder(@NonNull View itemView) {
        super(itemView);
        img=itemView.findViewById(R.id.img);
        label=itemView.findViewById(R.id.label);
        category=itemView.findViewById(R.id.category);
        view=itemView.findViewById(R.id.search_line);
    }
}
