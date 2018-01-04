package com.grouper.requestmodels;

import com.grouper.models.Event;
import com.grouper.models.Group;

import java.util.ArrayList;

public class DeleteGroupRequest {

    private String groupId;
    private String groupEventId;
    private ArrayList<String> groupUsers;

    DeleteGroupRequest() {
        this.groupId = Group.EMPTY_GROUP_ID;
        this.groupEventId = Event.EMPTY_EVENT_ID;
        this.groupUsers = new ArrayList();
    }

    DeleteGroupRequest(String groupId, String groupEventId, ArrayList<String> groupUsers) {
        this.groupId = groupId;
        this.groupEventId = groupEventId;
        this.groupUsers = groupUsers;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupEventId(String groupEventId) {
        this.groupEventId = groupEventId;
    }

    public String getGroupEventId() {
        return groupEventId;
    }

    public void setGroupUsers(ArrayList<String> groupUsers) {
        this.groupUsers = groupUsers;
    }

    public ArrayList<String> getGroupUsers() {
        return groupUsers;
    }

}
