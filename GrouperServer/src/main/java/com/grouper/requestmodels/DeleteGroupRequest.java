package com.grouper.requestmodels;

import com.grouper.models.Event;
import com.grouper.models.Group;

import java.util.ArrayList;

/**
 * Delete Group Request
 *
 * DeleteGroupRequest should be a DELETE request of type (application/json;charset=UTF-8), with the required
 * params in the body.
 */
public class DeleteGroupRequest {
    /** String groupId of the group to be deleted {groupId: String}
     */
    private String groupId;
    /** String eventId of the event this group belongs to {groupEventId: String}
     */
    private String groupEventId;
    /** [String] list of userIds belonging to this group {groupUsers: [String]}
     */
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
