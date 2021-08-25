package com.java.cuiyikai.fragments;

import android.content.Context;
import android.os.Bundle;

import com.alibaba.fastjson.JSONArray;
import com.java.cuiyikai.adapters.ItemAdapter;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSONObject;
import com.java.cuiyikai.activities.MainActivity;
import com.java.cuiyikai.network.RequestBuilder;
import com.jcodecraeer.xrecyclerview.XRecyclerView;


import com.java.cuiyikai.R;

import java.util.HashMap;
import java.util.Map;


public class ItemFragment extends Fragment {
    public  XRecyclerView xRecyclerView;
    public Context context;
    public  static String TITLE = "tile";
    public RecyclerView.LayoutManager layoutManager;
    public ItemAdapter itemAdapter;
    private String main_activity_backend_url="/api/uri/getname";
    public  static String main_activity_url="http://183.172.183.37:8080/api/uri/getname";
    public ItemFragment() {
    }
    public ItemFragment(String s, ItemAdapter a, Context c)
    {
        super();
        TITLE=s;
        itemAdapter=a;
        System.out.printf("Initialization called %b%n", itemAdapter == null);
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
        System.out.printf("On item fragment, title = %s, result: %b%n", TITLE, itemAdapter == null);
        xRecyclerView.setAdapter(itemAdapter);
        xRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                try {
                    Map<String,String> map=new HashMap<>();
                    map.put("subject",itemAdapter.chooseSubject);
                    System.out.println(itemAdapter.chooseSubject);
                    JSONObject msg = RequestBuilder.sendBackendGetRequest(main_activity_backend_url, map, false);
                    String rememberedSubject=itemAdapter.chooseSubject;
                    itemAdapter=null;
                    itemAdapter=new ItemAdapter(getActivity(),rememberedSubject);
                    itemAdapter.addSubject(msg.getJSONArray("data"));
                    xRecyclerView.setAdapter(itemAdapter);
                    xRecyclerView.refreshComplete();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onLoadMore() {
                try {
                    Map<String, String> map = new HashMap<>();
                    map.put("subject", itemAdapter.chooseSubject);
                    System.out.println(itemAdapter.chooseSubject);
                    JSONObject msg = RequestBuilder.sendBackendGetRequest(main_activity_backend_url, map, false);
                    itemAdapter.addMoreSubject(msg.getJSONArray("data"));
//                    JSONArray oldSubject=itemAdapter.subject;
//                    String rememberedSubject=itemAdapter.chooseSubject;
//                    itemAdapter=null;
//                    itemAdapter=new ItemAdapter(getActivity(),rememberedSubject);
//                    itemAdapter.addSubject(oldSubject);
//                    xRecyclerView.setAdapter(itemAdapter);
                    xRecyclerView.loadMoreComplete();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
        return view;
    }
}