package com.java.cuiyikai;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.java.cuiyikai.adapters.PropertyAdapter;
import com.java.cuiyikai.adapters.RelationAdapter;
import com.java.cuiyikai.database.DatabaseEntity;
import com.java.cuiyikai.database.EntityDatabaseHelper;
import com.java.cuiyikai.entities.PropertyEntity;
import com.java.cuiyikai.entities.RelationEntity;
import com.java.cuiyikai.network.RequestBuilder;
import com.java.cuiyikai.widgets.ListViewForScrollView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class EntityActivity extends AppCompatActivity {

    private class RelationViewOnClickListener implements View.OnClickListener {
        private final List<RelationEntity> fullList, prevList;
        private boolean extended;
        private final ListViewForScrollView relatedView;
        private final ImageButton relatedButton;

        @Override
        public void onClick(View v) {
            if(extended) {
                relatedButton.setBackgroundResource(R.drawable.pulldown);
                extended = false;
                relatedView.setAdapter(new RelationAdapter(EntityActivity.this, R.layout.relation_item, prevList));
            } else {
                relatedButton.setBackgroundResource(R.drawable.pullback);
                extended = true;
                relatedView.setAdapter(new RelationAdapter(EntityActivity.this, R.layout.relation_item, fullList));
            }
        }

        public RelationViewOnClickListener(List<RelationEntity> fullList, List<RelationEntity> prevList, boolean extended, ListViewForScrollView relatedView, ImageButton relatedButton) {
            this.fullList = fullList;
            this.prevList = prevList;
            this.extended = extended;
            this.relatedView = relatedView;
            this.relatedButton = relatedButton;
        }
    }

    private class PropertyViewOnClickListener implements View.OnClickListener {
        private final List<PropertyEntity> fullList, prevList;
        private boolean extended;
        private final ListViewForScrollView propertyView;
        private final ImageButton propertyButton;

        @Override
        public void onClick(View v) {
            if(extended) {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entity);
        ImageButton relationButton = (ImageButton) findViewById(R.id.relationButton);
        ImageButton propertyButton = (ImageButton) findViewById(R.id.propertyButton);
        String entityName = "李白";
        String subject = "chinese";
        JSONObject entityJson;

        EntityDatabaseHelper helper = EntityDatabaseHelper.getInstance(EntityActivity.this, 1);
        helper.openReadLink();
        List<DatabaseEntity> entityList = helper.queryEntityByName(entityName);
        helper.closeLink();
        if(!entityList.isEmpty())
            entityJson = JSONObject.parseObject(entityList.get(0).getJsonContent());
        else {

            System.out.println("No matches in database!!");

            Map<String, String> arguments = new HashMap<>();
            arguments.put("name", entityName);
            arguments.put("course", subject);

            JSONObject reply;

            try {
                reply = RequestBuilder.sendGetRequest("typeOpen/open/infoByInstanceName", arguments);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

            entityJson = reply.getJSONObject("data");

            helper.openWriteLink();
            DatabaseEntity databaseEntity = new DatabaseEntity();
            databaseEntity.setName(entityName);
            databaseEntity.setJsonContent(entityJson.toJSONString());
            databaseEntity.setUri("123");
            helper.insert(databaseEntity, "1=1");
            helper.closeLink();
        }

        TextView titleView = (TextView) findViewById(R.id.entityTitle);
        titleView.setText(entityName);

        List<JSONObject> objectList = entityJson.getJSONArray("content").toJavaList(JSONObject.class);
        List<RelationEntity> relationFullList, relationPrevList;
        relationFullList = new ArrayList<>();
        for (JSONObject relationJson : objectList) {
            RelationEntity entity = new RelationEntity();
            entity.setRelationName(relationJson.getString("predicate_label"));
            if(relationJson.getString("object") != null) {
                entity.setSubject(false);
                entity.setTargetName(relationJson.getString("object_label"));
            } else {
                entity.setSubject(true);
                entity.setTargetName(relationJson.getString("subject_label"));
            }
            relationFullList.add(entity);
        }
        Collections.sort(relationFullList);
        RelationAdapter relationAdapter;
        ListViewForScrollView relationsView = (ListViewForScrollView) findViewById(R.id.relationsView);
        if(relationFullList.size() >= 5) {
            relationPrevList = relationFullList.subList(0, 5);
            relationAdapter = new RelationAdapter(EntityActivity.this, R.layout.relation_item, relationPrevList);
            relationButton.setBackgroundResource(R.drawable.pulldown);
            relationButton.setOnClickListener(new RelationViewOnClickListener(relationFullList, relationPrevList, false, relationsView, relationButton));
        } else {
            relationButton.setVisibility(View.GONE);
            relationAdapter = new RelationAdapter(EntityActivity.this, R.layout.relation_item, relationFullList);
        }
        relationsView.setAdapter(relationAdapter);

        objectList = entityJson.getJSONArray("property").toJavaList(JSONObject.class);
        List<PropertyEntity> propertyFullList, propertyPrevList;
        propertyFullList = new ArrayList<>();
        for(JSONObject propertyJson : objectList) {
            PropertyEntity entity = new PropertyEntity();
            if(propertyJson.getString("object").contains("http"))
                continue;
            entity.setLabel(propertyJson.getString("predicateLabel"));
            entity.setObject(propertyJson.getString("object"));
            propertyFullList.add(entity);
        }

        PropertyAdapter propertyAdapter;
        ListViewForScrollView propertiesView = (ListViewForScrollView) findViewById(R.id.propertiesView);
        if(propertyFullList.size() >= 5) {
            propertyPrevList = propertyFullList.subList(0,5);
            propertyAdapter = new PropertyAdapter(EntityActivity.this, R.layout.property_item, propertyPrevList);
            propertyButton.setBackgroundResource(R.drawable.pulldown);
            propertyButton.setOnClickListener(new PropertyViewOnClickListener(propertyFullList, propertyPrevList, false, propertiesView, propertyButton));
        } else {
            propertyButton.setVisibility(View.GONE);
            propertyAdapter = new PropertyAdapter(EntityActivity.this, R.layout.property_item, propertyFullList);
        }
        propertiesView.setAdapter(propertyAdapter);
    }
}