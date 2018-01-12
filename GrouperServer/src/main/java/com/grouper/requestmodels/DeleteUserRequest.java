package com.grouper.requestmodels;

import com.grouper.models.User;

import java.util.HashMap;

/**
 * Delete User Request
 *
 * DeleteUserRequest should be a DELETE request of type (application/json;charset=UTF-8), with the required params
 * in the body.
 *
 * This request should probably not be called by the client, and instead users would send requests to Grouper
 * admins to delete their accounts for them with the internal data tool on grouper.site.
 * If we want it to be called within the app, it needs to be updated to delete their
 * email and password from the grouper-auth table.
 */
public class DeleteUserRequest {
    /** String userId of the user to be deleted {userId: String}
     */
    private String userId;
    /** Map(String: String), maps the user's enrolled events to the groups they belong to {userEventMap: Map(String:
     * String)}
     */
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
