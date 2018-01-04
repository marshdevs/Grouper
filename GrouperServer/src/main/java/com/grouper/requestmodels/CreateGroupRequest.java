package com.grouper.requestmodels;

import com.grouper.models.Event;
import com.grouper.models.Group;
import com.grouper.models.SkillSet;
import com.grouper.models.User;

import java.util.HashMap;

public class CreateGroupRequest {

    private String groupName;
    private String groupType;
    private String groupDescription;
    private String groupEventId;
    private String groupOwnerId;
    private HashMap<String, Boolean> groupSkills;

    CreateGroupRequest() {
        this.groupName = Group.DEFAULT_GROUP_NAME;
        this.groupType = Group.DEFAULT_GROUP_TYPE;
        this.groupDescription = Group.DEFAULT_GROUP_DESCRIPTION;
        this.groupEventId = Event.EMPTY_EVENT_ID;
        this.groupOwnerId = User.EMPTY_USER_ID;
        this.groupSkills = new SkillSet().getSkills();
    }

    CreateGroupRequest(String groupName, String groupType, String groupDescription, String groupEventId, String
        groupOwnerId, HashMap<String, Boolean> groupSkills) {
        this.groupName = groupName;
        this.groupType = groupType;
        this.groupDescription = groupDescription;
        this.groupEventId = groupEventId;
        this.groupOwnerId = groupOwnerId;
        this.groupSkills = groupSkills;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }

    public String getGroupType() {
        return groupType;
    }

    public void setGroupDescription(String groupDescription) {
        this.groupDescription = groupDescription;
    }

    public String getGroupDescription() {
        return groupDescription;
    }

    public void setGroupEventId(String groupEventId) {
        this.groupEventId = groupEventId;
    }

    public String getGroupEventId() {
        return groupEventId;
    }

    public void setGroupOwnerId(String groupOwnerId) {
        this.groupOwnerId = groupOwnerId;
    }

    public String getGroupOwnerId() {
        return groupOwnerId;
    }

    public void setGroupSkills(HashMap<String, Boolean> groupSkills) {
        this.groupSkills = groupSkills;
    }

    public HashMap<String, Boolean> getGroupSkills() {
        return groupSkills;
    }

}
