package com.java.cuiyikai.adapters.viewholders;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;
import com.java.cuiyikai.R;
import com.java.cuiyikai.activities.SearchViewActivity;
import com.java.cuiyikai.adapters.HistoryListAdapter;
import com.java.cuiyikai.network.RequestBuilder;

import java.util.HashMap;
import java.util.Map;

public class HistoryViewHolder extends RecyclerView.ViewHolder {
    public TextView historyRecord;
    public boolean flag;
    public String subject;
    public Context mContext;
    public boolean recommendflag=false;
    public SearchView searchView;
    public HistoryListAdapter historyListAdapter;
    public  ImageView editImg;
    private String RemoveUrl="/api/history/removeHistory";
    private String addHistoryUrl="/api/history/addHistory";
    public HistoryViewHolder(View view) {
        super(view);
        editImg=view.findViewById(R.id.iv_edit);
        historyRecord=view.findViewById(R.id.historyrecord);
        historyRecord.setLongClickable(true);
        editImg.setVisibility(View.INVISIBLE);
        historyRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SearchViewActivity)mContext).subject=subject;
                ((SearchViewActivity)mContext).subjectText.setText(((SearchViewActivity)mContext).reverseCheckSubject(subject));
                searchView.setQuery(historyRecord.getText(),true);
            }
        });
        historyRecord.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(recommendflag)
                    return false;
                editImg.setVisibility(View.VISIBLE);
                return true;
            }
        });
        editImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                historyRecord.setVisibility(View.INVISIBLE);
                editImg.setVisibility(View.INVISIBLE);
                ClearOne clearOne=new ClearOne(historyRecord.getText().toString());
                System.out.println("clear: "+historyRecord.getText().toString());
                Thread thread=new Thread(clearOne);
                thread.start();
            }
        });
    }
    private class ClearOne implements Runnable
    {
        private String s;
        ClearOne(String ss)
        {
            s=ss;
        }
        @Override
        public void run() {
            try{
                Map<String,String> map=new HashMap<>();
                map.put("content",s);
                map.put("all","false");
                RequestBuilder.sendBackendGetRequest(RemoveUrl,map,true);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    private class AddHistory implements Runnable{
        private String s;
        public AddHistory(String ss)
        {
            s=ss;
        }
        @Override
        public void run() {
            if(!RequestBuilder.checkedLogin())
                return;
            Map<String,String> map=new HashMap<>();
            map.put("content",s);
            try {
                RequestBuilder.sendBackendGetRequest(addHistoryUrl, map, true);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
