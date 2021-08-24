package com.java.cuiyikai;

import android.app.Application;
import com.alibaba.fastjson.JSONObject;
import com.java.cuiyikai.exceptions.BackendTokenExpiredException;
import com.java.cuiyikai.network.RequestBuilder;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.openapi.IWBAPI;
import com.sina.weibo.sdk.openapi.WBAPIFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class MainApplication extends Application {

    private String saveUsername = "";
    private String savePassword = "";

    private JSONObject favourite = null;

    public JSONObject getFavourite() {
        if(!RequestBuilder.checkedLogin()) {
            favourite = null;
            return null;
        } else {
            if(favourite == null)
                updateFavourite();
            return favourite;
        }
    }

    public void updateFavourite() {
        if(RequestBuilder.checkedLogin())
            try {
                favourite = RequestBuilder.sendBackendGetRequest("/api/favourite/getFavourite", new HashMap<>(), true).getJSONObject("data");
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            } catch (BackendTokenExpiredException | NullPointerException e) {
                e.printStackTrace();
            }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        File loadFile = new File(getFilesDir(), "cache.txt");
        if(!loadFile.exists())
            return;
        try (Scanner scanner = new Scanner(new FileInputStream(loadFile))) {
            String savedToken = scanner.nextLine();
            saveUsername = scanner.nextLine();
            savePassword = scanner.nextLine();
            if(!savedToken.equals(""))
                RequestBuilder.setBackendToken(savedToken);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void dumpCacheData() {
        File loadFile = new File(getFilesDir(), "cache.txt");
        if(!loadFile.exists())
            try {
                loadFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        try(PrintStream printStream = new PrintStream(new FileOutputStream(loadFile))) {
            printStream.println(RequestBuilder.getBackendToken());
            printStream.println(saveUsername);
            printStream.println(savePassword);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getSaveUsername() {
        return saveUsername;
    }

    public String getSavePassword() {
        return savePassword;
    }

    public void setSaveUsername(String saveUsername) {
        this.saveUsername = saveUsername;
    }

    public void setSavePassword(String savePassword) {
        this.savePassword = savePassword;
    }
}
