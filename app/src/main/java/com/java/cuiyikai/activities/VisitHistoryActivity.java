package com.java.cuiyikai.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java.cuiyikai.R;
import com.java.cuiyikai.adapters.VisitHistoryAdapter;
import com.java.cuiyikai.network.RequestBuilder;
import com.yanzhenjie.recyclerview.OnItemClickListener;
import com.yanzhenjie.recyclerview.OnItemMenuClickListener;
import com.yanzhenjie.recyclerview.SwipeMenu;
import com.yanzhenjie.recyclerview.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.SwipeMenuItem;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class VisitHistoryActivity extends AppCompatActivity {
    SimpleDateFormat setTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private ImageView backupImg;
    private String getHistoryUrl="/api/history/getVisitHistory";
    private SearchView searchHistory;
    private SwipeRecyclerView swipeRecyclerView;
    private VisitHistoryAdapter visitHistoryAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visit_history);
        visitHistoryAdapter=new VisitHistoryAdapter(VisitHistoryActivity.this);
        GetHistory getHistory=new GetHistory();
        Thread getHistoryThread=new Thread(getHistory);
        getHistoryThread.start();
        backupImg=findViewById(R.id.backImg);
        searchHistory=findViewById(R.id.searchHistory);
        swipeRecyclerView=findViewById(R.id.swipeRecyclerView);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(VisitHistoryActivity.this);
        swipeRecyclerView.setLayoutManager(linearLayoutManager);
        swipeRecyclerView.setSwipeMenuCreator(new SwipeMenuCreator() {
            @Override
            public void onCreateMenu(SwipeMenu leftMenu, SwipeMenu rightMenu, int position) {
                SwipeMenuItem deleteItem = new SwipeMenuItem(VisitHistoryActivity.this);
                deleteItem.setBackgroundColor(Color.parseColor("#FF3D39"))
                        .setText("删除")
                        .setTextColor(Color.WHITE)
                        .setHeight(ViewGroup.LayoutParams.MATCH_PARENT)
                        .setWidth(170);

                rightMenu.addMenuItem(deleteItem);

            }
        });
        swipeRecyclerView.setOnItemMenuClickListener(new OnItemMenuClickListener() {
            @Override
            public void onItemClick(SwipeMenuBridge menuBridge, int adapterPosition) {
                menuBridge.closeMenu();
                visitHistoryAdapter.ReportRemove(adapterPosition);
                visitHistoryAdapter.notifyDataSetChanged();
            }
        });
        swipeRecyclerView.setAdapter(visitHistoryAdapter);
        backupImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
    }
    MyHandler myHandler=new MyHandler();
    private class GetHistory implements Runnable
    {

        @Override
        public void run() {
            try {
                Map<String, String> map = new HashMap<>();
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
            switch(msg.what)
            {
                case 0:
                    JSONArray arr=JSONArray.parseArray(msg.obj.toString());
                    visitHistoryAdapter.addHistory(arr);
                    visitHistoryAdapter.notifyDataSetChanged();
                    break;
            }
        }
    }
}