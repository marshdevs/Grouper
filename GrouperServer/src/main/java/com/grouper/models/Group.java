package com.grouper.models;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.grouper.objectcache.UserObjectCache;
import com.grouper.service.GrouperServiceApplication;

import java.util.*;

public class Group {

    public static final String EMPTY_GROUP_ID = "00000000";

    private static final String DEFAULT_GROUP_NAME = "NO_GROUP_NAME";
    private static final String DEFAULT_GROUP_TYPE = "NO_GROUP_TYPE";
    private static final String DEFAULT_GROUP_DESCRIPTION = "NO_GROUP_DESCRIPTION";

    private static final String PROJECT_GROUP_TYPE = "PROJECT_GROUP_TYPE";
    private static final String HACKATHON_GROUP_TYPE = "HACKATHON_GROUP_TYPE";
    private static final String STARTUP_GROUP_TYPE = "STARTUP_GROUP_TYPE";

    private final String groupId;
    private String groupName;
    private String groupType;
    private String groupDescription;
    private final String groupEvent;
    private final String groupOwner;
    private SkillSet groupSkillSet;
    private ArrayList<String> groupUsers;

    private Group(GroupBuilder builder) {
        this.groupId = builder.groupId;
        this.groupName = builder.groupName;
        this.groupType = builder.groupType;
        this.groupDescription = builder.groupDescription;
        this.groupEvent = builder.groupEvent;
        this.groupOwner = builder.groupOwner;
        this.groupSkillSet = builder.groupSkillSet;
        this.groupUsers = builder.groupUsers;
    }

    public static class GroupBuilder {

        private final String groupId;
        private String groupName = DEFAULT_GROUP_NAME;
        private String groupType = DEFAULT_GROUP_TYPE;
        private String groupDescription = DEFAULT_GROUP_DESCRIPTION;
        private String groupEvent;
        private String groupOwner;
        private SkillSet groupSkillSet;
        private ArrayList<String> groupUsers;

        public GroupBuilder(String groupId) {
            this.groupId = groupId;
            this.groupEvent = Event.EMPTY_EVENT_ID;
            this.groupOwner = User.EMPTY_USER_ID;
            this.groupSkillSet = new SkillSet();
            this.groupUsers = new ArrayList<>();
        }

        public GroupBuilder withGroupName(String groupName) {
            this.groupName = groupName;
            return this;
        }

        public GroupBuilder withGroupType(String groupType) {
            this.groupType = groupType;
            return this;
        }

        public GroupBuilder withGroupDescription(String groupDescription) {
            this.groupDescription = groupDescription;
            return this;
        }

        public GroupBuilder withGroupEvent(String groupEvent) {
            this.groupEvent = groupEvent;
            return this;
        }

        public GroupBuilder withGroupOwner(String groupOwner) {
            this.groupOwner = groupOwner;
            return this;
        }

        public GroupBuilder withGroupSkillSet(SkillSet groupSkillSet) {
            this.groupSkillSet = groupSkillSet;
            return this;
        }

        public GroupBuilder withGroupUsers(List<String> stringList) {
            ArrayList<String> groupUsers = new ArrayList<>();

            for (String userId : stringList) {
                groupUsers.add(userId);
            }

            this.groupUsers = groupUsers;
            return this;
        }

        public Group build() {
            return new Group(this);
        }
    }

    public String getGroupId() {
        return this.groupId;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupName() {
        return this.groupName;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }

    public String getGroupType() {
        return this.groupType;
    }

    public void setGroupDescription(String groupDescription) {
        this.groupDescription = groupDescription;
    }

    public String getGroupDescription() {
        return this.groupDescription;
    }

    public String getGroupEvent() {
        return this.groupEvent;
    }

    public String getGroupOwner() {
        return this.groupOwner;
    }

    public void addSkill(String skill) {
        this.groupSkillSet.addSkill(skill);
    }

    public void removeSkill(String skill) {
        this.groupSkillSet.removeSkill(skill);
    }

    public SkillSet getGroupSkillSet() {
        return this.groupSkillSet;
    }

    public void addUser(User user) {
        this.groupUsers.add(user.getUserId());
    }

    public void removeUser(User user) {
        this.groupUsers.remove(user.getUserId());
    }

    public ArrayList<String> getGroupUsers() {
        return this.groupUsers;
    }

}
