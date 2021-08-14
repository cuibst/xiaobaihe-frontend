package com.java.cuiyikai.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.java.cuiyikai.R;
import com.java.cuiyikai.activities.EntityActivity;
import com.java.cuiyikai.entities.RelationEntity;

import java.util.List;

public class RelationAdapter extends ArrayAdapter<RelationEntity> {

    private final int resourceId;
    private final String subject;

    private final EntityActivity entityActivity;

    public RelationAdapter(Context context, int textViewResourceId, List<RelationEntity> relationEntities, final String subject) {
        super(context, textViewResourceId, relationEntities);
        resourceId = textViewResourceId;
        this.subject = subject;
        this.entityActivity = (EntityActivity) context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RelationEntity relationEntity = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
        View relationItem = view.findViewById(R.id.relationItem);
        relationItem.setOnClickListener(entityActivity.new RelationViewItemOnClickListener(relationEntity.getTargetName(), subject));
        TextView relationName = (TextView) view.findViewById(R.id.relationName);
        relationName.setText(relationEntity.getRelationName());
        ImageView relationPic = (ImageView) view.findViewById(R.id.relationPic);
        if(relationEntity.isSubject())
            relationPic.setImageResource(R.drawable.left);
        else
            relationPic.setImageResource(R.drawable.right);
        TextView targetName = (TextView) view.findViewById(R.id.targetName);
        targetName.setText(relationEntity.getTargetName());
        return view;
    }

}
