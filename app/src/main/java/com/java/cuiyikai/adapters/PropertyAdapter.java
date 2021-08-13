package com.java.cuiyikai.adapters;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.java.cuiyikai.R;
import com.java.cuiyikai.entities.PropertyEntity;

import java.util.List;

public class PropertyAdapter extends ArrayAdapter<PropertyEntity> {

    private int resourceId;

    public PropertyAdapter(Context context, int textViewResourceId, List<PropertyEntity> relationEntities) {
        super(context, textViewResourceId, relationEntities);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PropertyEntity propertyEntity = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
        TextView relationName = (TextView) view.findViewById(R.id.propertyItemName);
        String name = " " + propertyEntity.getLabel() + " ";
        String object = propertyEntity.getObject();
        SpannableStringBuilder builder = new SpannableStringBuilder(name + object);
        builder.setSpan(new BackgroundColorSpan(Color.rgb(0xff, 0xb6, 0xc1)), 0, name.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        relationName.setText(builder);
        return view;
    }

}
