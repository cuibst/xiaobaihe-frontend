package com.java.cuiyikai.entities;

import org.jetbrains.annotations.NotNull;

public class RelationEntity implements Comparable<RelationEntity> {

    private String relationName;

    private boolean subject;

    private String targetName;

    public String getRelationName() {
        return relationName;
    }

    public void setRelationName(String relationName) {
        this.relationName = relationName;
    }

    public boolean isSubject() {
        return subject;
    }

    public void setSubject(boolean subject) {
        this.subject = subject;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    @Override
    @NotNull
    public String toString() {
        return "Name: " + relationName + ", flag: " + subject + ", target:" + targetName;
    }

    @Override
    public int compareTo(RelationEntity entity) {
        if(subject != entity.subject)
            return subject ? 1 : -1;
        return 0;
    }

    @Override
    public boolean equals(Object entity) {
        if(!(entity instanceof RelationEntity))
            return false;
        return subject == ((RelationEntity) entity).isSubject() && relationName.equals(((RelationEntity) entity).getRelationName()) && targetName.equals(((RelationEntity) entity).getTargetName());
    }

    @Override
    public int hashCode() {
        return (subject ? 1 : 2) * relationName.hashCode() * targetName.hashCode();
    }
}
