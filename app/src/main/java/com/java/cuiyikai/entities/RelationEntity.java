package com.java.cuiyikai.entities;

public class RelationEntity implements Comparable<RelationEntity> {

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

    @Override
    public int compareTo(RelationEntity entity) {
        if(subject != entity.subject)
            return subject ? 1 : -1;
        return 0;
    }
}
