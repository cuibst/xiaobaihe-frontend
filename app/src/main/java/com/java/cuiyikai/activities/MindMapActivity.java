package com.java.cuiyikai.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java.cuiyikai.MainApplication;
import com.java.cuiyikai.R;
import com.java.cuiyikai.database.DatabaseEntity;
import com.java.cuiyikai.database.EntityDatabaseHelper;
import com.java.cuiyikai.entities.GraphNode;
import com.java.cuiyikai.network.RequestBuilder;
import com.java.cuiyikai.utilities.ConstantUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

import me.jagar.mindmappingandroidlibrary.Views.MindMappingView;

/**
 * {@link android.app.Activity} to demonstrate a mind map.
 */
public class MindMapActivity extends AppCompatActivity {

    private static final Logger logger = LoggerFactory.getLogger(MindMapActivity.class);

    //avoid and check same nodes in the graph
    private final Map<String, GraphNode> nameIdMap = new HashMap<>();

    private static class Edge {
        GraphNode start;
        GraphNode to;
    }

    private final Set<Edge> edges = new HashSet<>();

    //executor to submit the get item task
    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();

    /**
     * {@link Callable} to retrieve the entity information.
     */
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
                arguments.put(ConstantUtilities.ARG_NAME, name);
                arguments.put(ConstantUtilities.ARG_COURSE, subject);

                JSONObject reply;

                try {
                    reply = RequestBuilder.sendGetRequest("typeOpen/open/infoByInstanceName", arguments);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    throw e;
                }

                return reply.getJSONObject(ConstantUtilities.ARG_DATA);
            }
        }
    }

    //Max recursion depth
    private static final int MAX_LEVEL = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mind_map);

        MindMappingView mindMappingView = findViewById(R.id.mind_mapping_view);

        Intent intent = getIntent();
        String directoryName = intent.getStringExtra(ConstantUtilities.ARG_DIRECTORY_NAME);

        JSONArray favourite = ((MainApplication) getApplication()).getFavourite().getJSONArray(directoryName);

        GraphNode centralNode = new GraphNode(MindMapActivity.this, directoryName, mindMappingView);

        List<JSONObject> entityObjects = new ArrayList<>();
        List<Future<JSONObject>> futureList = new ArrayList<>();

        //Get the level 1 info, and build the graph for level 1
        for(Object obj : favourite) {
            JSONObject object = JSON.parseObject(obj.toString());
            String name = object.getString(ConstantUtilities.ARG_NAME);
            String subject = object.getString(ConstantUtilities.ARG_SUBJECT);
            GraphNode child = centralNode.addBaseEntity(MindMapActivity.this, mindMappingView, name, subject);
            nameIdMap.put(object.toString(), child);
            futureList.add(executorService.submit(new GetItemCallable(subject, name)));
            entityObjects.add(object);
        }

        List<Future<JSONObject>> nextLevelFutureList = new ArrayList<>();
        List<JSONObject> nextLevelObjects = new ArrayList<>();

        //recursive call each level and build the graph
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
                //construct sub properties.
                JSONArray properties = jsonObject.getJSONArray("property");
                Map<String, StringBuilder> propertyMap = new HashMap<>();
                for (Object o : properties) {
                    JSONObject property = JSON.parseObject(o.toString());
                    if (property.getString(ConstantUtilities.ARG_OBJECT).startsWith("http"))
                        continue;
                    if (propertyMap.containsKey(property.getString(ConstantUtilities.ARG_PREDICATE_LABEL))) {
                        propertyMap.get(property.getString(ConstantUtilities.ARG_PREDICATE_LABEL)).append("\n").append(property.getString(ConstantUtilities.ARG_OBJECT));
                    } else {
                        propertyMap.put(property.getString(ConstantUtilities.ARG_PREDICATE_LABEL), new StringBuilder(property.getString(ConstantUtilities.ARG_OBJECT)));
                    }
                }
                GraphNode parent = nameIdMap.get(entityObjects.get(i).toString());
                if (parent != null)
                    for (Map.Entry<String, StringBuilder> entry : propertyMap.entrySet()) {
                        parent.addNewProperty(MindMapActivity.this, mindMappingView, entry.getKey(), entry.getValue().toString());
                    }
                //construct relations and add new item when it doesn't exist.
                JSONArray relations = jsonObject.getJSONArray(ConstantUtilities.ARG_CONTENT);
                for (Object o : relations) {
                    JSONObject relation = JSON.parseObject(o.toString());
                    if(relation.containsKey(ConstantUtilities.ARG_OBJECT)) { //object relation.
                        String target = relation.getString("object_label");
                        String subject = entityObjects.get(i).getString(ConstantUtilities.ARG_SUBJECT);
                        JSONObject targetObject = new JSONObject();
                        targetObject.put(ConstantUtilities.ARG_NAME, target);
                        targetObject.put(ConstantUtilities.ARG_SUBJECT, subject);
                        if(nameIdMap.containsKey(targetObject.toString())) { //exist
                            GraphNode targetNode = nameIdMap.get(targetObject.toString());
                            Edge edge = new Edge();
                            edge.start = parent;
                            edge.to = targetNode;
                            Edge edge2 = new Edge();
                            edge2.to = parent;
                            edge2.start = targetNode;
                            if(!edges.contains(edge) && !edges.contains(edge2) && parent != targetNode.getParent() && targetNode != parent.getParent()) {
                                GraphNode.addSubConnection(parent, targetNode, relation.getString(ConstantUtilities.ARG_PREDICATE_LABEL_UNDERLINE), MindMapActivity.this, mindMappingView);
                                edges.add(edge);
                                edges.add(edge2);
                            }
                        } else { //doesn't exist, create new one.
                            GraphNode targetNode = parent.addNewEntity(MindMapActivity.this, mindMappingView, target, subject, relation.getString(ConstantUtilities.ARG_PREDICATE_LABEL_UNDERLINE));
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
                    } else { // subject relation
                        String target = relation.getString("subject_label");
                        String subject = entityObjects.get(i).getString(ConstantUtilities.ARG_SUBJECT);
                        JSONObject targetObject = new JSONObject();
                        targetObject.put(ConstantUtilities.ARG_NAME, target);
                        targetObject.put(ConstantUtilities.ARG_SUBJECT, subject);
                        if(nameIdMap.containsKey(targetObject.toString())) { //exist
                            GraphNode targetNode = nameIdMap.get(targetObject.toString());
                            Edge edge = new Edge();
                            edge.start = parent;
                            edge.to = targetNode;
                            Edge edge2 = new Edge();
                            edge2.to = parent;
                            edge2.start = targetNode;
                            if(!edges.contains(edge) && !edges.contains(edge2) && parent != targetNode.getParent() && targetNode != parent.getParent()) {
                                GraphNode.addSubConnection(parent, targetNode, relation.getString(ConstantUtilities.ARG_PREDICATE_LABEL_UNDERLINE), MindMapActivity.this, mindMappingView);
                                edges.add(edge);
                                edges.add(edge2);
                            }
                        } else { //doesn't exist
                            GraphNode targetNode = parent.addNewEntity(MindMapActivity.this, mindMappingView, target, subject, relation.getString(ConstantUtilities.ARG_PREDICATE_LABEL_UNDERLINE));
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
            //update the level
            futureList = nextLevelFutureList;
            nextLevelFutureList = new ArrayList<>();
            entityObjects = nextLevelObjects;
            nextLevelObjects = new ArrayList<>();
        }
    }

}