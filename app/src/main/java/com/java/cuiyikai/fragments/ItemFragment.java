package com.java.cuiyikai.fragments;

import android.content.Context;
import android.os.Bundle;

import com.alibaba.fastjson.JSON;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.java.cuiyikai.adapters.ItemAdapter;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewPropertyAnimatorListener;
import androidx.fragment.app.Fragment;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.java.cuiyikai.network.RequestBuilder;
import com.jcodecraeer.xrecyclerview.XRecyclerView;


import com.java.cuiyikai.R;

import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;


public class ItemFragment extends Fragment {
    private XRecyclerView xRecyclerView;
    private Context context;
    private String title = "title";
    public  RecyclerView.LayoutManager layoutManager;
    private ItemAdapter itemAdapter;
    private MyHandler myHandler;
    private ProgressBar progressBar;
    private static final String MAIN_ACTIVITY_BACKEND_URL ="/api/uri/getname";

    public ItemFragment(String s, Context c)
    {
        super();
        title =s;
        context=c;
    }

    public ItemFragment(){}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;
        switch (title) {
            case "chinese":
                view = inflater.inflate(R.layout.fragment_item_chinese, container, false);
                break;
            case "math":
                view = inflater.inflate(R.layout.fragment_item_math, container, false);
                break;
            case "english":
                view = inflater.inflate(R.layout.fragment_item_english, container, false);
                break;
            case "physics":
                view = inflater.inflate(R.layout.fragment_item_physics, container, false);
                break;
            case "chemistry":
                view = inflater.inflate(R.layout.fragment_item_chemistry, container, false);
                break;
            case "geo":
                view = inflater.inflate(R.layout.fragment_item_geo, container, false);
                break;
            case "politics":
                view = inflater.inflate(R.layout.fragment_item_politics, container, false);
                break;
            case "history":
                view = inflater.inflate(R.layout.fragment_item_history, container, false);
                break;
            case "biology":
                view = inflater.inflate(R.layout.fragment_item_biology, container, false);
                break;
            default:
                view = inflater.inflate(R.layout.fragment_item_recommend, container, false);
                break;
        }
        xRecyclerView=view.findViewById(R.id.fragment_xrecycleview);
        progressBar=view.findViewById(R.id.waitingBar);
        itemAdapter=new ItemAdapter(getActivity(), title);
        myHandler=new MyHandler(Looper.getMainLooper());
        xRecyclerView.setAdapter(itemAdapter);
        sendMessage();
        xRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                try {
                    Refresh runner=new Refresh(title);
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
                    LoadMore runner=new LoadMore();
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
        view.findViewById(R.id.switch_layout_btn).setOnClickListener(v -> {
            if(itemAdapter.getItemViewType(0) == ItemAdapter.LAYOUT_TYPE_LINEAR) {
                itemAdapter.setType(ItemAdapter.LAYOUT_TYPE_GRID);
                xRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
                ((FloatingActionButton)view.findViewById(R.id.switch_layout_btn)).setImageResource(R.drawable.linear);
            } else {
                itemAdapter.setType(ItemAdapter.LAYOUT_TYPE_LINEAR);
                LinearLayoutManager manager = new LinearLayoutManager(getActivity());
                manager.setOrientation(LinearLayoutManager.VERTICAL);
                xRecyclerView.setLayoutManager(manager);
                ((FloatingActionButton)view.findViewById(R.id.switch_layout_btn)).setImageResource(R.drawable.grid);
            }
            itemAdapter.notifyDataSetChanged();
        });

        xRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            private boolean visible = true;
            private int distance = 0;
            private final Interpolator interpolator = new FastOutSlowInInterpolator();

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(distance > 10 && visible) {
                    visible = false;
                    ViewCompat.animate(view.findViewById(R.id.switch_layout_btn)).scaleX(0.0F).scaleY(0.0F).alpha(0.0F).setInterpolator(interpolator).withLayer()
                            .setListener(new ViewPropertyAnimatorListener() {
                                @Override
                                public void onAnimationStart(View view) {

                                }

                                public void onAnimationEnd(View view) {
                                    view.setVisibility(View.GONE);
                                }

                                @Override
                                public void onAnimationCancel(View view) {

                                }
                            }).start();
                    distance = 0;
                } else if(distance < -20 && !visible) {
                    view.findViewById(R.id.switch_layout_btn).setVisibility(View.VISIBLE);
                    visible = true;
                    ViewCompat.animate(view.findViewById(R.id.switch_layout_btn)).scaleX(1.0f).scaleY(1.0f).alpha(1.0f)
                            .setInterpolator(interpolator).withLayer().setListener(null)
                            .start();
                    distance = 0;
                }
                if((visible && dy > 0) || (!visible && dy < 0)) {
                    distance += dy;
                }
            }
        });

        return view;
    }
    public void sendMessage()
    {
        ConnectToWeb runner=new ConnectToWeb(title);
        Thread thread=new Thread(runner);
        thread.start();
    }

    private class ConnectToWeb implements Runnable{
        private final String chooseSubject;
        ConnectToWeb(String s)
        {
            chooseSubject=s;
        }
        @Override
        public void run() {
            try {
                Map<String, String> map = new HashMap<>();
                map.put("subject", chooseSubject);
                JSONObject msg = RequestBuilder.sendBackendGetRequest(MAIN_ACTIVITY_BACKEND_URL, map, false);
                Message message=new Message();
                message.obj=msg.toString();
                message.what=0;
                myHandler.sendMessage(message);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    private class Refresh implements Runnable{
        private final String chooseSubject;
        Refresh(String s)
        {
            chooseSubject=s;
        }
        @Override
        public void run() {
            try {
                Map<String, String> map = new HashMap<>();
                map.put("subject", chooseSubject);
                JSONObject msg = RequestBuilder.sendBackendGetRequest(MAIN_ACTIVITY_BACKEND_URL, map, false);
                Message message=new Message();
                message.obj=msg.toString();
                message.what=1;
                myHandler.sendMessage(message);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    private class LoadMore implements Runnable{
        @Override
        public void run() {
            try {
                Map<String, String> map = new HashMap<>();
                map.put("subject", itemAdapter.getChooseSubject());
                JSONObject msg = RequestBuilder.sendBackendGetRequest(MAIN_ACTIVITY_BACKEND_URL, map, false);
                Message message=new Message();
                message.obj=msg.toString();
                message.what=2;
                myHandler.sendMessage(message);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    private class MyHandler extends Handler {
        MyHandler(Looper looper){
            super(looper);
        }
        @Override
        public void handleMessage(@NonNull Message msg) {
            JSONObject object;
            if (msg.what == 0) {
                progressBar.setVisibility(View.INVISIBLE);
                object = JSON.parseObject(msg.obj.toString());
                itemAdapter.addSubject(object.getJSONArray("data"));
                itemAdapter.notifyDataSetChanged();
            } else if (msg.what == 1) {
                object = JSON.parseObject(msg.obj.toString());
                itemAdapter.addSubject(object.getJSONArray("data"));
                itemAdapter.notifyDataSetChanged();
                xRecyclerView.refreshComplete();
            } else if (msg.what == 2) {
                object = JSON.parseObject(msg.obj.toString());
                itemAdapter.addMoreSubject(object.getJSONArray("data"));
                itemAdapter.notifyDataSetChanged();
                xRecyclerView.loadMoreComplete();

            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LoggerFactory.getLogger(ItemFragment.class).info("Item Fragment destroyed");
    }

}