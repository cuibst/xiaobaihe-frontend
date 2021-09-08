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
import com.java.cuiyikai.exceptions.BackendTokenExpiredException;
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
import java.util.concurrent.ExecutionException;

import com.java.cuiyikai.activities.VisitHistoryActivity;

/**
 * This Adapter is used for {@link VisitHistoryActivity}
 * It support two kinds of ViewHolder:{@link VisitHistoryViewHolder} which shows the item,
 * {@link VisitHistoryTimeViewHolder} which shows the visit time.
 */
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

    /**
     * Remove the visit history record from the JsonArray and tell backend the remove behavior
     * @param position the loaction of the object in JsonArray
     */
    public void reportRemove(int position)
    {
        logger.info("a={}", position);
        logger.info("class is: {}", allData.get(position).getClass());
        logger.info("a to String is : {}", allData.get(position));
        if(!(allData.get(position) instanceof Date))
        {
            RemoveHistory removeHistory=new RemoveHistory((JSONObject) allData.get(position));
            Thread removeThread=new Thread(removeHistory);
            removeThread.start();
            for(int i=0;i<historyArr.size();i++)
            {
                if(historyArr.get(i).equals(allData.get(position)))
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

    /**
     * To get all the date message, we need to know how many VisitViewTimeHolder we should construct.
     * If this visit record and the below one are generate in two days, we shuold add a new VisitViewTimeHolder between them.
     * considering that both time and item are all need to show as viewholder, we can use a map called allData to save both of them.
     */
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

    /**
     *
     * @param parent viewgroup
     * @param viewType to judge at this position it should be a time or an item.
     * @return one kind of visitviewholder
     */
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

    /**
     *
     * @param holder1 There are two kinds of holders, VisitHistoryViewHolder and VisitViewHistoryTimeViewholder
     *                we should handle them seperately.
     * @param position item's location
     */
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
            holder.getCategory().setText(setTimeFormatInADay.format(date));
            holder.getView().setOnClickListener((View view) -> {

                Intent intent = new Intent(mContext, EntityActivity.class);
                intent.putExtra(ConstantUtilities.ARG_NAME, name);
                intent.putExtra(ConstantUtilities.ARG_SUBJECT, sub);
                mContext.startActivity(intent);
            });
            holder.getLabel().setText(name);
            switch (sub) {
                case ConstantUtilities.SUBJECT_CHINESE :
                    holder.getView().setBackgroundResource(R.drawable.chinese_radius);
                    holder.getImg().setImageResource(R.drawable.chinese);
                    break;
                case ConstantUtilities.SUBJECT_MATH :
                    holder.getView().setBackgroundResource(R.drawable.maths_radius);
                    holder.getImg().setImageResource(R.drawable.maths);
                    break;
                case ConstantUtilities.SUBJECT_ENGLISH :
                    holder.getView().setBackgroundResource(R.drawable.english_radius);
                    holder.getImg().setImageResource(R.drawable.english);
                    break;
                case ConstantUtilities.SUBJECT_PHYSICS :
                    holder.getView().setBackgroundResource(R.drawable.physics_radius);
                    holder.getImg().setImageResource(R.drawable.physics);
                    break;
                case ConstantUtilities.SUBJECT_CHEMISTRY :
                    holder.getView().setBackgroundResource(R.drawable.chemistry_radius);
                    holder.getImg().setImageResource(R.drawable.chemistry);
                    break;
                case ConstantUtilities.SUBJECT_BIOLOGY :
                    holder.getView().setBackgroundResource(R.drawable.biology_radius);
                    holder.getImg().setImageResource(R.drawable.biology);
                    break;
                case ConstantUtilities.SUBJECT_HISTORY :
                    holder.getView().setBackgroundResource(R.drawable.history_radius);
                    holder.getImg().setImageResource(R.drawable.history);
                    break;
                case ConstantUtilities.SUBJECT_GEO :
                    holder.getView().setBackgroundResource(R.drawable.geography_radius);
                    holder.getImg().setImageResource(R.drawable.geography);
                    break;
                case ConstantUtilities.SUBJECT_POLITICS:
                default:
                    holder.getView().setBackgroundResource(R.drawable.politics_radius);
                    holder.getImg().setImageResource(R.drawable.politics);
                    break;
            }
        }
    }

    /**
     * this is used to judge which viewholder should be show in this postion
     * @param position
     * @return one of the viewholder:VisitHistoryViewHolder or VisitHistoryTimeViewHolder.
     */
    @Override
    public int getItemViewType(int position) {
        if(allData.get(position) instanceof Date)
            return 1;
        else
            return 0;
    }

    /**
     * Notice that the time is a kind of viewholder,it should be treated as an item.
     * @return
     */
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
        catch (InterruptedException| ExecutionException| BackendTokenExpiredException e)
        {
            e.printStackTrace();
        }
    }
}
