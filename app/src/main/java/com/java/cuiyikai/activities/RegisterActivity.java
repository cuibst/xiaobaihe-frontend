package com.java.cuiyikai.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.google.android.material.textfield.TextInputEditText;
import com.java.cuiyikai.R;
import com.java.cuiyikai.network.RequestBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>{@link RegisterActivity} is used to register a new user account. It is able to examine whether the
 * username is used. After printing the register button , it will send an email to user's mailbox. The registration operation
 * is not valid unless the url in the email is clicked.</p>
 */
public class RegisterActivity extends AppCompatActivity {

    private static final Logger logger = LoggerFactory.getLogger(RegisterActivity.class);

    private TextInputEditText userName;
    private TextInputEditText passWord;
    private TextInputEditText email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        userName=findViewById(R.id.register_username);
        passWord=findViewById(R.id.register_password);
        email=findViewById(R.id.register_email);
        Button registerButton = findViewById(R.id.btn_register);
        registerButton.setOnClickListener(v -> {
            CheckName checkName=new CheckName();
            Thread checkThread=new Thread(checkName);
            checkThread.start();
        });
    }
    private final MyHandler handler=new MyHandler();
    private class CheckName implements Runnable {

        @Override
        public void run() {
            Map<String,String > map=new HashMap<>();
            map.put("username",userName.getText().toString());
            try {
                String checkEmail = "/api/register/check/username";
                JSONObject msg=RequestBuilder.sendBackendGetRequest(checkEmail, map, false);
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
            logger.info("message {}", msg.what);
            if (msg.what == 0) {
                Register register = new Register();
                Thread registerThread = new Thread(register);
                registerThread.start();
            } else if (msg.what == 1) {
                Toast.makeText(RegisterActivity.this, "用户名已被注册", Toast.LENGTH_LONG).show();
            } else if (msg.what == 2) {
                Toast.makeText(RegisterActivity.this, "邮件已发送，请激活后使用", Toast.LENGTH_LONG).show();
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
                String registerUrl = "/api/register/";
                RequestBuilder.asyncSendBackendPostRequest(registerUrl, new JSONObject(map), false);
                handler.sendEmptyMessage(2);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}