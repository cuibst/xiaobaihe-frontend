package com.java.cuiyikai.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java.cuiyikai.fragments.DirectoryFragment;

import com.java.cuiyikai.R;
import com.jcodecraeer.xrecyclerview.ItemTouchHelperAdapter;

import java.util.Arrays;
import java.util.Collections;

class FavouriteItemHolder extends RecyclerView.ViewHolder {

    private final CheckBox checkBox;
    private final ImageView subjectImage;
    private final TextView itemName;
    private final ImageButton dragButton;
    private final View viewLine;

    public CheckBox getCheckBox() { return checkBox; }

    public ImageView getSubjectImage() { return subjectImage; }

    public TextView getItemName() {
        return itemName;
    }

    public ImageButton getDragButton() {
        return dragButton;
    }

    public View getViewLine() {
        return viewLine;
    }

    public FavouriteItemHolder(@NonNull View itemView) {

        super(itemView);

        checkBox = (CheckBox) itemView.findViewById(R.id.multiSelectItemCheckBox);
        subjectImage = (ImageView) itemView.findViewById(R.id.itemSubjectPicture);
        itemName = (TextView) itemView.findViewById(R.id.favouriteItemName);
        dragButton = (ImageButton) itemView.findViewById(R.id.itemDragButton);
        viewLine = itemView.findViewById(R.id.directoryFavouriteItem);

    }
}

public class FavouriteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private final DirectoryFragment directoryFragment;
    private JSONArray favouriteArray;

    private boolean editable = false;
    private boolean[] selected;

    public void selectAll() {
        if(selected == null)
            return;
        Arrays.fill(selected, true);
    }

    public boolean[] getSelected() {
        return selected;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        if(editable != this.editable) {
            this.editable = editable;
            if(editable)
                selected = new boolean[favouriteArray.size()];
        }
    }

    public JSONArray getFavouriteArray() {
        return favouriteArray;
    }

    public void setFavouriteArray(JSONArray favouriteArray) {
        this.favouriteArray = favouriteArray;
        if(favouriteArray == null)
            selected = null;
        else {
            selected = new boolean[favouriteArray.size()];
            Arrays.fill(selected, false);
        }
    }

    public FavouriteAdapter(DirectoryFragment directoryFragment, JSONArray favouriteArray) {
        this.directoryFragment = directoryFragment;
        this.favouriteArray = favouriteArray;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View favouriteItem = LayoutInflater.from(directoryFragment.getContext()).inflate(R.layout.directory_favourite_item, parent, false);
        return new FavouriteItemHolder(favouriteItem);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        FavouriteItemHolder favouriteItemHolder = (FavouriteItemHolder) holder;
        JSONObject object = favouriteArray.getJSONObject(position);
        favouriteItemHolder.getItemName().setText(object.getString("name"));
        switch(object.getString("subject")) {
            case "chinese" :
                favouriteItemHolder.getViewLine().setBackgroundResource(R.drawable.chinese_radius);
                favouriteItemHolder.getSubjectImage().setImageResource(R.drawable.chinese);
                break;
            case "math" :
                favouriteItemHolder.getViewLine().setBackgroundResource(R.drawable.maths_radius);
                favouriteItemHolder.getSubjectImage().setImageResource(R.drawable.maths);
                break;
            case "english" :
                favouriteItemHolder.getViewLine().setBackgroundResource(R.drawable.english_radius);
                favouriteItemHolder.getSubjectImage().setImageResource(R.drawable.english);
                break;
            case "physics" :
                favouriteItemHolder.getViewLine().setBackgroundResource(R.drawable.physics_radius);
                favouriteItemHolder.getSubjectImage().setImageResource(R.drawable.physics);
                break;
            case "chemistry" :
                favouriteItemHolder.getViewLine().setBackgroundResource(R.drawable.chemistry_radius);
                favouriteItemHolder.getSubjectImage().setImageResource(R.drawable.chemistry);
                break;
            case "biology" :
                favouriteItemHolder.getViewLine().setBackgroundResource(R.drawable.biology_radius);
                favouriteItemHolder.getSubjectImage().setImageResource(R.drawable.biology);
                break;
            case "history" :
                favouriteItemHolder.getViewLine().setBackgroundResource(R.drawable.history_radius);
                favouriteItemHolder.getSubjectImage().setImageResource(R.drawable.history);
                break;
            case "geo" :
                favouriteItemHolder.getViewLine().setBackgroundResource(R.drawable.geography_radius);
                favouriteItemHolder.getSubjectImage().setImageResource(R.drawable.geography);
                break;
            case "politics":
            default:
                favouriteItemHolder.getViewLine().setBackgroundResource(R.drawable.politics_radius);
                favouriteItemHolder.getSubjectImage().setImageResource(R.drawable.politics);
                break;
        }
        if(editable) {
            favouriteItemHolder.getCheckBox().setVisibility(View.VISIBLE);
            favouriteItemHolder.getDragButton().setVisibility(View.VISIBLE);
            if(selected[position])
                favouriteItemHolder.getCheckBox().setChecked(true);
            else
                favouriteItemHolder.getCheckBox().setChecked(false);
            favouriteItemHolder.getCheckBox().setOnCheckedChangeListener((CompoundButton compoundButton, boolean b) -> selected[position] = b);
        } else {
            favouriteItemHolder.getCheckBox().setVisibility(View.GONE);
            favouriteItemHolder.getDragButton().setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return favouriteArray == null ? 0 : favouriteArray.size();
    }
}