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
import com.java.cuiyikai.utilities.ConstantUtilities;

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
        this.data=data;
    }
    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HistoryViewHolder(LayoutInflater.from(mContext).inflate(R.layout.historylist_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        holder.setContext(mContext);
        holder.setSubject(((JSONObject)data.get(position)).getString(ConstantUtilities.ARG_SUBJECT));
        if(recommendflag)
        {
            holder.getHistoryRecord().setText(((JSONObject)data.get(position)).getString(ConstantUtilities.ARG_NAME));
            holder.setSearchView(searchView);
            holder.getEditImg().setVisibility(View.INVISIBLE);
            holder.setRecommendFlag(recommendflag);
            return;
        }
        holder.getEditImg().setVisibility(View.INVISIBLE);
        holder.getHistoryRecord().setText(((JSONObject)data.get(position)).getString(ConstantUtilities.ARG_CONTENT));
        holder.setSearchView(searchView);
        if(flag&&!holder.getHistoryRecord().isCursorVisible())
            data.remove(holder.getHistoryRecord().getText());
    }

    @Override
    public int getItemCount() {
        if(data==null)
            return 0;
        return data.size();
    }
}
