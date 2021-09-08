package com.java.cuiyikai.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java.cuiyikai.activities.EntityActivity;
import com.java.cuiyikai.adapters.viewholders.FavouriteItemHolder;
import com.java.cuiyikai.fragments.DirectoryFragment;

import com.java.cuiyikai.R;
import com.java.cuiyikai.utilities.ConstantUtilities;

import java.util.Arrays;

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
        favouriteItemHolder.getViewLine().setOnClickListener((View v) -> {
            Intent intent = new Intent(directoryFragment.getActivity(), EntityActivity.class);
            intent.putExtra(ConstantUtilities.ARG_NAME, object.getString(ConstantUtilities.ARG_NAME));
            intent.putExtra(ConstantUtilities.ARG_SUBJECT, object.getString(ConstantUtilities.ARG_SUBJECT));
            directoryFragment.getActivity().startActivity(intent);
        });
        favouriteItemHolder.getItemName().setText(object.getString(ConstantUtilities.ARG_NAME));
        switch(object.getString(ConstantUtilities.ARG_SUBJECT)) {
            case ConstantUtilities.SUBJECT_CHINESE :
                favouriteItemHolder.getViewLine().setBackgroundResource(R.drawable.chinese_radius);
                favouriteItemHolder.getSubjectImage().setImageResource(R.drawable.chinese);
                break;
            case ConstantUtilities.SUBJECT_MATH :
                favouriteItemHolder.getViewLine().setBackgroundResource(R.drawable.maths_radius);
                favouriteItemHolder.getSubjectImage().setImageResource(R.drawable.maths);
                break;
            case ConstantUtilities.SUBJECT_ENGLISH :
                favouriteItemHolder.getViewLine().setBackgroundResource(R.drawable.english_radius);
                favouriteItemHolder.getSubjectImage().setImageResource(R.drawable.english);
                break;
            case ConstantUtilities.SUBJECT_PHYSICS :
                favouriteItemHolder.getViewLine().setBackgroundResource(R.drawable.physics_radius);
                favouriteItemHolder.getSubjectImage().setImageResource(R.drawable.physics);
                break;
            case ConstantUtilities.SUBJECT_CHEMISTRY :
                favouriteItemHolder.getViewLine().setBackgroundResource(R.drawable.chemistry_radius);
                favouriteItemHolder.getSubjectImage().setImageResource(R.drawable.chemistry);
                break;
            case ConstantUtilities.SUBJECT_BIOLOGY :
                favouriteItemHolder.getViewLine().setBackgroundResource(R.drawable.biology_radius);
                favouriteItemHolder.getSubjectImage().setImageResource(R.drawable.biology);
                break;
            case ConstantUtilities.SUBJECT_HISTORY :
                favouriteItemHolder.getViewLine().setBackgroundResource(R.drawable.history_radius);
                favouriteItemHolder.getSubjectImage().setImageResource(R.drawable.history);
                break;
            case ConstantUtilities.SUBJECT_GEO :
                favouriteItemHolder.getViewLine().setBackgroundResource(R.drawable.geography_radius);
                favouriteItemHolder.getSubjectImage().setImageResource(R.drawable.geography);
                break;
            case ConstantUtilities.SUBJECT_POLITICS:
            default:
                favouriteItemHolder.getViewLine().setBackgroundResource(R.drawable.politics_radius);
                favouriteItemHolder.getSubjectImage().setImageResource(R.drawable.politics);
                break;
        }
        if(editable) {
            favouriteItemHolder.getCheckBox().setVisibility(View.VISIBLE);
            favouriteItemHolder.getDragButton().setVisibility(View.VISIBLE);
            favouriteItemHolder.getCheckBox().setChecked(selected[position]);
            favouriteItemHolder.getCheckBox().setOnCheckedChangeListener((CompoundButton compoundButton, boolean b) -> {
                if(position < selected.length)
                    selected[position] = b;
            });
        } else {
            favouriteItemHolder.getCheckBox().setVisibility(View.GONE);
            favouriteItemHolder.getDragButton().setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return favouriteArray == null ? 0 : favouriteArray.size();
    }
}