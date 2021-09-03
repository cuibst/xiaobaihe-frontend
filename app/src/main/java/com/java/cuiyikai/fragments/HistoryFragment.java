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

import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager;
import com.java.cuiyikai.R;
import com.java.cuiyikai.adapters.HistoryListAdapter;
import com.java.cuiyikai.network.RequestBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class HistoryFragment extends Fragment {

    private static final Logger logger = LoggerFactory.getLogger(HistoryFragment.class);

    private RecyclerView recyclerViewForHistory;
    private HistoryListAdapter historyListAdapter;
    private final SearchView searchView;

    public RecyclerView getRecyclerViewForHistory() {
        return recyclerViewForHistory;
    }

    public HistoryListAdapter getHistoryListAdapter() {
        return historyListAdapter;
    }

    public HistoryFragment(SearchView s)
    {
        searchView=s;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = View.inflate(getActivity(), R.layout.fragment_history, null);
        historyListAdapter=new HistoryListAdapter(getActivity(),searchView);
        recyclerViewForHistory=view.findViewById(R.id.historyRecyclerView);
        TextView clearText = view.findViewById(R.id.clearAll);
        clearText.setOnClickListener(v -> {
            historyListAdapter=new HistoryListAdapter(getActivity(),searchView);
            recyclerViewForHistory.setAdapter(historyListAdapter);
            logger.info("clear all");
            ClearAll clearAll = new ClearAll();
            Thread thread = new Thread(clearAll);
            thread.start();
        });
        ChipsLayoutManager chipsLayoutManager = ChipsLayoutManager.newBuilder(getActivity())
                .setChildGravity(Gravity.TOP)
                .setScrollingEnabled(true)
                .setGravityResolver(position -> Gravity.CENTER)
                .setOrientation(ChipsLayoutManager.HORIZONTAL)
                .setRowStrategy(ChipsLayoutManager.STRATEGY_DEFAULT)
                .withLastRow(true)
                .build();
        recyclerViewForHistory.setLayoutManager(chipsLayoutManager);
        historyListAdapter.historyListAdapter=historyListAdapter;
        recyclerViewForHistory.setAdapter(historyListAdapter);
        return view;
    }

    private static class ClearAll implements Runnable
    {

        @Override
        public void run() {
            try{
                Map<String,String> map=new HashMap<>();
                map.put("content","");
                map.put("all","true");
                String removeUrl = "/api/history/removeHistory";
                RequestBuilder.sendBackendGetRequest(removeUrl,map,true);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
