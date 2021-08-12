package com.java.cuiyikai;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSONObject;
import com.java.cuiyikai.network.RequestBuilder;
import android.os.Bundle;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

public class test extends AppCompatActivity {
    String infourl="typeOpen/open/instanceList";
    private TextView txt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        txt=findViewById(R.id.text1);
        try {

            Map<String, String> map = new HashMap<String, String>();
            map.put("course", "语文");
            map.put("searchKey", "李白");
            JSONObject msg=RequestBuilder.sendGetRequest(infourl, map);
            txt.setText(msg.toString());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            txt.setText(e.toString());
        }
    }
}