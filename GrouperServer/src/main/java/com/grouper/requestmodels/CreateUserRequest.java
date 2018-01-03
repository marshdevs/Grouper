package com.grouper.requestmodels;

import com.grouper.models.Event;
import com.grouper.models.SkillSet;
import com.grouper.models.User;

import java.util.HashMap;

public class CreateUserRequest {

    private String userName;
    private String userOccupation;
    private HashMap<String, Boolean> userSkills;
    private String userEventId;

    public CreateUserRequest(){
        this.userName = User.DEFAULT_USER_NAME;
        this.userOccupation = User.DEFAULT_USER_OCCUPATION;
        this.userSkills = new SkillSet().getSkills();
        this.userEventId = Event.EMPTY_EVENT_ID;
    }

    public CreateUserRequest(String userName, String userOccupation, HashMap<String, Boolean> userSkills, String
        userEventId) {
        this.userName = userName;
        this.userOccupation = userOccupation;
        this.userSkills = userSkills;
        this.userEventId = userEventId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getName() {
        return this.userName;
    }

    public void setUserOccupation(String userOccupation) {
        this.userOccupation = userOccupation;
    }

    public String getOccupation() {
        return userOccupation;
    }

    public void setUserSkills(HashMap<String, Boolean> userSkills) {
        this.userSkills = userSkills;
    }

    public HashMap<String, Boolean> getSkills() {
        return userSkills;
    }

    public void setUserEventId(String userEventId) {
        this.userEventId = userEventId;
    }

    public String getEventId() {
        return userEventId;
    }

}
