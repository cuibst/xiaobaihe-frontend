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

//public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.RcyViewHolder>{
//    public SearchAdapter(Context context)
//    {
//        mContext=context;
//    }
//    class RcyViewHolder extends RecyclerView.ViewHolder{
//        public String subject;
//        private TextView labeltxt,categorytxt;
//        public RcyViewHolder(View view)
//        {
//            super(view);
//            labeltxt=view.findViewById(R.id.label);
//            img=view.findViewById(R.id.img);
//            categorytxt=view.findViewById(R.id.category);
//            searchline=view.findViewById(R.id.search_line);
//        }
//    }
//    public void addSubject(JSONObject arr) {
//        subject=arr;
//        Set<String> set=subject.keySet();
//        if(!subject.isEmpty()) {
//            for (String str : set) {
//                if(!subject.getJSONObject(str).isEmpty())
//                    sum += subject.getJSONObject(str).getJSONArray("data").size();
//            }
//        }
//        if(sum<=10)
//            size=sum;
//        else
//            size=10;
//    }
//    public  int getRealLength()
//    {
//        return sum;
//    }
//    public  int size=0;
//    public  int sum=0;
//    private JSONObject subject=new JSONObject();
//    private Context mContext;
//    private LinearLayout searchline;
//    private ImageView img;
//    public  SearchAdapter.RcyViewHolder onCreateViewHolder(ViewGroup parent , int viewType)
//    {
//        return new SearchAdapter.RcyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.search_content,parent,false));
//    }
//    @Override
//    public void onBindViewHolder(SearchAdapter.RcyViewHolder holder, int position)
//    {
//        Map<String,Object> map=findActualItem(position);
//        searchline.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                System.out.println(map.get("name"));
//                Intent f=new Intent(mContext, EntityActivity.class);
//                f.putExtra("name",holder.labeltxt.getText());
//                f.putExtra("subject",(String) map.get("name"));
//                mContext.startActivity(f);
//            }
//        });
//        holder.labeltxt.setText(((JSONObject)map.get("item")).get("label").toString());
//        holder.subject=(String) map.get("name");
//        switch ((String) map.get("name"))
//        {
//            case "physics":
//                img.setImageResource(R.drawable.phy);
//                break;
//            case "chemistry":
//                img.setImageResource(R.drawable.che);
//                break;
//            case "biology":
//                img.setImageResource(R.drawable.bio);
//                break;
//            default:
//                img.setImageResource(R.drawable.book);
//                break;
//        }
//        if(((JSONObject)map.get("item")).get("category").toString().length()==0)
//            holder.categorytxt.setText("无");
//        else
//            holder.categorytxt.setText(((JSONObject)map.get("item")).get("category").toString());
//    }
//    @Override
//    public int getItemCount(){
//        return size;
//    }
//    public Map<String,Object> findActualItem(int position)
//    {
//        Set<String> set=subject.keySet();
//        Map<String,Object> map=new HashMap<String, Object>();
//        for(String str:set)
//        {
//            if((subject.getJSONObject(str).getJSONArray("data").size())>position) {
//                map.put("item", subject.getJSONObject(str).getJSONArray("data").get(position));
//                map.put("name",str);
//                break;
//            }
//            else
//                position-=subject.getJSONObject(str).getJSONArray("data").size();
//        }
//        return map;
//    }
//}

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
            sum+=subject.getJSONObject(str).getJSONArray("data").size();
        }
        if(sum<=10)
            size=sum;
        else
            size=10;
    }
    public int getRealLength()
    {
        return sum;
    }
    public int size=0;
    int sum=0;
    private JSONObject subject=new JSONObject();
    private Context mContext;
    public ItemViewHolder onCreateViewHolder(ViewGroup parent , int viewType)
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
        Map<String,Object> map=new HashMap<String, Object>();
        for(String str:set)
        {
            System.out.println(subject.getJSONObject(str).getJSONArray("data"));
            if((subject.getJSONObject(str).getJSONArray("data").size())>position) {
                map.put("item", subject.getJSONObject(str).getJSONArray("data").get(position));
                map.put("name",str);
                break;
            }
//            fragmentTransaction.commit();
        }
        return  map;
    };
}