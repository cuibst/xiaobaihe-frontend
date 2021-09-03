package com.java.cuiyikai.adapters.viewholders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.java.cuiyikai.R;

public class QuestionViewHolder extends RecyclerView.ViewHolder {
    private final ImageView img;
    private final TextView label;
    private final TextView category;
    private final View view;

    public ImageView getImg() {
        return img;
    }

    public TextView getLabel() {
        return label;
    }

    public TextView getCategory() {
        return category;
    }

    public View getView() {
        return view;
    }

    public QuestionViewHolder(@NonNull View itemView) {
        super(itemView);
        img=itemView.findViewById(R.id.img);
        label=itemView.findViewById(R.id.label);
        category=itemView.findViewById(R.id.category);
        view=itemView.findViewById(R.id.search_line);
    }
}
