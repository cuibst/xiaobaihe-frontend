package com.java.cuiyikai.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;


import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java.cuiyikai.ItemFragment;
import com.java.cuiyikai.R;

import com.java.cuiyikai.network.RequestBuilder;
import com.xuexiang.xui.XUI;
import android.widget.LinearLayout;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private  final String[] all_subject_item={"语文","数学","英语","物理","化学","生物","历史","地理","政治"};
    public  List<String> radiolist=new ArrayList<String>();
    private List<Fragment> fragments=new ArrayList<>();
    private List<String> fragmentTitles=new ArrayList<>();
    private Button btnForLogIn;
    private TextView searchtxt;
    private ViewPager viewpgr;
    private TabLayout tabLayout;
    private String main_activity_url="/api/uri/getname";
    private ItemFragment itemFragment[];
    String searchcontent;
    AdapterView.OnItemSelectedListener a;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        XUI.init(this.getApplication());
        XUI.debug(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Drawable searchimg=getResources().getDrawable(R.drawable.search);
        searchimg.setBounds(10,0,110,100);
        init();
        searchtxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });


        searchtxt.setCompoundDrawables(searchimg,null,null,null);
        searchcontent=searchtxt.getText().toString();


        btnForLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    public void init(){
        searchtxt=findViewById(R.id.searchText);
        tabLayout=findViewById(R.id.tablayout1);
        btnForLogIn=findViewById(R.id.btn_for_login);
        viewpgr=findViewById(R.id.viewpgr1);
        itemFragment=new ItemFragment[all_subject_item.length];
        initViewPager();
        tabLayout.setupWithViewPager(viewpgr);
    }
    private void initViewPager()
    {
        ItemFragment itemFragment;
        ViewPagerFragmentAdapter viewPagerFragmentAdapter=new ViewPagerFragmentAdapter(getSupportFragmentManager());

        viewpgr.setAdapter(viewPagerFragmentAdapter);
    }
    public class ViewPagerFragmentAdapter extends FragmentPagerAdapter
    {
        ViewPagerFragmentAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }
        public void setXRecyclerviewAdapter(RecyclerView.Adapter adapter)
        {

        }
        @NonNull
        @Override
        public Fragment getItem(int position)
        {
            initfragment(position);
            return itemFragment[position];
        }

        @Override
        public int getCount()
        {
            return all_subject_item.length;
        }
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object)
        {
            super.destroyItem(container, position, object);
        }
        public CharSequence getPageTitle(int position)
        {
            return all_subject_item[position];
        }
    }
    public class ItemAdapter extends RecyclerView.Adapter<MainActivity.ItemAdapter.ItemViewHolder>{
        ItemAdapter(Context context)
        {
            mContext=context;
        }
        private String name;
        private String chooseSubject;
        class ItemViewHolder extends RecyclerView.ViewHolder{
            private TextView labeltxt,categorytxt;
            public ItemViewHolder(View view)
            {
                super(view);
                labeltxt=view.findViewById(R.id.label);
                img=view.findViewById(R.id.img);
                categorytxt=view.findViewById(R.id.category);
                searchline=view.findViewById(R.id.search_line);
                searchline.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent f=new Intent(MainActivity.this,EntityActivity.class);
                        f.putExtra("name",name);
                        f.putExtra("subject",chooseSubject);
                        startActivity(f);
                    }
                });
            }
        }
        public void addsubjectname(String s)
        {
            chooseSubject=s;
        }
        public void addSubject(JSONArray arr) {
            subject=arr;
            if(subject.size()<=10)
                size=subject.size();
            else
                size=10;
        }
        private int size=0;
        private JSONArray subject=new JSONArray();
        private Context mContext;
        private LinearLayout searchline;
        private String for_pic_chose;
        private ImageView img;
        public MainActivity.ItemAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent , int viewType)
        {
            return new MainActivity.ItemAdapter.ItemViewHolder(LayoutInflater.from(mContext).inflate(R.layout.search_content,parent,false));
        }
        public void addpic(String s)
        {
            for_pic_chose=s;
        }
        @Override
        public void onBindViewHolder(MainActivity.ItemAdapter.ItemViewHolder holder, int position)
        {
            name=subject.getJSONObject(position).get("name").toString();
            System.out.println(name);
            holder.labeltxt.setText(subject.getJSONObject(position).get("name").toString());
            switch (for_pic_chose)
            {
                case "physics":
                    img.setImageResource(R.drawable.phy);
                    break;
                case "chemistry":
                    img.setImageResource(R.drawable.che);
                    break;
                case "biology":
                    img.setImageResource(R.drawable.bio);
                    break;
                default:
                    img.setImageResource(R.drawable.book);
                    break;

            }
            holder.categorytxt.setText("");
        }
        @Override
        public int getItemCount(){
            return size;
        }
    }
    public void initfragment(int position)
    {
            String TITLE=all_subject_item[position];
            String chooseSubject=CheckSubject(TITLE);
            Map<String,String> map=new HashMap();
            map.put("subject",chooseSubject);
            try {
                ItemAdapter itemAdapter=new ItemAdapter(MainActivity.this);
                JSONObject msg = RequestBuilder.sendBackendGetRequest(main_activity_url, map,false);
                itemAdapter.addpic(chooseSubject);
                itemAdapter.addsubjectname(chooseSubject);
                itemAdapter.addSubject(msg.getJSONArray("data"));
                RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(MainActivity.this);
                ItemFragment fragment = new ItemFragment(chooseSubject,itemAdapter,MainActivity.this);
                itemFragment[position]=fragment;
            }
            catch (Exception e)
            {
                System.out.println(e);
            }
    }
    public String CheckSubject(String TITLE)
    {
        String chooseSubject="";
        if(TITLE.equals("语文"))
        {
            chooseSubject="chinese";
        }
        else if(TITLE.equals("数学"))
        {
            chooseSubject="math";
        }
        else if(TITLE.equals("英语"))
        {
            chooseSubject="english";
        }
        else if(TITLE.equals("物理"))
        {
            chooseSubject="physics";
        }
        else if(TITLE.equals("化学"))
        {
            chooseSubject="chemistry";
        }
        else if(TITLE.equals("历史"))
        {
            chooseSubject="history";
        }
        else if(TITLE.equals("地理"))
        {
            chooseSubject="geo";
        }
        else if(TITLE.equals("政治"))
        {
            chooseSubject="politics";
        }
        else if(TITLE.equals("生物"))
        {
            chooseSubject="biology";
        }
        return chooseSubject;
    }
}