package com.java.cuiyikai.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.java.cuiyikai.R;
import com.java.cuiyikai.activities.EntityActivity;
import com.java.cuiyikai.adapters.viewholders.RelationViewHolder;
import com.java.cuiyikai.entities.RelationEntity;
import com.java.cuiyikai.utilities.ConstantUtilities;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} for relation {@link RecyclerView} in {@link com.java.cuiyikai.activities.EntityActivity}
 */
public class RelationAdapter extends RecyclerView.Adapter<RelationViewHolder> {

    private final List<RelationEntity> fullList;
    private final List<RelationEntity> curList;
    private final Context mContext;
    private final String subject;

    /**
     * Switch the view between full list and partial list.
     */
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

    /**
     * Constructor for {@link PropertyAdapter}
     * @param context The context that the view will inflate in
     * @param fullList Full property list
     * @param prevList partial property list. if don't want change, set cur list same as full list.
     * @param subject the subject of the entity that this view in.
     */
    public RelationAdapter(Context context, List<RelationEntity> fullList, List<RelationEntity> prevList, String subject) {
        this.fullList = fullList;
        this.curList = new ArrayList<>();
        this.curList.addAll(prevList);
        this.mContext = context;
        this.subject = subject;
    }

    /**
     * {@inheritDoc}
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public RelationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RelationViewHolder(LayoutInflater.from(mContext).inflate(R.layout.relation_item, parent, false));
    }

    /**
     * {@inheritDoc}
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull RelationViewHolder holder, int position) {
        holder.getRelationName().setText(curList.get(position).getRelationName());
        if(curList.get(position).isSubject())
            holder.getRelationPic().setImageResource(R.drawable.left);
        else
            holder.getRelationPic().setImageResource(R.drawable.right);
        holder.getTargetName().setText(curList.get(position).getTargetName());
        holder.getRelationView().setOnClickListener((View view) -> {
            Intent f=new Intent(mContext ,EntityActivity.class);
            f.putExtra(ConstantUtilities.ARG_NAME,curList.get(position).getTargetName());
            f.putExtra(ConstantUtilities.ARG_SUBJECT,subject);
            mContext.startActivity(f);
        });
    }

    /**
     * {@inheritDoc}
     * @return
     */
    @Override
    public int getItemCount() {
        return curList.size();
    }
}

