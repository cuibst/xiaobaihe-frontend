package com.java.cuiyikai.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.java.cuiyikai.R;
import com.java.cuiyikai.entities.PropertyEntity;
import com.java.cuiyikai.utilities.DensityUtilities;
import com.java.cuiyikai.widgets.RoundCornerBackgroundColorSpan;

import java.util.List;

public class PropertyAdapter extends ArrayAdapter<PropertyEntity> {

    private int resourceId;

    private final Context context;

    public PropertyAdapter(Context context, int textViewResourceId, List<PropertyEntity> relationEntities) {
        super(context, textViewResourceId, relationEntities);
        resourceId = textViewResourceId;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PropertyEntity propertyEntity = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
        TextView relationName = (TextView) view.findViewById(R.id.propertyItemName);
        String name = "   " + propertyEntity.getLabel() + "   ";
        String object = "   " + propertyEntity.getObject();
        SpannableStringBuilder builder = new SpannableStringBuilder(name + object);
        builder.setSpan(new RoundCornerBackgroundColorSpan(DensityUtilities.dp2px(context, 5), Color.rgb(0x99, 0xaa, 0xff), Color.rgb(0xff, 0xff, 0xff)), 0, name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(new StyleSpan(Typeface.BOLD), 0, name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        relationName.setText(builder);
        return view;
    }

}
