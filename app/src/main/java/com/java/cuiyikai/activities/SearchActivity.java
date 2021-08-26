package com.java.cuiyikai.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;
import com.java.cuiyikai.R;
import com.java.cuiyikai.adapters.ItemViewHolder;
import com.java.cuiyikai.network.RequestBuilder;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SearchActivity extends AppCompatActivity {
    private  final String[] all_subject_item={"语文","数学","英语","物理","化学","生物","历史","地理","政治"};
    JSONObject receivedMessage;
    private SearchView searchViewInSearch;
    private String searchContent;
    private XRecyclerView search_rcy;
    private SearchAdapter sadapter;
    String search_url="typeOpen/open/instanceList";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Intent prevIntent=getIntent();
        Bundle prevBundle = prevIntent.getExtras();
        search_rcy=findViewById(R.id.search_rcy);
        search_rcy.setArrowImageView(R.drawable.waiting);
        search_rcy.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
        sadapter=new SearchAdapter(SearchActivity.this);
        searchViewInSearch=findViewById(R.id.searchViewInSearch);
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
        initSearchView(searchViewInSearch,SearchActivity.this);
    }


    public class SearchAdapter extends RecyclerView.Adapter<ItemViewHolder>{
        SearchAdapter(Context context)
        {
            mContext=context;
        }

        public void addSubject(JSONObject arr) {
            subject=arr;
            Set<String> set=subject.keySet();
            for(String str:set)
            {
                System.out.println(str);
                sum+=subject.getJSONObject(str).getJSONArray("data").size();
            }
            if(sum<=10)
                size=sum;
            else
                size=10;
        }
        public int getRealLength()
        {
            return sum;
        }
        private int size=0;
        int sum=0;
        private JSONObject subject=new JSONObject();
        private Context mContext;
        public ItemViewHolder onCreateViewHolder(ViewGroup parent , int viewType)
        {
            return new ItemViewHolder(LayoutInflater.from(mContext).inflate(R.layout.search_content,parent,false));
        }
        @Override
        public void onBindViewHolder(ItemViewHolder holder, int position)
        {
            Map<String,Object> map=findActualItem(position);
            holder.getSearchLine().setOnClickListener((View view) -> {
                Intent f=new Intent(SearchActivity.this,EntityActivity.class);
                f.putExtra("name",holder.getLabelTextView().getText());
                f.putExtra("subject",(String) map.get("name"));
                startActivity(f);
            });

            holder.getLabelTextView().setText(((JSONObject)map.get("item")).get("label").toString());
            switch ((String) map.get("name"))
            {
                case "chinese" :
                    holder.getSearchLine().setBackgroundResource(R.drawable.chinese_radius);
                    holder.getImg().setImageResource(R.drawable.chinese);
                    break;
                case "math" :
                    holder.getSearchLine().setBackgroundResource(R.drawable.maths_radius);
                    holder.getImg().setImageResource(R.drawable.maths);
                    break;
                case "english" :
                    holder.getSearchLine().setBackgroundResource(R.drawable.english_radius);
                    holder.getImg().setImageResource(R.drawable.english);
                    break;
                case "physics" :
                    holder.getSearchLine().setBackgroundResource(R.drawable.physics_radius);
                    holder.getImg().setImageResource(R.drawable.physics);
                    break;
                case "chemistry" :
                    holder.getSearchLine().setBackgroundResource(R.drawable.chemistry_radius);
                    holder.getImg().setImageResource(R.drawable.chemistry);
                    break;
                case "biology" :
                    holder.getSearchLine().setBackgroundResource(R.drawable.biology_radius);
                    holder.getImg().setImageResource(R.drawable.biology);
                    break;
                case "history" :
                    holder.getSearchLine().setBackgroundResource(R.drawable.history_radius);
                    holder.getImg().setImageResource(R.drawable.history);
                    break;
                case "geo" :
                    holder.getSearchLine().setBackgroundResource(R.drawable.geography_radius);
                    holder.getImg().setImageResource(R.drawable.geography);
                    break;
                case "politics":
                default:
                    holder.getSearchLine().setBackgroundResource(R.drawable.politics_radius);
                    holder.getImg().setImageResource(R.drawable.politics);
                    break;
            }
            if(((JSONObject)map.get("item")).get("category").toString().length()==0)
                holder.getCategoryTextView().setText("无");
            else
                holder.getCategoryTextView().setText(((JSONObject)map.get("item")).get("category").toString());
        }
        @Override
        public int getItemCount(){
            return size;
        }
        public Map<String,Object> findActualItem(int position)
        {
            Set<String> set=subject.keySet();
            Map<String,Object> map=new HashMap<String, Object>();
            for(String str:set)
            {
                System.out.println(subject.getJSONObject(str).getJSONArray("data"));
                if((subject.getJSONObject(str).getJSONArray("data").size())>position) {
                    map.put("item", subject.getJSONObject(str).getJSONArray("data").get(position));
                    map.put("name",str);
                    break;
                }
                else
                    position-=subject.getJSONObject(str).getJSONArray("data").size();
            }
            return map;
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
                    searchContent=s;
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
                    sadapter=null;
                    sadapter=new SearchAdapter(SearchActivity.this);
                    sadapter.addSubject(receivedMessage);
                    search_rcy.setAdapter(sadapter);
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
}