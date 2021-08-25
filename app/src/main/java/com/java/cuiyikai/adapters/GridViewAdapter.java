package com.java.cuiyikai.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.java.cuiyikai.MainApplication;
import com.java.cuiyikai.R;

import java.util.List;

class CategoryViewHolder extends RecyclerView.ViewHolder {

    private final TextView textView;
    private final ImageView imageView;

    public TextView getTextView() {
        return textView;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public CategoryViewHolder(@NonNull View itemView) {
        super(itemView);
        textView = itemView.findViewById(R.id.text_item);
        imageView = itemView.findViewById(R.id.iv_edit);
    }
}

public class GridViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context context;
    private final List<String> strList;
    private final int mType;

    public GridViewAdapter(Context context, List<String> strList, int type) {
        this.context = context;
        this.strList = strList;
        this.mType = type;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View categoryItem = LayoutInflater.from(context).inflate(R.layout.channel_item, parent, false);
        return new CategoryViewHolder(categoryItem);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        CategoryViewHolder categoryViewHolder = (CategoryViewHolder) holder;
        String name = strList.get(position);
        categoryViewHolder.getTextView().setText(name);
        if(mType == 0)
            categoryViewHolder.getImageView().setImageResource(R.drawable.x);
        else
            categoryViewHolder.getImageView().setImageResource(R.drawable.add_channel);
    }


    @Override
    public int getItemCount() {
        return strList.size();
    }


}