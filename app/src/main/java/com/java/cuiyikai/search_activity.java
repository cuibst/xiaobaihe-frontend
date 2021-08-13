package com.java.cuiyikai;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;
import com.java.cuiyikai.network.RequestBuilder;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

public class search_activity extends AppCompatActivity {
    Spinner spinner;
    RecyclerView search_rcy;
    String chose="chinese";
//    private TextView txt;
    private Button btn_for_search;
    private EditText searchtxt;
    String search_url="typeOpen/open/instanceList";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        spinner=findViewById(R.id.spinner_1);
        btn_for_search=findViewById(R.id.searchbutton);
        searchtxt=findViewById(R.id.searchText);
//        txt=findViewById(R.id.showinfo);
        search_rcy=findViewById(R.id.search_rcy);
        search_rcy.setLayoutManager(new LinearLayoutManager(search_activity.this));
        btn_for_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String,String> map =new HashMap<String,String>();
                map.put("course", chose);
                map.put("searchKey",searchtxt.getText().toString());
                try {
//                    JSONObject msg = new JSONObject();
//                    msg.put("course", chose);
//                    msg.put("searchKey",searchtxt.getText().toString());
//                    System.out.println(msg.getJSONObject("course"));
                    JSONObject msg = RequestBuilder.sendGetRequest(search_url, map);
                    Search_adapter sadapter=new Search_adapter(search_activity.this);
                    sadapter.addSubject(msg.getJSONArray("data"));
                    sadapter.addpic(chose);
                    search_rcy.setAdapter(sadapter);
//                    txt.setText(msg.toString());
//                    System.out.println(msg.toString());
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
}