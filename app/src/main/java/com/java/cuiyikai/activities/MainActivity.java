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
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.java.cuiyikai.ItemFragment;
import com.java.cuiyikai.R;
import com.java.cuiyikai.adapters.RcyAdapter;

import com.java.cuiyikai.adapters.SubjectAdapter;
import com.xuexiang.xui.XUI;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import android.widget.LinearLayout;

import com.google.android.material.tabs.TabLayout;
import com.xuexiang.xui.widget.tabbar.EasyIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<String> item;
    public List<String> radiolist=new ArrayList<String>();
    private List<Fragment> fragments=new ArrayList<>();
    private List<String> fragmentTitles=new ArrayList<>();
    public String chose;
    private Button btnForLogIn;
    private TextView searchtxt;
    private ViewPager viewpgr;
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
                Intent intent=new Intent(MainActivity.this, LoginActivity.class);
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
            JSONArray userArray = jsonObject.optJSONArray("user");
            for(int i = 0; i <= userArray.length() - 1; i ++){
                item.add(userArray.optString(i));
            }
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
//        mEasyIndicator.setTabTitles(item);
//        mEasyIndicator.setViewPager(viewpgr, viewpgr.getAdapter());

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