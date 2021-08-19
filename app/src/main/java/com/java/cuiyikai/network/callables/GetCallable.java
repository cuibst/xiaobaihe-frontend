package com.java.cuiyikai.network.callables;

import android.os.Handler;
import android.os.Message;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.java.cuiyikai.network.RequestBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.zip.GZIPInputStream;

public class GetCallable implements Callable<JSONObject> {

    Handler handler = null;

    public void attachHandler(Handler handler) {
        this.handler = handler;
    }

    String sUrl;

    Map<String, String> arguments;

    public GetCallable(String u, Map<String, String> args) {
        sUrl = u;
        arguments = args;
    }

    @Override
    public JSONObject call() throws Exception {
        URL url = new URL(sUrl);
        System.out.printf("GET : %s%n", url);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        RequestBuilder.setConnectionHeader(connection, "GET");
        System.out.printf("Connection type : %s%n", connection.getRequestMethod());
        connection.connect();
        if(connection.getResponseCode() == 200)
        {
            if(connection.getContentEncoding() != null && connection.getContentEncoding().contains("gzip")) {
                GZIPInputStream gzipInputStream = new GZIPInputStream(connection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(gzipInputStream));
                String line;
                StringBuilder buffer = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                System.out.printf("Reply with : %s%n", buffer.toString());
                if(handler != null) {
                    Message message = handler.obtainMessage();
                    message.what = 1;
                    handler.sendMessage(message);
                }
                return JSON.parseObject(buffer.toString());
            } else {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                String line;
                StringBuilder buffer = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                System.out.printf("Reply with : %s%n", buffer.toString());
                if(handler != null) {
                    Message message = handler.obtainMessage();
                    message.what = 1;
                    handler.sendMessage(message);
                }
                return JSON.parseObject(buffer.toString());
            }
        }
        if(handler != null) {
            Message message = handler.obtainMessage();
            message.what = 2;
            handler.sendMessage(message);
        }
        return null;
    }
}
