package com.java.cuiyikai.activities;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.java.cuiyikai.MainApplication;
import com.java.cuiyikai.R;
import com.java.cuiyikai.adapters.BottomFavouriteAdapter;
import com.java.cuiyikai.adapters.ProblemAdapter;
import com.java.cuiyikai.adapters.PropertyAdapter;
import com.java.cuiyikai.adapters.RelationAdapter;
import com.java.cuiyikai.database.DatabaseEntity;
import com.java.cuiyikai.database.EntityDatabaseHelper;
import com.java.cuiyikai.entities.BottomFavouriteEntity;
import com.java.cuiyikai.entities.PropertyEntity;
import com.java.cuiyikai.entities.RelationEntity;
import com.java.cuiyikai.exceptions.BackendTokenExpiredException;
import com.java.cuiyikai.network.RequestBuilder;
import com.java.cuiyikai.utilities.DensityUtilities;
import com.java.cuiyikai.utilities.PermissionUtilities;
import com.java.cuiyikai.utilities.WeiboShareCallback;
import com.java.cuiyikai.widgets.ListViewForScrollView;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.openapi.IWBAPI;
import com.sina.weibo.sdk.openapi.WBAPIFactory;
import com.xiasuhuei321.loadingdialog.view.LoadingDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class EntityActivity extends AppCompatActivity {

    private static final String[] PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.CHANGE_WIFI_STATE};

    private static final String[] SUBJECTS = {"chinese", "english", "math", "physics", "chemistry", "biology", "history", "geo", "politics"};

    private RelationAdapter relationAdapter;
    private List<RelationEntity> relationFullList;
    private List<RelationEntity> relationPrevList;

    private PropertyAdapter propertyAdapter;
    private List<PropertyEntity> propertyFullList;
    private List<PropertyEntity> propertyPrevList;

    private ProblemAdapter problemAdapter;
    private List<JSONObject> questionFullList;
    private List<JSONObject> questionPrevList;

    private String entityName;
    private String description;

    private String speech;

    private class EntityActivityLoadCallable implements Callable<String> {

        private final Handler handler;

        public EntityActivityLoadCallable(Handler handler) {
            this.handler = handler;
        }

        private JSONObject entityJson;
        private JSONObject problems;

        private void initJsonObjects(String entityName, String subject) throws InterruptedException, ExecutionException{

            EntityDatabaseHelper helper = EntityDatabaseHelper.getInstance(EntityActivity.this, 1);
            helper.openReadLink();
            List<DatabaseEntity> entityList = helper.queryEntityByNameAndSubject(entityName, subject);
            helper.closeLink();

            if (!entityList.isEmpty()) {
                entityJson = JSON.parseObject(entityList.get(0).getJsonContent());
                problems = JSON.parseObject(entityList.get(0).getProblemsJson());
            } else {

                System.out.println("No matches in database!!");

                Map<String, String> arguments = new HashMap<>();
                arguments.put("name", entityName);
                arguments.put("course", subject);

                JSONObject reply;

                try {
                    if (subject.equals("")) {
                        reply = new JSONObject();
                        for (String sub : SUBJECTS) {
                            arguments.put("course", sub);
                            JSONObject tmp = RequestBuilder.sendGetRequest("typeOpen/open/infoByInstanceName", arguments);
                            if (tmp != null && tmp.toString().length() > reply.toString().length()) {
                                subject = sub;
                                reply = tmp;
                            }
                        }
                    } else
                        reply = RequestBuilder.sendGetRequest("typeOpen/open/infoByInstanceName", arguments);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    Message message = handler.obtainMessage();
                    message.what = 2;
                    handler.sendMessage(message);
                    throw e;
                }

                entityJson = reply.getJSONObject("data");

                Map<String, String> args = new HashMap<>();
                args.put("uriName", entityName);

                try {
                    problems = RequestBuilder.sendGetRequest("typeOpen/open/questionListByUriName", args);
                } catch (Exception e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }

                helper.openWriteLink();
                DatabaseEntity databaseEntity = new DatabaseEntity();
                databaseEntity.setName(entityName);
                databaseEntity.setJsonContent(entityJson.toJSONString());
                databaseEntity.setSubject(subject);
                databaseEntity.setProblemsJson(problems == null ? "" : problems.toJSONString());
                helper.insert(databaseEntity);
                helper.closeLink();
            }
        }

        @Override
        public String call() {
            Date start = new Date();

            System.out.println("Loading start!!");

            Intent prevIntent = getIntent();

            Bundle prevBundle = prevIntent.getExtras();

            String subject = prevBundle.getString("subject", "chinese");

            try {
                initJsonObjects(entityName, subject);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
                return "fail";
            }

            StringBuilder speechBuilder = new StringBuilder();
            speechBuilder.append(entityName).append("。");
            speechBuilder.append("关系").append("：");


            System.out.printf("load finished: %f%n", (new Date().getTime() - start.getTime()) / 1000.0);

            System.out.printf("Handling relations: %f%n", (new Date().getTime() - start.getTime()) / 1000.0);

            List<JSONObject> objectList = entityJson.getJSONArray("content").toJavaList(JSONObject.class);

            relationFullList = new ArrayList<>();
            Set<RelationEntity> relationSet = new HashSet<>();
            for (JSONObject relationJson : objectList) {
                RelationEntity entity = new RelationEntity();
                entity.setRelationName(relationJson.getString("predicate_label"));
                if (relationJson.getString("object") != null) {
                    entity.setSubject(false);
                    entity.setTargetName(relationJson.getString("object_label"));
                } else {
                    entity.setSubject(true);
                    entity.setTargetName(relationJson.getString("subject_label"));
                }
                if(!relationSet.contains(entity)) {
                    relationFullList.add(entity);
                    relationSet.add(entity);
                }
            }
            System.out.printf("Sorting relations: %f%n", (new Date().getTime() - start.getTime()) / 1000.0);
            Collections.sort(relationFullList);

            Map<String, List<String>> objectRelationMap = new HashMap<>();
            Map<String, List<String>> subjectRelationMap = new HashMap<>();

            for(RelationEntity relationEntity : relationFullList) {
                if(relationEntity.isSubject()) {
                    if(subjectRelationMap.containsKey(relationEntity.getRelationName())) {
                        List<String> tmp = subjectRelationMap.get(relationEntity.getRelationName());
                        tmp.add(relationEntity.getTargetName());
                        subjectRelationMap.replace(relationEntity.getRelationName(), tmp);
                    } else {
                        List<String> tmp = new ArrayList<>();
                        tmp.add(relationEntity.getTargetName());
                        subjectRelationMap.put(relationEntity.getRelationName(), tmp);
                    }
                } else {
                    if(objectRelationMap.containsKey(relationEntity.getRelationName())) {
                        List<String> tmp = objectRelationMap.get(relationEntity.getRelationName());
                        tmp.add(relationEntity.getTargetName());
                        objectRelationMap.replace(relationEntity.getRelationName(), tmp);
                    } else {
                        List<String> tmp = new ArrayList<>();
                        tmp.add(relationEntity.getTargetName());
                        objectRelationMap.put(relationEntity.getRelationName(), tmp);
                    }
                }
            }

            for(Map.Entry<String, List<String>> entry : objectRelationMap.entrySet()) {
                speechBuilder.append(entry.getKey()).append("：").append(String.join("，", entry.getValue())).append("。");
            }

            for(Map.Entry<String, List<String>> entry : subjectRelationMap.entrySet()) {
                speechBuilder.append(String.join("，", entry.getValue())).append(entry.getKey()).append(entityName).append("。");
            }

            Message message = handler.obtainMessage();
            message.what = 3;
            handler.sendMessage(message);

            System.out.printf("Handling properties: %f%n", (new Date().getTime() - start.getTime()) / 1000.0);

            objectList = entityJson.getJSONArray("property").toJavaList(JSONObject.class);

            StringBuilder stringBuilder = null;

            propertyFullList = new ArrayList<>();
            Set<PropertyEntity> propertyEntitySet = new HashSet<>();
            for (JSONObject propertyJson : objectList) {
                PropertyEntity entity = new PropertyEntity();
                if (propertyJson.getString("object").contains("http"))
                    continue;
                entity.setLabel(propertyJson.getString("predicateLabel"));
                entity.setObject(propertyJson.getString("object"));

                if(stringBuilder == null || stringBuilder.length() < 100) {
                    if (stringBuilder == null)
                        stringBuilder = new StringBuilder();
                    else
                        stringBuilder.append("；");
                    stringBuilder.append(entity.getLabel()).append("：").append(entity.getObject());
                }
                if(!propertyEntitySet.contains(entity)) {
                    propertyFullList.add(entity);
                    propertyEntitySet.add(entity);
                }
            }

            Collections.sort(propertyFullList);

            speechBuilder.append("属性：");

            for(PropertyEntity propertyEntity : propertyFullList) {
                speechBuilder.append(propertyEntity.getLabel()).append("：").append(propertyEntity.getObject()).append('。');
            }

            speech = speechBuilder.toString();

            description = (stringBuilder == null ? "" : stringBuilder.toString());

            if(description.length() >= 60)
                description = description.substring(0, 60) + "...";
            else
                description = description + "。";

            System.out.printf("Adapting properties: %f%n", (new Date().getTime() - start.getTime()) / 1000.0);

            message = handler.obtainMessage();
            message.what = 4;
            handler.sendMessage(message);

            System.out.printf("Handling problems: %f%n", (new Date().getTime() - start.getTime()) / 1000.0);

            questionFullList = problems.getJSONArray("data").toJavaList(JSONObject.class);

            System.out.printf("request finished: %f%n", (new Date().getTime() - start.getTime()) / 1000.0);

            message = handler.obtainMessage();
            message.what = 5;
            handler.sendMessage(message);

            System.out.printf("Done: %f%n", (new Date().getTime() - start.getTime()) / 1000.0);

            message = handler.obtainMessage();
            message.what = 1;
            handler.sendMessage(message);

            if(RequestBuilder.checkedLogin()) {
                Map<String, String> args = new HashMap<>();
                args.put("name", entityName);
                args.put("subject", subject);
                try {
                    RequestBuilder.asyncSendBackendGetRequest("/api/history/addVisitHistory", args, true);
                } catch (BackendTokenExpiredException e) {
                    e.printStackTrace();
                }
            }

            return "done!";
        }
    }

    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private boolean loadingFlag = false;

    private static final String APP_KEY = "1760115939";
    private static final String REDIRECT_URL = "http://open.weibo.com/apps/1760115939/privilege/oauth";
    private static final String SCOPE = "";

    private IWBAPI mWBAPI = null;

    private void initSdk() {
        if(PermissionUtilities.verifyPermissions(EntityActivity.this, Manifest.permission.CHANGE_WIFI_STATE) == 0) {
            ActivityCompat.requestPermissions(EntityActivity.this, PERMISSIONS, 3);
            return;
        }
        AuthInfo authInfo = new AuthInfo(this, APP_KEY, REDIRECT_URL, SCOPE);
        mWBAPI = WBAPIFactory.createWBAPI(this);
        mWBAPI.registerApp(this, authInfo);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for(int i : grantResults) {
            if(i != PERMISSION_GRANTED) {
                findViewById(R.id.shareButton).setVisibility(View.GONE);
                return;
            }
        }
        initSdk();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entity);

        initSdk();

        Intent prevIntent = getIntent();

        Bundle prevBundle = prevIntent.getExtras();

        entityName = prevBundle.getString("name", "李白");
        String subject = prevBundle.getString("subject", "chinese");

        TextView titleView = (TextView) findViewById(R.id.entityTitle);
        titleView.setText(entityName);

        LoadingDialog loadingDialog = new LoadingDialog(EntityActivity.this);
        loadingDialog.setDimissListener(() -> {
            if(!loadingFlag)
                EntityActivity.this.finish();
        });
        loadingDialog.setLoadingText("加载中")
                .setInterceptBack(false)
                .setSuccessText("加载成功")
                .setFailedText("加载失败")
                .show();

        long start = System.currentTimeMillis();

        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                if (message.what == 1) {
                    if (System.currentTimeMillis() - start < 500)
                        loadingDialog.close();
                    else
                        loadingDialog.loadSuccess();
                    loadingFlag = true;
                } else if (message.what == 2) {
                    loadingDialog.loadFailed();
                } else if (message.what == 3) {
                    RecyclerView relationsView = (RecyclerView) findViewById(R.id.relationsView);
                    ImageButton relationButton = (ImageButton) findViewById(R.id.relationButton);
                    if (relationFullList.size() > 5) {
                        relationPrevList = relationFullList.subList(0, 5);
                        relationAdapter = new RelationAdapter(EntityActivity.this, relationFullList, relationPrevList, subject);
                        relationButton.setBackgroundResource(R.drawable.pulldown);
                        relationButton.setOnClickListener((View v) -> {
                            if(relationAdapter.getItemCount() <=5) {
                                relationButton.setBackgroundResource(R.drawable.pullback);
                            } else {
                                relationButton.setBackgroundResource(R.drawable.pulldown);
                            }
                            relationAdapter.switchList();
                        });
                    } else {
                        relationButton.setVisibility(View.GONE);
                        relationAdapter = new RelationAdapter(EntityActivity.this, relationFullList, relationFullList, subject);
                    }
                    relationsView.setAdapter(relationAdapter);
                } else if (message.what == 4) {
                    RecyclerView propertiesView = (RecyclerView) findViewById(R.id.propertiesView);
                    ImageButton propertyButton = (ImageButton) findViewById(R.id.propertyButton);
                    if (propertyFullList.size() > 5) {
                        propertyPrevList = propertyFullList.subList(0, 5);
                        propertyButton.setBackgroundResource(R.drawable.pulldown);
                        propertyAdapter = new PropertyAdapter(EntityActivity.this, propertyFullList, propertyPrevList);
                        propertyButton.setOnClickListener((View view) -> {
                            if(propertyAdapter.getItemCount() <= 5) {
                                propertyButton.setBackgroundResource(R.drawable.pullback);
                            } else {
                                propertyButton.setBackgroundResource(R.drawable.pulldown);
                            }
                            propertyAdapter.switchList();
                        });
                    } else {
                        propertyButton.setVisibility(View.GONE);
                        propertyAdapter = new PropertyAdapter(EntityActivity.this, propertyFullList, propertyFullList);
                    }
                    propertiesView.setAdapter(propertyAdapter);
                } else if (message.what == 5) {
                    RecyclerView problemsView = (RecyclerView) findViewById(R.id.problemsView);
                    ImageButton problemButton = (ImageButton) findViewById(R.id.problemButton);
                    if (questionFullList.size() > 5) {
                        questionPrevList = questionFullList.subList(0, 5);
                        problemAdapter = new ProblemAdapter(EntityActivity.this, questionFullList, questionPrevList);
                        problemButton.setBackgroundResource(R.drawable.pulldown);
                        problemButton.setOnClickListener((View view) -> {
                            if(problemAdapter.getItemCount() <= 5) {
                                problemButton.setBackgroundResource(R.drawable.pullback);
                            } else {
                                problemButton.setBackgroundResource(R.drawable.pulldown);
                            }
                            problemAdapter.switchList();
                        });
                    } else {
                        problemButton.setVisibility(View.GONE);
                        problemAdapter = new ProblemAdapter(EntityActivity.this, questionFullList, questionFullList);
                    }
                    problemsView.setAdapter(problemAdapter);
                }
            }
        };

        relationAdapter = new RelationAdapter(EntityActivity.this, new ArrayList<>(), new ArrayList<>(), subject);
        ((RecyclerView) findViewById(R.id.relationsView)).setLayoutManager(new LinearLayoutManager(EntityActivity.this, LinearLayoutManager.VERTICAL, false));
        ((RecyclerView) findViewById(R.id.relationsView)).setAdapter(relationAdapter);

        propertyAdapter = new PropertyAdapter(EntityActivity.this, new ArrayList<>(), new ArrayList<>());
        ((RecyclerView) findViewById(R.id.propertiesView)).setLayoutManager(new LinearLayoutManager(EntityActivity.this, LinearLayoutManager.VERTICAL, false));
        ((RecyclerView) findViewById(R.id.propertiesView)).setAdapter(propertyAdapter);

        problemAdapter = new ProblemAdapter(EntityActivity.this, new ArrayList<>(), new ArrayList<>());
        ((RecyclerView) findViewById(R.id.problemsView)).setLayoutManager(new LinearLayoutManager(EntityActivity.this, LinearLayoutManager.VERTICAL, false));
        ((RecyclerView) findViewById(R.id.problemsView)).setAdapter(problemAdapter);

        executorService.submit(new EntityActivityLoadCallable(handler));

        FloatingActionButton shareButton = (FloatingActionButton) findViewById(R.id.shareButton);
        shareButton.setOnClickListener((View vi) -> doWeiboShare());

        System.out.println("Finish submitting!!");

        findViewById(R.id.playButton).setOnClickListener((View v) -> onTextToSpeech(textToSpeech == null || !textToSpeech.isSpeaking()));

        if(((MainApplication)getApplication()).getFavourite() != null) {
            System.out.println("Into favourite!!");

            Dialog bottomDialog = new Dialog(EntityActivity.this, R.style.BottomDialog);
            View contentView = LayoutInflater.from(this).inflate(R.layout.layout_bottom_favourite, null);
            bottomDialog.setContentView(contentView);
            bottomDialog.getWindow().setGravity(Gravity.BOTTOM);
            bottomDialog.getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) contentView.getLayoutParams();
            params.width = getResources().getDisplayMetrics().widthPixels - DensityUtilities.dp2px(this, 16f);
            params.bottomMargin = DensityUtilities.dp2px(this, 8f);
            contentView.setLayoutParams(params);

            bottomFavouriteView = (ListViewForScrollView) contentView.findViewById(R.id.bottomFavouriteListView);

            updateFavourite(((MainApplication)getApplication()).getFavourite());
            FloatingActionButton button = (FloatingActionButton) findViewById(R.id.favouriteButton);
            button.setVisibility(View.VISIBLE);

            bottomFavouriteView.setAdapter(bottomFavouriteAdapter);
            System.out.println("Dialog finish initialization!!");

            button.setOnClickListener((View view) -> bottomDialog.show());

            Button finishButton = (Button) contentView.findViewById(R.id.buttonBottomFinish);
            finishButton.setOnClickListener((View view) -> {
                Set<String> checked = bottomFavouriteAdapter.getCheckedSet();
                JSONObject args = new JSONObject();
                args.put("name", entityName);
                args.put("subject", subject);
                args.put("checked", JSON.toJSONString(checked));
                try {
                    RequestBuilder.sendBackendPostRequest("/api/favourite/updateFavourite", args, true);
                    ((MainApplication) getApplication()).updateFavourite();
                    updateFavourite(((MainApplication) getApplication()).getFavourite());
                } catch (BackendTokenExpiredException e) {
                    e.printStackTrace();
                } catch (InterruptedException | ExecutionException e) {
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                }
                bottomDialog.dismiss();
            });

            Dialog addNewDirectoryDialog = new Dialog(EntityActivity.this, R.style.BottomDialog);
            View directoryContentView = LayoutInflater.from(this).inflate(R.layout.layout_add_new_directory, null);
            addNewDirectoryDialog.setContentView(directoryContentView);
            addNewDirectoryDialog.getWindow().setGravity(Gravity.CENTER);
            addNewDirectoryDialog.getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
            params = (ViewGroup.MarginLayoutParams) directoryContentView.getLayoutParams();
            params.width = getResources().getDisplayMetrics().widthPixels - DensityUtilities.dp2px(this, 16f);
            params.bottomMargin = DensityUtilities.dp2px(this, 8f);
            directoryContentView.setLayoutParams(params);

            Button confirm = (Button) directoryContentView.findViewById(R.id.addDirectoryConfirm);
            Button cancel = (Button) directoryContentView.findViewById(R.id.addDirectoryCancel);

            cancel.setOnClickListener((View view) -> addNewDirectoryDialog.dismiss());

            confirm.setOnClickListener((View view) -> {
                EditText editText = directoryContentView.findViewById(R.id.newDirectoryName);
                if(editText.getText().toString().equals(""))
                    return;
                JSONObject args = new JSONObject();
                args.put("directory", editText.getText().toString());
                try {
                    RequestBuilder.sendBackendPostRequest("/api/favourite/addDirectory", args, true);
                } catch (BackendTokenExpiredException e) {
                    e.printStackTrace();
                } catch (InterruptedException | ExecutionException e) {
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                }
                updateFavourite(((MainApplication) getApplication()).getFavourite());
                addNewDirectoryDialog.dismiss();
            });

            Button addNewDirectoryButton = (Button) contentView.findViewById(R.id.bottomAddNewFavourite);
            addNewDirectoryButton.setOnClickListener((View view) -> addNewDirectoryDialog.show());
            System.out.println("Done!!");
        }
    }

    private ListViewForScrollView bottomFavouriteView;
    private BottomFavouriteAdapter bottomFavouriteAdapter;

    public void updateFavourite(JSONObject favourite) {

        ((MainApplication) getApplication()).updateFavourite();

        JSONObject favouriteJson = ((MainApplication)getApplication()).getFavourite();
        List<BottomFavouriteEntity> favouriteEntities = new ArrayList<>();

        for (Map.Entry<String, Object> entry : favouriteJson.entrySet()) {
            JSONArray array = JSON.parseArray(entry.getValue().toString());
            boolean flag = false;
            for(Object val : array) {
                JSONObject object = JSON.parseObject(val.toString());
                if(object.getString("name").equals(entityName)) {
                    flag = true;
                    break;
                }
            }
            favouriteEntities.add(new BottomFavouriteEntity(flag, entry.getKey()));
        }

        bottomFavouriteAdapter = new BottomFavouriteAdapter(EntityActivity.this, R.layout.bottom_dialog_favourite_item, favouriteEntities);
        bottomFavouriteView.setAdapter(bottomFavouriteAdapter);

        FloatingActionButton button = (FloatingActionButton) findViewById(R.id.favouriteButton);
        for(Map.Entry<String, Object> entry : favourite.entrySet()) {
            JSONArray array = JSON.parseArray(entry.getValue().toString());
            for(Object obj : array) {
                JSONObject object = JSON.parseObject(obj.toString());
                System.out.printf("Receive object in favourite %s%n", object.getString("name"));
                if(object.getString("name").equals(entityName)) {
                    button.setImageResource(R.drawable.star_yellow_16);
                    return;
                }
            }
        }
        button.setImageResource(R.drawable.star_gray_16);
    }

    private void doWeiboShare() {

        System.out.println("Doing weibo share!!!");

        WeiboMultiMessage message = new WeiboMultiMessage();

        TextObject textObject = new TextObject();
        textObject.text = "我正在 #小白盒# 中看：" + entityName + "。\n" + description;

        message.textObject = textObject;
        mWBAPI.shareMessage(message, true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(mWBAPI != null)
            mWBAPI.doResultIntent(data, new WeiboShareCallback(EntityActivity.this));
    }

    private TextToSpeech textToSpeech = null;

    private void onTextToSpeech(boolean flag) {
        if(!flag && textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech = null;
        } else if (flag) {
            System.out.println("Enter speech!");
            textToSpeech = new TextToSpeech(EntityActivity.this, (int i) -> {
                int result = textToSpeech.setLanguage(Locale.CHINA);
                System.out.printf("Result %d%n%n", result);
                if(result == TextToSpeech.LANG_NOT_SUPPORTED || result == TextToSpeech.LANG_MISSING_DATA) {
                    Toast.makeText(EntityActivity.this, "您的手机目前不支持中文tts，请下载语音包", Toast.LENGTH_LONG).show();
                } else {
                    textToSpeech.speak(speech, TextToSpeech.QUEUE_FLUSH, null, null);
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        onTextToSpeech(false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        onTextToSpeech(false);
    }
}
