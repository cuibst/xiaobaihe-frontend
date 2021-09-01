package com.java.cuiyikai.activities;

import static androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chaychan.library.BottomBarLayout;
import com.java.cuiyikai.MainApplication;
import com.java.cuiyikai.R;

import com.java.cuiyikai.fragments.DialogFragment;
import com.java.cuiyikai.fragments.HistoryFragment;
import com.java.cuiyikai.fragments.MainFragment;
import com.java.cuiyikai.fragments.PointExtractFragment;
import com.java.cuiyikai.fragments.UserPageEntryFragment;
import com.java.cuiyikai.network.RequestBuilder;
import com.xuexiang.xui.XUI;

import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

//    private Button btnForLogIn;


    private List<Fragment> fragmentList = new ArrayList<>();

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
        init();
        System.out.printf("Network available : %b%n", RequestBuilder.isNetworkNormal(MainActivity.this));

        if(!RequestBuilder.isNetworkNormal(MainActivity.this)) {
            Intent intent = new Intent(MainActivity.this, OfflineActivity.class);
            startActivity(intent);
            this.finish();
            return;
        }

//        btnForLogIn.setOnClickListener((View view) -> {
//            if(RequestBuilder.checkedLogin()) {
//                RequestBuilder.logOut();
//                Toast.makeText(MainActivity.this, "Logged out!", Toast.LENGTH_SHORT).show();
//                return;
//            }
//            Intent intent=new Intent(MainActivity.this, LoginActivity.class);
//            startActivity(intent);
//        });

        //FIXME: add all four fragments, current 3 of 4.
        fragmentList.add(new MainFragment());
        fragmentList.add(PointExtractFragment.newInstance()); //FIXME: this fragment is used to fill the places.
        fragmentList.add(new DialogFragment());
        fragmentList.add(new UserPageEntryFragment());

        ViewPager mainPager = findViewById(R.id.mainPager);
        mainPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return fragmentList.get(position);
            }

            @Override
            public int getCount() {
                return fragmentList.size();
            }
        });

        ((BottomBarLayout)findViewById(R.id.bottomBar)).setViewPager(mainPager);
        Intent intent=new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    public void init(){
//        searchView=findViewById(R.id.searchViewInMain);
//        btnForLogIn=findViewById(R.id.btn_for_login);

//        initSearchView(searchView);
    }


//    public void initSearchView(SearchView searchView)
//    {
//        searchView.setSubmitButtonEnabled(true);
//        searchView.setIconifiedByDefault(true);
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String s) {
//                AddHistory addHistory=new AddHistory(s);
//                Thread thread=new Thread(addHistory);
//                thread.start();
//                try {
//                    receivedMessage.clear();
//                    for(int i=0;i<all_subject_item.length;i++)
//                    {
//                        Map<String,String> map =new HashMap<String,String>();
//                        map.put("course",  checkSubject(all_subject_item[i]));
//                        map.put("searchKey",s);
//                        JSONObject msg = RequestBuilder.sendGetRequest(search_url, map);
//                        if((String)msg.get("code")=="-1")
//                        {
//                            Toast.makeText(MainActivity.this, "网络异常，请重试", Toast.LENGTH_SHORT).show();
//                        }
//                        else if(msg.get("data")!=null&&!msg.getJSONArray("data").isEmpty()) {
//                            receivedMessage.put(checkSubject(all_subject_item[i]),msg);
//                        }
//                    }
//                    Intent intent=new Intent(MainActivity.this,SearchActivity.class);
//                    intent.putExtra("msg",receivedMessage.toString());
//                    intent.putExtra("name",s);
//                    startActivity(intent);
//                }
//                catch (Exception e)
//                {
//                    e.printStackTrace();
//                }
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String s) {
//                return false;
//            }
//        });
//    }
    public String checkSubject(String title)
    {
        String chooseSubject;
        switch (title) {
            case "语文":
                chooseSubject = "chinese";
                break;
            case "数学":
                chooseSubject = "math";
                break;
            case "英语":
                chooseSubject = "english";
                break;
            case "物理":
                chooseSubject = "physics";
                break;
            case "化学":
                chooseSubject = "chemistry";
                break;
            case "历史":
                chooseSubject = "history";
                break;
            case "地理":
                chooseSubject = "geo";
                break;
            case "政治":
                chooseSubject = "politics";
                break;
            case "生物":
                chooseSubject = "biology";
                break;
            default:
                chooseSubject = "";
                break;
        }
        return chooseSubject;
    }
    public String reverseCheckSubject(String TITLE)
    {
        String chooseSubject="";
        if(TITLE.equals("chinese"))
        {
            chooseSubject="语文";
        }
        else if(TITLE.equals("math"))
        {
            chooseSubject="数学";
        }
        else if(TITLE.equals("english"))
        {
            chooseSubject="英语";
        }
        else if(TITLE.equals("physics"))
        {
            chooseSubject="物理";
        }
        else if(TITLE.equals("chemistry"))
        {
            chooseSubject="化学";
        }
        else if(TITLE.equals("history"))
        {
            chooseSubject="历史";
        }
        else if(TITLE.equals("geo"))
        {
            chooseSubject="地理";
        }
        else if(TITLE.equals("politics"))
        {
            chooseSubject="政治";
        }
        else if(TITLE.equals("biology"))
        {
            chooseSubject="生物";
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

//    private MyHandler handler=new MyHandler();
//
//    private class MyHandler extends Handler {
//        @Override
//        public void handleMessage(@NonNull Message msg) {
//            switch(msg.what)
//            {
//                case 0:
//                    JSONObject data=JSONObject.parseObject(msg.obj.toString());
//                    JSONArray arr=data.getJSONArray("data");
//                    historyFragment.historyListAdapter.addData(arr);
//                    historyFragment.array=arr;
//                    historyFragment.recyclerViewForHistory.setAdapter(historyFragment.historyListAdapter);
//                    break;
//            }
//        }
//    }
}
