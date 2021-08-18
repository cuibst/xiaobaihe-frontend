package com.java.cuiyikai;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.java.cuiyikai.activities.EntityActivity;
import com.java.cuiyikai.activities.MainActivity;
import com.java.cuiyikai.activities.SearchActivity;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

public class ItemFragment extends Fragment {

    private XRecyclerView xRecyclerView;
    private static final String TITLE = "tile";
    public ItemFragment() {
    }

    public static ItemFragment newInstance(String item) {
        ItemFragment fragment = new ItemFragment();
        Bundle args = new Bundle();
        args.putString(TITLE, item);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item, container, false);
        Bundle bundle = getArguments();
        xRecyclerView=view.findViewById(R.id.xrecycleview);

        return view;
    }

}