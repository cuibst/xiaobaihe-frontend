package com.java.cuiyikai;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java.cuiyikai.network.RequestBuilder;

import android.content.Context;
import android.content.Intent;
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

public class search_activity extends AppCompatActivity {
    Spinner spinner;
    RecyclerView search_rcy;
    String chose="chinese";
    private Button btn_for_search;
    private EditText searchtxt;
    private String name;
    String search_url="typeOpen/open/instanceList";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        spinner=findViewById(R.id.spinner_1);
        btn_for_search=findViewById(R.id.searchbutton);
        searchtxt=findViewById(R.id.searchText);
        search_rcy=findViewById(R.id.search_rcy);
        search_rcy.setLayoutManager(new LinearLayoutManager(search_activity.this));
        btn_for_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String,String> map =new HashMap<String,String>();
                map.put("course", chose);
                name=searchtxt.getText().toString();
                map.put("searchKey",searchtxt.getText().toString());
                try {
                    JSONObject msg = RequestBuilder.sendGetRequest(search_url, map);
                    Search_adapter sadapter=new Search_adapter(search_activity.this);
                    sadapter.addSubject(msg.getJSONArray("data"));
                    sadapter.addpic(chose);
                    search_rcy.setAdapter(sadapter);
                }
                catch (Exception e)
                {
                    System.out.println(e);
                }
            }
        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view , int position , long id)
            {
                String content =parent.getItemAtPosition(position).toString();
                if(content.equals("语文"))
                {
                    chose="chinese";
                }
                else if(content.equals("数学"))
                {
                    chose="math";
                }
                else if(content.equals("英语"))
                {
                    chose="english";
                }
                else if(content.equals("物理"))
                {
                    chose="physics";
                }
                else if(content.equals("化学"))
                {
                    chose="chemistry";
                }
                else if(content.equals("历史"))
                {
                    chose="history";
                }
                else if(content.equals("地理"))
                {
                    chose="geo";
                }
                else if(content.equals("政治"))
                {
                    chose="politics";
                }
                else if(content.equals("生物"))
                {
                    chose="biology";
                }
                System.out.println(chose);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                chose="chinese";
            }
        });
    }
    public class Search_adapter extends RecyclerView.Adapter<Search_adapter.RcyViewHolder>{
        Search_adapter(Context context)
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
                        Intent f=new Intent(search_activity.this,EntityActivity.class);
                        f.putExtra("name",name);
                        f.putExtra("subject",chose);
                        startActivity(f);
                    }
                });
            }
        }
        public void addSubject(JSONArray arr) {
            subject=arr;
        }
        private JSONArray subject=new JSONArray();
        private Context mContext;
        private LinearLayout searchline;
        private String for_pic_chose;
        private ImageView img;
        public Search_adapter.RcyViewHolder onCreateViewHolder(ViewGroup parent , int viewType)
        {
            return new Search_adapter.RcyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.search_content,parent,false));
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
        public void onBindViewHolder(Search_adapter.RcyViewHolder holder, int position)
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
            return subject.size();
        }
    }
}