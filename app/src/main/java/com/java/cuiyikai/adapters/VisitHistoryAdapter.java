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
import com.java.cuiyikai.utilities.ConstantUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

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
            Calendar calendar=Calendar.getInstance();
            calendar.setTime(date);
            Calendar allDataCalendar=Calendar.getInstance();
            allDataCalendar.setTime(mDate);
            if(i==0)
            {
                allData.put(i,date);
                timeNumber.add(i);
                cnt++;
                mDate=date;
            }
            else  if(!((allDataCalendar.get(Calendar.YEAR)==calendar.get(Calendar.YEAR))&&(allDataCalendar.get(Calendar.MONTH)==calendar.get(Calendar.MONTH))&&(allDataCalendar.get(Calendar.DATE)==calendar.get(Calendar.DATE))))
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
            Calendar calendar=Calendar.getInstance();
            calendar.setTime(date);
            Calendar allDataCalendar=Calendar.getInstance();
            allDataCalendar.setTime(allDataDate);
            String s;
            if((allDataCalendar.get(Calendar.YEAR)==calendar.get(Calendar.YEAR))&&(allDataCalendar.get(Calendar.MONTH)==calendar.get(Calendar.MONTH))&&(allDataCalendar.get(Calendar.DATE)==calendar.get(Calendar.DATE)))
                s="今天";
            else
                s=setTimeFormatInAYear.format(allDataDate);
            holder.getTimeText().setText(s);
        }
        if(holder1 instanceof VisitHistoryViewHolder)
        {
            VisitHistoryViewHolder holder=(VisitHistoryViewHolder)holder1;
            JSONObject m=(JSONObject) allData.get(position);
            String name = m.getString(ConstantUtilities.ARG_NAME);
            String sub = m.getString(ConstantUtilities.ARG_SUBJECT);
            long time=Long.parseLong(m.getString("time"));
            Date date=new Date(time);
            holder.category.setText(setTimeFormatInADay.format(date));
            holder.view.setOnClickListener((View view) -> {

                Intent intent = new Intent(mContext, EntityActivity.class);
                intent.putExtra(ConstantUtilities.ARG_NAME, name);
                intent.putExtra(ConstantUtilities.ARG_SUBJECT, sub);
                mContext.startActivity(intent);
            });
            holder.label.setText(name);
            switch (sub) {
                case ConstantUtilities.SUBJECT_CHINESE :
                    holder.view.setBackgroundResource(R.drawable.chinese_radius);
                    holder.img.setImageResource(R.drawable.chinese);
                    break;
                case ConstantUtilities.SUBJECT_MATH :
                    holder.view.setBackgroundResource(R.drawable.maths_radius);
                    holder.img.setImageResource(R.drawable.maths);
                    break;
                case ConstantUtilities.SUBJECT_ENGLISH :
                    holder.view.setBackgroundResource(R.drawable.english_radius);
                    holder.img.setImageResource(R.drawable.english);
                    break;
                case ConstantUtilities.SUBJECT_PHYSICS :
                    holder.view.setBackgroundResource(R.drawable.physics_radius);
                    holder.img.setImageResource(R.drawable.physics);
                    break;
                case ConstantUtilities.SUBJECT_CHEMISTRY :
                    holder.view.setBackgroundResource(R.drawable.chemistry_radius);
                    holder.img.setImageResource(R.drawable.chemistry);
                    break;
                case ConstantUtilities.SUBJECT_BIOLOGY :
                    holder.view.setBackgroundResource(R.drawable.biology_radius);
                    holder.img.setImageResource(R.drawable.biology);
                    break;
                case ConstantUtilities.SUBJECT_HISTORY :
                    holder.view.setBackgroundResource(R.drawable.history_radius);
                    holder.img.setImageResource(R.drawable.history);
                    break;
                case ConstantUtilities.SUBJECT_GEO :
                    holder.view.setBackgroundResource(R.drawable.geography_radius);
                    holder.img.setImageResource(R.drawable.geography);
                    break;
                case ConstantUtilities.SUBJECT_POLITICS:
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
            map.put(ConstantUtilities.ARG_NAME,(num.getString(ConstantUtilities.ARG_NAME)));
            map.put(ConstantUtilities.ARG_SUBJECT,(num.getString(ConstantUtilities.ARG_SUBJECT)));
            String removeHistoryUrl = "/api/history/removeVisitHistory";
            RequestBuilder.sendBackendGetRequest(removeHistoryUrl, map, true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
