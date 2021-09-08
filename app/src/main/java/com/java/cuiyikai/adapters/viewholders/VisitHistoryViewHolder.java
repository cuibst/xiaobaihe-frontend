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
    private  ImageView img;
    private TextView label;
    private  TextView category;
    private View view;
    public ImageView getImg()
    {
        return img;
    }
    public TextView getCategory()
    {
        return category;
    }
    public TextView getLabel()
    {
        return label;
    }
    public View getView()
    {
        return  view;
    }
    public VisitHistoryViewHolder(@NonNull View itemView) {
        super(itemView);
        img=itemView.findViewById(R.id.img);
        label=itemView.findViewById(R.id.label);
        category=itemView.findViewById(R.id.category);
        view=itemView.findViewById(R.id.search_line);
    }
}
