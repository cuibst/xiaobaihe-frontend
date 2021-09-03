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
    public JSONArray data;
    public  boolean flag=false;
    public String subject;
    private SearchView searchView;
    private String RemoveUrl="/api/history/removeHistory";
    public HistoryListAdapter(Context c, SearchView s)
    {
        mContext=c;
        searchView=s;
    }
    public void addOneItem(JSONObject  m)
    {
        JSONArray arr=new JSONArray();
        arr.add(m);
        System.out.println(arr.toString());
        if(data==null)
        {
            data=arr;
            return;
        }
        for(int i=0;i<data.size();i++)
        {
            if(m.equals(data.get(i)))
                continue;
            arr.add(data.get(i));
        }
        data=arr;
        return;
    }
    public void addData(JSONArray data)
    {
        System.out.println(data.toString());
        this.data=data;
        System.out.println(this.data);
    }
    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HistoryViewHolder(LayoutInflater.from(mContext).inflate(R.layout.historylist_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        holder.mContext=mContext;
        holder.subject=((JSONObject)data.get(position)).get("subject").toString();
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
        holder.historyRecord.setText(((JSONObject)data.get(position)).get("content").toString());
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
