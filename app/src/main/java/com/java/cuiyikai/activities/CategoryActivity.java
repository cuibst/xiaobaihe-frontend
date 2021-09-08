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

/**
 * {@link android.app.Activity} for Category selection.
 */
public class CategoryActivity extends AppCompatActivity {

    //List of the category selected
    private List<String> userList = new ArrayList<>();
    //List of the category not selected
    private List<String> otherList = new ArrayList<>();

    //Adapter for the compound recycler view in this activity.
    private GridViewAdapter adapter;

    /**
     * {@inheritDoc}
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        initData();
        initView();
    }

    /**
     * Init {@link #userList} and {@link #otherList} from the saved text file.
     */
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

    /**
     * Init the recycler view in the activity.
     */
    private void initView() {
        RecyclerView userView = findViewById(R.id.user_gv);

        //Phase 1: add the objects to the recycler view object list.

        List<CategoryObject> objectList = new ArrayList<>();
        CategoryTitle mTitle = new CategoryTitle("我的频道");

        objectList.add(mTitle);

        for(String title : userList)
            objectList.add(new CategoryItem(title));

        objectList.add(new CategoryTitle("推荐频道"));

        for(String title : otherList)
            objectList.add(new CategoryItem(title));

        //Phase 2: set up the grid layout for recycler view.

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

    /**
     * Change the recycler view when the editable status changed.
     * @param editable whether the page become editable or not.
     */
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