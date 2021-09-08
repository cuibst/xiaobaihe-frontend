package com.java.cuiyikai.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;

import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.java.cuiyikai.exceptions.BackendTokenExpiredException;
import com.java.cuiyikai.network.callables.GetCallable;
import com.java.cuiyikai.network.callables.JsonPostCallable;
import com.java.cuiyikai.network.callables.PostCallable;
import com.java.cuiyikai.utilities.ConstantUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * This class is used to send request to the <strong>fucking knowledge site</strong>. <br>
 * This is an utility class.
 */
public class RequestBuilder {

    private static final Logger logger = LoggerFactory.getLogger(RequestBuilder.class);

//    private static final String PHONE = "18211517925";
//    private static final String PASSWORD = "ldx0881110103";
    private static final String PHONE = "16688092093";
    private static final String PASSWORD = "0730llhh";
//    private static final String PHONE = "15910826331";
//    private static final String PASSWORD = "cbst20001117";
    private static String token = null;
    public static final String BASE_URL = "http://open.edukg.cn/opedukg/api/";
    private static final String POST_CONTENT_TYPE = "application/x-www-form-urlencoded; charset=UTF-8";
    private static final String POST_JSON_CONTENT_TYPE = "application/json;charset=UTF-8";

    public static boolean isNetworkNormal(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private RequestBuilder() {}

    public static void setConnectionHeader(HttpURLConnection connection, String method) throws ProtocolException {
        logger.info("Set connection method : {}", method);
        connection.setRequestMethod(method);
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(10000);
        if(method.equals("POST")) {
            connection.setRequestProperty("Accept", "*/*");
            connection.setRequestProperty("Accept-Language", "zh-CN");
            connection.setRequestProperty("Accept-Encoding", "gzip,deflate");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", POST_CONTENT_TYPE);
        }
        connection.setDoInput(true);
    }

    public static void setConnectionHeader(HttpURLConnection connection, String method, boolean sendJson) throws ProtocolException {
        logger.info("Set connection method : {}, type : {}", method, sendJson);
        connection.setRequestMethod(method);
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(10000);
        if(method.equals("POST")) {
            connection.setRequestProperty("Accept", "*/*");
            connection.setRequestProperty("Accept-Language", "zh-CN");
            connection.setDoOutput(true);
            if(sendJson)
                connection.setRequestProperty("Content-Type", POST_JSON_CONTENT_TYPE);
            else
                connection.setRequestProperty("Content-Type", POST_CONTENT_TYPE);
        }
        connection.setDoOutput(true);
    }

    public static String buildForm(Map<String,String> form) {
        if(form.size() == 0)
            return "";
        StringBuilder builder = new StringBuilder();
        form.forEach((key, value) -> {
            try {
                builder.append(URLEncoder.encode(key, "UTF-8"));
                builder.append('=');
                builder.append(URLEncoder.encode(value, "UTF-8"));
                builder.append('&');
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        builder.deleteCharAt(builder.length()-1);
        return builder.toString();
    }

    private static final ExecutorService executorService = Executors.newCachedThreadPool();

    private static Future<String> getToken() {
        return executorService.submit(() -> {
            if(token == null) {
                try {
                    URL loginUrl = new URL(BASE_URL + "typeAuth/user/login");
                    HttpURLConnection connection = (HttpURLConnection) loginUrl.openConnection();
                    setConnectionHeader(connection, "POST");
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8));
                    Map<String, String> form = new HashMap<>();
                    form.put("password", PASSWORD);
                    form.put("phone", PHONE);
                    logger.info("POST : {} {}", loginUrl, buildForm(form));
                    writer.write(buildForm(form));
                    writer.flush();
                    if(connection.getResponseCode() == 200)
                    {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                        String line;
                        StringBuilder buffer = new StringBuilder();
                        while((line = reader.readLine()) != null) {
                            buffer.append(line);
                        }
                        JSONObject response = JSON.parseObject(buffer.toString());
                        logger.info("Reply with : {}", response);
                        token = response.get(ConstantUtilities.ARG_ID).toString();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return token;
        });
    }

    public static Future<JSONObject> asyncSendPostRequest(String remainUrl, Map<String,String> arguments) throws InterruptedException, ExecutionException {
        String id;
        Future<String> token = getToken();
        id = token.get();
        arguments.put(ConstantUtilities.ARG_ID, id);
        PostCallable postCallable = new PostCallable(BASE_URL + remainUrl, arguments);
        return executorService.submit(postCallable);
    }

    @Nullable
    public static JSONObject sendPostRequest(String remainUrl, Map<String,String> arguments) throws InterruptedException, ExecutionException {
        return asyncSendPostRequest(remainUrl, arguments).get();
    }

    public static Future<JSONObject> asyncSendPostRequest(String remainUrl, Map<String,String> arguments, Handler handler) throws InterruptedException, ExecutionException {
        String id;
        Future<String> token = getToken();
        id = token.get();
        arguments.put(ConstantUtilities.ARG_ID, id);
        PostCallable postCallable = new PostCallable(BASE_URL + remainUrl, arguments);
        postCallable.attachHandler(handler);
        return executorService.submit(postCallable);
    }

    @Nullable
    public static JSONObject sendPostRequest(String remainUrl, Map<String,String> arguments, Handler handler) throws InterruptedException, ExecutionException {
        return asyncSendPostRequest(remainUrl, arguments, handler).get();
    }

    public static Future<JSONObject> asyncSendGetRequest(String remainUrl, Map<String,String> arguments) throws InterruptedException, ExecutionException {
        String id;
        Future<String> token = getToken();
        id = token.get();
        arguments.put(ConstantUtilities.ARG_ID, id);
        String builder = BASE_URL +
                remainUrl +
                '?' +
                buildForm(arguments);
        GetCallable getCallable = new GetCallable(builder, arguments);
        return executorService.submit(getCallable);
    }

    @Nullable
    public static JSONObject sendGetRequest(String remainUrl, Map<String,String> arguments) throws InterruptedException, ExecutionException {
        return asyncSendGetRequest(remainUrl, arguments).get();
    }

    public static Future<JSONObject> asyncSendGetRequest(String remainUrl, Map<String,String> arguments, Handler handler) throws InterruptedException, ExecutionException {
        String id;
        Future<String> token = getToken();
        id = token.get();
        arguments.put(ConstantUtilities.ARG_ID, id);
        String builder = BASE_URL +
                remainUrl +
                '?' +
                buildForm(arguments);
        GetCallable getCallable = new GetCallable(builder, arguments);
        getCallable.attachHandler(handler);
        return executorService.submit(getCallable);
    }

    @Nullable
    public static JSONObject sendGetRequest(String remainUrl, Map<String,String> arguments, Handler handler) throws InterruptedException, ExecutionException {
        return asyncSendGetRequest(remainUrl, arguments,handler).get();
    }

    private static String backendToken = null;

    public static void setBackendToken(String backendToken) {
        RequestBuilder.backendToken = backendToken;
    }

    public static String getBackendToken() {
        return backendToken;
    }

    private static long expireTime = 0;

    private static final String BACKEND_ADDRESS = "http://183.172.183.37:8080";

    public interface OnTokenChangedListener {
        void onTokenChanged();
    }

    public static OnTokenChangedListener onTokenChangedListener = null;

    public static boolean checkedLogin() {
        if(backendToken == null)
            return false;
        if(expireTime < System.currentTimeMillis()) {
            Map<String, String> args = new HashMap<>();
            args.put(ConstantUtilities.ARG_TOKEN, backendToken);
            try {
                JSONObject reply = sendBackendGetRequest("/api/login/exchangeToken", args, false);
                backendToken = reply.getString(ConstantUtilities.ARG_TOKEN);
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_WEEK, 1);
                expireTime = calendar.getTimeInMillis();
                if(onTokenChangedListener != null)
                    onTokenChangedListener.onTokenChanged();
            } catch (BackendTokenExpiredException | InterruptedException | ExecutionException  | NullPointerException e) {
                e.printStackTrace();
                return false;
            }
            return backendToken != null;
        }
        return true;
    }

    public static void logOut() {
        backendToken = null;
        onTokenChangedListener.onTokenChanged();
    }

    public static Future<String> getBackendToken(String username, String password) {
        return executorService.submit(() -> {
            if(backendToken == null || new Date().getTime() > expireTime) {
                try {
                    URL loginUrl = new URL(BACKEND_ADDRESS + "/api/login/");
                    HttpURLConnection connection = (HttpURLConnection) loginUrl.openConnection();
                    setConnectionHeader(connection, "POST", true);
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8));
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("username", username);
                    jsonObject.put("password", password);
                    writer.write(jsonObject.toString());
                    writer.flush();
                    if(connection.getResponseCode() == 200)
                    {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                        String line;
                        StringBuilder buffer = new StringBuilder();
                        while((line = reader.readLine()) != null) {
                            buffer.append(line);
                        }
                        JSONObject response = JSON.parseObject(buffer.toString());
                        backendToken = response.get(ConstantUtilities.ARG_TOKEN).toString();
                        Calendar calendar = Calendar.getInstance();
                        calendar.add(Calendar.DAY_OF_WEEK, 1);
                        expireTime = calendar.getTimeInMillis();
                    }
                    else
                        return null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return backendToken;
        });
    }

    public static Future<JSONObject> asyncSendBackendGetRequest(String remainUrl, Map<String,String> arguments, boolean needToken) throws BackendTokenExpiredException {
        if(needToken) {
            if(!checkedLogin())
                throw new BackendTokenExpiredException(ConstantUtilities.MESSAGE_TOKEN_EXPIRED);
            arguments.put(ConstantUtilities.ARG_TOKEN, backendToken);
        }
        String builder = BACKEND_ADDRESS +
                remainUrl +
                '?' +
                buildForm(arguments);
        GetCallable getCallable = new GetCallable(builder, arguments);
        return executorService.submit(getCallable);
    }

    public static JSONObject sendBackendGetRequest(String remainUrl, Map<String,String> arguments, boolean needToken) throws InterruptedException, ExecutionException, BackendTokenExpiredException {
        return asyncSendBackendGetRequest(remainUrl, arguments, needToken).get();
    }

    public static Future<JSONObject> asyncSendBackendGetRequest(String remainUrl, Map<String,String> arguments, Handler handler, boolean needToken) throws BackendTokenExpiredException {
        if(needToken) {
            if(!checkedLogin())
                throw new BackendTokenExpiredException(ConstantUtilities.MESSAGE_TOKEN_EXPIRED);
            arguments.put(ConstantUtilities.ARG_TOKEN, backendToken);
        }
        String builder = BACKEND_ADDRESS +
                remainUrl +
                '?' +
                buildForm(arguments);
        GetCallable getCallable = new GetCallable(builder, arguments);
        getCallable.attachHandler(handler);
        return executorService.submit(getCallable);
    }

    public static JSONObject sendBackendGetRequest(String remainUrl, Map<String,String> arguments, Handler handler, boolean needToken) throws InterruptedException, ExecutionException, BackendTokenExpiredException {
        return asyncSendBackendGetRequest(remainUrl, arguments, handler, needToken).get();
    }

    public static Future<JSONObject> asyncSendBackendPostRequest(String remainUrl, JSONObject arguments, boolean needToken) throws BackendTokenExpiredException {
        if(needToken) {
            if(!checkedLogin())
                throw new BackendTokenExpiredException(ConstantUtilities.MESSAGE_TOKEN_EXPIRED);
            arguments.put(ConstantUtilities.ARG_TOKEN, backendToken);
        }
        JsonPostCallable jsonPostCallable = new JsonPostCallable(BACKEND_ADDRESS + remainUrl, arguments);
        return executorService.submit(jsonPostCallable);
    }

    @Nullable
    public static JSONObject sendBackendPostRequest(String remainUrl, JSONObject arguments, boolean needToken) throws InterruptedException, ExecutionException, BackendTokenExpiredException {
        return asyncSendBackendPostRequest(remainUrl, arguments, needToken).get();
    }

    public static Future<JSONObject> asyncSendBackendPostRequest(String remainUrl, JSONObject arguments, Handler handler, boolean needToken) throws BackendTokenExpiredException {
        if(needToken) {
            if(!checkedLogin())
                throw new BackendTokenExpiredException(ConstantUtilities.MESSAGE_TOKEN_EXPIRED);
            arguments.put(ConstantUtilities.ARG_TOKEN, backendToken);
        }
        JsonPostCallable jsonPostCallable = new JsonPostCallable(BACKEND_ADDRESS + remainUrl, arguments);
        jsonPostCallable.attachHandler(handler);
        return executorService.submit(jsonPostCallable);
    }

    @Nullable
    public static JSONObject sendBackendPostRequest(String remainUrl, JSONObject arguments, Handler handler, boolean needToken) throws InterruptedException, ExecutionException, BackendTokenExpiredException {
        return asyncSendBackendPostRequest(remainUrl, arguments, handler, needToken).get();
    }
}
