package com.java.cuiyikai.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;
import com.java.cuiyikai.R;
import com.java.cuiyikai.activities.ProblemActivity;
import com.java.cuiyikai.adapters.viewholders.ProblemViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link ProblemAdapter} is used to send information to {@link ProblemActivity}.
 */
public class ProblemAdapter extends RecyclerView.Adapter<ProblemViewHolder> {

    private final List<JSONObject> fullList;
    private final List<JSONObject> curList;
    private final Context mContext;
    private final String subject;

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

    public ProblemAdapter(Context context, List<JSONObject> fullList, List<JSONObject> curList, String subject) {
        this.subject = subject;
        this.fullList = fullList;
        this.curList = new ArrayList<>();
        this.curList.addAll(curList);
        this.mContext = context;
    }

    @NonNull
    @Override
    public ProblemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProblemViewHolder(LayoutInflater.from(mContext).inflate(R.layout.problem_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ProblemViewHolder holder, int position) {
        holder.getProblemText().setText(curList.get(position).getString("qBody"));
        holder.getProblemView().setOnClickListener((View view) -> {
            Intent f=new Intent(mContext, ProblemActivity.class);
            f.putExtra("body 0", curList.get(position).getString("qBody"));
            f.putExtra("answer 0", curList.get(position).getString("qAnswer"));
            f.putExtra("type", "single");
            f.putExtra("sum", 1 + "");
            f.putExtra("subject 0", subject);

            mContext.startActivity(f);
        });
    }

    @Override
    public int getItemCount() {
        return curList.size();
    }
}