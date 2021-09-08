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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button logInPostBtn = findViewById(R.id.btn_login);
        CheckBox checkBox = findViewById(R.id.remember_password);
        EditText usernameTextView = findViewById(R.id.edittext_username);
        EditText passwordTextView = findViewById(R.id.edittext_password);
        TextView registerButton = findViewById(R.id.tv_register);

        registerButton.setOnClickListener((View view) -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        //Get the saved data from the Main application (save text)
        MainApplication mainApplication = (MainApplication) getApplication();
        usernameTextView.setText(mainApplication.getSaveUsername());
        passwordTextView.setText(mainApplication.getSavePassword());
        checkBox.setChecked(mainApplication.isSaveChecked());

        logInPostBtn.setOnClickListener((View view) -> {
            String username = usernameTextView.getText().toString();
            String password = passwordTextView.getText().toString();
            try {
                String token = RequestBuilder.getBackendToken(username, password).get();
                if (token == null)
                    throw new AuthorizeFaliedException("Incorrect username or password");
                Toast.makeText(LoginActivity.this, "successfully login", Toast.LENGTH_SHORT).show();
                if(checkBox.isChecked()) {
                    mainApplication.setSaveUsername(username);
                    mainApplication.setSavePassword(password);
                } else {
                    mainApplication.setSavePassword("");
                    mainApplication.setSaveUsername(username);
                }
                mainApplication.setSaveChecked(checkBox.isChecked());
                //dump the data when the user logins
                mainApplication.dumpCacheData();
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                this.finish();
            } catch (Exception e) {
                Toast.makeText(LoginActivity.this, "login failed", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });
    }
}