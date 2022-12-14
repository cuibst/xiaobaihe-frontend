package com.java.cuiyikai.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.java.cuiyikai.R;
import com.java.cuiyikai.adapters.OfflineAdapter;
import com.java.cuiyikai.database.DatabaseEntity;
import com.java.cuiyikai.database.EntityDatabaseHelper;
import com.java.cuiyikai.network.RequestBuilder;

import java.util.List;

/**
 * {@link android.app.Activity} for offline behaviours.
 */
public class OfflineActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline);

        //get the data from the database and show them
        EntityDatabaseHelper helper = EntityDatabaseHelper.getInstance(OfflineActivity.this, 1);
        helper.openReadLink();

        List<DatabaseEntity> entityList = helper.queryAllEntity();

        helper.closeLink();

        //activate return to online mode.
        findViewById(R.id.btnReturnToOnlineMode).setOnClickListener((View v) -> {
            if(!RequestBuilder.isNetworkNormal(OfflineActivity.this))
                Toast.makeText(OfflineActivity.this, "网络链接异常", Toast.LENGTH_LONG).show();
            else
            {
                Intent intent = new Intent(OfflineActivity.this, MainActivity.class);
                startActivity(intent);
                this.finish();
            }
        });

        ((ListView) findViewById(R.id.listViewOffline)).setAdapter(new OfflineAdapter(OfflineActivity.this, R.layout.directory_favourite_item, entityList));

    }
}