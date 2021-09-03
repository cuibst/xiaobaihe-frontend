package com.java.cuiyikai.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.java.cuiyikai.R;
import com.java.cuiyikai.adapters.viewholders.PropertyViewHolder;
import com.java.cuiyikai.entities.PropertyEntity;
import com.java.cuiyikai.utilities.DensityUtilities;
import com.java.cuiyikai.widgets.RoundCornerBackgroundColorSpan;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class PropertyAdapter extends RecyclerView.Adapter<PropertyViewHolder> {

    private final List<PropertyEntity> fullList;
    private final List<PropertyEntity> curList;
    private final Context mContext;
    private static final Logger logger = LoggerFactory.getLogger(PropertyAdapter.class);

    public void switchList() {
        if(curList.size() == fullList.size()) {
            if (curList.size() > 5) {
                curList.subList(5, curList.size()).clear();
            }
            notifyItemRangeRemoved(5, fullList.size()-5);
        } else {
            for(int i=5;i<fullList.size();i++) {
                curList.add(fullList.get(i));
            }
            notifyItemRangeInserted(5, curList.size() - 5);
        }
    }

    public PropertyAdapter(Context context, List<PropertyEntity> fullList, List<PropertyEntity> curList) {
        this.fullList = fullList;
        this.curList = new ArrayList<>();
        this.curList.addAll(curList);
        this.mContext = context;
    }

    @NonNull
    @Override
    public PropertyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PropertyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.property_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PropertyViewHolder holder, int position) {
        PropertyEntity propertyEntity = curList.get(position);
        String name = "   " + propertyEntity.getLabel() + "   ";
        String object = "   " + propertyEntity.getObject();
        SpannableStringBuilder builder = new SpannableStringBuilder(name + object);
        builder.setSpan(new RoundCornerBackgroundColorSpan(DensityUtilities.dp2px(mContext, 5), Color.rgb(0x99, 0xaa, 0xff), Color.rgb(0xff, 0xff, 0xff)), 0, name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(new StyleSpan(Typeface.BOLD), 0, name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        logger.info("Spanned result: {}", builder);
        holder.getPropertyText().setText(builder);
    }

    @Override
    public int getItemCount() {
        return curList.size();
    }
}
