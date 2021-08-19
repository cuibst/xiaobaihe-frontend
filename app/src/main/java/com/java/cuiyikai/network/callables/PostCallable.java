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
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.zip.GZIPInputStream;

public class PostCallable implements Callable<JSONObject> {

    String url;

    Map<String,String> arguments;

    public PostCallable(String u, Map<String, String> arg) {
        url = u;
        arguments = arg;
    }

    @Override
    public JSONObject call() throws Exception {
        URL loginUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) loginUrl.openConnection();
        RequestBuilder.setConnectionHeader(connection, "POST");
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8));
        System.out.printf("POST form = %s to %s%n", RequestBuilder.buildForm(arguments), url);
        writer.write(RequestBuilder.buildForm(arguments));
        writer.flush();
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
                return JSON.parseObject(buffer.toString());
            } else {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                String line;
                StringBuilder buffer = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                System.out.printf("Reply with : %s%n", buffer.toString());
                return JSON.parseObject(buffer.toString());
            }
        }
        return null;
    }
}