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
import com.java.cuiyikai.utilities.ConstantUtilities;

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
import com.java.cuiyikai.activities.SearchActivity;
/**
 * This Adatper is used for the recyclerview in {@link SearchActivity} to show the results item.
 * its Viewholder is {@link ItemViewHolder}
 */
public class SearchAdapter extends RecyclerView.Adapter<ItemViewHolder>{

    public static final int LAYOUT_TYPE_LINEAR = 1;
    public static final int LAYOUT_TYPE_GRID = 2;
    private int type = LAYOUT_TYPE_LINEAR;

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public int getItemViewType(int position) {
        return type;
    }

    static private JSONArray visitHistory;
    private static final Logger logger = LoggerFactory.getLogger(SearchAdapter.class);
    private MyHandler myHandler;
    private final static String getVisitHistoryUrl="/api/history/getVisitHistory";
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
    //handle the JasonObject ,get the real item
    public void addSubject(JSONObject jsonObject) {
        Map<String, List<JSONObject>> actualArray = new TreeMap<>();
        for(String key : jsonObject.keySet()) {
            JSONArray array = jsonObject.getJSONObject(key).getJSONArray(ConstantUtilities.ARG_DATA);
            if(array==null)
                return;
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
        if(viewType == 1)
            return new ItemViewHolder(LayoutInflater.from(mContext).inflate(R.layout.search_content,parent,false));
        else
            return new ItemViewHolder(LayoutInflater.from(mContext).inflate(R.layout.search_content_for_grid, parent, false));
    }
    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position)
    {
        Map<String,Object> map=findActualItem(position);
        String name=((JSONObject)map.get("item")).getString("label");
        String sub=map.get(ConstantUtilities.ARG_NAME).toString();
        holder.getSearchLine().setOnClickListener((View view) -> {
            if(visitHistory!=null) {
                JSONObject m = new JSONObject();
                m.put(ConstantUtilities.ARG_SUBJECT, sub);
                m.put(ConstantUtilities.ARG_NAME, name);
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
            f.putExtra(ConstantUtilities.ARG_NAME,holder.getLabelTextView().getText());
            f.putExtra(ConstantUtilities.ARG_SUBJECT,(String) map.get(ConstantUtilities.ARG_NAME));
            mContext.startActivity(f);
        });
        holder.getLabelTextView().setText(((JSONObject)map.get("item")).getString("label"));
        holder.getLabelTextView().setTextColor(Color.WHITE);
        if(visitHistory!=null)
        {
            if(RequestBuilder.checkedLogin()) {
                for (int i = 0; i < visitHistory.size(); i++) {
                    JSONObject m = (JSONObject) visitHistory.get(i);
                    if (m.getString(ConstantUtilities.ARG_NAME).equals(((JSONObject)map.get("item")).getString("label")) && m.getString(ConstantUtilities.ARG_SUBJECT).equals(map.get(ConstantUtilities.ARG_NAME)))
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
        switch ((String) map.get(ConstantUtilities.ARG_NAME))
        {
            case ConstantUtilities.SUBJECT_CHINESE :
                holder.getSearchLine().setBackgroundResource(R.drawable.chinese_radius);
                holder.getImg().setImageResource(R.drawable.chinese);
                break;
            case ConstantUtilities.SUBJECT_MATH :
                holder.getSearchLine().setBackgroundResource(R.drawable.maths_radius);
                holder.getImg().setImageResource(R.drawable.maths);
                break;
            case ConstantUtilities.SUBJECT_ENGLISH :
                holder.getSearchLine().setBackgroundResource(R.drawable.english_radius);
                holder.getImg().setImageResource(R.drawable.english);
                break;
            case ConstantUtilities.SUBJECT_PHYSICS :
                holder.getSearchLine().setBackgroundResource(R.drawable.physics_radius);
                holder.getImg().setImageResource(R.drawable.physics);
                break;
            case ConstantUtilities.SUBJECT_CHEMISTRY :
                holder.getSearchLine().setBackgroundResource(R.drawable.chemistry_radius);
                holder.getImg().setImageResource(R.drawable.chemistry);
                break;
            case ConstantUtilities.SUBJECT_BIOLOGY :
                holder.getSearchLine().setBackgroundResource(R.drawable.biology_radius);
                holder.getImg().setImageResource(R.drawable.biology);
                break;
            case ConstantUtilities.SUBJECT_HISTORY :
                holder.getSearchLine().setBackgroundResource(R.drawable.history_radius);
                holder.getImg().setImageResource(R.drawable.history);
                break;
            case ConstantUtilities.SUBJECT_GEO :
                holder.getSearchLine().setBackgroundResource(R.drawable.geography_radius);
                holder.getImg().setImageResource(R.drawable.geography);
                break;
            case ConstantUtilities.SUBJECT_POLITICS:
            default:
                holder.getSearchLine().setBackgroundResource(R.drawable.politics_radius);
                holder.getImg().setImageResource(R.drawable.politics);
                break;
        }
        if(((JSONObject)map.get("item")).getString(ConstantUtilities.ARG_CATEGORY).length()==0)
            holder.getCategoryTextView().setText("无");
        else
            holder.getCategoryTextView().setText(((JSONObject)map.get("item")).getString(ConstantUtilities.ARG_CATEGORY));
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
                map.put(ConstantUtilities.ARG_NAME,str);
                break;
            } else {
                position = position - (subject.get(str).size());
            }
        }
        logger.info("map: {}", map);
        return  map;
    }
    // change the sequence of results
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
            list.sort(Comparator.comparing(jsonObject -> jsonObject.getString(ConstantUtilities.ARG_CATEGORY)));
            subject.replace(entry.getKey(), list);
        }
        notifyDataSetChanged();
    }

    public void sortCategoryDescend() {
        for(Map.Entry<String, List<JSONObject>> entry : subject.entrySet()) {
            List<JSONObject> list = entry.getValue();
            list.sort(Comparator.comparing(jsonObject -> jsonObject.getString(ConstantUtilities.ARG_CATEGORY)));
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
                JSONArray arr=object.getJSONArray(ConstantUtilities.ARG_DATA);
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