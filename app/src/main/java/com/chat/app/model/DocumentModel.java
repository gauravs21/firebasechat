package com.chat.app.model;

/*
 * Created by kopite on 4/4/17.
 */

public class DocumentModel {
    private String path;
    private String name;
    private String type;


    public DocumentModel(String absolutePath, String name, String type) {
        this.type = type;
        this.name = name;
        this.path = absolutePath;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public DocumentModel() {
    }
}
