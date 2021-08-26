package com.java.cuiyikai.adapters.viewholders;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.java.cuiyikai.R;

public class FavouriteItemHolder extends RecyclerView.ViewHolder {

    private final CheckBox checkBox;
    private final ImageView subjectImage;
    private final TextView itemName;
    private final ImageButton dragButton;
    private final View viewLine;

    public CheckBox getCheckBox() {
        return checkBox;
    }

    public ImageView getSubjectImage() {
        return subjectImage;
    }

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
