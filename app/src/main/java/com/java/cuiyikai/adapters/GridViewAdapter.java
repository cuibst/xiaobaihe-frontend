package com.java.cuiyikai.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.java.cuiyikai.MainApplication;
import com.java.cuiyikai.R;
import com.java.cuiyikai.activities.CategoryActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class CategoryViewHolder extends RecyclerView.ViewHolder {

    private final TextView textView;
    private final ImageView imageView;
    private final View wholeView;

    public TextView getTextView() {
        return textView;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public View getWholeView() {
        return wholeView;
    }

    public CategoryViewHolder(@NonNull View itemView) {
        super(itemView);
        textView = itemView.findViewById(R.id.text_item);
        imageView = itemView.findViewById(R.id.iv_edit);
        this.wholeView = itemView;
    }
}

class CategoryTitleHolder extends RecyclerView.ViewHolder {

    private final TextView textView;

    public CategoryTitleHolder(@NonNull View itemView) {
        super(itemView);
        textView = itemView.findViewById(R.id.channel_title);
    }

    public TextView getTextView() {
        return textView;
    }


}

public class GridViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private boolean isEditable = false;

    private final CategoryActivity context;
    private final List<CategoryObject> itemList;

    public static final int TYPE_ITEM = 1;
    public static final int TYPE_TITLE = 2;

    private int userSectionSize;

    public int getUserSectionSize() {
        return userSectionSize;
    }

    public void setEditable(boolean editable) {
        if(isEditable != editable) {
            isEditable = editable;
            notifyDataSetChanged();
        }
    }

    public boolean isEditable() {
        return isEditable;
    }

    public GridViewAdapter(CategoryActivity context, List<CategoryObject> itemList, int userSectionSize) {
        this.context = context;
        this.itemList = itemList;
        this.userSectionSize = userSectionSize;
    }

    public void onItemMove(int fromPosition, int toPosition) {
        if(toPosition == 0)
            return;
        System.out.printf("Move from %d to %d%n", fromPosition, toPosition);
        if(inUserSection(fromPosition) && inUserSection(toPosition)) {
            CategoryObject object = itemList.get(fromPosition);
            itemList.remove(fromPosition);
            itemList.add(toPosition, object);
            notifyItemMoved(fromPosition, toPosition);
        } else if(fromPosition > userSectionSize + 1 && toPosition > userSectionSize + 1) {
            CategoryObject object = itemList.get(fromPosition);
            itemList.remove(fromPosition);
            itemList.add(toPosition, object);
            notifyItemMoved(fromPosition, toPosition);
        } else if(inUserSection(fromPosition) && toPosition > userSectionSize) {
            CategoryObject object = itemList.get(fromPosition);
            itemList.remove(fromPosition);
            itemList.add(toPosition, object);
            userSectionSize --;
            notifyItemMoved(fromPosition, toPosition);
        } else if(fromPosition > userSectionSize + 1 && toPosition <= userSectionSize + 1) {
            CategoryObject object = itemList.get(fromPosition);
            itemList.remove(fromPosition);
            itemList.add(toPosition, object);
            userSectionSize ++;
            notifyItemMoved(fromPosition, toPosition);
        }

        List<String> subjectList = new ArrayList<>();
        for(CategoryObject object : itemList.subList(1, userSectionSize + 1))
            subjectList.add(object.getName());

        ((MainApplication)((CategoryActivity)context).getApplication()).setSubjects(subjectList);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == TYPE_ITEM) {
            View categoryItem = LayoutInflater.from(context).inflate(R.layout.channel_item, parent, false);
            return new CategoryViewHolder(categoryItem);
        } else {
            View categoryItem = LayoutInflater.from(context).inflate(R.layout.channel_title_item, parent, false);
            return new CategoryTitleHolder(categoryItem);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof CategoryTitleHolder) {
            ((CategoryTitleHolder) holder).getTextView().setText(itemList.get(position).getName());
        } else {
            ((CategoryViewHolder) holder).getTextView().setText(itemList.get(position).getName());
            if(!isEditable)
                ((CategoryViewHolder) holder).getImageView().setVisibility(View.GONE);
            else
                ((CategoryViewHolder) holder).getImageView().setVisibility(View.VISIBLE);
            if(inUserSection(position)) {
                ((CategoryViewHolder) holder).getImageView().setImageResource(R.drawable.x);
                if(isEditable)
                    ((CategoryViewHolder) holder).getWholeView().setOnClickListener((View v) -> {
                        CategoryObject object = itemList.get(position);
                        itemList.remove(position);
                        itemList.add(object);
                        userSectionSize --;
                        List<String> subjectList = new ArrayList<>();
                        for(CategoryObject obj : itemList.subList(1, userSectionSize + 1))
                            subjectList.add(obj.getName());

                        ((MainApplication)((CategoryActivity)context).getApplication()).setSubjects(subjectList);
                        notifyDataSetChanged();
                    });
                else
                    ((CategoryViewHolder) holder).getWholeView().setOnLongClickListener((View v) -> {
                        setEditable(true);
                        context.onEditableChanged(true);
                        return true;
                    });
            } else {
                ((CategoryViewHolder) holder).getImageView().setImageResource(R.drawable.add_channel);
                if(isEditable)
                    ((CategoryViewHolder) holder).getWholeView().setOnClickListener((View v) -> {
                        CategoryObject object = itemList.get(position);
                        itemList.remove(position);
                        itemList.add(userSectionSize + 1, object);
                        userSectionSize ++;
                        List<String> subjectList = new ArrayList<>();
                        for(CategoryObject obj : itemList.subList(1, userSectionSize + 1))
                            subjectList.add(obj.getName());

                        ((MainApplication)((CategoryActivity)context).getApplication()).setSubjects(subjectList);
                        notifyDataSetChanged();
                    });
                else
                    ((CategoryViewHolder) holder).getWholeView().setOnLongClickListener((View v) -> {
                        setEditable(true);
                        context.onEditableChanged(true);
                        return true;
                    });
            }
        }
    }

    public boolean inUserSection(int position) {
        return position <= userSectionSize;
    }


    @Override
    public int getItemCount() {
        return itemList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(itemList.get(position) instanceof CategoryItem)
            return TYPE_ITEM;
        else
            return TYPE_TITLE;
    }
}

