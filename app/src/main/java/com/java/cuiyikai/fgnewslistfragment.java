package com.java.cuiyikai;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class fgnewslistfragment extends Fragment {
    private int type;
    private TextView tv_news;
    private List<String> title=new ArrayList<>();
    public fgnewslistfragment newInstance(int type)
    {
        Bundle args=new Bundle();
        fgnewslistfragment fragment =new fgnewslistfragment();
        args.putInt("type",type);
        fragment.setArguments(args);
        return fragment;
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fg_news_list,null);
    }
    public  void onViewCreated(View view ,Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);
        type=getArguments().getInt("type");
        tv_news=view.findViewById(R.id.tv_news);
        if(type<title.size())
            tv_news.setText(title.get(type));
    }
}
