package com.java.cuiyikai.entities;

import android.app.Activity;
import android.content.Intent;

import com.java.cuiyikai.R;
import com.java.cuiyikai.activities.EntityActivity;
import com.java.cuiyikai.utilities.ConstantUtilities;

import java.util.Map;

import me.jagar.mindmappingandroidlibrary.Views.ConnectionTextMessage;
import me.jagar.mindmappingandroidlibrary.Views.Item;
import me.jagar.mindmappingandroidlibrary.Views.ItemLocation;
import me.jagar.mindmappingandroidlibrary.Views.MindMappingView;

public class GraphNode {
    private final Item item;
    private final GraphNode parent;
    private final int parentDirection;

    private static final int[] DIRECTIONS = {
        ItemLocation.TOP, ItemLocation.RIGHT, ItemLocation.BOTTOM, ItemLocation.LEFT
    };

    //Root node
    public GraphNode(Activity activity, String title, MindMappingView view) {
        this.parent = null;
        this.parentDirection = -1;
        item = new Item(activity, title, "", true);
        view.addCentralItem(item, true);
    }

    //For property
    private GraphNode(Activity activity, String title, String content, GraphNode parent, MindMappingView mindMappingView) {
        this.parent = parent;
        this.parentDirection = parent.parentDirection;
        if(content.length() > 25)
            content = content.substring(0, 25) + "...";
        item = new Item(activity, title, content, true);
        mindMappingView.addItem(item, parent.item, 200, 15, DIRECTIONS[parentDirection], true, null);
    }

    //For base entity
    private GraphNode(Activity activity, String title, String content, GraphNode parent, MindMappingView mindMappingView, int dummy) {
        this.parent = parent;
        this.parentDirection = parent.getRandomDirection();
        item = new Item(activity, title, content, true);
        setEntityBackground(content);
        mindMappingView.addItem(item, parent.item, 400, 15, DIRECTIONS[parentDirection], true, null);
    }

    //For Entity
    private GraphNode(Activity activity, String title, String content, String connectionText, GraphNode parent, MindMappingView mindMappingView) {
        this.parent = parent;
        this.parentDirection = parent.getRandomDirection();
        item = new Item(activity, title, content, true);
        ConnectionTextMessage connectionTextMessage = new ConnectionTextMessage(activity);
        connectionTextMessage.setText(connectionText);
        setEntityBackground(content);
        mindMappingView.addItem(item, parent.item, 400, 15, DIRECTIONS[parentDirection], true, connectionTextMessage);
    }

    private void setEntityBackground(String content) {
        switch(content) {
            case ConstantUtilities.SUBJECT_CHINESE:
                item.setBackgroundResource(R.drawable.chinese_radius);
                break;
            case ConstantUtilities.SUBJECT_MATH:
                item.setBackgroundResource(R.drawable.maths_radius);
                break;
            case ConstantUtilities.SUBJECT_ENGLISH:
                item.setBackgroundResource(R.drawable.english_radius);
                break;
            case ConstantUtilities.SUBJECT_PHYSICS:
                item.setBackgroundResource(R.drawable.physics_radius);
                break;
            case ConstantUtilities.SUBJECT_CHEMISTRY:
                item.setBackgroundResource(R.drawable.chemistry_radius);
                break;
            case ConstantUtilities.SUBJECT_BIOLOGY:
                item.setBackgroundResource(R.drawable.biology_radius);
                break;
            case ConstantUtilities.SUBJECT_HISTORY:
                item.setBackgroundResource(R.drawable.history_radius);
                break;
            case ConstantUtilities.SUBJECT_GEO:
                item.setBackgroundResource(R.drawable.geography_radius);
                break;
            case ConstantUtilities.SUBJECT_POLITICS:
            default:
                item.setBackgroundResource(R.drawable.politics_radius);
                break;
        }
    }

    public Item getItem() {
        return item;
    }

    public GraphNode getParent() {
        return parent;
    }

    public GraphNode addNewEntity(Activity activity, MindMappingView view, String title, String content, String connectionText) {
        return new GraphNode(activity, title, content, connectionText, this, view);
    }

    public GraphNode addNewProperty(Activity activity, MindMappingView view, String title, String content) {
        return new GraphNode(activity, title, content, this, view);
    }

    public GraphNode addBaseEntity(Activity activity, MindMappingView view, String title, String content) {
        return new GraphNode(activity, title, content, this, view, 1);
    }

    public int getRandomDirection() {
        if(parentDirection != -1)
            return (Math.random() < 0.5) ? (parentDirection + 1) % 4 : (parentDirection + 3) % 4;
        else
            return (int)(System.currentTimeMillis() % 4);
    }

    public static void addSubConnection(GraphNode parent, GraphNode child, String connectionText, Activity activity, MindMappingView view) {
        Item parentItem = parent.getItem();
        Item childItem = child.getItem();
        ConnectionTextMessage connectionTextMessage = new ConnectionTextMessage(activity);
        connectionTextMessage.setText(connectionText);
        view.addCustomConnection(childItem, DIRECTIONS[child.getRandomDirection()], parentItem, DIRECTIONS[parent.getRandomDirection()], connectionTextMessage, 10, "#000000", 5, 20);
    }
}
