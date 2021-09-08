package com.java.cuiyikai.activities;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewPropertyAnimatorListener;
import androidx.core.widget.NestedScrollView;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
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
import android.view.animation.Interpolator;
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
import com.java.cuiyikai.utilities.ConstantUtilities;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

/**
 * {@link android.app.Activity} for Category selection.
 */
public class EntityActivity extends AppCompatActivity {

    private static final Logger logger = LoggerFactory.getLogger(EntityActivity.class);

    private static final String[] PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.CHANGE_WIFI_STATE};

    private static final String[] SUBJECTS = {ConstantUtilities.SUBJECT_CHINESE, ConstantUtilities.SUBJECT_ENGLISH, ConstantUtilities.SUBJECT_MATH, ConstantUtilities.SUBJECT_PHYSICS, ConstantUtilities.SUBJECT_CHEMISTRY, ConstantUtilities.SUBJECT_BIOLOGY, ConstantUtilities.SUBJECT_HISTORY, ConstantUtilities.SUBJECT_GEO, ConstantUtilities.SUBJECT_POLITICS};

    //Adapter and lists for relation recycler view.
    private RelationAdapter relationAdapter;
    private List<RelationEntity> relationFullList; //Full relation lists
    private List<RelationEntity> relationPrevList; //contain first 5 relations, same for the following ones.

    //Adapter and lists for property recycler view.
    private PropertyAdapter propertyAdapter;
    private List<PropertyEntity> propertyFullList;
    private List<PropertyEntity> propertyPrevList;

    //Adapter and lists for problem recycler view.
    private ProblemAdapter problemAdapter;
    private List<JSONObject> questionFullList;
    private List<JSONObject> questionPrevList;

    //entity name, description and subject.
    private String entityName;
    private String description;
    private String subject;

    //The speech for the Voice output.
    private String speech;

    /**
     * A {@link Callable} to request and load the data for the activity.
     */
    private class EntityActivityLoadCallable implements Callable<String> {

        //Callback handler
        private final Handler handler;

        /**
         * Only constructor for {@link EntityActivityLoadCallable}
         * @param handler the callback handler for this callable.
         */
        public EntityActivityLoadCallable(Handler handler) {
            this.handler = handler;
        }

        private JSONObject entityJson;
        private JSONObject problems;

        /**
         * initialize the {@link #entityJson} and {@link #problems} for the callable
         * <p>Method will first check the database, and request from edukg if such entry doesn't exist</p>
         * @param entityName the name of the entity requested.
         * @param subject the subject of the entity.
         * @throws InterruptedException when the request edukg thread is interrupted
         * @throws ExecutionException when the request to edukg failed
         */
        private void initJsonObjects(String entityName, String subject) throws InterruptedException, ExecutionException{

            //Check the database for the entry.
            EntityDatabaseHelper helper = EntityDatabaseHelper.getInstance(EntityActivity.this, 1);
            helper.openReadLink();
            List<DatabaseEntity> entityList = helper.queryEntityByNameAndSubject(entityName, subject);
            helper.closeLink();

            if (!entityList.isEmpty()) {
                entityJson = JSON.parseObject(entityList.get(0).getJsonContent());
                problems = JSON.parseObject(entityList.get(0).getProblemsJson());
            } else {

                //No match, request from edukg.
                logger.info("No matches in database!!");

                Map<String, String> arguments = new HashMap<>();
                arguments.put(ConstantUtilities.ARG_NAME, entityName);
                arguments.put(ConstantUtilities.ARG_COURSE, subject);

                JSONObject reply;

                try {
                    if (subject.equals("")) {
                        reply = new JSONObject();
                        for (String sub : SUBJECTS) {
                            arguments.put(ConstantUtilities.ARG_COURSE, sub);
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

                entityJson = reply.getJSONObject(ConstantUtilities.ARG_DATA);

                //get the related problems.
                Map<String, String> args = new HashMap<>();
                args.put("uriName", entityName);

                try {
                    problems = RequestBuilder.sendGetRequest("typeOpen/open/questionListByUriName", args);
                } catch (Exception e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }

                //save the entry to the database.
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

        /**
         * {@inheritDoc}
         * @return the return message of the process.
         */
        @Override
        public String call() {
            Date start = new Date();

            //Get the parameters from the caller.

            logger.info("Loading start!!");

            Intent prevIntent = getIntent();

            Bundle prevBundle = prevIntent.getExtras();

            subject = prevBundle.getString(ConstantUtilities.ARG_SUBJECT, ConstantUtilities.SUBJECT_CHINESE);

            //Init the data.
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

            logger.info("Handling relations: {}", (new Date().getTime() - start.getTime()) / 1000.0);

            //parse the relation data.
            List<JSONObject> objectList = entityJson.getJSONArray(ConstantUtilities.ARG_CONTENT).toJavaList(JSONObject.class);

            relationFullList = new ArrayList<>();
            Set<RelationEntity> relationSet = new HashSet<>();
            for (JSONObject relationJson : objectList) {
                RelationEntity entity = new RelationEntity();
                entity.setRelationName(relationJson.getString(ConstantUtilities.ARG_PREDICATE_LABEL_UNDERLINE));
                if (relationJson.getString(ConstantUtilities.ARG_OBJECT) != null) {
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
            logger.info("Sorting relations: {}", (new Date().getTime() - start.getTime()) / 1000.0);
            Collections.sort(relationFullList);

            Map<String, List<String>> objectRelationMap = new HashMap<>();
            Map<String, List<String>> subjectRelationMap = new HashMap<>();

            //add the shorten message to the voice.
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

            logger.info("Handling properties: {}", (new Date().getTime() - start.getTime()) / 1000.0);

            //parse the data of the properties.
            objectList = entityJson.getJSONArray("property").toJavaList(JSONObject.class);

            StringBuilder stringBuilder = null;

            propertyFullList = new ArrayList<>();
            Set<PropertyEntity> propertyEntitySet = new HashSet<>();
            for (JSONObject propertyJson : objectList) {
                PropertyEntity entity = new PropertyEntity();
                if (propertyJson.getString(ConstantUtilities.ARG_OBJECT).contains("http"))
                    continue;
                entity.setLabel(propertyJson.getString(ConstantUtilities.ARG_PREDICATE_LABEL));
                entity.setObject(propertyJson.getString(ConstantUtilities.ARG_OBJECT));

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

            //Add the speech for property.
            speechBuilder.append("属性：");

            for(PropertyEntity propertyEntity : propertyFullList) {
                speechBuilder.append(propertyEntity.getLabel()).append("：").append(propertyEntity.getObject()).append('。');
            }

            speech = speechBuilder.toString();

            //Init entity description for weibo share.
            description = (stringBuilder == null ? "" : stringBuilder.toString());

            if(description.length() >= 60)
                description = description.substring(0, 60) + "...";
            else
                description = description + "。";

            logger.info("Adapting properties: {}", (new Date().getTime() - start.getTime()) / 1000.0);

            message = handler.obtainMessage();
            message.what = 4;
            handler.sendMessage(message);

            //parsing problems
            logger.info("Handling problems: {}", (new Date().getTime() - start.getTime()) / 1000.0);

            questionFullList = problems.getJSONArray(ConstantUtilities.ARG_DATA).toJavaList(JSONObject.class);

            logger.info("request finished: {}", (new Date().getTime() - start.getTime()) / 1000.0);

            message = handler.obtainMessage();
            message.what = 5;
            handler.sendMessage(message);

            logger.info("Done: {}", (new Date().getTime() - start.getTime()) / 1000.0);

            message = handler.obtainMessage();
            message.what = 1;
            handler.sendMessage(message);

            //add the visit history if the user has logged in.
            if(RequestBuilder.checkedLogin()) {
                Map<String, String> args = new HashMap<>();
                args.put(ConstantUtilities.ARG_NAME, entityName);
                args.put(ConstantUtilities.ARG_SUBJECT, subject);
                try {
                    RequestBuilder.asyncSendBackendGetRequest("/api/history/addVisitHistory", args, true);
                } catch (BackendTokenExpiredException e) {
                    e.printStackTrace();
                }
            }

            return "done!";
        }
    }

    //Thread pool for executing the init callable.
    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private boolean loadingFlag = false;

    //weibo share keys.
    private static final String APP_KEY = "1760115939";
    private static final String REDIRECT_URL = "http://open.weibo.com/apps/1760115939/privilege/oauth";
    private static final String SCOPE = "";

    private IWBAPI mWeiboAPI = null;

    /**
     * initialize the weibo sdk for share function.
     */
    private void initSdk() {
        if(PermissionUtilities.verifyPermissions(EntityActivity.this, Manifest.permission.CHANGE_WIFI_STATE) == 0) {
            ActivityCompat.requestPermissions(EntityActivity.this, PERMISSIONS, 3);
            return;
        }
        AuthInfo authInfo = new AuthInfo(this, APP_KEY, REDIRECT_URL, SCOPE);
        mWeiboAPI = WBAPIFactory.createWBAPI(this);
        mWeiboAPI.registerApp(this, authInfo);
    }

    /**
     * Grant the permission for weibo sdk, hide the button if the user denies these.
     * {@inheritDoc}
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
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

    /**
     * {@inheritDoc}
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entity);

        initSdk();

        Intent prevIntent = getIntent();

        Bundle prevBundle = prevIntent.getExtras();

        entityName = prevBundle.getString(ConstantUtilities.ARG_NAME, "李白");
        subject = prevBundle.getString(ConstantUtilities.ARG_SUBJECT, ConstantUtilities.SUBJECT_CHINESE);

        TextView titleView = findViewById(R.id.entityTitle);
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

        //Hide the floating menu when scrolling downward, show when scrolling upward.
        NestedScrollView scrollView = findViewById(R.id.entity_scroll_view);
        scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {

            private boolean visible = true;
            private int distance = 0;
            private final Interpolator interpolator = new FastOutSlowInInterpolator();

            @Override
            public void onScrollChange(View view, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                int dy = scrollY - oldScrollY;
                if(distance > 10 && visible) {
                    visible = false;
                    ViewCompat.animate(findViewById(R.id.floatMenu)).alpha(0.0F).setInterpolator(interpolator).withLayer()
                            .setListener(new ViewPropertyAnimatorListener() {
                                @Override
                                public void onAnimationStart(View view) {

                                }

                                public void onAnimationEnd(View view) {
                                    view.setVisibility(View.GONE);
                                }

                                @Override
                                public void onAnimationCancel(View view) {

                                }
                            }).start();
                    distance = 0;
                } else if(distance < -20 && !visible) {
                    findViewById(R.id.floatMenu).setVisibility(View.VISIBLE);
                    visible = true;
                    ViewCompat.animate(findViewById(R.id.floatMenu)).alpha(1.0f)
                            .setInterpolator(interpolator).withLayer().setListener(null)
                            .start();
                    distance = 0;
                }
                if((visible && dy > 0) || (!visible && dy < 0)) {
                    distance += dy;
                }
            }
        });

        //Set up the handler for the callable.
        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                if (message.what == 1) { // finished
                    if (System.currentTimeMillis() - start < 500)
                        loadingDialog.close();
                    else
                        loadingDialog.loadSuccess();
                    loadingFlag = true;
                } else if (message.what == 2) { // failed
                    loadingDialog.loadFailed();
                } else if (message.what == 3) { // relation data available
                    RecyclerView relationsView = findViewById(R.id.relationsView);
                    ImageButton relationButton = findViewById(R.id.relationButton);
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
                } else if (message.what == 4) { // property data available
                    RecyclerView propertiesView = findViewById(R.id.propertiesView);
                    ImageButton propertyButton = findViewById(R.id.propertyButton);
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
                } else if (message.what == 5) { // problems available
                    RecyclerView problemsView = findViewById(R.id.problemsView);
                    ImageButton problemButton = findViewById(R.id.problemButton);
                    if (questionFullList.size() > 5) {
                        questionPrevList = questionFullList.subList(0, 5);
                        problemAdapter = new ProblemAdapter(EntityActivity.this, questionFullList, questionPrevList, subject);
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
                        problemAdapter = new ProblemAdapter(EntityActivity.this, questionFullList, questionFullList, subject);
                    }
                    problemsView.setAdapter(problemAdapter);
                }
            }
        };

        //set up the adapters
        relationAdapter = new RelationAdapter(EntityActivity.this, new ArrayList<>(), new ArrayList<>(), subject);
        ((RecyclerView) findViewById(R.id.relationsView)).setLayoutManager(new LinearLayoutManager(EntityActivity.this, LinearLayoutManager.VERTICAL, false));
        ((RecyclerView) findViewById(R.id.relationsView)).setAdapter(relationAdapter);

        propertyAdapter = new PropertyAdapter(EntityActivity.this, new ArrayList<>(), new ArrayList<>());
        ((RecyclerView) findViewById(R.id.propertiesView)).setLayoutManager(new LinearLayoutManager(EntityActivity.this, LinearLayoutManager.VERTICAL, false));
        ((RecyclerView) findViewById(R.id.propertiesView)).setAdapter(propertyAdapter);

        problemAdapter = new ProblemAdapter(EntityActivity.this, new ArrayList<>(), new ArrayList<>(), subject);
        ((RecyclerView) findViewById(R.id.problemsView)).setLayoutManager(new LinearLayoutManager(EntityActivity.this, LinearLayoutManager.VERTICAL, false));
        ((RecyclerView) findViewById(R.id.problemsView)).setAdapter(problemAdapter);

        //submit callable
        executorService.submit(new EntityActivityLoadCallable(handler));

        FloatingActionButton shareButton = findViewById(R.id.shareButton);
        shareButton.setOnClickListener((View vi) -> doWeiboShare());

        logger.info("Finish submitting!!");

        findViewById(R.id.playButton).setOnClickListener((View v) -> onTextToSpeech(textToSpeech == null || !textToSpeech.isSpeaking()));

        if(((MainApplication)getApplication()).getFavourite() != null) {

            //solving favorite

            logger.info("Into favourite!!");

            //set up the dialog
            Dialog bottomDialog = new Dialog(EntityActivity.this, R.style.BottomDialog);
            View contentView = LayoutInflater.from(this).inflate(R.layout.layout_bottom_favourite, null);
            bottomDialog.setContentView(contentView);
            bottomDialog.getWindow().setGravity(Gravity.BOTTOM);
            bottomDialog.getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) contentView.getLayoutParams();
            params.width = getResources().getDisplayMetrics().widthPixels - DensityUtilities.dp2px(this, 16f);
            params.bottomMargin = DensityUtilities.dp2px(this, 8f);
            contentView.setLayoutParams(params);

            bottomFavouriteView = contentView.findViewById(R.id.bottomFavouriteListView);

            updateFavourite(((MainApplication)getApplication()).getFavourite());
            FloatingActionButton button = findViewById(R.id.favouriteButton);
            button.setVisibility(View.VISIBLE);

            bottomFavouriteView.setAdapter(bottomFavouriteAdapter);
            logger.info("Dialog finish initialization!!");

            button.setOnClickListener((View view) -> bottomDialog.show());

            //finish button, submit new favourite json
            Button finishButton = contentView.findViewById(R.id.buttonBottomFinish);
            finishButton.setOnClickListener((View view) -> {
                Set<String> checked = bottomFavouriteAdapter.getCheckedSet();
                JSONObject args = new JSONObject();
                args.put(ConstantUtilities.ARG_NAME, entityName);
                args.put(ConstantUtilities.ARG_SUBJECT, subject);
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

            //set up add new directory sub-dialog
            Dialog addNewDirectoryDialog = new Dialog(EntityActivity.this, R.style.BottomDialog);
            View directoryContentView = LayoutInflater.from(this).inflate(R.layout.layout_add_new_directory, null);
            addNewDirectoryDialog.setContentView(directoryContentView);
            addNewDirectoryDialog.getWindow().setGravity(Gravity.CENTER);
            addNewDirectoryDialog.getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
            params = (ViewGroup.MarginLayoutParams) directoryContentView.getLayoutParams();
            params.width = getResources().getDisplayMetrics().widthPixels - DensityUtilities.dp2px(this, 16f);
            params.bottomMargin = DensityUtilities.dp2px(this, 8f);
            directoryContentView.setLayoutParams(params);

            Button confirm = directoryContentView.findViewById(R.id.addDirectoryConfirm);
            Button cancel = directoryContentView.findViewById(R.id.addDirectoryCancel);

            cancel.setOnClickListener((View view) -> addNewDirectoryDialog.dismiss());

            //set up the submit new directory button.
            confirm.setOnClickListener((View view) -> {
                EditText editText = directoryContentView.findViewById(R.id.newDirectoryName);
                if(editText.getText().toString().equals(""))
                    return;
                JSONObject args = new JSONObject();
                args.put(ConstantUtilities.ARG_DIRECTORY, editText.getText().toString());
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

            Button addNewDirectoryButton = contentView.findViewById(R.id.bottomAddNewFavourite);
            addNewDirectoryButton.setOnClickListener((View view) -> addNewDirectoryDialog.show());
            logger.info("Done!!");
        }
    }

    private ListViewForScrollView bottomFavouriteView;
    private BottomFavouriteAdapter bottomFavouriteAdapter;

    /**
     * Update the views when the favourite is changed.
     * @param favourite
     */
    public void updateFavourite(JSONObject favourite) {

        //update the favourite in main application
        ((MainApplication) getApplication()).updateFavourite();

        JSONObject favouriteJson = ((MainApplication)getApplication()).getFavourite();
        List<BottomFavouriteEntity> favouriteEntities = new ArrayList<>();

        //re-setup the bottom favourite dialog.
        for (Map.Entry<String, Object> entry : favouriteJson.entrySet()) {
            JSONArray array = JSON.parseArray(entry.getValue().toString());
            boolean flag = false;
            for(Object val : array) {
                JSONObject object = JSON.parseObject(val.toString());
                if(object.getString(ConstantUtilities.ARG_NAME).equals(entityName)) {
                    flag = true;
                    break;
                }
            }
            favouriteEntities.add(new BottomFavouriteEntity(flag, entry.getKey()));
        }

        bottomFavouriteAdapter = new BottomFavouriteAdapter(EntityActivity.this, R.layout.bottom_dialog_favourite_item, favouriteEntities);
        bottomFavouriteView.setAdapter(bottomFavouriteAdapter);

        //re-setup the favourite button icon, i.e, check whether it is in the favourite.
        FloatingActionButton button = findViewById(R.id.favouriteButton);
        for(Map.Entry<String, Object> entry : favourite.entrySet()) {
            JSONArray array = JSON.parseArray(entry.getValue().toString());
            for(Object obj : array) {
                JSONObject object = JSON.parseObject(obj.toString());
                logger.info("Receive object in favourite {}", object.getString(ConstantUtilities.ARG_NAME));
                if(object.getString(ConstantUtilities.ARG_NAME).equals(entityName)) {
                    button.setIcon(R.drawable.star_yellow_16);
                    return;
                }
            }
        }
        button.setIcon(R.drawable.star_gray_16);
    }

    /**
     * share the entity by weibo when called.
     */
    private void doWeiboShare() {

        logger.info("Doing weibo share!!!");

        WeiboMultiMessage message = new WeiboMultiMessage();

        TextObject textObject = new TextObject();
        textObject.text = "我正在 #小白盒# 中看：" + entityName + "。\n" + description;

        message.textObject = textObject;
        mWeiboAPI.shareMessage(message, true);
    }

    /**
     * rewrite to fit weibo activity callback
     * {@inheritDoc}
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(mWeiboAPI != null)
            mWeiboAPI.doResultIntent(data, new WeiboShareCallback(EntityActivity.this));
    }

    private TextToSpeech textToSpeech = null;

    /**
     * do the speech or pause the speech
     * @param flag whether to activate the speech.
     */
    private void onTextToSpeech(boolean flag) {
        if(!flag && textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech = null;
        } else if (flag) {
            logger.info("Enter speech!");
            textToSpeech = new TextToSpeech(EntityActivity.this, (int i) -> {
                int result = textToSpeech.setLanguage(Locale.CHINA);
                logger.info("Result {}", result);
                if(result == TextToSpeech.LANG_NOT_SUPPORTED || result == TextToSpeech.LANG_MISSING_DATA) {
                    Toast.makeText(EntityActivity.this, "您的手机目前不支持中文tts，请下载语音包", Toast.LENGTH_LONG).show();
                } else {
                    textToSpeech.speak(speech, TextToSpeech.QUEUE_FLUSH, null, null);
                }
            });
        }
    }

    /**
     * rewrite to stop the speech.
     * {@inheritDoc}
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        onTextToSpeech(false);
    }

    /**
     * rewrite to stop the speech.
     * {@inheritDoc}
     */
    @Override
    protected void onPause() {
        super.onPause();
        onTextToSpeech(false);
    }
}
