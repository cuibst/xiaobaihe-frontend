package com.java.cuiyikai.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.java.cuiyikai.R;
import com.java.cuiyikai.activities.EntityActivity;
import com.java.cuiyikai.activities.ProblemActivity;


import java.util.List;

public class ProblemAdapter extends ArrayAdapter<JSONObject> {

    private final int resourceId;
    private final EntityActivity entityActivity;

    public ProblemAdapter(Context context, int textViewResourceId, List<JSONObject> optionTexts) {
        super(context, textViewResourceId, optionTexts);
        resourceId = textViewResourceId;
        this.entityActivity = (EntityActivity) context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        JSONObject problemObject = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
        View problemItem = view.findViewById(R.id.problem_item);
        problemItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent f=new Intent(entityActivity,ProblemActivity.class);
                f.putExtra("body", problemObject.getString("qBody"));
                f.putExtra("answer", problemObject.getString("qAnswer"));
                entityActivity.startActivity(f);
            }
        });
        TextView problemDescription = (TextView) view.findViewById(R.id.problem_description_in_entity);
        problemDescription.setText(problemObject.getString("qBody"));
        return view;
    }

}