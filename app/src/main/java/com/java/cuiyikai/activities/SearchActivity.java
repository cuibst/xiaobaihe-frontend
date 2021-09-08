package com.java.cuiyikai.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewPropertyAnimatorListener;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.java.cuiyikai.adapters.ItemAdapter;
import com.java.cuiyikai.adapters.SelectAdapter;
import com.java.cuiyikai.fragments.SelectFragment;
import com.alibaba.fastjson.JSONObject;
import com.java.cuiyikai.R;
import com.java.cuiyikai.network.RequestBuilder;
import com.java.cuiyikai.utilities.ConstantUtilities;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import com.java.cuiyikai.adapters.SearchAdapter;

import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>This class is used to show the search results </p>
 * <p>You can select the category of the results to choose the most relevant answer.This function is implemented in {@link SelectFragment}</p>
 * <p>The layout is implemented by {@link XRecyclerView}</p>
 * <p>The category selection fragment is implemented by {@link RecyclerView},its adapter is {@link SelectAdapter}</p>
 */
public class SearchActivity extends AppCompatActivity {

    private MyHandler handler;
    private boolean exitFlag;
    private String subject;
    private JSONObject receivedMessage;
    private List<String> checkSubject=new ArrayList<>();
    private List<String> checkMarked=new ArrayList<>();
    private final FragmentManager fragmentManager=getSupportFragmentManager();
    private FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
    private String searchContent;
    private XRecyclerView searchRecyclerView;
    private SearchAdapter searchAdapter;
    private SelectFragment selectFragment;
    private Button selectButton;
    private final static String unexpectedValue="Unexpected value: ";
    SelectAdapter selectAdapter;
    String searchUrl ="typeOpen/open/instanceList";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Intent prevIntent=getIntent();
        Bundle prevBundle = prevIntent.getExtras();
        exitFlag=false;
        subject=prevBundle.getString("sub");
        selectAdapter=new SelectAdapter(SearchActivity.this);
        selectFragment=new SelectFragment(selectAdapter);
        searchRecyclerView =findViewById(R.id.search_rcy);
        searchRecyclerView.setArrowImageView(R.drawable.waiting);
        handler=new MyHandler(Looper.getMainLooper());
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
        searchAdapter =new SearchAdapter(SearchActivity.this);
        receivedMessage = JSON.parseObject(prevBundle.getString("msg"));
        searchContent=prevBundle.getString(ConstantUtilities.ARG_NAME);
        // Set the loadMore and refresh method.
        searchRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                Refresh refresh=new Refresh();
                Thread refreshThread=new Thread(refresh);
                refreshThread.start();
            }

            @Override
            public void onLoadMore() {
                if(searchAdapter.getSize()+10 < searchAdapter.getRealLength())
                    searchAdapter.setSize(searchAdapter.getSize() + 10);
                else if(searchAdapter.getSize() <= (searchAdapter.getRealLength()))
                    searchAdapter.setSize(searchAdapter.getRealLength());
                searchRecyclerView.loadMoreComplete();
            }
        });
        searchAdapter =new SearchAdapter(SearchActivity.this);
        searchAdapter.addSubject(receivedMessage);
        searchRecyclerView.setAdapter(searchAdapter);
        selectButton=findViewById(R.id.selectButton);
        fragmentTransaction.add(R.id.selectFragment, selectFragment);
        fragmentTransaction.hide(selectFragment);
        fragmentTransaction.commit();
        initFragment();

        //selectButton is used to check whether the selection is completed.

        selectButton.setOnClickListener((View view) -> {
            fragmentTransaction = fragmentManager.beginTransaction();
            if (selectFragment.isHidden()) {
                selectButton.setText("完成");
                fragmentTransaction.show(selectFragment);
            } else {
                selectButton.setText("筛选");
                fragmentTransaction.hide(selectFragment);
                checkMarked = selectFragment.returnCheckMarked();
                checkSubject = selectFragment.returnCheckSubject();
                Set<String> set = receivedMessage.keySet();
                Map<String, Object> totalMap = new HashMap<>();
                JSONObject finalInfo;
                for (int lesson = 0; lesson < checkSubject.size(); lesson++) {
                    Map<String, Object> lessonMap = new HashMap<>();
                    for (String str : set) {
                        if (!str.equals(checkSubject(checkSubject.get(lesson))))
                            continue;
                        JSONArray categoryMap = new JSONArray();
                        for (int category = 0; category < checkMarked.size(); category++) {
                            for (int i = 0; i < receivedMessage.getJSONObject(str).getJSONArray(ConstantUtilities.ARG_DATA).size(); i++) {
                                String s = (String) ((JSONObject) receivedMessage.getJSONObject(str).getJSONArray(ConstantUtilities.ARG_DATA).get(i)).get(ConstantUtilities.ARG_CATEGORY);
                                if (!s.equals(checkMarked.get(category)))
                                    continue;
                                JSONObject cate = ((JSONObject) receivedMessage.getJSONObject(str).getJSONArray(ConstantUtilities.ARG_DATA).get(i));
                                if (categoryMap.contains(cate))
                                    continue;
                                categoryMap.add(cate);
                            }
                            lessonMap.put(ConstantUtilities.ARG_DATA, categoryMap);
                        }
                        totalMap.put(checkSubject(checkSubject.get(lesson)), lessonMap);
                    }

                }
                finalInfo = new JSONObject(totalMap);
                searchAdapter = null;
                searchAdapter = new SearchAdapter(SearchActivity.this);
                searchAdapter.addSubject(finalInfo);
                searchRecyclerView.setAdapter(searchAdapter);
            }
            fragmentTransaction.commit();
        });

        RadioGroup radioGroup = findViewById(R.id.sortingRadioGroup);
        ((RadioButton)findViewById(R.id.radioDefault)).setSelected(true);

        //Choose the sequence of the results.
        radioGroup.setOnCheckedChangeListener((radioGroup1, i) -> {
            LoggerFactory.getLogger(SearchActivity.class).info("Radio check changed, {}", i);
            if (i == R.id.radioNameDesc) {
                searchAdapter.sortNameAscend();
            } else if (i == R.id.radioNameAsc) {
                searchAdapter.sortNameDescend();
            } else if (i == R.id.radioCategoryAsc) {
                searchAdapter.sortCategoryAscend();
            } else if (i == R.id.radioCategoryDesc) {
                searchAdapter.sortCategoryDescend();
            } else {
                searchAdapter.addSubject(receivedMessage);
                searchAdapter.notifyDataSetChanged();
            }
        });

        // Change the way of layout
        findViewById(R.id.switch_layout_btn).setOnClickListener(v -> {
            if(searchAdapter.getItemViewType(0) == ItemAdapter.LAYOUT_TYPE_LINEAR) {
                searchAdapter.setType(ItemAdapter.LAYOUT_TYPE_GRID);
                searchRecyclerView.setLayoutManager(new GridLayoutManager(SearchActivity.this, 2));
                ((FloatingActionButton)findViewById(R.id.switch_layout_btn)).setImageResource(R.drawable.linear);
            } else {
                searchAdapter.setType(ItemAdapter.LAYOUT_TYPE_LINEAR);
                LinearLayoutManager manager = new LinearLayoutManager(SearchActivity.this);
                manager.setOrientation(LinearLayoutManager.VERTICAL);
                searchRecyclerView.setLayoutManager(manager);
                ((FloatingActionButton)findViewById(R.id.switch_layout_btn)).setImageResource(R.drawable.grid);
            }
            searchAdapter.notifyDataSetChanged();
        });

        searchRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            private boolean visible = true;
            private int distance = 0;
            private final Interpolator interpolator = new FastOutSlowInInterpolator();

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(distance > 10 && visible) {
                    visible = false;
                    ViewCompat.animate(findViewById(R.id.switch_layout_btn)).scaleX(0.0F).scaleY(0.0F).alpha(0.0F).setInterpolator(interpolator).withLayer()
                            .setListener(new ViewPropertyAnimatorListener() {
                                @Override
                                public void onAnimationStart(View view) {

                                }

                                public void onAnimationEnd(View view) {
                                    view.setVisibility(View.GONE);
                                }

                                @Override
                                public void onAnimationCancel(View view) {

                                }
                            }).start();
                    distance = 0;
                } else if(distance < -20 && !visible) {
                    findViewById(R.id.switch_layout_btn).setVisibility(View.VISIBLE);
                    visible = true;
                    ViewCompat.animate(findViewById(R.id.switch_layout_btn)).scaleX(1.0f).scaleY(1.0f).alpha(1.0f)
                            .setInterpolator(interpolator).withLayer().setListener(null)
                            .start();
                    distance = 0;
                }
                if((visible && dy > 0) || (!visible && dy < 0)) {
                    distance += dy;
                }
            }
        });

    }
    @Override
    public void onBackPressed()
    {
        exitFlag=true;
        super.onBackPressed();
    }

    /**
     * <p>This method is designed to convert the chinese to english. </p>
     * @param title chinese string
     * @return english string
     */
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
                throw new IllegalStateException(unexpectedValue + title);
        }
        return chooseSubject;
    }

    private void initFragment()
    {
        Set<String> set=receivedMessage.keySet();
        ArrayList<String> type=new ArrayList<>();
        ArrayList<String> subjectType=new ArrayList<>();
        for(String str:set)
        {
            subjectType.add(str);
            for(int i=0;i<receivedMessage.getJSONObject(str).getJSONArray(ConstantUtilities.ARG_DATA).size();i++)
            {
                String s=(String)((JSONObject)receivedMessage.getJSONObject(str).getJSONArray(ConstantUtilities.ARG_DATA).get(i)).get(ConstantUtilities.ARG_CATEGORY);
                if(!type.contains(s))
                    type.add(s);
            }
        }
        selectFragment.getSubjectType(subjectType);
        selectFragment.getType(type);
        selectButton.setText("筛选");
    }

    /**This class is a subclass of {@link Runnable},and it is used for constructing a new thread<
     *Pulling down the layout means you want to refresh the results, in order to avoid lagging ,we construct a new thread to run it .
     * When we get the refreshing results, we will send message to main thread to refresh the results.
     */
    private class Refresh implements Runnable {

        @Override
        public void run() {
            try {
                receivedMessage.clear();
                Map<String,String> map =new HashMap<>();
                map.put(ConstantUtilities.ARG_COURSE,  subject);
                map.put("searchKey",searchContent);
                JSONObject msg = RequestBuilder.sendGetRequest(searchUrl, map);
                try {
                    if (msg != null && msg.get("code").equals("-1")) {
                        handler.sendEmptyMessage(1);
                    } else if (msg != null && msg.get(ConstantUtilities.ARG_DATA) != null && !msg.getJSONArray(ConstantUtilities.ARG_DATA).isEmpty()) {
                        receivedMessage.put(subject, msg);
                    }
                }
                catch (NullPointerException e)
                {
                    e.printStackTrace();
                }
                Message message=new Message();
                message.what=0;
                message.obj=searchContent;
                handler.sendMessage(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class MyHandler extends Handler {
        MyHandler(Looper looper)
        {
            super(looper);
        }
        @Override
        public void handleMessage(@NonNull Message msg) {
            if(exitFlag)
                return;
            switch(msg.what)
            {
                case 0:
                    searchAdapter.addSubject(receivedMessage);
                    searchAdapter.notifyDataSetChanged();
                    searchRecyclerView.refreshComplete();
                    break;
                case 1:
                    Toast.makeText(SearchActivity.this, "网络异常，请重试", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    throw new IllegalStateException(unexpectedValue + msg.what);
            }
        }
    }
}