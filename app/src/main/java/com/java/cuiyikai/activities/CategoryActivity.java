package com.java.cuiyikai.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.java.cuiyikai.MainApplication;
import com.java.cuiyikai.R;
import com.java.cuiyikai.adapters.GridViewAdapter;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;
import com.yanzhenjie.recyclerview.touch.OnItemMoveListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {

    private List<String> userList = new ArrayList<>();
    private List<String> otherList = new ArrayList<>();
    private GridViewAdapter userAdapter;
    private GridViewAdapter otherAdapter;
    private SwipeRecyclerView userView;
    private SwipeRecyclerView otherView;

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
        userView = (SwipeRecyclerView) findViewById(R.id.user_gv);
        otherView = (SwipeRecyclerView) findViewById(R.id.other_gv);
        userAdapter = new GridViewAdapter(this, userList, 0);
        otherAdapter = new GridViewAdapter(this, otherList, 1);

        GridLayoutManager userLayoutManager = new GridLayoutManager(CategoryActivity.this, 4);
        userLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        userView.setLayoutManager(userLayoutManager);
        userView.setLongPressDragEnabled(true);

        GridLayoutManager otherLayoutManager = new GridLayoutManager(CategoryActivity.this, 4);
        otherLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        otherView.setLayoutManager(otherLayoutManager);
        otherView.setLongPressDragEnabled(true);


        userView.setOnItemClickListener((View v, int adapterPosition) -> {
            otherList.add(userList.get(adapterPosition));
            otherAdapter.notifyItemInserted(otherList.size() - 1);
            userList.remove(adapterPosition);
            userAdapter.notifyItemRemoved(adapterPosition);
            ((MainApplication)getApplication()).setSubjects(userList);
        });
        otherView.setOnItemClickListener((View v, int adapterPosition) -> {
            userList.add(otherList.get(adapterPosition));
            userAdapter.notifyItemInserted(userList.size() - 1);
            otherList.remove(adapterPosition);
            otherAdapter.notifyItemRemoved(adapterPosition);
            ((MainApplication)getApplication()).setSubjects(userList);
        });

        userView.setOnItemMoveListener(new OnItemMoveListener() {
            @Override
            public boolean onItemMove(RecyclerView.ViewHolder srcHolder, RecyclerView.ViewHolder targetHolder) {
                if(srcHolder.getItemViewType() != targetHolder.getItemViewType())
                    return false;
                int fromPosition = srcHolder.getAdapterPosition();
                int toPosition = targetHolder.getAdapterPosition();

                Collections.swap(userList, fromPosition, toPosition);
                userAdapter.notifyItemMoved(fromPosition, toPosition);
                ((MainApplication)getApplication()).setSubjects(userList);
                return true;
            }

            @Override
            public void onItemDismiss(RecyclerView.ViewHolder srcHolder) {
                throw new UnsupportedOperationException("No swipe dismiss!!");
            }
        });

        otherView.setOnItemMoveListener(new OnItemMoveListener() {
            @Override
            public boolean onItemMove(RecyclerView.ViewHolder srcHolder, RecyclerView.ViewHolder targetHolder) {
                if(srcHolder.getItemViewType() != targetHolder.getItemViewType())
                    return false;
                int fromPosition = srcHolder.getAdapterPosition();
                int toPosition = targetHolder.getAdapterPosition();

                Collections.swap(otherList, fromPosition, toPosition);
                otherAdapter.notifyItemMoved(fromPosition, toPosition);
                return true;
            }

            @Override
            public void onItemDismiss(RecyclerView.ViewHolder srcHolder) {
                throw new UnsupportedOperationException("No swipe dismiss!!");
            }
        });

        userView.setAdapter(userAdapter);
        otherView.setAdapter(otherAdapter);

        findViewById(R.id.btn_close).setOnClickListener((View v) -> this.finish());

    }

}