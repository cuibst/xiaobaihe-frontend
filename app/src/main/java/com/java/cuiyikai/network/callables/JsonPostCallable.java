package com.java.cuiyikai.network.callables;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.java.cuiyikai.network.RequestBuilder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;

public class JsonPostCallable implements Callable<JSONObject> {

    String url;

    JSONObject arguments;

    public JsonPostCallable(String u, JSONObject arg) {
        url = u;
        arguments = arg;
    }

    @Override
    public JSONObject call() throws Exception {
        URL loginUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) loginUrl.openConnection();
        RequestBuilder.setConnectionHeader(connection, "POST", true);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8));
        System.out.printf("POST json = %s to %s%n", arguments, url);
        writer.write(arguments.toString());
        writer.flush();
        if(connection.getResponseCode() == 200)
        {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            String line;
            StringBuilder buffer = new StringBuilder();
            while((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            return JSON.parseObject(buffer.toString());
        }
        return null;
    }
}