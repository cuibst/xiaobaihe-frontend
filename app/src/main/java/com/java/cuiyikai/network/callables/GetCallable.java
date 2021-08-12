package com.java.cuiyikai.network.callables;

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

public class GetCallable implements Callable<JSONObject> {

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
