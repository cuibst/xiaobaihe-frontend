package com.java.cuiyikai.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;


import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import com.java.cuiyikai.ItemFragment;
import com.java.cuiyikai.R;
import com.java.cuiyikai.adapters.RcyAdapter;

import com.xuexiang.xui.XUI;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import android.widget.LinearLayout;

import com.google.android.material.tabs.TabLayout;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private String[] item={"推荐","语文","数学","英语","物理","化学","生物","历史","地理","政治"};
    public List<String> radiolist=new ArrayList<String>();
    private List<Fragment> fragments=new ArrayList<>();
    private List<String> fragmentTitles=new ArrayList<>();
    public String chose;
    private Button btnForLogIn;
    private TextView searchtxt;
    private ViewPager viewpgr;
    private LinearLayout mLinearLayout;
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
                Intent intent=new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    public void init(){
//        mLinearLayout=findViewById(R.id.scrollline1);
        searchtxt=findViewById(R.id.searchText);
        tabLayout=findViewById(R.id.tablayout1);
        btnForLogIn=findViewById(R.id.btn_for_login);
        viewpgr=findViewById(R.id.viewpgr1);
        initViewPager();
        tabLayout.setupWithViewPager(viewpgr);
    }
    private void initViewPager()
    {
        viewpgr.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return ItemFragment.newInstance(item[position]);
            }

            @Override
            public int getCount() {
                return item.length;
            }
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                super.destroyItem(container, position, object);
            }
            public CharSequence getPageTitle(int position) {
                return item[position];
            }
        });
    }
    /*public void updatelist()
    {
        for(int i=0;i<radiolist.size();i++)
        {
            RadioButton radiobtn=new RadioButton(this);
            RadioGroup.LayoutParams layoutParams=new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT,RadioGroup.LayoutParams.WRAP_CONTENT);
            radiobtn.setLayoutParams(layoutParams);
            radiobtn.setText(radiolist.get(i));
            radiobtn.setButtonDrawable(android.R.color.transparent);
        }
    }*/

}