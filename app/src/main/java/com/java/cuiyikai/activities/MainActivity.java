package com.java.cuiyikai.activities;

import static androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;

import com.chaychan.library.BottomBarLayout;
import com.java.cuiyikai.MainApplication;
import com.java.cuiyikai.R;

import com.java.cuiyikai.fragments.DialogFragment;
import com.java.cuiyikai.fragments.MainFragment;
import com.java.cuiyikai.fragments.PointExtractFragment;
import com.java.cuiyikai.fragments.UserPageEntryFragment;
import com.java.cuiyikai.network.RequestBuilder;
import com.java.cuiyikai.utilities.ConstantUtilities;
import com.xuexiang.xui.XUI;

import android.widget.Toast;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final Logger logger = LoggerFactory.getLogger(MainActivity.class);


    private final List<Fragment> fragmentList = new ArrayList<>();

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
        logger.info("Network available : {}", RequestBuilder.isNetworkNormal(MainActivity.this));

        if(!RequestBuilder.isNetworkNormal(MainActivity.this)) {
            Intent intent = new Intent(MainActivity.this, OfflineActivity.class);
            startActivity(intent);
            this.finish();
            return;
        }

        fragmentList.add(new MainFragment());
        fragmentList.add(PointExtractFragment.newInstance());
        fragmentList.add(new DialogFragment());
        fragmentList.add(new UserPageEntryFragment());
        ViewPager mainPager = findViewById(R.id.mainPager);
        mainPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                Log.v("item", position + "");
                return fragmentList.get(position);
            }

            @Override
            public int getCount() {
                return fragmentList.size();
            }
        });
        ((BottomBarLayout)findViewById(R.id.bottomBar)).setViewPager(mainPager);
    }

    public String checkSubject(String title)
    {
        String chooseSubject;
        switch (title) {
            case "语文":
                chooseSubject = ConstantUtilities.SUBJECT_CHINESE;
                break;
            case "数学":
                chooseSubject = ConstantUtilities.SUBJECT_MATH;
                break;
            case "英语":
                chooseSubject = ConstantUtilities.SUBJECT_ENGLISH;
                break;
            case "物理":
                chooseSubject = ConstantUtilities.SUBJECT_PHYSICS;
                break;
            case "化学":
                chooseSubject = ConstantUtilities.SUBJECT_CHEMISTRY;
                break;
            case "历史":
                chooseSubject = ConstantUtilities.SUBJECT_HISTORY;
                break;
            case "地理":
                chooseSubject = ConstantUtilities.SUBJECT_GEO;
                break;
            case "政治":
                chooseSubject = ConstantUtilities.SUBJECT_POLITICS;
                break;
            case "生物":
                chooseSubject = ConstantUtilities.SUBJECT_BIOLOGY;
                break;
            default:
                chooseSubject = "";
                break;
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
