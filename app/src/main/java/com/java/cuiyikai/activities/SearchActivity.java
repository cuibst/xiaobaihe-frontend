package com.java.cuiyikai.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java.cuiyikai.R;
import com.java.cuiyikai.network.RequestBuilder;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.yalantis.phoenix.PullToRefreshView;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {
    Spinner spinner;
//    RecyclerView search_rcy;
    String chose="chinese";
    String choose="chinese";
    private XRecyclerView search_rcy;
    private ImageView btn_for_search;
    private EditText searchtxt;
    private String name;
    private SearchAdapter sadapter;
    String search_url="typeOpen/open/instanceList";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setContentView(R.layout.activity_search);
//        spinner=findViewById(R.id.spinner_1);
        btn_for_search=findViewById(R.id.searchbutton);
        searchtxt=findViewById(R.id.searchText);
        search_rcy=findViewById(R.id.search_rcy);
        search_rcy.setArrowImageView(R.drawable.waiting);
        search_rcy.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
        search_rcy.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                try {
                    Map<String,String> map =new HashMap<String,String>();
                    map.put("course", chose);
                    map.put("searchKey",name);
                    JSONObject msg = RequestBuilder.sendGetRequest(search_url, map);
                    sadapter.size=0;
                    sadapter=null;
                    sadapter=new SearchAdapter(SearchActivity.this);
                    sadapter.addSubject(msg.getJSONArray("data"));
                    sadapter.addpic(chose);
                    search_rcy.setAdapter(sadapter);
                }
                catch (Exception e)
                {
                    System.out.println("hello");
                    System.out.println(e);
                }
                search_rcy.refreshComplete();
            }

            @Override
            public void onLoadMore() {
                if(sadapter.size+10<sadapter.subject.size())
                {
                    sadapter.size+=10;
                }
                else if(sadapter.size<= (sadapter.subject.size()))
                {
                    sadapter.size= sadapter.subject.size();
                }
                search_rcy.loadMoreComplete();
            }
        });
        btn_for_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choose=chose;
                Map<String,String> map =new HashMap<String,String>();
                map.put("course", choose);
                name=searchtxt.getText().toString();
                map.put("searchKey",searchtxt.getText().toString());
                try {
                    sadapter=null;
                    JSONObject msg = RequestBuilder.sendGetRequest(search_url, map);
                    sadapter=new SearchAdapter(SearchActivity.this);
                    sadapter.addSubject(msg.getJSONArray("data"));
                    sadapter.addpic(choose);
                    search_rcy.setAdapter(sadapter);
                }
                catch (Exception e)
                {
                    System.out.println(e);
                }
                search_rcy.refreshComplete();
            }
        });
//        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view , int position , long id)
//            {
//                String content =parent.getItemAtPosition(position).toString();
//                if(content.equals("语文"))
//                {
//                    chose="chinese";
//                }
//                else if(content.equals("数学"))
//                {
//                    chose="math";
//                }
//                else if(content.equals("英语"))
//                {
//                    chose="english";
//                }
//                else if(content.equals("物理"))
//                {
//                    chose="physics";
//                }
//                else if(content.equals("化学"))
//                {
//                    chose="chemistry";
//                }
//                else if(content.equals("历史"))
//                {
//                    chose="history";
//                }
//                else if(content.equals("地理"))
//                {
//                    chose="geo";
//                }
//                else if(content.equals("政治"))
//                {
//                    chose="politics";
//                }
//                else if(content.equals("生物"))
//                {
//                    chose="biology";
//                }
//            }
//            @Override
//            public void onNothingSelected(AdapterView<?> parent)
//            {
//                chose="chinese";
//            }
//        });
    }
    public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.RcyViewHolder>{
        SearchAdapter(Context context)
        {
            mContext=context;
        }
        class RcyViewHolder extends RecyclerView.ViewHolder{
            private TextView labeltxt,categorytxt;
            public RcyViewHolder(View view)
            {
                super(view);
                labeltxt=view.findViewById(R.id.label);
                img=view.findViewById(R.id.img);
                categorytxt=view.findViewById(R.id.category);
                searchline=view.findViewById(R.id.search_line);
                searchline.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent f=new Intent(SearchActivity.this,EntityActivity.class);
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
        public SearchAdapter.RcyViewHolder onCreateViewHolder(ViewGroup parent , int viewType)
        {
            return new SearchAdapter.RcyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.search_content,parent,false));
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
        public void onBindViewHolder(SearchAdapter.RcyViewHolder holder, int position)
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
        }
        @Override
        public int getItemCount(){
            return size;
        }
    }
}