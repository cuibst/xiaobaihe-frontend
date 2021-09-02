package com.java.cuiyikai.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.google.android.material.textfield.TextInputEditText;
import com.java.cuiyikai.R;
import com.java.cuiyikai.network.RequestBuilder;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText userName,passWord,email,phone;
    private String registerUrl="/api/register/";
    private String checkemail="/api/register/check/username";
    private Button register_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        userName=findViewById(R.id.register_username);
        passWord=findViewById(R.id.register_password);
        email=findViewById(R.id.register_email);
        phone=findViewById(R.id.register_phone);
        register_btn=findViewById(R.id.btn_register);
        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckName checkName=new CheckName();
                Thread checkThread=new Thread(checkName);
                checkThread.start();
            }
        });
    }
    MyHandler handler=new MyHandler();
    private class CheckName implements Runnable {

        @Override
        public void run() {
            Map<String,String > map=new HashMap<>();
            map.put("username",userName.getText().toString());
            try {
                JSONObject msg=RequestBuilder.sendBackendGetRequest(checkemail, map, false);
                if(msg.get("status").toString().equals("ok"))
                    handler.sendEmptyMessage(0);
                else
                    handler.sendEmptyMessage(1);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    private class MyHandler extends Handler {
        @Override
        public void handleMessage(@NonNull android.os.Message msg) {
            switch(msg.what)
            {
                case 0:
                    System.out.println(msg.what);
                    Register register=new Register();
                    Thread registerThread=new Thread(register);
                    registerThread.start();
                    break;
                case 1:
                    System.out.println(msg.what);
                    Toast.makeText(RegisterActivity.this, "用户名已被注册", Toast.LENGTH_LONG).show();
                    break;
                case 2:
                    Toast.makeText(RegisterActivity.this, "邮件已发送，请激活后使用", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }
    private class Register implements Runnable{

        @Override
        public void run() {
            Map<String,Object > map=new HashMap<>();
            map.put("password",passWord.getText().toString());
            map.put("email",email.getText().toString());
            map.put("username",userName.getText().toString());
            try {
                JSONObject msg=RequestBuilder.sendBackendPostRequest(registerUrl, new JSONObject(map), false);
                handler.sendEmptyMessage(2);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}