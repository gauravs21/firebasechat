package com.chat.app.model;

/*
 * Created by kopite on 30/3/17.
 */

import java.util.Date;

public class ChatMessage {

    private String messageBody;
    private String to;
    private String from;
    private long timestamp;

    public ChatMessage() {
    }

    public ChatMessage(String messageBody, String toEmail, String fromEmail, long time) {
        this.messageBody = messageBody;
        this.to = toEmail;
        this.from = fromEmail;
        this.timestamp=time;
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


}
