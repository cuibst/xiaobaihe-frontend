package com.java.cuiyikai.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java.cuiyikai.R;
import com.java.cuiyikai.activities.EntityActivity;
import com.java.cuiyikai.adapters.viewholders.VisitHistoryTimeViewHolder;
import com.java.cuiyikai.adapters.viewholders.VisitHistoryViewHolder;
import com.java.cuiyikai.network.RequestBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VisitHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final Logger logger = LoggerFactory.getLogger(VisitHistoryAdapter.class);

    private final Context mContext;
    private JSONArray historyArr;
    private int cnt;
    private List<Integer> timeNumber;
    private Map<Integer,Object> allData;
    SimpleDateFormat setTimeFormatInADay = new SimpleDateFormat("HH:mm");
    SimpleDateFormat setTimeFormatInAYear = new SimpleDateFormat("yyyy-MM-dd");
    public VisitHistoryAdapter(Context context)
    {
        mContext=context;
        cnt=0;
    }
    public void addHistory(JSONArray arr)
    {
        historyArr=arr;
        initDateList();
    }
    public void reportRemove(int a)
    {
        logger.info("a={}", a);
        logger.info("class is: {}", allData.get(a).getClass());
        logger.info("a to String is : {}", allData.get(a));
        if(!(allData.get(a) instanceof Date))
        {
            RemoveHistory removeHistory=new RemoveHistory((JSONObject) allData.get(a));
            Thread removeThread=new Thread(removeHistory);
            removeThread.start();
            for(int i=0;i<historyArr.size();i++)
            {
                if(historyArr.get(i).equals(allData.get(a)))
                {
                    historyArr.remove(i);
                    addHistory(historyArr);
                    break;
                }
            }
        }
    }
    public List<Integer> getTimeNumber(){
        return timeNumber;
    }
    public void initDateList()
    {
        timeNumber=new ArrayList<>();
        allData=new HashMap<>();
        cnt=0;
        Date mDate=new Date();
        for(int i=0;i<historyArr.size()+cnt;i++)
        {
            long time=Long.parseLong(historyArr.getJSONObject(i-cnt).get("time").toString());
            Date date=new Date(time);
            if(i==0)
            {
                allData.put(i,date);
                timeNumber.add(i);
                cnt++;
                mDate=date;
            }
            else if(mDate.getYear()!=date.getYear()||mDate.getMonth()!=date.getMonth()||date.getDate()!=mDate.getDate())
            {
                mDate=date;
                allData.put(i,date);
                timeNumber.add(i);
                cnt++;
            }
            else
                allData.put(i,historyArr.getJSONObject(i-cnt));
        }
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType==0)
        {
            view=LayoutInflater.from(mContext).inflate(R.layout.visit_history_content,parent,false);
            return new VisitHistoryViewHolder(view);
        }
        else
        {
            view=LayoutInflater.from(mContext).inflate(R.layout.visit_history_time,parent,false);
            return new VisitHistoryTimeViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder1, int position) {
        if(holder1 instanceof VisitHistoryTimeViewHolder)
        {
            VisitHistoryTimeViewHolder holder=(VisitHistoryTimeViewHolder) holder1;
            Date date=new Date();
            Date allDataDate=(Date)allData.get(position);
            String s;
            if(((allDataDate.getYear()==date.getYear())&&(allDataDate.getMonth()==date.getMonth())&&(date.getDate()==allDataDate.getDate())))
                s="今天";
            else
                s=setTimeFormatInAYear.format(allDataDate);
            holder.getTimeText().setText(s);
        }
        if(holder1 instanceof VisitHistoryViewHolder)
        {
            VisitHistoryViewHolder holder=(VisitHistoryViewHolder)holder1;
            JSONObject m=(JSONObject) allData.get(position);
            String name = m.getString("name");
            String sub = m.getString("subject");
            long time=Long.parseLong(m.getString("time"));
            Date date=new Date(time);
            holder.category.setText(setTimeFormatInADay.format(date));
            holder.view.setOnClickListener((View view) -> {

                Intent intent = new Intent(mContext, EntityActivity.class);
                intent.putExtra("name", name);
                intent.putExtra("subject", sub);
                mContext.startActivity(intent);
            });
            holder.label.setText(name);
            switch (sub) {
                case "chinese" :
                    holder.view.setBackgroundResource(R.drawable.chinese_radius);
                    holder.img.setImageResource(R.drawable.chinese);
                    break;
                case "math" :
                    holder.view.setBackgroundResource(R.drawable.maths_radius);
                    holder.img.setImageResource(R.drawable.maths);
                    break;
                case "english" :
                    holder.view.setBackgroundResource(R.drawable.english_radius);
                    holder.img.setImageResource(R.drawable.english);
                    break;
                case "physics" :
                    holder.view.setBackgroundResource(R.drawable.physics_radius);
                    holder.img.setImageResource(R.drawable.physics);
                    break;
                case "chemistry" :
                    holder.view.setBackgroundResource(R.drawable.chemistry_radius);
                    holder.img.setImageResource(R.drawable.chemistry);
                    break;
                case "biology" :
                    holder.view.setBackgroundResource(R.drawable.biology_radius);
                    holder.img.setImageResource(R.drawable.biology);
                    break;
                case "history" :
                    holder.view.setBackgroundResource(R.drawable.history_radius);
                    holder.img.setImageResource(R.drawable.history);
                    break;
                case "geo" :
                    holder.view.setBackgroundResource(R.drawable.geography_radius);
                    holder.img.setImageResource(R.drawable.geography);
                    break;
                case "politics":
                default:
                    holder.view.setBackgroundResource(R.drawable.politics_radius);
                    holder.img.setImageResource(R.drawable.politics);
                    break;
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(allData.get(position) instanceof Date)
            return 1;
        else
            return 0;
    }
    @Override
    public int getItemCount() {
        if(historyArr==null)
            return 0;
        return allData.size();
    }
}

class RemoveHistory implements Runnable
{
    private final JSONObject num;
    public RemoveHistory(JSONObject i)
    {
        num=i;
    }
    @Override
    public void run() {
        try {
            Map<String, String> map = new HashMap<>();
            map.put("name",(num.getString("name")));
            map.put("subject",(num.getString("subject")));
            String removeHistoryUrl = "/api/history/removeVisitHistory";
            RequestBuilder.sendBackendGetRequest(removeHistoryUrl, map, true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
