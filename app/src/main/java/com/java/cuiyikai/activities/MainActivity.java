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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java.cuiyikai.MainApplication;
import com.java.cuiyikai.fragments.ItemFragment;
import com.java.cuiyikai.R;

import com.java.cuiyikai.network.RequestBuilder;
import com.xuexiang.xui.XUI;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private  final String[] all_subject_item={"语文","数学","英语","物理","化学","生物","历史","地理","政治"};
    public  List<String> radiolist=new ArrayList<String>();
    private List<String> item;
    private List<Fragment> fragments=new ArrayList<>();
    private List<String> fragmentTitles=new ArrayList<>();
    private Button btnForLogIn;
    private ViewPager viewpgr;
    public String choose="";
    public JSONObject receivedMessage=new JSONObject();
    private ImageView tabAdd;
    String search_url="typeOpen/open/instanceList";
    private TabLayout tabLayout;
    private String main_activity_url="/api/uri/getname";
    private SearchView searchView;
    private ItemFragment itemFragment[];
    AdapterView.OnItemSelectedListener a;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        exitHandler = new Handler(getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                super.handleMessage(message);
                exitFlag = false;
            }
        };

        XUI.init(this.getApplication());
        XUI.debug(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Drawable searchimg=getResources().getDrawable(R.drawable.search);
        tabAdd = findViewById(R.id.tab_add);
//        searchimg.setBounds(10,0,110,100);
        init();
//        searchtxt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent=new Intent(MainActivity.this, SearchActivity.class);
//                startActivity(intent);
//            }
//        });
        tabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this, CategoryActivity.class);
                startActivity(intent);
            }
        });


//        searchtxt.setCompoundDrawables(searchimg,null,null,null);
//        searchcontent=searchtxt.getText().toString();


        btnForLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(RequestBuilder.checkedLogin()) {
                    RequestBuilder.logOut();
                    Toast.makeText(MainActivity.this, "Logged out!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent=new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    public void init(){
        item = new ArrayList<String>();
        searchView=findViewById(R.id.searchViewInMain);
        tabLayout=findViewById(R.id.tablayout1);
        btnForLogIn=findViewById(R.id.btn_for_login);
        viewpgr=findViewById(R.id.viewpgr1);
        itemFragment=new ItemFragment[all_subject_item.length];
        initViewPager();
        tabLayout.setupWithViewPager(viewpgr);
        initSearchView(searchView,MainActivity.this);
    }
    private void initViewPager() {
        ItemFragment itemFragment;
        ViewPagerFragmentAdapter viewPagerFragmentAdapter = new ViewPagerFragmentAdapter(getSupportFragmentManager());
        try(InputStream is = getAssets().open(CategoryActivity.getSubjectData())) {
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
    public class ItemAdapter extends RecyclerView.Adapter<MainActivity.ItemAdapter.ItemViewHolder>{
        ItemAdapter(Context context,String s)
        {
            mContext=context;
            chooseSubject=s;
        }
        private String name;
        private String chooseSubject;
        private int size=0;
        private JSONArray subject=new JSONArray();
        private Context mContext;
        private LinearLayout searchline;
        private String for_pic_chose;
        private ImageView img;
        public void addSubject(JSONArray arr) {
            subject=arr;
            if(subject.size()<=10)
                size=subject.size();
            else
                size=10;
        }
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
                            Intent f = new Intent(MainActivity.this, EntityActivity.class);
                            f.putExtra("name", labeltxt.getText());
                            f.putExtra("subject", chooseSubject);
                            startActivity(f);
                }
                });
            }
        }
    }
    public void initfragment(int position)
    {
            String TITLE=all_subject_item[position];
            String chooseSubject=CheckSubject(TITLE);
            Map<String,String> map=new HashMap<>();
            map.put("subject",chooseSubject);
            try {
                ItemAdapter itemAdapter=new ItemAdapter(MainActivity.this,chooseSubject);
                JSONObject msg = RequestBuilder.sendBackendGetRequest(main_activity_url, map,false);
                itemAdapter.addpic(chooseSubject);
                itemAdapter.addSubject(msg.getJSONArray("data"));
                ItemFragment fragment = new ItemFragment(chooseSubject,itemAdapter,MainActivity.this);
                itemFragment[position]=fragment;
            }
            catch (Exception e)
            {
                System.out.println(e);
            }
    }
    public void initSearchView(SearchView searchView,Context mcontext)
    {
        searchView.setSubmitButtonEnabled(true);
        searchView.setIconifiedByDefault(true);
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                try {
                    receivedMessage.clear();
                    for(int i=0;i<all_subject_item.length;i++)
                    {
                        Map<String,String> map =new HashMap<String,String>();
                        map.put("course",  CheckSubject(all_subject_item[i]));
                        map.put("searchKey",s);
                        JSONObject msg = RequestBuilder.sendGetRequest(search_url, map);
                        if(msg.getJSONArray("data").size()!=0) {
                            receivedMessage.put(CheckSubject(all_subject_item[i]),msg);
                        }
                    }
                    Intent intent=new Intent(MainActivity.this,SearchActivity.class);
                    intent.putExtra("msg",receivedMessage.toString());
                    intent.putExtra("name",s);
                    startActivity(intent);
                }
                catch (Exception e)
                {
                    System.out.println(e);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            exits();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private boolean exitFlag = false;

    private Handler exitHandler;

    public void exits() {
        if(!exitFlag) {
            exitFlag = true;
            Toast.makeText(MainActivity.this, "再按一次退出应用", Toast.LENGTH_SHORT).show();
            exitHandler.sendEmptyMessageDelayed(0, 3000);
        } else {
            this.finish();
            ((MainApplication)getApplication()).dumpCacheData();
            System.exit(0);
        }
    }
}
