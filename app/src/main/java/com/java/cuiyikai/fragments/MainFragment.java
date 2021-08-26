package com.java.cuiyikai.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.java.cuiyikai.adapters.ItemAdapter;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.android.material.tabs.TabLayout;
import com.java.cuiyikai.R;
import com.java.cuiyikai.activities.CategoryActivity;
import com.java.cuiyikai.activities.MainActivity;
import com.java.cuiyikai.network.RequestBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class MainFragment extends Fragment {
    private ViewPager viewpgr;
    private ImageView tabAdd;
    private Fragment[] itemFragment;
    private ViewPagerFragmentAdapter viewPagerFragmentAdapter ;
    private final String[] all_subject_item={"语文","数学","英语","物理","化学","生物","历史","地理","政治"};
    private TabLayout tabLayout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = View.inflate(getActivity(), R.layout.fragment_main, null);
        initViewPager(view);
        itemFragment=new Fragment[all_subject_item.length];

        tabLayout=view.findViewById(R.id.tablayout1);
        tabLayout.setupWithViewPager(viewpgr);
        tabAdd = view.findViewById(R.id.tab_add);
        tabAdd.setOnClickListener((View v) -> {
            Intent intent=new Intent(getActivity(), CategoryActivity.class);
            startActivity(intent);
        });
        return view;
    }

    private void initViewPager(View view) {
        viewpgr = view.findViewById(R.id.viewpgr1);
        viewPagerFragmentAdapter = new ViewPagerFragmentAdapter(getActivity().getSupportFragmentManager());
        try(InputStream is = getActivity().getAssets().open(CategoryActivity.getSubjectData())) {
            int length = is.available();
            byte[] buffer = new byte[length];
            is.read(buffer);
            String result = new String(buffer, "utf-8");
            JSONObject jsonObject = JSON.parseObject(result);
            viewpgr.setAdapter(viewPagerFragmentAdapter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class ViewPagerFragmentAdapter extends FragmentPagerAdapter {
        ViewPagerFragmentAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }
        @NonNull
        @Override
        public Fragment getItem(int position) {
            initfragment(position);
//            System.out.println(itemFragment[position].T);
            return itemFragment[position];
        }

        @Override
        public int getCount() {
            return all_subject_item.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return all_subject_item[position];
        }
    }

    private String main_activity_url="/api/uri/getname";

    public void initfragment(int position)
    {
        System.out.println("position: "+position);
        String TITLE=all_subject_item[position];
        String chooseSubject = ((MainActivity)getActivity()).checkSubject(TITLE);
        ItemFragment fragment=new ItemFragment(chooseSubject,getActivity());
        itemFragment[position]=fragment;
    }
}
