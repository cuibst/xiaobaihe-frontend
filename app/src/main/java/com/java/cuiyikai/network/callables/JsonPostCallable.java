package com.java.cuiyikai.network.callables;

import android.os.Handler;
import android.os.Message;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.java.cuiyikai.network.RequestBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;
import java.util.zip.GZIPInputStream;

/**
 * A {@link Callable} designed to make <strong>POST</strong> requests that send a {@link JSONObject}.
 */
public class JsonPostCallable implements Callable<JSONObject> {

    private static final Logger logger = LoggerFactory.getLogger(JsonPostCallable.class);

    private Handler handler = null;

    /**
     * Attach a handler to receive callback.
     * <p>will receive 1 for fail, 2 for success</p>
     * @param handler the handler to be attached
     */
    public void attachHandler(Handler handler) {
        this.handler = handler;
    }

    private final String url;

    private final JSONObject arguments;

    /**
     * Only constructor for {@link JsonPostCallable}
     * @param u the full url of the target
     * @param arg the args needed to pass.
     */
    public JsonPostCallable(String u, JSONObject arg) {
        url = u;
        arguments = arg;
    }

    /**
     * {@inheritDoc}
     * @return the reply in {@link JSONObject}
     * @throws Exception when request failed to operate
     */
    @Override
    public JSONObject call() throws Exception {
        URL loginUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) loginUrl.openConnection();
        RequestBuilder.setConnectionHeader(connection, "POST", true);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8));
        logger.info("POST json = {} to {}", arguments, url);
        writer.write(arguments.toString());
        writer.flush();
        if(connection.getResponseCode() == 200)
        {
            if(connection.getContentEncoding() != null && connection.getContentEncoding().contains("gzip")) { //gzip deflated
                GZIPInputStream gzipInputStream = new GZIPInputStream(connection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(gzipInputStream));
                String line;
                StringBuilder buffer = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                logger.info("Reply with : {}", buffer);
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
                logger.info("Reply with : {}", buffer);
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