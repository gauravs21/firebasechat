package com.chat.app.model;

/*
 * Created by kopite on 30/3/17.
 */

public class ChatMessage {

    private String messageBody;
    private String to;
    private String from;
    private long timestamp;
    private String messageType;
    private long fileLength;
    private String downloadLink;
    public ChatMessage() {
    }

    public ChatMessage(String messageBody, String toEmail, String fromEmail, long time, String messageType, long fileSize, String link) {
        this.messageBody = messageBody;
        this.to = toEmail;
        this.from = fromEmail;
        this.timestamp = time;
        this.messageType = messageType;
        this.fileLength = fileSize;
        this.downloadLink = link;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }


    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public long getFileLength() {
        return fileLength;
    }

    public void setFileLength(long fileLength) {
        this.fileLength = fileLength;
    }

    public String getDownloadLink() {
        return downloadLink;
    }

    public void setDownloadLink(String downloadLink) {
        this.downloadLink = downloadLink;
    }
}
