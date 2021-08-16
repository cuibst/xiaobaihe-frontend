package com.java.cuiyikai.database;

public class DatabaseEntity {
    private String name;
    private String uri;
    private String jsonContent;
    private String problemsJson;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
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
