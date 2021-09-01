package com.java.cuiyikai.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java.cuiyikai.R;
import com.java.cuiyikai.adapters.viewholders.HistoryViewHolder;

public class HistoryListAdapter extends RecyclerView.Adapter<HistoryViewHolder> {
    public  boolean recommendflag=false;
    public HistoryListAdapter historyListAdapter;
    private Context mContext;
    private JSONArray data;
    public  boolean flag=false;
    private SearchView searchView;
    private String RemoveUrl="/api/history/removeHistory";
    public HistoryListAdapter(Context c, SearchView s)
    {
        mContext=c;
        searchView=s;
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
        if(recommendflag)
        {
            holder.historyRecord.setText(((JSONObject)data.get(position)).get("name").toString());
            holder.flag=flag;
            holder.searchView=searchView;
            holder.editImg.setVisibility(View.INVISIBLE);
            holder.recommendflag=recommendflag;
            return;
        }
        holder.historyListAdapter=historyListAdapter;
        holder.editImg.setVisibility(View.INVISIBLE);
        holder.historyRecord.setText(data.get(position).toString());
        holder.flag=flag;
        holder.searchView=searchView;
        if(flag&&!holder.historyRecord.isCursorVisible())
        {
            data.remove(holder.historyRecord.getText());
        }
    }

    @Override
    public int getItemCount() {
        if(data==null)
            return 0;
        return data.size();
    }
}
