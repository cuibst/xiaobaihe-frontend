package com.java.cuiyikai.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.java.cuiyikai.R;
import com.java.cuiyikai.adapters.ProblemAdapter;
import com.java.cuiyikai.adapters.PropertyAdapter;
import com.java.cuiyikai.adapters.RelationAdapter;
import com.java.cuiyikai.database.DatabaseEntity;
import com.java.cuiyikai.database.EntityDatabaseHelper;
import com.java.cuiyikai.entities.PropertyEntity;
import com.java.cuiyikai.entities.RelationEntity;
import com.java.cuiyikai.network.RequestBuilder;
import com.java.cuiyikai.widgets.ListViewForScrollView;
import com.xiasuhuei321.loadingdialog.view.LoadingDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class EntityActivity extends AppCompatActivity {

    public class RelationViewItemOnClickListener implements View.OnClickListener {
        private String name;
        private String subject;

        public RelationViewItemOnClickListener(String name, String subject) {
            this.name = name;
            this.subject = subject;
        }

        @Override
        public void onClick(View view) {
            Intent f=new Intent(EntityActivity.this,EntityActivity.class);
            f.putExtra("name",name);
            f.putExtra("subject",subject);
            startActivity(f);
        }
    }

    private class RelationViewOnClickListener implements View.OnClickListener {
        private final List<RelationEntity> fullList, prevList;
        private boolean extended;
        private final ListViewForScrollView relatedView;
        private final ImageButton relatedButton;
        private final String subject;

        @Override
        public void onClick(View v) {
            if (extended) {
                relatedButton.setBackgroundResource(R.drawable.pulldown);
                extended = false;
                relatedView.setAdapter(new RelationAdapter(EntityActivity.this, R.layout.relation_item, prevList, subject));
            } else {
                relatedButton.setBackgroundResource(R.drawable.pullback);
                extended = true;
                relatedView.setAdapter(new RelationAdapter(EntityActivity.this, R.layout.relation_item, fullList, subject));
            }
        }

        public RelationViewOnClickListener(List<RelationEntity> fullList, List<RelationEntity> prevList, boolean extended, ListViewForScrollView relatedView, ImageButton relatedButton, final String subject) {
            this.fullList = fullList;
            this.prevList = prevList;
            this.extended = extended;
            this.relatedView = relatedView;
            this.relatedButton = relatedButton;
            this.subject = subject;
        }
    }

    private class PropertyViewOnClickListener implements View.OnClickListener {
        private final List<PropertyEntity> fullList, prevList;
        private boolean extended;
        private final ListViewForScrollView propertyView;
        private final ImageButton propertyButton;

        @Override
        public void onClick(View v) {
            if (extended) {
                propertyButton.setBackgroundResource(R.drawable.pulldown);
                extended = false;
                propertyView.setAdapter(new PropertyAdapter(EntityActivity.this, R.layout.property_item, prevList));
            } else {
                propertyButton.setBackgroundResource(R.drawable.pullback);
                extended = true;
                propertyView.setAdapter(new PropertyAdapter(EntityActivity.this, R.layout.property_item, fullList));
            }
        }

        public PropertyViewOnClickListener(List<PropertyEntity> fullList, List<PropertyEntity> prevList, boolean extended, ListViewForScrollView propertyView, ImageButton propertyButton) {
            this.fullList = fullList;
            this.prevList = prevList;
            this.extended = extended;
            this.propertyView = propertyView;
            this.propertyButton = propertyButton;
        }
    }

    private class ProblemViewOnClickListener implements View.OnClickListener {
        private final List<JSONObject> fullList, prevList;
        private boolean extended;
        private final ListViewForScrollView problemView;
        private final ImageButton problemButton;

        @Override
        public void onClick(View v) {
            if (extended) {
                problemButton.setBackgroundResource(R.drawable.pulldown);
                extended = false;
                problemView.setAdapter(new ProblemAdapter(EntityActivity.this, R.layout.problem_item, prevList));
            } else {
                problemButton.setBackgroundResource(R.drawable.pullback);
                extended = true;
                problemView.setAdapter(new ProblemAdapter(EntityActivity.this, R.layout.problem_item, fullList));
            }
        }

        public ProblemViewOnClickListener(List<JSONObject> fullList, List<JSONObject> prevList, boolean extended, ListViewForScrollView propertyView, ImageButton propertyButton) {
            this.fullList = fullList;
            this.prevList = prevList;
            this.extended = extended;
            this.problemView = propertyView;
            this.problemButton = propertyButton;
        }
    }

    private static final String[] SUBJECTS = {"chinese", "english", "math", "physics", "chemistry", "biology", "history", "geo", "politics"};

    private RelationAdapter relationAdapter;
    private List<RelationEntity> relationFullList, relationPrevList;

    private PropertyAdapter propertyAdapter;
    private List<PropertyEntity> propertyFullList, propertyPrevList;

    private ProblemAdapter problemAdapter;
    private List<JSONObject> questionFullList, questionPrevList;

    private class EntityActivityLoadCallable implements Callable<String> {

        private final Handler handler;

        public EntityActivityLoadCallable(Handler handler) {
            this.handler = handler;
        }

        @Override
        public String call() {
            Date start = new Date();

            System.out.println("Loading start!!");

            Intent prevIntent = getIntent();

            Bundle prevBundle = prevIntent.getExtras();

            String entityName = prevBundle.getString("name", "李白");
            String subject = prevBundle.getString("subject", "chinese");
            JSONObject entityJson;

            EntityDatabaseHelper helper = EntityDatabaseHelper.getInstance(EntityActivity.this, 1);
            helper.openReadLink();
            List<DatabaseEntity> entityList = helper.queryEntityByName(entityName);
            helper.closeLink();

            System.out.printf("Database checked: %f%n", (new Date().getTime() - start.getTime()) / 1000.0);

            JSONObject problems = new JSONObject();

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
                            if (tmp != null && tmp.toString().length() > reply.toString().length())
                                reply = tmp;
                        }
                    } else
                        reply = RequestBuilder.sendGetRequest("typeOpen/open/infoByInstanceName", arguments);
                } catch (Exception e) {
                    e.printStackTrace();
                    Message message = handler.obtainMessage();
                    message.what = 2;
                    handler.sendMessage(message);
                    return "fail";
                }

                entityJson = reply.getJSONObject("data");

                Map<String, String> args = new HashMap<>();
                args.put("uriName", entityName);

                try {
                    problems = RequestBuilder.sendGetRequest("typeOpen/open/questionListByUriName", args);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                helper.openWriteLink();
                DatabaseEntity databaseEntity = new DatabaseEntity();
                databaseEntity.setName(entityName);
                databaseEntity.setJsonContent(entityJson.toJSONString());
                databaseEntity.setUri("123");
                databaseEntity.setProblemsJson(problems == null ? "" : problems.toJSONString());
                helper.insert(databaseEntity);
                helper.closeLink();
            }

            System.out.printf("load finished: %f%n", (new Date().getTime() - start.getTime()) / 1000.0);

            TextView titleView = (TextView) findViewById(R.id.entityTitle);
            titleView.setText(entityName);

            System.out.printf("Handling relations: %f%n", (new Date().getTime() - start.getTime()) / 1000.0);

            List<JSONObject> objectList = entityJson.getJSONArray("content").toJavaList(JSONObject.class);

            relationFullList = new ArrayList<>();
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
                relationFullList.add(entity);
            }
            System.out.printf("Sorting relations: %f%n", (new Date().getTime() - start.getTime()) / 1000.0);
            Collections.sort(relationFullList);

            Message message = handler.obtainMessage();
            message.what = 3;
            handler.sendMessage(message);

            System.out.printf("Handling properties: %f%n", (new Date().getTime() - start.getTime()) / 1000.0);

            objectList = entityJson.getJSONArray("property").toJavaList(JSONObject.class);

            propertyFullList = new ArrayList<>();
            for (JSONObject propertyJson : objectList) {
                PropertyEntity entity = new PropertyEntity();
                if (propertyJson.getString("object").contains("http"))
                    continue;
                entity.setLabel(propertyJson.getString("predicateLabel"));
                entity.setObject(propertyJson.getString("object"));

                propertyFullList.add(entity);
            }

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

            return "done!";
        }
    }

    private static ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entity);

        LoadingDialog loadingDialog = new LoadingDialog(EntityActivity.this);
        loadingDialog.setLoadingText("加载中")
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
                } else if (message.what == 2) {
                    loadingDialog.loadFailed();
                } else if (message.what == 3) {
                    ListViewForScrollView relationsView = (ListViewForScrollView) findViewById(R.id.relationsView);
                    ImageButton relationButton = (ImageButton) findViewById(R.id.relationButton);
                    if (relationFullList.size() > 5) {
                        relationPrevList = relationFullList.subList(0, 5);
                        relationAdapter = new RelationAdapter(EntityActivity.this, R.layout.relation_item, relationPrevList, "");
                        relationButton.setBackgroundResource(R.drawable.pulldown);
                        relationButton.setOnClickListener(new RelationViewOnClickListener(relationFullList, relationPrevList, false, relationsView, relationButton, ""));
                    } else {
                        relationButton.setVisibility(View.GONE);
                        relationAdapter = new RelationAdapter(EntityActivity.this, R.layout.relation_item, relationFullList, "");
                    }
                    relationsView.setAdapter(relationAdapter);
                } else if (message.what == 4) {
                    ListViewForScrollView propertiesView = (ListViewForScrollView) findViewById(R.id.propertiesView);
                    ImageButton propertyButton = (ImageButton) findViewById(R.id.propertyButton);
                    if (propertyFullList.size() > 5) {
                        propertyPrevList = propertyFullList.subList(0, 5);
                        propertyAdapter = new PropertyAdapter(EntityActivity.this, R.layout.property_item, propertyPrevList);
                        propertyButton.setBackgroundResource(R.drawable.pulldown);
                        propertyButton.setOnClickListener(new PropertyViewOnClickListener(propertyFullList, propertyPrevList, false, propertiesView, propertyButton));
                    } else {
                        propertyButton.setVisibility(View.GONE);
                        propertyAdapter = new PropertyAdapter(EntityActivity.this, R.layout.property_item, propertyFullList);
                    }
                    propertiesView.setAdapter(propertyAdapter);
                } else if (message.what == 5) {
                    ListViewForScrollView problemsView = (ListViewForScrollView) findViewById(R.id.problemsView);
                    ImageButton problemButton = (ImageButton) findViewById(R.id.problemButton);
                    if (questionFullList.size() > 5) {
                        questionPrevList = questionFullList.subList(0, 5);
                        problemAdapter = new ProblemAdapter(EntityActivity.this, R.layout.problem_item, questionPrevList);
                        problemButton.setBackgroundResource(R.drawable.pulldown);
                        problemButton.setOnClickListener(new ProblemViewOnClickListener(questionFullList, questionPrevList, false, problemsView, problemButton));
                    } else {
                        problemButton.setVisibility(View.GONE);
                        problemAdapter = new ProblemAdapter(EntityActivity.this, R.layout.problem_item, questionFullList);
                    }
                    problemsView.setAdapter(problemAdapter);
                }
            }
        };
        executorService.submit(new EntityActivityLoadCallable(handler));
    }
}