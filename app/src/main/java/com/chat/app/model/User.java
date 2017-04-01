package com.chat.app.model;

/*
 * Created by kopite on 29/3/17.
 */

public class User {

    private String email;
    private String device_token;
    private String userId;
    public User(){

    }
    public User(String email, String fcm, String userId) {
        this.email=email;
        this.device_token=fcm;
        this.userId=userId;
    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDevice_token() {
        return device_token;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
