package com.java.cuiyikai.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.java.cuiyikai.MainApplication;
import com.java.cuiyikai.adapters.ItemAdapter;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.android.material.tabs.TabLayout;
import com.java.cuiyikai.R;
import com.java.cuiyikai.activities.CategoryActivity;
import com.java.cuiyikai.activities.EntityActivity;
import com.java.cuiyikai.activities.MainActivity;
import com.java.cuiyikai.network.RequestBuilder;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainFragment extends Fragment {

    private ViewPager viewpgr;
    private ImageView tabAdd;
    private ItemFragment[] itemFragment;
    private List<String> all_subject_item=new ArrayList<>(Arrays.asList("语文","数学","英语","物理","化学","生物","历史","地理","政治"));
    private TabLayout tabLayout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = View.inflate(getActivity(), R.layout.fragment_main, null);

        initViewPager(view);

        itemFragment=new ItemFragment[all_subject_item.size()];

        tabLayout=view.findViewById(R.id.tablayout1);
        tabLayout.setupWithViewPager(viewpgr);

        tabAdd = view.findViewById(R.id.tab_add);
        tabAdd.setOnClickListener((View v) -> {
            Intent intent=new Intent(getActivity(), CategoryActivity.class);
            startActivityForResult(intent, 1);
        });

        return view;
    }

    private ViewPagerFragmentAdapter viewPagerFragmentAdapter;

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1) {
            viewPagerFragmentAdapter.clear(viewpgr);
            itemFragment=new ItemFragment[all_subject_item.size()];
            initViewPager(getView());
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void initViewPager(View view) {
        viewpgr = view.findViewById(R.id.viewpgr1);
        viewPagerFragmentAdapter = new ViewPagerFragmentAdapter(getActivity().getSupportFragmentManager());
        all_subject_item = ((MainApplication)getActivity().getApplication()).getSubjects();
        viewpgr.setAdapter(viewPagerFragmentAdapter);
    }

    public class ViewPagerFragmentAdapter extends FragmentPagerAdapter {
        ViewPagerFragmentAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }
        @NonNull
        @Override
        public Fragment getItem(int position) {
            initfragment(position);
            return itemFragment[position];
        }

        @Override
        public int getCount() {
            return all_subject_item.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return all_subject_item.get(position);
        }

        public void clear(ViewGroup container) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            for (ItemFragment fragment : itemFragment)
                if (fragment != null)
                    transaction.remove(fragment);
            transaction.commit();
        }
    }

//    public class ItemAdapter extends RecyclerView.Adapter<MainFragment.ItemAdapter.ItemViewHolder>{
//        ItemAdapter(Context context, String s)
//        {
//            mContext=context;
//            chooseSubject=s;
//        }
//        public  String chooseSubject;
//        private int size=0;
//        private JSONArray subject=new JSONArray();
//        private Context mContext;
//        private LinearLayout searchline;
//        private String for_pic_chose;
//        private ImageView img;
//        public void addSubject(JSONArray arr) {
//            subject=arr;
//            size = Math.min(subject.size(), 10);
//        }
//        public void addMoreSubject(JSONArray arr)
//        {
//            subject.addAll(arr);
//            System.out.println(subject.toString());
//            size = Math.min(subject.size(), 10);
//        }
//        public void clearSubject()
//        {
//            subject.clear();
//        }
//        public MainFragment.ItemAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent , int viewType)
//        {
//            return new MainFragment.ItemAdapter.ItemViewHolder(LayoutInflater.from(mContext).inflate(R.layout.search_content,parent,false));
//        }
//        public void addpic(String s)
//        {
//            for_pic_chose=s;
//        }
//        @Override
//        public void onBindViewHolder(MainFragment.ItemAdapter.ItemViewHolder holder, int position)
//        {
//            holder.labeltxt.setText(subject.getJSONObject(position).get("name").toString());
//            switch (for_pic_chose)
//            {
//                case "physics":
//                    img.setImageResource(R.drawable.phy);
//                    break;
//                case "chemistry":
//                    img.setImageResource(R.drawable.che);
//                    break;
//                case "biology":
//                    img.setImageResource(R.drawable.bio);
//                    break;
//                default:
//                    img.setImageResource(R.drawable.book);
//                    break;
//
//            }
//            holder.categorytxt.setText("");
//        }
//        @Override
//        public int getItemCount(){
//            return size;
//        }
//        class ItemViewHolder extends RecyclerView.ViewHolder{
//            private TextView labeltxt,categorytxt;
//            public ItemViewHolder(View view)
//            {
//                super(view);
//                labeltxt=view.findViewById(R.id.label);
//                img=view.findViewById(R.id.img);
//                categorytxt=view.findViewById(R.id.category);
//                searchline=view.findViewById(R.id.search_line);
//                searchline.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        Intent f = new Intent(getActivity(), EntityActivity.class);
//                        f.putExtra("name", labeltxt.getText());
//                        f.putExtra("subject", chooseSubject);
//                        startActivity(f);
//                    }
//                });
//            }
//        }
//    }

    private String main_activity_url="/api/uri/getname";

    public void initfragment(int position)
    {
        System.out.printf("On init fragment : %d%n", position);
        String TITLE=all_subject_item.get(position);
        String chooseSubject = ((MainActivity)getActivity()).checkSubject(TITLE);
        Map<String,String> map=new HashMap<>();
        map.put("subject",chooseSubject);
        try {
            ItemAdapter itemAdapter = new ItemAdapter(getActivity(),chooseSubject);
            JSONObject msg = RequestBuilder.sendBackendGetRequest(main_activity_url, map, RequestBuilder.checkedLogin());
            itemAdapter.addSubject(msg.getJSONArray("data"));
            System.out.printf("In init fragment result %b%n", itemAdapter == null);
            ItemFragment fragment = new ItemFragment(chooseSubject,itemAdapter,getActivity());
            itemFragment[position]=fragment;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
