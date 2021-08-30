package com.java.cuiyikai.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;
import com.java.cuiyikai.R;
import com.java.cuiyikai.activities.EntityActivity;
import com.java.cuiyikai.activities.SearchActivity;
import com.java.cuiyikai.adapters.viewholders.ItemViewHolder;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class SearchAdapter extends RecyclerView.Adapter<ItemViewHolder>{
    public SearchAdapter(Context context)
    {
        mContext=context;
    }

    public void addSubject(JSONObject arr) {
        subject=arr;
        Set<String> set=subject.keySet();
        for(String str:set)
        {
            System.out.println(str);
            if(subject.getJSONObject(str).isEmpty())
                continue;
            sum+=subject.getJSONObject(str).getJSONArray("data").size();
        }
        if(sum<=10)
            size=sum;
        else
            size=10;
    }
    public  int getRealLength()
    {
        return sum;
    }
    public  int size=0;
    private int sum=0;
    private JSONObject subject=new JSONObject();
    private Context mContext;
    public  ItemViewHolder onCreateViewHolder(ViewGroup parent , int viewType)
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

        holder.getLabelTextView().setText(((JSONObject)map.get("item")).get("label").toString());
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
        if(((JSONObject)map.get("item")).get("category").toString().length()==0)
            holder.getCategoryTextView().setText("无");
        else
            holder.getCategoryTextView().setText(((JSONObject)map.get("item")).get("category").toString());
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
            System.out.println(subject.getJSONObject(str).getJSONArray("data"));
            if((subject.getJSONObject(str).getJSONArray("data").size())>position) {
                System.out.printf("Enter: %s %s%n", subject.getJSONObject(str).getJSONArray("data").get(position), str);
                map.put("item", subject.getJSONObject(str).getJSONArray("data").get(position));
                map.put("name",str);
                break;
            } else {
                position = position - (subject.getJSONObject(str).getJSONArray("data").size());
            }
        }
        return  map;
    }
}