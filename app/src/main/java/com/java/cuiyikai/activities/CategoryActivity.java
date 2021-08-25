package com.java.cuiyikai.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.java.cuiyikai.MainApplication;
import com.java.cuiyikai.R;
import com.java.cuiyikai.View.DragGridView;
import com.java.cuiyikai.adapters.GridViewAdapter;
import com.java.cuiyikai.adapters.SubjectAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryActivity extends AppCompatActivity {

    private List<String> userList = new ArrayList<>();
    private List<String> otherList = new ArrayList<>();
    private DragGridView otherDGV;
    private DragGridView userDGV;
    private GridViewAdapter userAdapter;
    private GridViewAdapter otherAdapter;
    private TextView mEditTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        initData();
        initView();
    }

    private void initData(){
        userList = ((MainApplication) getApplication()).getSubjects();
        for(String subject : Arrays.asList("推荐", "语文", "数学", "英语", "物理", "化学", "生物", "历史", "地理", "政治"))
            if(!userList.contains(subject))
                otherList.add(subject);
    }

    private void initView() {
        mEditTextView = findViewById(R.id.edit_event);
        userDGV = (DragGridView)findViewById(R.id.user_gv);
        otherDGV = findViewById(R.id.other_gv);
        Log.v("grid", "in");
        userAdapter = new GridViewAdapter(this, userList, 0);
        userAdapter.setOnListSwapChangeListener(this::onSubjectChanged);
        otherAdapter = new GridViewAdapter(this, otherList, 1);
        userDGV.setAdapter(userAdapter);
        otherDGV.setAdapter(otherAdapter);
        mEditTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SubjectAdapter.setEdit(!SubjectAdapter.getEdit());

                mEditTextView.setText(SubjectAdapter.getEdit() ? "完成" : "编辑");
                userAdapter.notifyDataSetChanged();
                otherAdapter.notifyDataSetChanged();
            }
        });

        userDGV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.v("grid", i + " " + l + " " + userList.get(i));
                if(!SubjectAdapter.getEdit())
                    return;
                otherList.add(userList.get(i));
                userList.remove(i);
//                Log.v("mytag", userList.get(i));
                userAdapter.notifyDataSetChanged();
                otherAdapter.notifyDataSetChanged();
                onSubjectChanged();
            }
        });

        otherDGV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(!SubjectAdapter.getEdit())
                    return;
                userList.add(otherList.get(i));
                otherList.remove(i);
                userAdapter.notifyDataSetChanged();
                otherAdapter.notifyDataSetChanged();
                onSubjectChanged();
            }
        });

        findViewById(R.id.btn_close).setOnClickListener((View v) -> {
            this.finish();
        });
    }

    private void onSubjectChanged() {
        ((MainApplication)getApplication()).setSubjects(userList);
    }

}