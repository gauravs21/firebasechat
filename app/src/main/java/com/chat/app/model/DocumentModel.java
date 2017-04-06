package com.chat.app.model;

/*
 * Created by kopite on 4/4/17.
 */

import java.io.Serializable;

public class DocumentModel implements Serializable {
    private String path;
    private String name;
    private String type;
    private long fileLength;


    public DocumentModel(String absolutePath, String name, String type, long fileLength) {
        this.type = type;
        this.name = name;
        this.path = absolutePath;
        this.fileLength=fileLength;
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

    public long getFileLength() {
        return fileLength;
    }

    public void setFileLength(long fileLength) {
        this.fileLength = fileLength;
    }
}
