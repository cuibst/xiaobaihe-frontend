package com.java.cuiyikai;

import android.app.Application;
import com.alibaba.fastjson.JSONObject;
import com.java.cuiyikai.exceptions.BackendTokenExpiredException;
import com.java.cuiyikai.network.RequestBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class MainApplication extends Application {

    private String saveUsername = "";
    private String savePassword = "";

    private JSONObject favourite = null;

    private List<String> subjects = new ArrayList<>(Arrays.asList("推荐", "语文", "数学", "英语", "物理", "化学", "生物", "历史", "地理", "政治"));

    public List<String> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<String> subjects) {
        this.subjects = subjects;
        dumpCacheData();
    }

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
        if(loadFile.exists()) {
            try (Scanner scanner = new Scanner(new FileInputStream(loadFile))) {
                String savedToken = scanner.nextLine();
                saveUsername = scanner.nextLine();
                savePassword = scanner.nextLine();
                if (!savedToken.equals(""))
                    RequestBuilder.setBackendToken(savedToken);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        RequestBuilder.onTokenChangedListener = this::dumpCacheData;

        loadFile = new File(getFilesDir(), "subjects.txt");
        if(loadFile.exists()) {
            subjects = new ArrayList<>();
            try (Scanner scanner = new Scanner(new FileInputStream(loadFile))) {
                int num = scanner.nextInt();
                scanner.nextLine();
                for(int i=0;i<num;i++) {
                    String subject = scanner.nextLine();
                    subjects.add(subject);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
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
        loadFile = new File(getFilesDir(), "subjects.txt");
        if(!loadFile.exists())
            try {
                loadFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        try(PrintStream printStream = new PrintStream(new FileOutputStream(loadFile))) {
            printStream.println(subjects.size());
            for(String subject : subjects)
                printStream.println(subject);
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
