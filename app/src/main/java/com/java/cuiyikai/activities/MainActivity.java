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
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.java.cuiyikai.fragments.ItemFragment;
import com.java.cuiyikai.R;

import com.java.cuiyikai.adapters.SubjectAdapter;
import com.xuexiang.xui.XUI;

import android.widget.LinearLayout;

import com.google.android.material.tabs.TabLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<String> item;
    public List<String> radiolist=new ArrayList<String>();
    private List<Fragment> fragments=new ArrayList<>();
    private List<String> fragmentTitles=new ArrayList<>();
    private Button btnForLogIn;
    private TextView searchtxt;
    private ViewPager viewpgr;
    private String chose;
    private LinearLayout mLinearLayout;
    private ImageView tabAdd;
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
        tabAdd = findViewById(R.id.tab_add);
        searchimg.setBounds(10,0,110,100);
        init();
        searchtxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });
        tabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this, CategoryActivity.class);
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
        item = new ArrayList<String>();
//        mLinearLayout=findViewById(R.id.scrollline1);
        searchtxt=findViewById(R.id.searchText);
        tabLayout=findViewById(R.id.tablayout1);
//        mEasyIndicator = findViewById(R.id.easy_indicator);
        btnForLogIn=findViewById(R.id.btn_for_login);
        viewpgr=findViewById(R.id.viewpgr1);
        initViewPager();
        tabLayout.setupWithViewPager(viewpgr);
    }
    private void initViewPager() {
        try {
            InputStream is = getAssets().open(CategoryActivity.getSubjectData());
            int length = is.available();
            byte[] buffer = new byte[length];
            is.read(buffer);
            String result = new String(buffer, "utf-8");
            JSONObject jsonObject = new JSONObject(result);
//            JSONArray userArray = jsonObject.optJSONArray("user");
//            for(int i = 0; i <= userArray.size() - 1; i ++){
//                item.add(userArray.get(i));
//            }
        }
        catch (IOException | JSONException e){
            e.printStackTrace();
        }
        viewpgr.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return ItemFragment.newInstance(item.get(position));
            }

            @Override
            public int getCount() {
                return item.size();
            }
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                super.destroyItem(container, position, object);
            }
            public CharSequence getPageTitle(int position) {
                return item.get(position);
            }
        });
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