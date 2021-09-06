package com.java.cuiyikai.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java.cuiyikai.R;
import com.java.cuiyikai.adapters.HistoryListAdapter;
import com.java.cuiyikai.fragments.HistoryFragment;
import com.java.cuiyikai.network.RequestBuilder;
import com.java.cuiyikai.utilities.ConstantUtilities;
import com.java.cuiyikai.utilities.DensityUtilities;

import java.util.HashMap;
import java.util.Map;

public class SearchViewActivity extends AppCompatActivity {
    private String subject=ConstantUtilities.SUBJECT_CHINESE;
    private HistoryFragment historyFragment;
    private boolean exitflag;
    private HistoryListAdapter historyListAdapter;
    private TextView subjectText;
    private SearchView searchView;
    private final JSONObject receivedMessage=new JSONObject();
    private final FragmentManager fragmentManager=getSupportFragmentManager();

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public TextView getSubjectText() {
        return subjectText;
    }

    private MyHandler myHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_view);
        exitflag=false;
        myHandler=new MyHandler(getMainLooper());
        searchView=findViewById(R.id.searchView);
        RecyclerView recommendXRecyclerView = findViewById(R.id.recommendXRecylcerView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recommendXRecyclerView.setLayoutManager(gridLayoutManager);
        initSearchView();
        TextView quitText = findViewById(R.id.quit);
        quitText.setOnClickListener(v -> onBackPressed());
        subjectText=findViewById(R.id.subject);
        Dialog bottomDialog = new Dialog(SearchViewActivity.this, R.style.BottomDialog);
        View contentView = LayoutInflater.from(SearchViewActivity.this).inflate(R.layout.layout_subject_select, null);
        bottomDialog.setContentView(contentView);
        bottomDialog.getWindow().setGravity(Gravity.BOTTOM);
        bottomDialog.getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) contentView.getLayoutParams();
        params.width = getResources().getDisplayMetrics().widthPixels - DensityUtilities.dp2px(SearchViewActivity.this, 16f);
        params.bottomMargin = DensityUtilities.dp2px(SearchViewActivity.this, 8f);
        contentView.setLayoutParams(params);
        Button chinese = contentView.findViewById(R.id.chinese);
        Button math = contentView.findViewById(R.id.math);
        Button english = contentView.findViewById(R.id.english);
        Button physics = contentView.findViewById(R.id.physics);
        Button chemistry = contentView.findViewById(R.id.chemistry);
        Button biology = contentView.findViewById(R.id.biology);
        Button geo = contentView.findViewById(R.id.geo);
        Button history = contentView.findViewById(R.id.history);
        Button politics = contentView.findViewById(R.id.politics);
        chinese.setOnClickListener(v -> {
            subject= ConstantUtilities.SUBJECT_CHINESE;
            subjectText.setText(reverseCheckSubject(subject));
            bottomDialog.dismiss();
        });
        math.setOnClickListener(v -> {
            subject=ConstantUtilities.SUBJECT_MATH;
            subjectText.setText(reverseCheckSubject(subject));
            bottomDialog.dismiss();
        });
        english.setOnClickListener(v -> {
            subject=ConstantUtilities.SUBJECT_ENGLISH;
            subjectText.setText(reverseCheckSubject(subject));
            bottomDialog.dismiss();
        });
        physics.setOnClickListener(v -> {
            subject=ConstantUtilities.SUBJECT_PHYSICS;
            subjectText.setText(reverseCheckSubject(subject));
            bottomDialog.dismiss();
        });
        chemistry.setOnClickListener(v -> {
            subject=ConstantUtilities.SUBJECT_CHEMISTRY;
            subjectText.setText(reverseCheckSubject(subject));
            bottomDialog.dismiss();
        });
        biology.setOnClickListener(v -> {
            subject=ConstantUtilities.SUBJECT_BIOLOGY;
            subjectText.setText(reverseCheckSubject(subject));
            bottomDialog.dismiss();
        });
        geo.setOnClickListener(v -> {
            subject=ConstantUtilities.SUBJECT_GEO;
            subjectText.setText(reverseCheckSubject(subject));
            bottomDialog.dismiss();
        });
        politics.setOnClickListener(v -> {
            subject=ConstantUtilities.SUBJECT_POLITICS;
            subjectText.setText(reverseCheckSubject(subject));
            bottomDialog.dismiss();
        });
        history.setOnClickListener(v -> {
            subject=ConstantUtilities.SUBJECT_HISTORY;
            subjectText.setText(reverseCheckSubject(subject));
            bottomDialog.dismiss();
        });
        subjectText.setOnClickListener((View v) -> bottomDialog.show());
        historyFragment=new HistoryFragment(searchView);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.historyFragment,historyFragment);
        fragmentTransaction.hide(historyFragment);
        fragmentTransaction.commit();
        historyListAdapter=new HistoryListAdapter(SearchViewActivity.this,searchView);
        historyListAdapter.recommendflag=true;
        recommendXRecyclerView.setAdapter(historyListAdapter);

        GetRecommend getRecommend=new GetRecommend();
        Thread recommendThread=new Thread(getRecommend);
        recommendThread.start();
        if(RequestBuilder.checkedLogin())
        {
            GetHistory getHistory=new GetHistory();
            Thread thread=new Thread(getHistory);
            thread.start();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.show(historyFragment);
            fragmentTransaction.commit();
        }


    }
    @Override
    public void onBackPressed()
    {
        exitflag=true;
        super.onBackPressed();
    }
    public void initSearchView()
    {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                AddHistory addHistory=new AddHistory(s);
                Thread thread=new Thread(addHistory);
                thread.start();
                JSONObject m=new JSONObject();
                m.put(ConstantUtilities.ARG_SUBJECT,subject);
                m.put(ConstantUtilities.ARG_CONTENT,s);
                historyFragment.getHistoryListAdapter().addOneItem(m);
                historyFragment.getHistoryListAdapter().notifyDataSetChanged();
                StartSearch startSearch=new StartSearch(s);
                Thread startSearchThread=new Thread(startSearch);
                startSearchThread.start();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }



    public String reverseCheckSubject(String title)
    {
        String chooseSubject;
        switch (title) {
            case ConstantUtilities.SUBJECT_CHINESE:
                chooseSubject = "语文";
                break;
            case ConstantUtilities.SUBJECT_MATH:
                chooseSubject = "数学";
                break;
            case ConstantUtilities.SUBJECT_ENGLISH:
                chooseSubject = "英语";
                break;
            case ConstantUtilities.SUBJECT_PHYSICS:
                chooseSubject = "物理";
                break;
            case ConstantUtilities.SUBJECT_CHEMISTRY:
                chooseSubject = "化学";
                break;
            case ConstantUtilities.SUBJECT_HISTORY:
                chooseSubject = "历史";
                break;
            case ConstantUtilities.SUBJECT_GEO:
                chooseSubject = "地理";
                break;
            case ConstantUtilities.SUBJECT_POLITICS:
                chooseSubject = "政治";
                break;
            case ConstantUtilities.SUBJECT_BIOLOGY:
            default:
                chooseSubject = "生物";
                break;
        }
        return chooseSubject;
    }



    private class MyHandler extends Handler{
        MyHandler(Looper looper){
            super(looper);
        }
        @Override
        public void handleMessage(@NonNull Message msg)
        {
            if(exitflag)
                return;
            switch(msg.what)
            {
                case 0:
                    Intent intent=new Intent(SearchViewActivity.this,SearchActivity.class);
                    intent.putExtra("msg",receivedMessage.toString());
                    intent.putExtra(ConstantUtilities.ARG_NAME,msg.obj.toString());
                    intent.putExtra("sub",subject);
                    startActivity(intent);
                    break;
                case 1:
                    Toast.makeText(SearchViewActivity.this, "网络异常，请重试", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    JSONObject data= JSON.parseObject(msg.obj.toString());
                    JSONArray arr=data.getJSONArray(ConstantUtilities.ARG_DATA);
                    historyFragment.getHistoryListAdapter().addData(arr);
                    historyFragment.getRecyclerViewForHistory().setAdapter(historyFragment.getHistoryListAdapter());
                    break;
                case 3:
                    JSONObject object=JSON.parseObject(msg.obj.toString());
                    int max=object.getJSONArray(ConstantUtilities.ARG_DATA).size();
                    if(max>=8)
                        max=8;
                    JSONArray array=new JSONArray();
                    for(int i=0;i<max;i++)
                        array.add(object.getJSONArray(ConstantUtilities.ARG_DATA).get(i));
                    historyListAdapter.addData(array);
                    historyListAdapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
    }
    private class  GetRecommend implements  Runnable{

        @Override
        public void run() {
            try {
                Map<String, String> map = new HashMap<>();
                map.put(ConstantUtilities.ARG_SUBJECT, "");
                String mainActivityBackendUrl = "/api/uri/getname";
                JSONObject msg = RequestBuilder.sendBackendGetRequest(mainActivityBackendUrl, map, false);
                Message message = new Message();
                message.obj = msg.toString();
                message.what = 3;
                myHandler.sendMessage(message);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    private class StartSearch implements Runnable{
        String s;
        StartSearch(String ss)
        {
            s=ss;
        }
        @Override
        public void run() {
            try {
                receivedMessage.clear();
                Map<String,String> map =new HashMap<>();
                map.put(ConstantUtilities.ARG_COURSE,  subject);
                map.put("searchKey",s);
                String searchUrl = "typeOpen/open/instanceList";
                JSONObject msg = RequestBuilder.sendGetRequest(searchUrl, map);
                if(msg.get("code").equals("-1"))
                {
                    myHandler.sendEmptyMessage(1);
                }
                else if(msg.get(ConstantUtilities.ARG_DATA)!=null&&!msg.getJSONArray(ConstantUtilities.ARG_DATA).isEmpty()) {
                    receivedMessage.put(subject,msg);
                }
                Message message=new Message();
                message.what=0;
                message.obj=s;
                myHandler.sendMessage(message);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }


    private class GetHistory implements Runnable{

        @Override
        public void run() {
            if(!RequestBuilder.checkedLogin())
                return;
            Map<String,String> map=new HashMap<>();
            try {
                String getHistoryUrl = "/api/history/getHistory";
                JSONObject msg=RequestBuilder.sendBackendGetRequest(getHistoryUrl, map, true);
                Message message=new Message();
                message.what=2;
                message.obj=msg.toString();
                myHandler.sendMessage(message);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    private class AddHistory implements Runnable{
        private final String s;
        public AddHistory(String ss)
        {
            s=ss;
        }
        @Override
        public void run() {
            if(!RequestBuilder.checkedLogin())
                return;
            Map<String,String> map=new HashMap<>();
            map.put(ConstantUtilities.ARG_CONTENT,s);
            map.put(ConstantUtilities.ARG_SUBJECT,subject);
            try {
                String addHistoryUrl = "/api/history/addHistory";
                RequestBuilder.sendBackendGetRequest(addHistoryUrl, map, true);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}