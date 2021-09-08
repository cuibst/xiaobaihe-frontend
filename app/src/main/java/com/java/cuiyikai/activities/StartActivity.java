package com.java.cuiyikai.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.java.cuiyikai.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Class {@link StartActivity} is the loading page exhibited when entering the app.
 * After the app finishes loading all the data, this page will jump to main page.
 */
public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        Intent []intent = new Intent[1];
        intent[0] = new Intent(this, MainActivity.class);
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                startActivities(intent);
                StartActivity.this.finish();
            }
        };
        timer.schedule(task, 500);
    }
}