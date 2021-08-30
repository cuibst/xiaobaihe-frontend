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
import com.java.cuiyikai.activities.ProblemActivity;
import com.java.cuiyikai.entities.RelationEntity;

import org.w3c.dom.Text;

import java.util.List;

public class OptionAdapter extends ArrayAdapter<String> {

    private final int resourceId;
    private final ProblemActivity problemActivity;

    private final int answerId;

    private final int clickId;

    public OptionAdapter(Context context, int textViewResourceId, List<String> optionTexts, int answerId, int clickId) {
        super(context, textViewResourceId, optionTexts);
        resourceId = textViewResourceId;
        this.answerId = answerId;
        this.problemActivity = (ProblemActivity) context;
        this.clickId = clickId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String description = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
        View optionItem = view.findViewById(R.id.option_item);
//        optionItem.setOnClickListener(problemActivity.new OptionOnClickListener(position));
        TextView choiceView = (TextView) view.findViewById(R.id.option_id);
        TextView descriptionView = (TextView) view.findViewById(R.id.option_description);
        Character option = (char)('A' + position);
        choiceView.setText(option.toString());
        descriptionView.setText(description);
        if(clickId != -1 && answerId == position)
            optionItem.setBackgroundResource(R.drawable.green_radius);
        else if(clickId != -1 && clickId == position)
            optionItem.setBackgroundResource(R.drawable.red_radius);
        else
            optionItem.setBackgroundResource(R.drawable.white_radius);
        return view;
    }

}