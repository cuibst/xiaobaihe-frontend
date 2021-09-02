package com.java.cuiyikai.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java.cuiyikai.R;
import com.java.cuiyikai.adapters.HistoryListAdapter;
import com.java.cuiyikai.fragments.HistoryFragment;
import com.java.cuiyikai.network.RequestBuilder;

import java.util.HashMap;
import java.util.Map;

public class SearchViewActivity extends AppCompatActivity {

    private FragmentTransaction fragmentTransaction;
    private HistoryFragment historyFragment;
    private RecyclerView recommendXRecyclerView;
    private String getHistoryUrl="/api/history/getHistory";
    private HistoryListAdapter historyListAdapter;
    private String search_url="typeOpen/open/instanceList";
    private  final String[] all_subject_item={"语文","数学","英语","物理","化学","生物","历史","地理","政治"};
    private SearchView searchView;
    public  JSONObject receivedMessage=new JSONObject();
    private FragmentManager fragmentManager=getSupportFragmentManager();
    private String addHistoryUrl="/api/history/addHistory";
    private String main_activity_backend_url="/api/uri/getname";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_view);
        searchView=findViewById(R.id.searchView);
        recommendXRecyclerView=findViewById(R.id.recommendXRecylcerView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recommendXRecyclerView.setLayoutManager(gridLayoutManager);
        initSearchView();
        historyFragment=new HistoryFragment(searchView);
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.historyFragment,historyFragment);
        fragmentTransaction.hide(historyFragment);
        fragmentTransaction.commit();
        historyListAdapter=new HistoryListAdapter(SearchViewActivity.this,searchView);
        historyListAdapter.recommendflag=true;
        recommendXRecyclerView.setAdapter(historyListAdapter);
        GetRecommend getRecommend=new GetRecommend();
        Thread recommendthread=new Thread(getRecommend);
        recommendthread.start();
        if(RequestBuilder.checkedLogin())
        {
            GetHistory getHistory=new GetHistory();
            Thread thread=new Thread(getHistory);
            thread.start();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.show(historyFragment);
            fragmentTransaction.commit();
        }



    }
    public void initSearchView()
    {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                AddHistory addHistory=new AddHistory(s);
                Thread thread=new Thread(addHistory);
                thread.start();
                historyFragment.historyListAdapter.data.add(s);
                historyFragment.historyListAdapter.notifyDataSetChanged();
                StartSearch startSearch=new StartSearch(s);
                Thread startSearchthread=new Thread(startSearch);
                startSearchthread.start();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
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
                chooseSubject = "";
                break;
        }
        return chooseSubject;
    }

    private MyHandler myHandler=new MyHandler();

    private class MyHandler extends Handler{
        @Override
        public void handleMessage(@NonNull Message msg)
        {
            switch(msg.what)
            {
                case 0:
                    Intent intent=new Intent(SearchViewActivity.this,SearchActivity.class);
                    intent.putExtra("msg",receivedMessage.toString());
                    intent.putExtra("name",msg.obj.toString());
                    startActivity(intent);
                    break;
                case 1:
                    Toast.makeText(SearchViewActivity.this, "网络异常，请重试", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    JSONObject data=JSONObject.parseObject(msg.obj.toString());
                    JSONArray arr=data.getJSONArray("data");
                    historyFragment.historyListAdapter.addData(arr);
                    historyFragment.array=arr;
                    historyFragment.recyclerViewForHistory.setAdapter(historyFragment.historyListAdapter);
                    break;
                case 3:
                    JSONObject object=JSONObject.parseObject(msg.obj.toString());
                    historyListAdapter.addData(object.getJSONArray("data"));
                    historyListAdapter.notifyDataSetChanged();
                    break;
            }
        }
    }
    private class  GetRecommend implements  Runnable{

        @Override
        public void run() {
            try {
                Map<String, String> map = new HashMap<>();
                map.put("subject", "");
                JSONObject msg = RequestBuilder.sendBackendGetRequest(main_activity_backend_url, map, false);
                Message message = new Message();
                message.obj = msg.toString();
                message.what = 3;
                myHandler.sendMessage(message);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    private class StartSearch implements Runnable{
        String s;
        StartSearch(String ss)
        {
            s=ss;
        }
        @Override
        public void run() {
            try {
                receivedMessage.clear();
                for(int i=0;i<all_subject_item.length;i++)
                {
                    Map<String,String> map =new HashMap<String,String>();
                    map.put("course",  checkSubject(all_subject_item[i]));
                    map.put("searchKey",s);
                    JSONObject msg = RequestBuilder.sendGetRequest(search_url, map);
                    if((String)msg.get("code")=="-1")
                    {
                        myHandler.sendEmptyMessage(1);
                        break;
                    }
                    else if(msg.get("data")!=null&&!msg.getJSONArray("data").isEmpty()) {
                        receivedMessage.put(checkSubject(all_subject_item[i]),msg);
                    }
                }
                Message message=new Message();
                message.what=0;
                message.obj=s;
                myHandler.sendMessage(message);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }


    private class GetHistory implements Runnable{

        @Override
        public void run() {
            if(!RequestBuilder.checkedLogin())
                return;
            Map<String,String> map=new HashMap<>();
            try {
                JSONObject msg=RequestBuilder.sendBackendGetRequest(getHistoryUrl, map, true);
                Message message=new Message();
                message.what=2;
                message.obj=msg.toString();
                myHandler.sendMessage(message);
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