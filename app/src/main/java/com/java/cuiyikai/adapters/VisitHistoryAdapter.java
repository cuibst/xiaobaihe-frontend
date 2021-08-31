package com.java.cuiyikai.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Message;
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
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VisitHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private JSONArray historyArr;
    private List<Date> datelist;
    private String removeHistoryUrl="/api/history/removeVisitHistory";
    private int cnt=0;
    private Date mDate;
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
    public void SomeContentChanged()
    {
        initDateList();
    }
    public void ReportRemove(int a)
    {
        if(!(allData.get(a) instanceof Date))
        {
            RemoveHistory removeHistory=new RemoveHistory(a);
            Thread removethread=new Thread(removeHistory);
            removethread.start();
            for(int i=0;i<historyArr.size();i++)
            {
                if(historyArr.get(i).equals(allData.get(a)))
                {
                    historyArr.remove(i);
                    addHistory(historyArr);
                }
            }

        }
    }
    public void initDateList()
    {
        datelist=new ArrayList<>();
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
                cnt++;
                mDate=date;
                datelist.add(date);
            }
            else if(mDate.getYear()!=date.getYear()||mDate.getMonth()!=date.getMonth()||date.getDate()!=mDate.getDate())
            {
                mDate=date;
                allData.put(i,date);
                cnt++;
                datelist.add(date);
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
            Date adate=(Date)allData.get(position);
            String s="";
            if(((adate.getYear()==date.getYear())&&(adate.getMonth()==date.getMonth())&&(date.getDate()==adate.getDate())))
                s="今天";
            else
                s=setTimeFormatInAYear.format(adate);
            holder.timetext.setText(s);
        }
        if(holder1 instanceof VisitHistoryViewHolder)
        {
            VisitHistoryViewHolder holder=(VisitHistoryViewHolder)holder1;
            JSONObject m=(JSONObject) allData.get(position);
            String name = m.getString("name");
            String sub = m.getString("subject");
            long time=Long.parseLong(m.get("time").toString());
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
        return historyArr.size()+datelist.size();
    }

    private class RemoveHistory implements Runnable
    {
        private int  num;
        public RemoveHistory(int i)
        {
            num=i;
        }
        @Override
        public void run() {
            try {
                Map<String, String> map = new HashMap<>();
                map.put("name",((JSONObject)allData.get(num)).get("name").toString());
                map.put("subject",((JSONObject)allData.get(num)).get("subject").toString());
                RequestBuilder.sendBackendGetRequest(removeHistoryUrl, map, true);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
