package com.java.cuiyikai.entities;

public class PropertyEntity {
    private String label;
    private String object;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    @Override
    public String toString() {
        return "Label: " + label + ", object: " + object;
    }
}
