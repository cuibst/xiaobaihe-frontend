package com.java.cuiyikai.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.java.cuiyikai.R;
import com.java.cuiyikai.exceptions.AuthorizeFaliedException;
import com.java.cuiyikai.network.RequestBuilder;

import com.alibaba.fastjson.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Future;

public class LoginActivity extends AppCompatActivity {

    private Button logInPostBtn;
    private TextView username;
    private TextView passWord;
    private Button jumpLoginBtn;
    private RadioButton radioButton;
    String name, password;
    URL url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            url=new URL("http://183.172.176.163g:8080/api/login/");
        }
        catch (MalformedURLException e)
        {
            Toast.makeText(LoginActivity.this,"An error occured when getting url!",Toast.LENGTH_SHORT).show();
        }
        finally {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);
            radioButton = (RadioButton) findViewById(R.id.remember_password);
            logInPostBtn = findViewById(R.id.btn_login);
            jumpLoginBtn = findViewById(R.id.btn_skip);
            username = (EditText) findViewById(R.id.edittext_username);
            passWord = (EditText) findViewById(R.id.edittext_password);
            logInPostBtn.setOnClickListener(mPostClickListener);
            jumpLoginBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(LoginActivity.this ,MainActivity.class);
                    startActivity(intent);
                }
            });
            radioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view){

                }
//                public void toggle() {
//                    radioButton.setChecked(!radioButton.isChecked());
//                    if (!radioButton.isChecked()) {
//                        ((RadioGroup)radioButton.getParent()).clearCheck();
//                    }
//                }
            });
        }
    };

    private View.OnClickListener mPostClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            name = username.getText().toString();
            password = passWord.getText().toString();
//            new Thread(networkconnection).start();
            try {
//                String info;
//                JSONObject map = new JSONObject();
//                map.put("username", name);
//                map.put("password", password);
//                JSONObject reply=RequestBuilder.sendJsonPostRequest(url.toString(),map);
                String token = RequestBuilder.getBackendToken(name, password).get();
                if(token == null)
                    throw new AuthorizeFaliedException("Incorrect username or password");
                Toast.makeText(LoginActivity.this, "successfully login", Toast.LENGTH_SHORT).show();
            }
            catch(Exception e)
            {
                Toast.makeText(LoginActivity.this, "login failed", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    };
//    Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what) {
//                case 0:
//                    Toast.makeText(Login_Activity.this, (String) msg.obj, Toast.LENGTH_SHORT).show();
//                    break;
//                case 1:
//                    Toast.makeText(Login_Activity.this, (String) msg.obj, Toast.LENGTH_SHORT).show();
//                    break;
//                default:
//                    break;
//            }
//        }
//    };
//    Runnable networkconnection = new Runnable() {
//        @Override
//        public void run() {
//            try {
//                URLConnection connection = url.openConnection();
//                HttpURLConnection httpurlconnection = (HttpURLConnection) connection;
//                httpurlconnection.setDoOutput(true);
//                httpurlconnection.setDoInput(true);
//                httpurlconnection.setRequestMethod("POST");
//                httpurlconnection.setRequestProperty("Accept-Charset", "utf-8");
//                httpurlconnection.setRequestProperty("Connection", "Keep-Alive");
//                httpurlconnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
//
//                httpurlconnection.setConnectTimeout(5 * 1000);// 设置连接超时时间为5秒
//                httpurlconnection.setReadTimeout(20 * 1000);// 设置读取超时时间为20秒
////               g
//                httpurlconnection.connect();
//
//                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(httpurlconnection.getOutputStream(), "UTF-8"));
//                String info;
//                JSONObject map = new JSONObject();
//                map.put("username", name);
//                map.put("password", password);
//                info = map.toString();
////                handler.sendEmptyMessage(1);
////                usermsg.obj=info;
////                handler.sendMessage(usermsg);
////                Toast.makeText(Login_Activity.this,info,Toast.LENGTH_SHORT).show();
//                writer.write(info);
//                writer.flush();
//                writer.close();
//                String result;
//                String chk = "\0";
//                int responseCode = httpurlconnection.getResponseCode();
//                if (responseCode == HttpURLConnection.HTTP_OK) {
//                    BufferedReader responseReader = new BufferedReader(new InputStreamReader(httpurlconnection.getInputStream(), "UTF-8"));
//                    while ((result = responseReader.readLine()) != null) {
//                        chk += result;
//                    }
//                    chk += "yes";
//                } else {
//                    result = "connection failed";
//                    chk += result;
//                    chk += "no";
//                }
//                handler.sendEmptyMessage(0);
//                Message msg = new Message();
//                msg.obj = chk;
//                handler.sendMessage(msg);
//            } catch (Exception e) {
//                Message usermsg = new Message();
//                usermsg.obj=e.toString();
//                handler.sendMessage(usermsg);
////                Toast.makeText(Login_Activity.this,e.toString(),Toast.LENGTH_SHORT).show();
//            }
//        }
//    };
}