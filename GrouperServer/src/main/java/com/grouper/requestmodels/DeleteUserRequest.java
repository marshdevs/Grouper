package com.grouper.requestmodels;

import com.grouper.models.User;

import java.util.HashMap;

public class DeleteUserRequest {

    private String userId;
    private HashMap<String, String> userEventMap;

    DeleteUserRequest() {
        this.userId = User.EMPTY_USER_ID;
        this.userEventMap = new HashMap<>();
    }

    DeleteUserRequest(String userId, HashMap<String, String> userEventMap) {
        this.userId = userId;
        this.userEventMap = userEventMap;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserEventMap(HashMap<String, String> userEventMap) {
        this.userEventMap = userEventMap;
    }

    public HashMap<String, String> getUserEventMap() {
        return userEventMap;
    }

}
