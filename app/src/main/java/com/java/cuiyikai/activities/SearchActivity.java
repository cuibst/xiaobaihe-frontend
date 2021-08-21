package com.java.cuiyikai.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;
import com.java.cuiyikai.R;
import com.java.cuiyikai.network.RequestBuilder;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

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
            }
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
        private LinearLayout searchline;
        private ImageView img;
        public SearchAdapter.RcyViewHolder onCreateViewHolder(ViewGroup parent , int viewType)
        {
            return new SearchAdapter.RcyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.search_content,parent,false));
        }
        @Override
        public void onBindViewHolder(SearchAdapter.RcyViewHolder holder, int position)
        {
            Map<String,Object> map=findActualItem(position);
            searchline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent f=new Intent(SearchActivity.this,EntityActivity.class);
                    f.putExtra("name",holder.labeltxt.getText());
                    f.putExtra("subject",(String) map.get("name"));
                    startActivity(f);
                }
            });

            holder.labeltxt.setText(((JSONObject)map.get("item")).get("label").toString());
            switch ((String) map.get("name"))
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
            if(((JSONObject)map.get("item")).get("category").toString().length()==0)
                holder.categorytxt.setText("无");
            else
                holder.categorytxt.setText(((JSONObject)map.get("item")).get("category").toString());
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