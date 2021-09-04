package com.java.cuiyikai.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java.cuiyikai.MainApplication;
import com.java.cuiyikai.R;
import com.java.cuiyikai.database.DatabaseEntity;
import com.java.cuiyikai.database.EntityDatabaseHelper;
import com.java.cuiyikai.entities.GraphNode;
import com.java.cuiyikai.network.RequestBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import me.jagar.mindmappingandroidlibrary.Helpers.SaveAs;
import me.jagar.mindmappingandroidlibrary.Views.MindMappingView;

public class MindMapActivity extends AppCompatActivity {
    private MindMappingView mindMappingView;
    private static final Logger logger = LoggerFactory.getLogger(MindMapActivity.class);

    private final Map<String, GraphNode> nameIdMap = new HashMap<>();

    private static class Edge {
        GraphNode start;
        GraphNode to;
    }

    private final Set<Edge> edges = new HashSet<>();

    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private class GetItemCallable implements Callable<JSONObject> {

        private final String subject;
        private final String name;

        public GetItemCallable(String subject, String name) {
            this.subject = subject;
            this.name = name;
        }

        @Override
        public JSONObject call() throws Exception {
            EntityDatabaseHelper helper = EntityDatabaseHelper.getInstance(MindMapActivity.this, 1);
            helper.openReadLink();
            List<DatabaseEntity> entityList = helper.queryEntityByNameAndSubject(name, subject);
            helper.closeLink();
            if (!entityList.isEmpty())
                return JSON.parseObject(entityList.get(0).getJsonContent());
            else {
                logger.info("No matches in database!!");
                Map<String, String> arguments = new HashMap<>();
                arguments.put("name", name);
                arguments.put("course", subject);

                JSONObject reply;

                try {
                    reply = RequestBuilder.sendGetRequest("typeOpen/open/infoByInstanceName", arguments);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    throw e;
                }

                return reply.getJSONObject("data");
            }
        }
    }

    private static final int MAX_LEVEL = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mind_map);

        mindMappingView = findViewById(R.id.mind_mapping_view);

        Intent intent = getIntent();
        String directoryName = intent.getStringExtra("directoryName");

        JSONArray favourite = ((MainApplication) getApplication()).getFavourite().getJSONArray(directoryName);

        GraphNode centralNode = new GraphNode(MindMapActivity.this, directoryName, mindMappingView);

        List<JSONObject> entityObjects = new ArrayList<>();
        List<Future<JSONObject>> futureList = new ArrayList<>();

        for(Object obj : favourite) {
            JSONObject object = JSON.parseObject(obj.toString());
            String name = object.getString("name");
            String subject = object.getString("subject");
            GraphNode child = centralNode.addBaseEntity(MindMapActivity.this, mindMappingView, name, subject);
            nameIdMap.put(object.toString(), child);
            futureList.add(executorService.submit(new GetItemCallable(subject, name)));
            entityObjects.add(object);
        }

        List<Future<JSONObject>> nextLevelFutureList = new ArrayList<>();
        List<JSONObject> nextLevelObjects = new ArrayList<>();

        for(int level = 0; level <MAX_LEVEL; level++) {
            for (int i = 0; i < futureList.size(); i++) {
                JSONObject jsonObject;
                try {
                    jsonObject = futureList.get(i).get();
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }
                if(jsonObject == null)
                    continue;
                JSONArray properties = jsonObject.getJSONArray("property");
                Map<String, StringBuilder> propertyMap = new HashMap<>();
                for (Object o : properties) {
                    JSONObject property = JSON.parseObject(o.toString());
                    if (property.getString("object").startsWith("http"))
                        continue;
                    if (propertyMap.containsKey(property.getString("predicateLabel"))) {
                        propertyMap.get(property.getString("predicateLabel")).append("\n").append(property.getString("object"));
                    } else {
                        propertyMap.put(property.getString("predicateLabel"), new StringBuilder(property.getString("object")));
                    }
                }
                GraphNode parent = nameIdMap.get(entityObjects.get(i).toString());
                if (parent != null)
                    for (Map.Entry<String, StringBuilder> entry : propertyMap.entrySet()) {
                        parent.addNewProperty(MindMapActivity.this, mindMappingView, entry.getKey(), entry.getValue().toString());
                    }
                JSONArray relations = jsonObject.getJSONArray("content");
                for (Object o : relations) {
                    JSONObject relation = JSON.parseObject(o.toString());
                    if(relation.containsKey("object")) {
                        String target = relation.getString("object_label");
                        String subject = entityObjects.get(i).getString("subject");
                        JSONObject targetObject = new JSONObject();
                        targetObject.put("name", target);
                        targetObject.put("subject", subject);
                        if(nameIdMap.containsKey(targetObject.toString())) {
                            GraphNode targetNode = nameIdMap.get(targetObject.toString());
                            Edge edge = new Edge();
                            edge.start = parent;
                            edge.to = targetNode;
                            Edge edge2 = new Edge();
                            edge2.to = parent;
                            edge2.start = targetNode;
                            if(!edges.contains(edge) && !edges.contains(edge2) && parent != targetNode.getParent() && targetNode != parent.getParent()) {
                                GraphNode.addSubConnection(parent, targetNode, relation.getString("predicate_label"), MindMapActivity.this, mindMappingView);
                                edges.add(edge);
                                edges.add(edge2);
                            }
                        } else {
                            GraphNode targetNode = parent.addNewEntity(MindMapActivity.this, mindMappingView, target, subject, relation.getString("predicate_label"));
                            if(level != MAX_LEVEL - 1) {
                                nextLevelFutureList.add(executorService.submit(new GetItemCallable(target, subject)));
                                nextLevelObjects.add(targetObject);
                                nameIdMap.put(targetObject.toString(), targetNode);
                            }
                            Edge edge = new Edge();
                            edge.start = parent;
                            edge.to = targetNode;
                            Edge edge2 = new Edge();
                            edge2.to = parent;
                            edge2.start = targetNode;
                            edges.add(edge);
                            edges.add(edge2);
                        }
                    } else {
                        String target = relation.getString("subject_label");
                        String subject = entityObjects.get(i).getString("subject");
                        JSONObject targetObject = new JSONObject();
                        targetObject.put("name", target);
                        targetObject.put("subject", subject);
                        if(nameIdMap.containsKey(targetObject.toString())) {
                            GraphNode targetNode = nameIdMap.get(targetObject.toString());
                            Edge edge = new Edge();
                            edge.start = parent;
                            edge.to = targetNode;
                            Edge edge2 = new Edge();
                            edge2.to = parent;
                            edge2.start = targetNode;
                            if(!edges.contains(edge) && !edges.contains(edge2) && parent != targetNode.getParent() && targetNode != parent.getParent()) {
                                GraphNode.addSubConnection(parent, targetNode, relation.getString("predicate_label"), MindMapActivity.this, mindMappingView);
                                edges.add(edge);
                                edges.add(edge2);
                            }
                        } else {
                            GraphNode targetNode = parent.addNewEntity(MindMapActivity.this, mindMappingView, target, subject, relation.getString("predicate_label"));
                            if(level != MAX_LEVEL - 1) {
                                nextLevelFutureList.add(executorService.submit(new GetItemCallable(target, subject)));
                                nextLevelObjects.add(targetObject);
                                nameIdMap.put(targetObject.toString(), targetNode);
                            }
                            Edge edge = new Edge();
                            edge.start = parent;
                            edge.to = targetNode;
                            Edge edge2 = new Edge();
                            edge2.to = parent;
                            edge2.start = targetNode;
                            edges.add(edge);
                            edges.add(edge2);
                        }
                    }
                }
            }
            futureList = nextLevelFutureList;
            nextLevelFutureList = new ArrayList<>();
            entityObjects = nextLevelObjects;
            nextLevelObjects = new ArrayList<>();
        }

        findViewById(R.id.btnGeneratePhoto).setOnClickListener(view -> checkPermissions());
    }
    private static final int ALL_PERMISSIONS = 1;

    private void checkPermissions() {
        //Permissions we need
        String[] permissions = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        //Permissions that we will ask for
        ArrayList<String> needed_permissions = new ArrayList<>();

        //Check which is not granted yet
        for (String permission : permissions){
            if (ContextCompat.checkSelfPermission(MindMapActivity.this, permission) !=
                    PackageManager.PERMISSION_GRANTED){
                needed_permissions.add(permission);
            }
        }

        //Ask for multiple not granted permissions
        if(!needed_permissions.isEmpty())
            ActivityCompat.requestPermissions(MindMapActivity.this, needed_permissions.toArray(new String[needed_permissions.size()]), ALL_PERMISSIONS);
        else
            SaveAs.saveAsImage(mindMappingView, Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +
                    "image.jpg");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ALL_PERMISSIONS){
            if ((grantResults.length > 0) &&
                    (grantResults[0] == PackageManager.PERMISSION_GRANTED)){
                SaveAs.saveAsImage(mindMappingView, Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +
                        "image.jpg");

            }else {
                Toast.makeText(MindMapActivity.this, "All permissions need to be granted", Toast.LENGTH_LONG)
                        .show();
            }
        }
    }

}