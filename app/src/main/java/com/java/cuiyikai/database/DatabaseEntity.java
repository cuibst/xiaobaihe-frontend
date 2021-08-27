package com.java.cuiyikai.database;

public class DatabaseEntity {
    private String name;
    private String subject;
    private String jsonContent;
    private String problemsJson;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getJsonContent() {
        return jsonContent;
    }

    public void setJsonContent(String jsonContent) {
        this.jsonContent = jsonContent;
    }

    public String getProblemsJson() {
        return problemsJson;
    }

    public void setProblemsJson(String problemsJson) {
        this.problemsJson = problemsJson;
    }
}
