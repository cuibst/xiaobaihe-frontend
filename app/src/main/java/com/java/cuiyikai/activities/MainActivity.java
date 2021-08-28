package com.java.cuiyikai.activities;

import static androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;

import com.alibaba.fastjson.JSONObject;
import com.chaychan.library.BottomBarItem;
import com.java.cuiyikai.MainApplication;
import com.java.cuiyikai.R;

import com.java.cuiyikai.fragments.DialogFragment;
import com.java.cuiyikai.fragments.MainFragment;
import com.java.cuiyikai.fragments.UserPageEntryFragment;
import com.java.cuiyikai.network.RequestBuilder;
import com.xuexiang.xui.XUI;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private  final String[] all_subject_item={"语文","数学","英语","物理","化学","生物","历史","地理","政治"};
    private Button btnForLogIn;
    public JSONObject receivedMessage=new JSONObject();
    private SwipeRecyclerView historyList;
    String search_url="typeOpen/open/instanceList";

    private SearchView searchView;
    String historyurl="/api/history/getHistory";
    String send="/api/history/addHistory";
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

        System.out.printf("Network available : %b%n", RequestBuilder.isNetworkNormal(MainActivity.this));

        if(!RequestBuilder.isNetworkNormal(MainActivity.this)) {
            Intent intent = new Intent(MainActivity.this, OfflineActivity.class);
            startActivity(intent);
            this.finish();
            return;
        }

        init();

        btnForLogIn.setOnClickListener((View view) -> {
            if(RequestBuilder.checkedLogin()) {
                RequestBuilder.logOut();
                Toast.makeText(MainActivity.this, "Logged out!", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent=new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        //FIXME: add all four fragments, current 2 of 4.
        fragmentList.add(new MainFragment());
        fragmentList.add(new UserPageEntryFragment());
        fragmentList.add(new DialogFragment());
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

        BottomBarItem btnHome = (BottomBarItem) findViewById(R.id.btnBottomBarHome);
        btnHome.setOnClickListener((View view) -> {
            mainPager.setCurrentItem(0);
        });

        BottomBarItem btnUser = (BottomBarItem) findViewById(R.id.btnBottomBarUser);
        btnUser.setOnClickListener((View view) -> {
            mainPager.setCurrentItem(1); //FIXME: this should be 3!!
        });

        BottomBarItem btnPoint= (BottomBarItem) findViewById(R.id.btnBottomBarPoint);
        btnPoint.setOnClickListener((View view)->{
            mainPager.setCurrentItem(2);
        });
    }

    public void init(){
        searchView=findViewById(R.id.searchViewInMain);
        btnForLogIn=findViewById(R.id.btn_for_login);
        initSearchView(searchView);
    }


    public void initSearchView(SearchView searchView)
    {
        searchView.setSubmitButtonEnabled(true);
        searchView.setIconifiedByDefault(true);
        historyList=findViewById(R.id.historylist);



        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                try {
                    receivedMessage.clear();
                    for(int i=0;i<all_subject_item.length;i++)
                    {
                        Map<String,String> map =new HashMap<String,String>();
                        map.put("course",  checkSubject(all_subject_item[i]));
                        map.put("searchKey",s);
                        JSONObject msg = RequestBuilder.sendGetRequest(search_url, map);
                        if((String)msg.get("code")=="-1")
                        {
                            Toast.makeText(MainActivity.this, "网络异常，请重试", Toast.LENGTH_SHORT).show();
                        }
                        else if(msg.get("data")!=null&&!msg.getJSONArray("data").isEmpty()) {
                            receivedMessage.put(checkSubject(all_subject_item[i]),msg);
                        }
                    }
                    Intent intent=new Intent(MainActivity.this,SearchActivity.class);
                    intent.putExtra("msg",receivedMessage.toString());
                    intent.putExtra("name",s);
                    startActivity(intent);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                return true;
            }
        });
    }
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

}
