package com.java.cuiyikai;

import android.app.Application;
import android.view.KeyEvent;

import com.java.cuiyikai.network.RequestBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Writer;
import java.util.Scanner;

public class MainApplication extends Application {

    private String saveUsername = "";
    private String savePassword = "";

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
            System.out.println(loadFile.getAbsolutePath());
            System.out.println(RequestBuilder.getBackendToken());
            System.out.println(saveUsername);
            System.out.println(savePassword);
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
