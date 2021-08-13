package com.java.cuiyikai.entities;

public class RelationEntity {

    private String relationName;

    private boolean subject;

    private String targetName;

    public RelationEntity() {}

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
    public String toString() {
        return "Name: " + relationName + ", flag: " + subject + ", target:" + targetName;
    }
}
