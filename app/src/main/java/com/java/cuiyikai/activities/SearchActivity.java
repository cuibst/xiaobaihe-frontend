package com.java.cuiyikai.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.java.cuiyikai.adapters.SelectAdapter;
import com.java.cuiyikai.fragments.SelectFragment;
import com.alibaba.fastjson.JSONObject;
import com.java.cuiyikai.R;
import com.java.cuiyikai.network.RequestBuilder;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import com.java.cuiyikai.adapters.SearchAdapter;

import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class SearchActivity extends AppCompatActivity {

    private String subject;
    private JSONObject receivedMessage;
    private List<String> checkSubject=new ArrayList<>();
    private List<String> checkMarked=new ArrayList<>();
    private final FragmentManager fragmentManager=getSupportFragmentManager();
    private FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
    private String searchContent;
    private XRecyclerView searchRecyclerView;
    private SearchAdapter searchAdapter;
    private SelectFragment selectFragment;
    private Button selectButton;
    SelectAdapter selectAdapter;
    String searchUrl ="typeOpen/open/instanceList";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Intent prevIntent=getIntent();
        Bundle prevBundle = prevIntent.getExtras();
        subject=prevBundle.getString("sub");
        selectAdapter=new SelectAdapter(SearchActivity.this);
        selectFragment=new SelectFragment(selectAdapter);
        searchRecyclerView =findViewById(R.id.search_rcy);
        searchRecyclerView.setArrowImageView(R.drawable.waiting);
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
        searchAdapter =new SearchAdapter(SearchActivity.this);
        receivedMessage = JSON.parseObject(prevBundle.getString("msg"));
        searchContent=prevBundle.getString("name");
        searchRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                Refresh refresh=new Refresh();
                Thread refreshThread=new Thread(refresh);
                refreshThread.start();
            }

            @Override
            public void onLoadMore() {
                if(searchAdapter.getSize()+10 < searchAdapter.getRealLength())
                    searchAdapter.setSize(searchAdapter.getSize() + 10);
                else if(searchAdapter.getSize() <= (searchAdapter.getRealLength()))
                    searchAdapter.setSize(searchAdapter.getRealLength());
                searchRecyclerView.loadMoreComplete();
            }
        });
        searchAdapter =new SearchAdapter(SearchActivity.this);
        searchAdapter.addSubject(receivedMessage);
        searchRecyclerView.setAdapter(searchAdapter);
        selectButton=findViewById(R.id.selectButton);
        fragmentTransaction.add(R.id.selectFragment, selectFragment);
        fragmentTransaction.hide(selectFragment);
        fragmentTransaction.commit();
        initFragment();
        selectButton.setOnClickListener((View view) -> {
            fragmentTransaction = fragmentManager.beginTransaction();
            if (selectFragment.isHidden()) {
                selectButton.setText("完成");
                fragmentTransaction.show(selectFragment);
            } else {
                selectButton.setText("筛选");
                fragmentTransaction.hide(selectFragment);
                checkMarked = selectFragment.returnCheckMarked();
                checkSubject = selectFragment.returnCheckSubject();
                Set<String> set = receivedMessage.keySet();
                Map<String, Object> totalMap = new HashMap<>();
                JSONObject finalInfo;
                for (int lesson = 0; lesson < checkSubject.size(); lesson++) {
                    Map<String, Object> lessonMap = new HashMap<>();
                    for (String str : set) {
                        if (!str.equals(checkSubject(checkSubject.get(lesson))))
                            continue;
                        JSONArray categoryMap = new JSONArray();
                        for (int category = 0; category < checkMarked.size(); category++) {
                            for (int i = 0; i < receivedMessage.getJSONObject(str).getJSONArray("data").size(); i++) {
                                String s = (String) ((JSONObject) receivedMessage.getJSONObject(str).getJSONArray("data").get(i)).get("category");
                                if (!s.equals(checkMarked.get(category)))
                                    continue;
                                JSONObject cate = ((JSONObject) receivedMessage.getJSONObject(str).getJSONArray("data").get(i));
                                if (categoryMap.contains(cate))
                                    continue;
                                categoryMap.add(cate);
                            }
                            lessonMap.put("data", categoryMap);
                        }
                        totalMap.put(checkSubject(checkSubject.get(lesson)), lessonMap);
                    }

                }
                finalInfo = new JSONObject(totalMap);
                searchAdapter = null;
                searchAdapter = new SearchAdapter(SearchActivity.this);
                searchAdapter.addSubject(finalInfo);
                searchRecyclerView.setAdapter(searchAdapter);
            }
            fragmentTransaction.commit();
        });

        RadioGroup radioGroup = findViewById(R.id.sortingRadioGroup);
        ((RadioButton)findViewById(R.id.radioDefault)).setSelected(true);

        radioGroup.setOnCheckedChangeListener((radioGroup1, i) -> {
            LoggerFactory.getLogger(SearchActivity.class).info("Radio check changed, {}", i);
            if (i == R.id.radioNameDesc) {
                searchAdapter.sortNameAscend();
            } else if (i == R.id.radioNameAsc) {
                searchAdapter.sortNameDescend();
            } else if (i == R.id.radioCategoryAsc) {
                searchAdapter.sortCategoryAscend();
            } else if (i == R.id.radioCategoryDesc) {
                searchAdapter.sortCategoryDescend();
            } else {
                searchAdapter.addSubject(receivedMessage);
                searchAdapter.notifyDataSetChanged();
            }
        });

    }

    public String reverseCheckSubject(String title)
    {
        String chooseSubject;
        switch (title) {
            case "chinese":
                chooseSubject = "语文";
                break;
            case "math":
                chooseSubject = "数学";
                break;
            case "english":
                chooseSubject = "英语";
                break;
            case "physics":
                chooseSubject = "物理";
                break;
            case "chemistry":
                chooseSubject = "化学";
                break;
            case "history":
                chooseSubject = "历史";
                break;
            case "geo":
                chooseSubject = "地理";
                break;
            case "politics":
                chooseSubject = "政治";
                break;
            case "biology":
                chooseSubject = "生物";
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + title);
        }
        return chooseSubject;
    }
    public String checkSubject(String title)
    {
        String chooseSubject;
        switch (title) {
            case "语文":
                chooseSubject = "chinese";
                break;
            case "数学":
                chooseSubject = "math";
                break;
            case "英语":
                chooseSubject = "english";
                break;
            case "物理":
                chooseSubject = "physics";
                break;
            case "化学":
                chooseSubject = "chemistry";
                break;
            case "历史":
                chooseSubject = "history";
                break;
            case "地理":
                chooseSubject = "geo";
                break;
            case "政治":
                chooseSubject = "politics";
                break;
            case "生物":
                chooseSubject = "biology";
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + title);
        }
        return chooseSubject;
    }

    public void initFragment()
    {
        Set<String> set=receivedMessage.keySet();
        ArrayList<String> type=new ArrayList<>();
        ArrayList<String> subjectType=new ArrayList<>();
        for(String str:set)
        {
            subjectType.add(str);
            for(int i=0;i<receivedMessage.getJSONObject(str).getJSONArray("data").size();i++)
            {
                String s=(String)((JSONObject)receivedMessage.getJSONObject(str).getJSONArray("data").get(i)).get("category");
                if(!type.contains(s))
                    type.add(s);
            }
        }
        selectFragment.getSubjectType(subjectType);
        selectFragment.getType(type);
        selectButton.setText("筛选");
    }

    private class Refresh implements Runnable {

        @Override
        public void run() {
            try {
                receivedMessage.clear();
                Map<String,String> map =new HashMap<>();
                map.put("course",  subject);
                map.put("searchKey",searchContent);
                JSONObject msg = RequestBuilder.sendGetRequest(searchUrl, map);
                if(msg.get("code").equals("-1"))
                {
                    handler.sendEmptyMessage(1);
                }
                else if(msg.get("data")!=null&&!msg.getJSONArray("data").isEmpty()) {
                    receivedMessage.put(subject,msg);
                }
                Message message=new Message();
                message.what=0;
                message.obj=searchContent;
                handler.sendMessage(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private final MyHandler handler=new MyHandler();
    private class MyHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch(msg.what)
            {
                case 0:
                    searchAdapter.addSubject(receivedMessage);
                    searchAdapter.notifyDataSetChanged();
                    searchRecyclerView.refreshComplete();
                    break;
                case 1:
                    Toast.makeText(SearchActivity.this, "网络异常，请重试", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + msg.what);
            }
        }
    }
}