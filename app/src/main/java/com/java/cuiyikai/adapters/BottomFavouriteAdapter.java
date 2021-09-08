package com.java.cuiyikai.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.java.cuiyikai.R;
import com.java.cuiyikai.entities.BottomFavouriteEntity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A {@link ArrayAdapter} for the {@link android.widget.ListView} in the bottom favourite dialog.
 */
public class BottomFavouriteAdapter extends ArrayAdapter<BottomFavouriteEntity> {

    private final int resourceId;

    private final Set<String> checkedSet = new HashSet<>();

    /**
     * get the set of the elements that is chosen in the list view
     * @return a {@link Set} contains Strings of chosen elements.
     */
    public Set<String> getCheckedSet() {
        return checkedSet;
    }

    /**
     * Constructor for {@link BottomFavouriteAdapter}
     * @param context the {@link Context} in which we inflate the dialog
     * @param textViewResourceId the item's resource id, please use {@link R.layout#bottom_dialog_favourite_item}
     * @param favouriteEntities {@link List} of the {@link BottomFavouriteEntity} to be demonstrated.
     */
    public BottomFavouriteAdapter(Context context, int textViewResourceId, List<BottomFavouriteEntity> favouriteEntities) {
        super(context, textViewResourceId, favouriteEntities);
        resourceId = textViewResourceId;
    }

    /**
     * {@inheritDoc}
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BottomFavouriteEntity bottomFavouriteEntity = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
        View bottomFavouriteItem = view.findViewById(R.id.bottomFavoriteItem);
        CheckBox checkBox = bottomFavouriteItem.findViewById(R.id.bottomFavouriteCheck);
        checkBox.setChecked(bottomFavouriteEntity.isFavoured());
        String text = bottomFavouriteEntity.getDirectoryName().equals("default") ? "默认收藏夹" : bottomFavouriteEntity.getDirectoryName();
        TextView directoryName = view.findViewById(R.id.bottomDialogFavouriteText);
        directoryName.setText(text);

        if(bottomFavouriteEntity.isFavoured())
            checkedSet.add(bottomFavouriteEntity.getDirectoryName());

        //attach a listener to the check box to update the set.
        checkBox.setOnCheckedChangeListener((CompoundButton compoundButton, boolean b) -> {
            if(b)
                checkedSet.add(bottomFavouriteEntity.getDirectoryName());
            else
                checkedSet.remove(bottomFavouriteEntity.getDirectoryName());
        });

        view.setOnClickListener((View v) -> checkBox.setChecked(!checkBox.isChecked()));

        return view;
    }

}
