package com.java.cuiyikai.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.java.cuiyikai.activities.MainActivity;
import com.jcodecraeer.xrecyclerview.XRecyclerView;


import com.java.cuiyikai.R;


public class ItemFragment extends Fragment {
    public  XRecyclerView xRecyclerView;
    public Context context;
    public  static String TITLE = "tile";
    public RecyclerView.LayoutManager layoutManager;
    public MainFragment.ItemAdapter itemAdapter;
    public  static String main_activity_url="http://183.172.183.37:8080/api/uri/getname";
    public ItemFragment() {
    }
    public ItemFragment(String s, MainFragment.ItemAdapter a, Context c)
    {
        super();
        TITLE=s;
        itemAdapter=a;
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
        xRecyclerView.setAdapter(itemAdapter);
        return view;
    }
}