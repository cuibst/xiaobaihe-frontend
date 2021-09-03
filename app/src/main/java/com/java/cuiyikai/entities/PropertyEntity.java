package com.java.cuiyikai.entities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class PropertyEntity implements Comparable<PropertyEntity> {
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

    @NonNull
    @Override
    public String toString() {
        return "Label: " + label + ", object: " + object;
    }

    @Override
    public int hashCode() {
        return label.hashCode() * object.hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return obj instanceof PropertyEntity && ((PropertyEntity)obj).label.equals(label) && ((PropertyEntity)obj).object.equals(object);
    }

    @Override
    public int compareTo(PropertyEntity propertyEntity) {
        if(!label.equals(propertyEntity.label))
            return label.compareTo(propertyEntity.label);
        if(!object.equals(propertyEntity.object))
            return object.compareTo(propertyEntity.object);
        return 0;
    }
}
