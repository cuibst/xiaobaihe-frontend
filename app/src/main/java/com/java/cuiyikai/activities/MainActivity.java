package com.java.cuiyikai.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
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
import com.java.cuiyikai.fragments.ItemFragment;
import com.java.cuiyikai.R;

import com.xuexiang.xui.XUI;

import android.widget.LinearLayout;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private String[] item={"推荐","语文","数学","英语","物理","化学","生物","历史","地理","政治"};
//    private String[]
    public List<String> radiolist=new ArrayList<String>();
    private List<Fragment> fragments=new ArrayList<>();
    private List<String> fragmentTitles=new ArrayList<>();
    private Button btnForLogIn;
    private TextView searchtxt;
    private ViewPager viewpgr;
    private String chose;
    private LinearLayout mLinearLayout;
    private AdapterView mAdaptView;
    private TabLayout tabLayout;
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

        /*
    *recyclerview 实现菜单栏，现已被弃用
 */
        //        rcy1=findViewById(R.id.rcy_1);
//        rcy1.setLayoutManager(new LinearLayoutManager(MainActivity.this));
//        LinearLayoutManager linearLayoutManager =new LinearLayoutManager(MainActivity.this);
//        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
//        rcy1.setLayoutManager(linearLayoutManager);
//        rcy1.setAdapter(new RcyAdapter(MainActivity.this));

//        updatelist();
        btnForLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this, PointExtractActivity.class);
                startActivity(intent);
            }
        });
    }

    public void init(){
//        mLinearLayout=findViewById(R.id.scrollline1);
        searchtxt=findViewById(R.id.searchText);
        tabLayout=findViewById(R.id.tablayout1);
//        mEasyIndicator = findViewById(R.id.easy_indicator);
        btnForLogIn=findViewById(R.id.btn_for_login);
        viewpgr=findViewById(R.id.viewpgr1);
        initViewPager();
        tabLayout.setupWithViewPager(viewpgr);
    }
    private void initViewPager()
    {
        ItemFragment itemFragment;
        ViewPagerFragmentAdapter viewPagerFragmentAdapter=new ViewPagerFragmentAdapter(getSupportFragmentManager());
        ItemAdapter itemAdapter=new ItemAdapter(MainActivity.this);
        viewPagerFragmentAdapter.setXRecyclerviewAdapter(itemAdapter);
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
            return ItemFragment.newInstance(item[position]);
        }

        @Override
        public int getCount()
        {
            return item.length;
        }
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object)
        {
            super.destroyItem(container, position, object);
        }
        public CharSequence getPageTitle(int position)
        {
            return item[position];
        }
    }
    public class ItemAdapter extends RecyclerView.Adapter<MainActivity.ItemAdapter.ItemViewHolder>{
        ItemAdapter(Context context)
        {
            mContext=context;
        }
        private String name;
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
                        f.putExtra("subject",chose);
                        startActivity(f);
                    }
                });
            }
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
        //    @Override
//    public int getItemViewType(int position) {
//        if(position%2==0)
//            return 0;
//        else
//            return 1;
//    }
        @Override
        public void onBindViewHolder(MainActivity.ItemAdapter.ItemViewHolder holder, int position)
        {
            holder.labeltxt.setText(subject.getJSONObject(position).get("label").toString());
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
            if(subject.getJSONObject(position).get("category").toString().length()==0)
                holder.categorytxt.setText("无");
            else
                holder.categorytxt.setText(subject.getJSONObject(position).get("category").toString());
            name=subject.getJSONObject(position).get("category").toString();

        }
        @Override
        public int getItemCount(){
            return size;
        }
    }
}