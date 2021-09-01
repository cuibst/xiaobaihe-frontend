package com.java.cuiyikai.fragments;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONArray;
import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager;
import com.beloo.widget.chipslayoutmanager.gravity.IChildGravityResolver;
import com.java.cuiyikai.R;
import com.java.cuiyikai.adapters.HistoryListAdapter;
import com.java.cuiyikai.network.RequestBuilder;

import java.util.HashMap;
import java.util.Map;

public class HistoryFragment extends Fragment {
    private String RemoveUrl="/api/history/removeHistory";
    public RecyclerView recyclerViewForHistory;
    public HistoryListAdapter historyListAdapter;
    public TextView clearText,editText;
    public JSONArray array;
    private SearchView searchView;
    public HistoryFragment(SearchView s)
    {
        searchView=s;
    }
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = View.inflate(getActivity(), R.layout.fragment_history, null);
        historyListAdapter=new HistoryListAdapter(getActivity(),searchView);
        recyclerViewForHistory=view.findViewById(R.id.historyRecyclerView);
        clearText=view.findViewById(R.id.clearAll);
        clearText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                historyListAdapter=new HistoryListAdapter(getActivity(),searchView);
                recyclerViewForHistory.setAdapter(historyListAdapter);
                System.out.println("clear all");
                ClearAll clearAll = new ClearAll();
                Thread thread = new Thread(clearAll);
                thread.start();
            }
        });
        ChipsLayoutManager chipsLayoutManager = ChipsLayoutManager.newBuilder(getActivity())
                .setChildGravity(Gravity.TOP)
                .setScrollingEnabled(true)
                .setGravityResolver(new IChildGravityResolver() {
                    @Override
                    public int getItemGravity(int position) {
                        return Gravity.CENTER;
                    }
                })
                .setOrientation(ChipsLayoutManager.HORIZONTAL)
                .setRowStrategy(ChipsLayoutManager.STRATEGY_DEFAULT)
                .withLastRow(true)
                .build();
        recyclerViewForHistory.setLayoutManager(chipsLayoutManager);
        historyListAdapter.historyListAdapter=historyListAdapter;
        recyclerViewForHistory.setAdapter(historyListAdapter);
        return view;
    }

    private class ClearAll implements Runnable
    {

        @Override
        public void run() {
            try{
                Map<String,String> map=new HashMap<>();
                map.put("content","");
                map.put("all","true");
                RequestBuilder.sendBackendGetRequest(RemoveUrl,map,true);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
