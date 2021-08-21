package com.java.cuiyikai.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.java.cuiyikai.R;
import com.java.cuiyikai.activities.EntityActivity;
import com.java.cuiyikai.entities.BottomFavouriteEntity;
import com.java.cuiyikai.entities.RelationEntity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BottomFavouriteAdapter extends ArrayAdapter<BottomFavouriteEntity> {

    private final int resourceId;

    private final EntityActivity entityActivity;

    private final Set<String> checkedSet = new HashSet<>();

    public Set<String> getCheckedSet() {
        return checkedSet;
    }

    public BottomFavouriteAdapter(Context context, int textViewResourceId, List<BottomFavouriteEntity> favouriteEntities) {
        super(context, textViewResourceId, favouriteEntities);
        resourceId = textViewResourceId;
        this.entityActivity = (EntityActivity) context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BottomFavouriteEntity bottomFavouriteEntity = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
        View bottomFavouriteItem = view.findViewById(R.id.bottomFavoriteItem);
        CheckBox checkBox = (CheckBox) bottomFavouriteItem.findViewById(R.id.bottomFavouriteCheck);
        checkBox.setChecked(bottomFavouriteEntity.isFavoured());
        String text = bottomFavouriteEntity.getDirectoryName().equals("default") ? "默认收藏夹" : bottomFavouriteEntity.getDirectoryName();
        TextView directoryName = (TextView) view.findViewById(R.id.bottomDialogFavouriteText);
        directoryName.setText(text);

        if(bottomFavouriteEntity.isFavoured())
            checkedSet.add(bottomFavouriteEntity.getDirectoryName());

        checkBox.setOnCheckedChangeListener((CompoundButton compoundButton, boolean b) -> {
            if(b)
                checkedSet.add(bottomFavouriteEntity.getDirectoryName());
            else
                checkedSet.remove(bottomFavouriteEntity.getDirectoryName());
        });

        view.setOnClickListener((View v) -> {
            checkBox.setChecked(!checkBox.isChecked());
        });

        return view;
    }

}
