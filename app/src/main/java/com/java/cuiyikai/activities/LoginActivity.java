package com.java.cuiyikai.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.java.cuiyikai.MainApplication;
import com.java.cuiyikai.R;
import com.java.cuiyikai.exceptions.AuthorizeFaliedException;
import com.java.cuiyikai.network.RequestBuilder;

public class LoginActivity extends AppCompatActivity {

    private Button logInPostBtn;
    private TextView usernameTextView;
    private TextView passwordTextView;
    private Button jumpLoginBtn;
    private CheckBox checkBox;
    private TextView registerButton;
    String username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        logInPostBtn = findViewById(R.id.btn_login);
        jumpLoginBtn = findViewById(R.id.btn_skip);
        checkBox = findViewById(R.id.remember_password);
        usernameTextView = (EditText) findViewById(R.id.edittext_username);
        passwordTextView = (EditText) findViewById(R.id.edittext_password);
        registerButton = findViewById(R.id.tv_register);

        registerButton.setOnClickListener((View view) -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        MainApplication mainApplication = (MainApplication) getApplication();
        usernameTextView.setText(mainApplication.getSaveUsername());
        passwordTextView.setText(mainApplication.getSavePassword());
        checkBox.setChecked(mainApplication.isSaveChecked());

        logInPostBtn.setOnClickListener((View view) -> {
            username = usernameTextView.getText().toString();
            password = passwordTextView.getText().toString();
            try {
                String token = RequestBuilder.getBackendToken(username, password).get();
                if (token == null)
                    throw new AuthorizeFaliedException("Incorrect username or password");
                Toast.makeText(LoginActivity.this, "successfully login", Toast.LENGTH_SHORT).show();
                if(checkBox.isChecked()) {
                    mainApplication.setSaveUsername(username);
                    mainApplication.setSavePassword(password);
                    mainApplication.setSaveChecked(checkBox.isChecked());
                } else {
                    mainApplication.setSavePassword("");
                    mainApplication.setSaveUsername(username);
                    mainApplication.setSaveChecked(checkBox.isChecked());
                }
                mainApplication.dumpCacheData();
                this.finish();
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