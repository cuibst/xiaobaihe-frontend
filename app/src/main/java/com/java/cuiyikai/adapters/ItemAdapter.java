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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java.cuiyikai.R;
import com.java.cuiyikai.activities.EntityActivity;
import com.java.cuiyikai.adapters.viewholders.ItemViewHolder;
import com.java.cuiyikai.network.RequestBuilder;
import com.java.cuiyikai.utilities.ConstantUtilities;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ItemAdapter extends RecyclerView.Adapter<ItemViewHolder>{

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
    public ItemAdapter(Context context, String s) {
        mContext=context;
        myHandler=new MyHandler(context.getMainLooper());
        chooseSubject=s;
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

    private final String chooseSubject;
    private JSONArray subject=new JSONArray();
    private final Context mContext;
    private MyHandler myHandler;
    private final String getVisitHistoryUrl="/api/history/getVisitHistory";
    private static final Logger logger = LoggerFactory.getLogger(ItemAdapter.class);

    public String getChooseSubject() {
        return chooseSubject;
    }

    public void addSubject(JSONArray arr) {
        subject=arr;
    }
    public void addMoreSubject(JSONArray arr) {
        JSONArray newSubject=new JSONArray();
        newSubject.addAll(subject);
        newSubject.addAll(arr);
        subject=newSubject;
        logger.info("Array size: {}", arr.size());
        logger.info("New subject size: {}", newSubject.size());
        logger.info("subject size: {}", subject.size());
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent , int viewType) {
        if(viewType == 1)
            return new ItemViewHolder(LayoutInflater.from(mContext).inflate(R.layout.search_content,parent,false));
        else
            return new ItemViewHolder(LayoutInflater.from(mContext).inflate(R.layout.search_content_for_grid, parent, false));
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        holder.getCategoryTextView().setVisibility(View.GONE);
        String name = subject.getJSONObject(position).getString(ConstantUtilities.ARG_NAME);
        String sub = subject.getJSONObject(position).getString(ConstantUtilities.ARG_SUBJECT);
        holder.getLabelTextView().setText(name);
        holder.getLabelTextView().setTextColor(Color.WHITE);
        if(visitHistory!=null)
        {
            if(RequestBuilder.checkedLogin()) {
                for (int i = 0; i < visitHistory.size(); i++) {
                    JSONObject m = (JSONObject) visitHistory.get(i);
                    if (m.getString(ConstantUtilities.ARG_NAME).equals(name) && m.getString(ConstantUtilities.ARG_SUBJECT).equals(sub)) {
                        System.out.println(name+"------"+sub);
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
            Intent intent = new Intent(mContext, EntityActivity.class);
            intent.putExtra(ConstantUtilities.ARG_NAME, name);
            intent.putExtra(ConstantUtilities.ARG_SUBJECT, sub);
            mContext.startActivity(intent);
        });
        switch (sub) {
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

        holder.getCategoryTextView().setText("");
    }
    @Override
    public int getItemCount(){
        if(subject==null)
            return 0;
        return subject.size();
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

    private class GetVisitHistory implements Runnable{

        @Override
        public void run() {
            Map<String, String> map = new HashMap<>();
            try {
                JSONObject object=RequestBuilder.sendBackendGetRequest(getVisitHistoryUrl, map, true);
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
}