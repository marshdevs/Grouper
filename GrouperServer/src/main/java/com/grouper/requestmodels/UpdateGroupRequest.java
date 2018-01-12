package com.grouper.requestmodels;

import com.grouper.models.Event;
import com.grouper.models.Group;
import com.grouper.models.SkillSet;
import com.grouper.models.User;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Update Group Request
 *
 * UpdateGroupRequest should be a POST request of type (application/json;charset=UTF-8), with the required params
 * in the body.
 *
 * This request is not for updates to groupUsers. Additions/deletions of users have their own dedicated requests.
 */
public class UpdateGroupRequest {

    /** String groupId to be updated {groupId: String}
     */
    private String groupId;
    /** String group name {groupName: String}
     */
    private String groupName;
    /** String group type (in hackathon, class, startup) {groupType: String}
     */
    private String groupType;
    /** String group description {groupDescription: String}
     */
    private String groupDescription;
    /** string eventId the group belongs to (never updated, should probably remove) {groupEventId: String}
     */
    private String groupEventId;
    /** String userId of the group's owner {groupOwnerId: String}
     */
    private String groupOwnerId;
    /** Map(String: boolean), skills the group is in need of {groupSkills: Map(String: Boolean)}
     */
    private HashMap<String, Boolean> groupSkills;

    UpdateGroupRequest() {
        this.groupId = Group.EMPTY_GROUP_ID;
        this.groupName = Group.DEFAULT_GROUP_NAME;
        this.groupType = Group.DEFAULT_GROUP_TYPE;
        this.groupDescription = Group.DEFAULT_GROUP_DESCRIPTION;
        this.groupEventId = Event.EMPTY_EVENT_ID;
        this.groupOwnerId = User.EMPTY_USER_ID;
        this.groupSkills = new SkillSet().getSkills();
    }

    UpdateGroupRequest(String groupId, String groupName, String groupType, String groupDescription, String
        groupEventId, String groupOwnerId, HashMap<String, Boolean> groupSkills) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.groupType = groupType;
        this.groupDescription = groupDescription;
        this.groupEventId = groupEventId;
        this.groupOwnerId = groupOwnerId;
        this.groupSkills = groupSkills;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupId() {
        return groupId;
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
