package com.java.cuiyikai.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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
    private CheckBox checkBox;
    private TextView registerButton;
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
            logInPostBtn = findViewById(R.id.btn_login);
            jumpLoginBtn = findViewById(R.id.btn_skip);
            checkBox = findViewById(R.id.remember_password);
            username = (EditText) findViewById(R.id.edittext_username);
            passWord = (EditText) findViewById(R.id.edittext_password);
            registerButton = findViewById(R.id.tv_register);

            registerButton.setOnClickListener((View view) -> {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            });

            logInPostBtn.setOnClickListener((View view) -> {
                name = username.getText().toString();
                password = passWord.getText().toString();
                try {
                    String token = RequestBuilder.getBackendToken(name, password).get();
                    if (token == null)
                        throw new AuthorizeFaliedException("Incorrect username or password");
                    Toast.makeText(LoginActivity.this, "successfully login", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(LoginActivity.this, "login failed", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            });

            jumpLoginBtn.setOnClickListener((View view) -> {
                Intent intent=new Intent(LoginActivity.this ,MainActivity.class);
                startActivity(intent);
            });
        }
    }
}