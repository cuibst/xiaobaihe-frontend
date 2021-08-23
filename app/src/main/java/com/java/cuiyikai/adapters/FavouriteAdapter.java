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

    public CheckBox getCheckBox() { return checkBox; }

    public ImageView getSubjectImage() { return subjectImage; }

    public TextView getItemName() {
        return itemName;
    }

    public ImageButton getDragButton() {
        return dragButton;
    }

    public FavouriteItemHolder(@NonNull View itemView) {

        super(itemView);

        checkBox = (CheckBox) itemView.findViewById(R.id.multiSelectItemCheckBox);
        subjectImage = (ImageView) itemView.findViewById(R.id.itemSubjectPicture);
        itemName = (TextView) itemView.findViewById(R.id.favouriteItemName);
        dragButton = (ImageButton) itemView.findViewById(R.id.itemDragButton);

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
        selected = new boolean[favouriteArray.size()];
        Arrays.fill(selected, false);
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
            case "physics":
                favouriteItemHolder.getSubjectImage().setImageResource(R.drawable.phy);
                break;
            case "chemistry":
                favouriteItemHolder.getSubjectImage().setImageResource(R.drawable.che);
                break;
            case "biology":
                favouriteItemHolder.getSubjectImage().setImageResource(R.drawable.bio);
                break;
            case "geo":
                favouriteItemHolder.getSubjectImage().setImageResource(R.drawable.geo);
                break;
            case "chinese": //FIXME: add different resource for different subject!!
            case "math":
            case "english":
            case "history":
            case "politics":
            default:
                favouriteItemHolder.getSubjectImage().setImageResource(R.drawable.book);
                break;
        }
        if(editable) {
            favouriteItemHolder.getCheckBox().setVisibility(View.VISIBLE);
            favouriteItemHolder.getDragButton().setVisibility(View.VISIBLE);
            if(selected[position])
                favouriteItemHolder.getCheckBox().setChecked(true);
            favouriteItemHolder.getCheckBox().setOnCheckedChangeListener((CompoundButton compoundButton, boolean b) -> selected[position] = b);
        } else {
            favouriteItemHolder.getCheckBox().setVisibility(View.GONE);
            favouriteItemHolder.getDragButton().setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return favouriteArray.size();
    }
}