package com.grouper.requestmodels;

import com.grouper.models.Event;
import com.grouper.models.SkillSet;
import com.grouper.models.User;

import java.util.HashMap;

/**
 * Create User Request
 *
 * CreateUserRequest should be a POST request of type (application/json;charset=UTF-8) with the required
 * parameters in the body.
 *
 * These parameters will be provided in the profile creation view controller
 */

public class CreateUserRequest {
    /** String user's name {userName: string}
     */
    private String userName;
    /** String user's occupation {userOccupation: String}
     */
    private String userOccupation;
    /** Map(string: boolean) skills belonging to the user {userSkills: Map(String: Boolean)}
     */
    private HashMap<String, Boolean> userSkills;

    public CreateUserRequest(){
        this.userName = User.DEFAULT_USER_NAME;
        this.userOccupation = User.DEFAULT_USER_OCCUPATION;
        this.userSkills = new SkillSet().getSkills();
    }

    public CreateUserRequest(String userName, String userOccupation, HashMap<String, Boolean> userSkills) {
        this.userName = userName;
        this.userOccupation = userOccupation;
        this.userSkills = userSkills;
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
