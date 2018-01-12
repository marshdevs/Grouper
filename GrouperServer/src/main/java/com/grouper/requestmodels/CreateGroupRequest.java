package com.grouper.requestmodels;

import com.grouper.models.Event;
import com.grouper.models.Group;
import com.grouper.models.SkillSet;
import com.grouper.models.User;

import java.util.HashMap;

/**
 * Create Group Request
 *
 * CreateGroupRequest should be a POST request of type (application/json;charset=UTF-8) with the required
 * parameters in the body.
 *
 * These parameters will be provided in the create group view controller
 */

public class CreateGroupRequest {
    /** String name of the group {groupName: string}
     */
    private String groupName;
    /** String group type (class, hackathon, startup) {groupType: string}
     */
    private String groupType;
    /** String groupDescription {groupDescription: string}
     */
    private String groupDescription;
    /** String id of the event the group belongs to {groupEventId: string}
     */
    private String groupEventId;
    /** String id of the owner of the group {groupOwnerId: string}
     */
    private String groupOwnerId;
    /** Map(string: boolean), depicting the skills the group needs {groupSkills: Map(String, Boolean)}
     */
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
