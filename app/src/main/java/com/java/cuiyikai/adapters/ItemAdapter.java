package com.java.cuiyikai.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONArray;
import com.java.cuiyikai.R;
import com.java.cuiyikai.activities.EntityActivity;
import com.java.cuiyikai.adapters.viewholders.ItemViewHolder;

public class ItemAdapter extends RecyclerView.Adapter<ItemViewHolder>{
    public ItemAdapter(Context context, String s) {
        mContext=context;
        chooseSubject=s;
    }
    public  String chooseSubject;
    public JSONArray subject=new JSONArray();
    private Context mContext;
    private LinearLayout searchline;
    public void addSubject(JSONArray arr) {
        subject=arr;
    }
    public void addMoreSubject(JSONArray arr) {
        subject.addAll(arr);
        System.out.println(subject.toString());
    }
    public void clearSubject()
    {
        subject.clear();
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent , int viewType) {
        return new ItemViewHolder(LayoutInflater.from(mContext).inflate(R.layout.search_content,parent,false));
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {

        String name = subject.getJSONObject(position).getString("name");
        String sub = subject.getJSONObject(position).getString("subject");

        holder.getLabelTextView().setText(name);
        holder.getSearchLine().setOnClickListener((View view) -> {

            Intent intent = new Intent(mContext, EntityActivity.class);
            intent.putExtra("name", name);
            intent.putExtra("subject", sub);
            mContext.startActivity(intent);
        });

        switch (sub) {
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

        holder.getCategoryTextView().setText("");
    }
    @Override
    public int getItemCount(){
        return subject.size();
    }
}