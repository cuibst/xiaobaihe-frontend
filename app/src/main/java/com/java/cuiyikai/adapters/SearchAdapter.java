package com.java.cuiyikai.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java.cuiyikai.R;
import com.java.cuiyikai.activities.EntityActivity;
import com.java.cuiyikai.adapters.viewholders.ItemViewHolder;
import com.java.cuiyikai.network.RequestBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;


public class SearchAdapter extends RecyclerView.Adapter<ItemViewHolder>{
    static private JSONArray visitHistory;
    private static final Logger logger = LoggerFactory.getLogger(SearchAdapter.class);
    private MyHandler myHandler;
    private final String getVisitHistoryUrl="/api/history/getVisitHistory";
    public SearchAdapter(Context context)
    {
        mContext=context;
        myHandler=new MyHandler(context.getMainLooper());
        if(RequestBuilder.checkedLogin()) {
            if (visitHistory == null) {
                GetVisitHistory getVisitHistory = new GetVisitHistory();
                Thread getHistoryThread = new Thread(getVisitHistory);
                getHistoryThread.start();
            }
        }
        else
        {
            visitHistory=null;
        }
    }

    public void addSubject(JSONObject jsonObject) {
        Map<String, List<JSONObject>> actualArray = new TreeMap<>();
        for(String key : jsonObject.keySet()) {
            JSONArray array = jsonObject.getJSONObject(key).getJSONArray("data");
            List<JSONObject> objectList = new ArrayList<>();
            for(Object o : array) {
                objectList.add(JSON.parseObject(o.toString()));
            }
            actualArray.put(key, objectList);
        }
        addSubject(actualArray);
    }

    public void addSubject(Map<String, List<JSONObject>> arr) {
        subject=arr;
        sum=0;
        Set<String> set=subject.keySet();
        for(String str:set)
        {
            if(subject.get(str) == null || subject.get(str).isEmpty())
                continue;
            sum+=subject.get(str).size();
        }
        size = Math.min(sum, 10);
    }
    public  int getRealLength()
    {
        return sum;
    }
    private int size=0;
    private int sum=0;
    private Map<String, List<JSONObject>> subject = new TreeMap<>();
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
        String name=((JSONObject)map.get("item")).getString("label");
        String sub=map.get("name").toString();
        holder.getSearchLine().setOnClickListener((View view) -> {
            if(visitHistory!=null) {
                JSONObject m = new JSONObject();
                m.put("subject", sub);
                m.put("name", name);
                visitHistory.add(m);
            }
            else
            {
                if(RequestBuilder.checkedLogin()) {
                    GetVisitHistory getVisitHistory = new GetVisitHistory();
                    Thread getHistoryThread = new Thread(getVisitHistory);
                    getHistoryThread.start();
                }
            }
            if(RequestBuilder.checkedLogin())
                holder.getLabelTextView().setTextColor(Color.GRAY);
            Intent f=new Intent(mContext,EntityActivity.class);
            f.putExtra("name",holder.getLabelTextView().getText());
            f.putExtra("subject",(String) map.get("name"));
            mContext.startActivity(f);
        });
        holder.getLabelTextView().setText(((JSONObject)map.get("item")).getString("label"));
        holder.getLabelTextView().setTextColor(Color.WHITE);
        if(visitHistory!=null)
        {
            if(RequestBuilder.checkedLogin()) {
                for (int i = 0; i < visitHistory.size(); i++) {
                    JSONObject m = (JSONObject) visitHistory.get(i);
                    if (m.getString("name").equals(((JSONObject)map.get("item")).getString("label")) && m.getString("subject").equals(map.get("name")))
                    {
                        holder.getLabelTextView().setTextColor(Color.GRAY);
                        break;
                    }
                }
            }
        }
        else
        {
            if(RequestBuilder.checkedLogin()) {
                GetVisitHistory getVisitHistory = new GetVisitHistory();
                Thread getHistoryThread = new Thread(getVisitHistory);
                getHistoryThread.start();
            }
        }
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
            if((subject.get(str).size())>position) {
                map.put("item", subject.get(str).get(position));
                map.put("name",str);
                break;
            } else {
                position = position - (subject.get(str).size());
            }
        }
        logger.info("map: {}", map);
        return  map;
    }

    public void sortNameAscend() {
        for(Map.Entry<String, List<JSONObject>> entry : subject.entrySet()) {
            List<JSONObject> list = entry.getValue();
            list.sort(Comparator.comparing(jsonObject -> jsonObject.getString("label")));
            subject.replace(entry.getKey(), list);
        }
        notifyDataSetChanged();
    }

    public void sortNameDescend() {
        for(Map.Entry<String, List<JSONObject>> entry : subject.entrySet()) {
            List<JSONObject> list = entry.getValue();
            logger.info("key : {} , value : {}", entry.getKey(), entry.getValue());
            list.sort(Comparator.comparing(jsonObject -> jsonObject.getString("label")));
            Collections.reverse(list);
            subject.replace(entry.getKey(), list);
        }
        notifyDataSetChanged();
    }

    public void sortCategoryAscend() {
        for(Map.Entry<String, List<JSONObject>> entry : subject.entrySet()) {
            List<JSONObject> list = entry.getValue();
            list.sort(Comparator.comparing(jsonObject -> jsonObject.getString("category")));
            subject.replace(entry.getKey(), list);
        }
        notifyDataSetChanged();
    }

    public void sortCategoryDescend() {
        for(Map.Entry<String, List<JSONObject>> entry : subject.entrySet()) {
            List<JSONObject> list = entry.getValue();
            list.sort(Comparator.comparing(jsonObject -> jsonObject.getString("category")));
            Collections.reverse(list);
            subject.replace(entry.getKey(), list);
        }
        notifyDataSetChanged();
    }
    private class GetVisitHistory implements Runnable{

        @Override
        public void run() {
            Map<String, String> map = new HashMap<>();
            try {
                JSONObject object= RequestBuilder.sendBackendGetRequest(getVisitHistoryUrl, map, true);
                JSONArray arr=object.getJSONArray("data");
                System.out.println("data: "+arr.toString());
                Message msg=new Message();
                msg.what=0;
                msg.obj=arr.toString();
                myHandler.sendMessage(msg);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    private class MyHandler extends Handler {
        MyHandler(Looper looper){
            super(looper);
        }
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch(msg.what)
            {
                case 0:
                    JSONArray arr=JSONArray.parseArray(msg.obj.toString());
                    visitHistory=arr;
                    notifyDataSetChanged();
            }
        }
    }
}