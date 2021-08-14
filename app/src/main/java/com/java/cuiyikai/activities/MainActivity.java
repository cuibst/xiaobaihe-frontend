package com.java.cuiyikai.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import com.java.cuiyikai.R;
import com.java.cuiyikai.adapters.RcyAdapter;

public class MainActivity extends AppCompatActivity {

    public String chose;
    private RecyclerView rcy1;
    private Button use_for_test;
    private Button btnForLogIn;
    private TextView searchtxt;
    String searchcontent;
    AdapterView.OnItemSelectedListener a;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Drawable searchimg=getResources().getDrawable(R.drawable.search);
        searchimg.setBounds(10,0,110,100);
        searchtxt=findViewById(R.id.searchText);
        searchtxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });
        searchtxt.setCompoundDrawables(searchimg,null,null,null);
        searchcontent=searchtxt.getText().toString();
        btnForLogIn=findViewById(R.id.btn_for_login);
        rcy1=findViewById(R.id.rcy_1);
        rcy1.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        LinearLayoutManager linearLayoutManager =new LinearLayoutManager(MainActivity.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rcy1.setLayoutManager(linearLayoutManager);
        rcy1.setAdapter(new RcyAdapter(MainActivity.this));
        btnForLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this, EntityActivity.class);
                startActivity(intent);
            }
        });
    }
}