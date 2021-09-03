package com.java.cuiyikai.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.java.cuiyikai.MainApplication;
import com.java.cuiyikai.R;
import com.java.cuiyikai.entities.CategoryItem;
import com.java.cuiyikai.adapters.CategoryLayoutManagerSizeLookUp;
import com.java.cuiyikai.entities.CategoryObject;
import com.java.cuiyikai.entities.CategoryTitle;
import com.java.cuiyikai.adapters.GridViewAdapter;
import com.java.cuiyikai.adapters.GridViewItemTouchCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {

    private List<String> userList = new ArrayList<>();
    private List<String> otherList = new ArrayList<>();

    private GridViewAdapter adapter;

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
        if(userList.isEmpty()) {
            userList = otherList;
            otherList = new ArrayList<>();
        }
    }

    private void initView() {
        RecyclerView userView = findViewById(R.id.user_gv);

        List<CategoryObject> objectList = new ArrayList<>();
        CategoryTitle mTitle = new CategoryTitle("我的频道");

        objectList.add(mTitle);

        for(String title : userList)
            objectList.add(new CategoryItem(title));

        objectList.add(new CategoryTitle("推荐频道"));

        for(String title : otherList)
            objectList.add(new CategoryItem(title));

        GridLayoutManager gridLayoutManager = new GridLayoutManager(CategoryActivity.this, 4);
        adapter = new GridViewAdapter(CategoryActivity.this, objectList, userList.size());
        gridLayoutManager.setSpanSizeLookup(new CategoryLayoutManagerSizeLookUp(adapter, gridLayoutManager));
        userView.setLayoutManager(gridLayoutManager);
        userView.setAdapter(adapter);

        ItemTouchHelper helper = new ItemTouchHelper(new GridViewItemTouchCallback(adapter));
        helper.attachToRecyclerView(userView);

        findViewById(R.id.btn_close).setOnClickListener((View v) -> this.finish());

        onEditableChanged(false);

    }

    public void onEditableChanged(boolean editable) {
        if(editable) {
            ((TextView) findViewById(R.id.edit_event)).setText("完成");
            findViewById(R.id.edit_event).setOnClickListener((View v) -> {
                adapter.setEditable(false);
                onEditableChanged(false);
            });
        } else {
            ((TextView) findViewById(R.id.edit_event)).setText("编辑");
            findViewById(R.id.edit_event).setOnClickListener((View v) -> {
                adapter.setEditable(true);
                onEditableChanged(true);
            });
        }
    }

}