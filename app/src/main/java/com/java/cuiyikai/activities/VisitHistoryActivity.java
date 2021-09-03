package com.java.cuiyikai.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java.cuiyikai.R;
import com.java.cuiyikai.adapters.VisitHistoryAdapter;
import com.java.cuiyikai.network.RequestBuilder;
import com.yanzhenjie.recyclerview.SwipeMenuItem;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VisitHistoryActivity extends AppCompatActivity {
    private SwipeRecyclerView swipeRecyclerView;
    private List<Integer> timeNumber;
    private VisitHistoryAdapter visitHistoryAdapter;
    private JSONArray historyArr;
    private TextView historyText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visit_history);
        visitHistoryAdapter=new VisitHistoryAdapter(VisitHistoryActivity.this);
        GetHistory getHistory=new GetHistory();
        Thread getHistoryThread=new Thread(getHistory);
        getHistoryThread.start();
        ImageView backupImg = findViewById(R.id.backImg);
        SearchView searchHistory = findViewById(R.id.searchHistory);
        swipeRecyclerView=findViewById(R.id.swipeRecyclerView);
        historyText=findViewById(R.id.historytext);
        searchHistory.setOnSearchClickListener(v -> historyText.setVisibility(View.INVISIBLE));
        searchHistory.setOnCloseListener(() -> {
            historyText.setVisibility(View.VISIBLE);
            visitHistoryAdapter.addHistory(historyArr);
            timeNumber=visitHistoryAdapter.getTimeNumber();
            for(int i=0;i<timeNumber.size();i++) {
                swipeRecyclerView.setSwipeItemMenuEnabled(timeNumber.get(i),false);
            }
            visitHistoryAdapter.notifyDataSetChanged();
            return false;
        });
        searchHistory.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(query.equals(""))
                {
                    visitHistoryAdapter.addHistory(historyArr);
                    timeNumber=visitHistoryAdapter.getTimeNumber();
                    for(int i=0;i<timeNumber.size();i++) {
                        swipeRecyclerView.setSwipeItemMenuEnabled(timeNumber.get(i),false);
                    }
                }
                else
                {
                    JSONArray arr=new JSONArray();
                    for(int i=0;i<historyArr.size();i++)
                    {
                        String s=historyArr.get(i).toString();
                        if(s.contains(query))
                        {
                            arr.add(historyArr.get(i));
                        }
                    }
                    visitHistoryAdapter.addHistory(arr);
                    timeNumber=visitHistoryAdapter.getTimeNumber();
                    for(int i=0;i<timeNumber.size();i++) {
                        swipeRecyclerView.setSwipeItemMenuEnabled(timeNumber.get(i),false);
                    }
                }
                visitHistoryAdapter.notifyDataSetChanged();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.equals(""))
                {
                    visitHistoryAdapter.addHistory(historyArr);
                    timeNumber=visitHistoryAdapter.getTimeNumber();
                    for(int i=0;i<timeNumber.size();i++) {
                        swipeRecyclerView.setSwipeItemMenuEnabled(timeNumber.get(i),false);
                    }
                }
                else
                {
                    JSONArray arr=new JSONArray();
                    for(int i=0;i<historyArr.size();i++)
                    {
                        String s=historyArr.get(i).toString();
                        if(s.contains(newText))
                        {
                            arr.add(historyArr.get(i));
                        }
                    }
                    visitHistoryAdapter.addHistory(arr);
                    timeNumber=visitHistoryAdapter.getTimeNumber();
                    for(int i=0;i<timeNumber.size();i++) {
                        swipeRecyclerView.setSwipeItemMenuEnabled(timeNumber.get(i),false);
                    }
                }
                visitHistoryAdapter.notifyDataSetChanged();
                return true;
            }
        });
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(VisitHistoryActivity.this);
        swipeRecyclerView.setLayoutManager(linearLayoutManager);
        swipeRecyclerView.setSwipeMenuCreator((leftMenu, rightMenu, position) -> {
            SwipeMenuItem deleteItem = new SwipeMenuItem(VisitHistoryActivity.this);
            deleteItem.setBackgroundColor(Color.parseColor("#FF3D39"))
                    .setText("删除")
                    .setTextColor(Color.WHITE)
                    .setHeight(ViewGroup.LayoutParams.MATCH_PARENT)
                    .setWidth(170);

            rightMenu.addMenuItem(deleteItem);

        });
        swipeRecyclerView.setOnItemMenuClickListener((menuBridge, adapterPosition) -> {
            menuBridge.closeMenu();
            visitHistoryAdapter.reportRemove(adapterPosition);
            visitHistoryAdapter.notifyItemRemoved(adapterPosition);
        });
        swipeRecyclerView.setAdapter(visitHistoryAdapter);
        backupImg.setOnClickListener(v -> onBackPressed());
    }
    private final MyHandler myHandler=new MyHandler();
    private class GetHistory implements Runnable
    {

        @Override
        public void run() {
            try {
                Map<String, String> map = new HashMap<>();
                String getHistoryUrl = "/api/history/getVisitHistory";
                JSONObject msg = RequestBuilder.sendBackendGetRequest(getHistoryUrl, map, true);
                JSONArray arr=msg.getJSONArray("data");
                Message message=new Message();
                message.what=0;
                message.obj=arr.toString();
                myHandler.sendMessage(message);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    private class MyHandler extends Handler {

        @Override
        public void handleMessage(@NonNull android.os.Message msg) {
            if (msg.what == 0) {
                JSONArray arr = JSON.parseArray(msg.obj.toString());
                visitHistoryAdapter.addHistory(arr);
                historyArr = arr;
                timeNumber = visitHistoryAdapter.getTimeNumber();
                for (int i = 0; i < timeNumber.size(); i++) {
                    swipeRecyclerView.setSwipeItemMenuEnabled(timeNumber.get(i), false);
                }
                visitHistoryAdapter.notifyDataSetChanged();
            }
        }
    }
}