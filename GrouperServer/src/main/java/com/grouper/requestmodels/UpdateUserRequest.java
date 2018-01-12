package com.grouper.requestmodels;

import com.grouper.models.Event;
import com.grouper.models.SkillSet;
import com.grouper.models.User;

import java.util.HashMap;

/**
 * Update User Request
 *
 * UpdateUserRequest should be a POST request of type (application/json;charset=UTF-8), with the required params
 * in the body.
 *
 * This request is not for updates to userEventMap (addition/removal of events, addition/removal of groups).
 * Those operations have their own dedicated requests.
 */
public class UpdateUserRequest {
    /**
     * String userId to be updated {userId: String}
     */
    private String userId;
    /** String user name {userName: String}
     */
    private String userName;
    /** String user occupation {userOccupation: String}
     */
    private String userOccupation;
    /** Map(String: Boolean), skills belonging to the user {userSkills: Map(String: Boolean)}
     */
    private HashMap<String, Boolean> userSkills;

    public UpdateUserRequest(){
        this.userId = User.EMPTY_USER_ID;
        this.userName = User.DEFAULT_USER_NAME;
        this.userOccupation = User.DEFAULT_USER_OCCUPATION;
        this.userSkills = new SkillSet().getSkills();
    }

    public UpdateUserRequest(String userId, String userName, String userOccupation, HashMap<String, Boolean>
        userSkills) {
        this.userId = userId;
        this.userName = userName;
        this.userOccupation = userOccupation;
        this.userSkills = userSkills;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserOccupation(String userOccupation) {
        this.userOccupation = userOccupation;
    }

    public String getUserOccupation() {
        return userOccupation;
    }

    public void setUserSkills(HashMap<String, Boolean> userSkills) {
        this.userSkills = userSkills;
    }

    public HashMap<String, Boolean> getUserSkills() {
        return userSkills;
    }

}
