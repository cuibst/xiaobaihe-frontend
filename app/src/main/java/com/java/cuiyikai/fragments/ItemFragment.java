package com.java.cuiyikai.fragments;

import android.content.Context;
import android.os.Bundle;

import com.java.cuiyikai.adapters.ItemAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;


import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.java.cuiyikai.network.RequestBuilder;
import com.jcodecraeer.xrecyclerview.XRecyclerView;


import com.java.cuiyikai.R;

import java.util.HashMap;
import java.util.Map;


public class ItemFragment extends Fragment {
    public  XRecyclerView xRecyclerView;
    public  Context context;
    public  String TITLE = "tile";
    public  RecyclerView.LayoutManager layoutManager;
    public  ItemAdapter itemAdapter;
    private ProgressBar progressBar;
    private String main_activity_backend_url="/api/uri/getname";
    public ItemFragment() {
    }
    public ItemFragment(String s, Context c)
    {
        super();
        TITLE=s;
        context=c;
    }
    public static ItemFragment newInstance(String item) {
        return new ItemFragment();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;
        if(TITLE.equals("chinese"))
            view = inflater.inflate(R.layout.fragment_item_chinese, container, false);
        else if (TITLE.equals("math"))
            view = inflater.inflate(R.layout.fragment_item_math, container, false);
        else if (TITLE.equals("english"))
            view = inflater.inflate(R.layout.fragment_item_english, container, false);
        else if (TITLE.equals("physics"))
            view = inflater.inflate(R.layout.fragment_item_physics, container, false);
        else if (TITLE.equals("chemistry"))
            view = inflater.inflate(R.layout.fragment_item_chemistry, container, false);
        else if (TITLE.equals("geo"))
            view = inflater.inflate(R.layout.fragment_item_geo, container, false);
        else if (TITLE.equals("politics"))
            view = inflater.inflate(R.layout.fragment_item_politics, container, false);
        else if (TITLE.equals("history"))
            view = inflater.inflate(R.layout.fragment_item_history, container, false);
        else if (TITLE.equals("biology"))
            view = inflater.inflate(R.layout.fragment_item_biology, container, false);
        else
            view =inflater.inflate(R.layout.fragment_item_recommend, container, false);
        xRecyclerView=view.findViewById(R.id.fragment_xrecycleview);
        progressBar=view.findViewById(R.id.waitingBar);
        itemAdapter=new ItemAdapter(getActivity(),TITLE);
        xRecyclerView.setAdapter(itemAdapter);
        sendMessage();
        xRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                try {
                    Refresh runner=new Refresh(TITLE);
                    Thread thread=new Thread(runner);
                    thread.start();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    xRecyclerView.refreshComplete();
                    Toast.makeText(context,"更新失败，请重试",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onLoadMore() {
                try {
                    LoadMore runner=new LoadMore(TITLE);
                    Thread thread=new Thread(runner);
                    thread.start();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    xRecyclerView.loadMoreComplete();
                    Toast.makeText(context,"加载失败，请重试",Toast.LENGTH_LONG).show();
                }
            }
        });
        return view;
    }
    public void sendMessage()
    {
        ConnectToWeb runner=new ConnectToWeb(TITLE);
        Thread thread=new Thread(runner);
        thread.start();
    }

    MyHandler handler=new MyHandler();
    private class ConnectToWeb implements Runnable{
        private String chooseSubject;
        ConnectToWeb(String s)
        {
            chooseSubject=s;
        }
        @Override
        public void run() {
            try {
                Map<String, String> map = new HashMap<>();
                map.put("subject", chooseSubject);
                JSONObject msg = RequestBuilder.sendBackendGetRequest(main_activity_backend_url, map, false);
                Message message=new Message();
                message.obj=msg.toString();
                message.what=0;
                handler.sendMessage(message);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    private class Refresh implements Runnable{
        private String chooseSubject;
        Refresh(String s)
        {
            chooseSubject=s;
        }
        @Override
        public void run() {
            try {
                Map<String, String> map = new HashMap<>();
                map.put("subject", chooseSubject);
                JSONObject msg = RequestBuilder.sendBackendGetRequest(main_activity_backend_url, map, false);
                Message message=new Message();
                message.obj=msg.toString();
                message.what=1;
                handler.sendMessage(message);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    private class LoadMore implements Runnable{
        private String chooseSubject;
        LoadMore(String s)
        {
            chooseSubject=s;
        }
        @Override
        public void run() {
            try {
                Map<String, String> map = new HashMap<>();
                map.put("subject", itemAdapter.chooseSubject);
                JSONObject msg = RequestBuilder.sendBackendGetRequest(main_activity_backend_url, map, false);
                Message message=new Message();
                message.obj=msg.toString();
                message.what=2;
                handler.sendMessage(message);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    private class MyHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            JSONObject object;
            switch(msg.what)
            {
                case 0:
                    progressBar.setVisibility(View.INVISIBLE);
                    object=JSONObject.parseObject(msg.obj.toString());
                    itemAdapter.addSubject(object.getJSONArray("data"));
                    itemAdapter.notifyDataSetChanged();
                    break;
                case 1:
                    object=JSONObject.parseObject(msg.obj.toString());
                    itemAdapter.addSubject(object.getJSONArray("data"));
                    itemAdapter.notifyDataSetChanged();
                    xRecyclerView.refreshComplete();
                    break;
                case 2:
                    object=JSONObject.parseObject(msg.obj.toString());
                    itemAdapter.addMoreSubject(object.getJSONArray("data"));
                    itemAdapter.notifyDataSetChanged();
                    xRecyclerView.loadMoreComplete();
            }
        }
    }
}