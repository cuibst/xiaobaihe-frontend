package com.java.cuiyikai.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.alibaba.fastjson.JSONArray;
import com.java.cuiyikai.adapters.SelectAdapter;
import com.java.cuiyikai.fragments.SelectFragment;
import com.alibaba.fastjson.JSONObject;
import com.java.cuiyikai.R;
import com.java.cuiyikai.network.RequestBuilder;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;
import com.java.cuiyikai.adapters.SearchAdapter;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;


public class SearchActivity extends AppCompatActivity {
    private final String[] all_subject_item={"语文","数学","英语","物理","化学","生物","历史","地理","政治"};
    private JSONObject receivedMessage;
    private Vector<String> checkSubject=new Vector<>();
    private Vector<String> checkMarked=new Vector<>();
    private FragmentManager fragmentManager=getFragmentManager();
    private FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
    private String searchContent;
    private XRecyclerView search_rcy;
    private SearchAdapter sadapter;
    private SelectFragment selectFragment;
    private Button selectButton;
    SelectAdapter selectAdapter;
    String search_url="typeOpen/open/instanceList";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Intent prevIntent=getIntent();
        Bundle prevBundle = prevIntent.getExtras();
        selectAdapter=new SelectAdapter(SearchActivity.this);
        selectFragment=new SelectFragment(selectAdapter);
        search_rcy=findViewById(R.id.search_rcy);
        search_rcy.setArrowImageView(R.drawable.waiting);
        search_rcy.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
        sadapter=new SearchAdapter(SearchActivity.this);
        receivedMessage=JSONObject.parseObject(prevBundle.getString("msg"));
        searchContent=prevBundle.getString("name");
        search_rcy.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                try {
                    receivedMessage.clear();
                    for(int i=0;i<all_subject_item.length;i++)
                    {
                        Map<String,String> map =new HashMap<String,String>();
                        map.put("course",  CheckSubject(all_subject_item[i]));
                        map.put("searchKey",searchContent);
                        JSONObject msg = RequestBuilder.sendGetRequest(search_url, map);
                        if(msg.getJSONArray("data").size()!=0) {
                            receivedMessage.put(CheckSubject(all_subject_item[i]),msg);
                        }
                    }
                    sadapter=null;
                    sadapter=new SearchAdapter(SearchActivity.this);
                    sadapter.addSubject(receivedMessage);
                    search_rcy.setAdapter(sadapter);
                }
                catch (Exception e)
                {
                    System.out.println(e);
                }
                search_rcy.refreshComplete();
            }

            @Override
            public void onLoadMore() {
                if(sadapter.size+10<sadapter.getRealLength())
                {
                    sadapter.size+=10;
                }
                else if(sadapter.size<= (sadapter.getRealLength()))
                {
                    sadapter.size= sadapter.getRealLength();
                }
                search_rcy.loadMoreComplete();
            }
        });
        sadapter=new SearchAdapter(SearchActivity.this);
        sadapter.addSubject(receivedMessage);
        search_rcy.setAdapter(sadapter);
        selectButton=findViewById(R.id.selectButton);
        fragmentTransaction.add(R.id.selectFragment, selectFragment);
        fragmentTransaction.hide(selectFragment);
        fragmentTransaction.commit();
        initFragment();
        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                    Map<String, Object> totalmap = new HashMap<>();
                    JSONObject finalInfo;
                    for (int lesson = 0; lesson < checkSubject.size(); lesson++) {
                        Map<String, Object> lessonmap = new HashMap<>();
                        for (String str : set) {
                            if (!str.equals(CheckSubject(checkSubject.get(lesson))))
                                continue;
                            JSONArray categorymap = new JSONArray();
                            for (int category = 0; category < checkMarked.size(); category++) {
                                for (int i = 0; i < receivedMessage.getJSONObject(str).getJSONArray("data").size(); i++) {
                                    String s = (String) ((JSONObject) receivedMessage.getJSONObject(str).getJSONArray("data").get(i)).get("category");
                                    if (!s.equals(checkMarked.get(category)))
                                        continue;
                                    JSONObject cate = ((JSONObject) receivedMessage.getJSONObject(str).getJSONArray("data").get(i));
                                    if (categorymap.contains(cate))
                                        continue;
                                    categorymap.add(cate);
                                }
                                lessonmap.put("data", categorymap);
                            }
                            totalmap.put(CheckSubject(checkSubject.get(lesson)), lessonmap);
                        }

                    }
                    finalInfo = new JSONObject(totalmap);
                    sadapter = null;
                    sadapter = new SearchAdapter(SearchActivity.this);
                    sadapter.addSubject(finalInfo);
                    search_rcy.setAdapter(sadapter);
                }
                fragmentTransaction.commit();
            }
        });
//        initSearchView(searchViewInSearch,SearchActivity.this);
    }


//    public void initSearchView(SearchView searchView,Context mcontext)
//    {
//        searchView.setSubmitButtonEnabled(true);
//        searchView.setIconifiedByDefault(true);
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String s) {
//                try {
//                    receivedMessage.clear();
//                    searchContent=s;
//                    for(int i=0;i<all_subject_item.length;i++)
//                    {
//                        Map<String,String> map =new HashMap<String,String>();
//                        map.put("course",  CheckSubject(all_subject_item[i]));
//                        map.put("searchKey",s);
//                        JSONObject msg = RequestBuilder.sendGetRequest(search_url, map);
//                        if((String)msg.get("code")=="-1")
//                        {
//                            Toast.makeText(SearchActivity.this, "网络异常，请重试", Toast.LENGTH_SHORT).show();
//                        }
//                        else if(msg.get("data")!=null&&!msg.getJSONArray("data").isEmpty()) {
//                            receivedMessage.put(CheckSubject(all_subject_item[i]),msg);
//                        }
//                    }
//                    sadapter=null;
//                    sadapter=new SearchAdapter(SearchActivity.this);
//                    sadapter.addSubject(receivedMessage);
//                    initFragment();
//                    search_rcy.setAdapter(sadapter);
//                }
//                catch (Exception e)
//                {
//                    System.out.println(e);
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
    public void initFragment()
    {
        Set<String> set=receivedMessage.keySet();
        Vector<String> type=new Vector<>();
        Vector<String> subjectType=new Vector<>();
        for(String str:set)
        {
            subjectType.add(str);
            for(int i=0;i<receivedMessage.getJSONObject(str).getJSONArray("data").size();i++)
            {
                String s=(String)((JSONObject)receivedMessage.getJSONObject(str).getJSONArray("data").get(i)).get("category");
                if(!type.contains(s))
                    type.add(s);
            }
        }
        selectFragment.getSubjectType(subjectType);
        selectFragment.getType(type);
//        initSearchView(searchViewInSearch,SearchActivity.this);
        selectButton.setText("筛选");
    }
}