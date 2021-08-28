package com.java.cuiyikai.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONArray;
import com.java.cuiyikai.R;
import com.java.cuiyikai.adapters.viewholders.HistoryViewHolder;

public class HistoryListAdapter extends RecyclerView.Adapter<HistoryViewHolder> {
    private Context mContext;
    private JSONArray data;
    public HistoryListAdapter(Context c)
    {
        mContext=c;
    }
    public void addData(JSONArray data)
    {
        this.data=data;
    }
    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HistoryViewHolder(LayoutInflater.from(mContext).inflate(R.layout.historylist_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        holder.historyRecord.setText(data.get(position).toString());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
