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
import com.java.cuiyikai.activities.EntityActivity;
import com.java.cuiyikai.adapters.viewholders.ItemViewHolder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class SearchAdapter extends RecyclerView.Adapter<ItemViewHolder>{

    private static final Logger logger = LoggerFactory.getLogger(SearchAdapter.class);

    public SearchAdapter(Context context)
    {
        mContext=context;
    }

    public void addSubject(JSONObject arr) {
        subject=arr;
        sum=0;
        Set<String> set=subject.keySet();
        for(String str:set)
        {
            if(subject.getJSONObject(str).isEmpty())
                continue;
            sum+=subject.getJSONObject(str).getJSONArray("data").size();
        }
        size = Math.min(sum, 10);
    }
    public  int getRealLength()
    {
        return sum;
    }
    private int size=0;
    private int sum=0;
    private JSONObject subject=new JSONObject();
    private final Context mContext;

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @NonNull
    public  ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent , int viewType)
    {
        return new ItemViewHolder(LayoutInflater.from(mContext).inflate(R.layout.search_content,parent,false));
    }
    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position)
    {
        Map<String,Object> map=findActualItem(position);
        holder.getSearchLine().setOnClickListener((View view) -> {
            Intent f=new Intent(mContext,EntityActivity.class);
            f.putExtra("name",holder.getLabelTextView().getText());
            f.putExtra("subject",(String) map.get("name"));
            mContext.startActivity(f);
        });
        holder.getLabelTextView().setText(((JSONObject)map.get("item")).getString("label"));
        switch ((String) map.get("name"))
        {
            case "chinese" :
                holder.getSearchLine().setBackgroundResource(R.drawable.chinese_radius);
                holder.getImg().setImageResource(R.drawable.chinese);
                break;
            case "math" :
                holder.getSearchLine().setBackgroundResource(R.drawable.maths_radius);
                holder.getImg().setImageResource(R.drawable.maths);
                break;
            case "english" :
                holder.getSearchLine().setBackgroundResource(R.drawable.english_radius);
                holder.getImg().setImageResource(R.drawable.english);
                break;
            case "physics" :
                holder.getSearchLine().setBackgroundResource(R.drawable.physics_radius);
                holder.getImg().setImageResource(R.drawable.physics);
                break;
            case "chemistry" :
                holder.getSearchLine().setBackgroundResource(R.drawable.chemistry_radius);
                holder.getImg().setImageResource(R.drawable.chemistry);
                break;
            case "biology" :
                holder.getSearchLine().setBackgroundResource(R.drawable.biology_radius);
                holder.getImg().setImageResource(R.drawable.biology);
                break;
            case "history" :
                holder.getSearchLine().setBackgroundResource(R.drawable.history_radius);
                holder.getImg().setImageResource(R.drawable.history);
                break;
            case "geo" :
                holder.getSearchLine().setBackgroundResource(R.drawable.geography_radius);
                holder.getImg().setImageResource(R.drawable.geography);
                break;
            case "politics":
            default:
                holder.getSearchLine().setBackgroundResource(R.drawable.politics_radius);
                holder.getImg().setImageResource(R.drawable.politics);
                break;
        }
        if(((JSONObject)map.get("item")).getString("category").length()==0)
            holder.getCategoryTextView().setText("无");
        else
            holder.getCategoryTextView().setText(((JSONObject)map.get("item")).getString("category"));
    }
    @Override
    public int getItemCount(){
        return size;
    }
    public Map<String,Object> findActualItem(int position)
    {
        Set<String> set=subject.keySet();
        Map<String,Object> map=new HashMap<>();
        for(String str:set)
        {
            if((subject.getJSONObject(str).getJSONArray("data").size())>position) {
                map.put("item", subject.getJSONObject(str).getJSONArray("data").get(position));
                map.put("name",str);
                break;
            } else {
                position = position - (subject.getJSONObject(str).getJSONArray("data").size());
            }
        }
        logger.info("map: {}", map);
        return  map;
    }
}