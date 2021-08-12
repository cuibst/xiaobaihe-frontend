package com.java.cuiyikai;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rcy1;
    private Button use_for_test;
    private Button btnForLogIn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnForLogIn=findViewById(R.id.btn_for_login);
        rcy1=findViewById(R.id.rcy_1);
        rcy1.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        LinearLayoutManager linearLayoutManager =new LinearLayoutManager(MainActivity.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rcy1.setLayoutManager(linearLayoutManager);
        rcy1.setAdapter(new RcyAdapter(MainActivity.this));
        use_for_test=findViewById(R.id.use_for_test);
        use_for_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this, test.class);
                startActivity(intent);
            }
        });
        btnForLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this, Login_Activity.class);
                startActivity(intent);
            }
        });
    }
}