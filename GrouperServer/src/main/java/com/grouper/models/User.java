package com.grouper.models;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.grouper.objectcache.GroupObjectCache;
import com.grouper.service.GrouperServiceApplication;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class User {

    public static final String EMPTY_USER_ID = "00000000";

    public static final String DEFAULT_USER_NAME = "NO_USER_NAME";
    public static final String DEFAULT_USER_OCCUPATION = "NO_USER_OCCUPATION";

    private final String userId;
    private String userName;
    private String userOccupation;
    private SkillSet userSkillSet;
    private HashMap<String, String> userEventMap;

    private User(UserBuilder builder) {
        this.userId = builder.userId;
        this.userName = builder.userName;
        this.userOccupation = builder.userOccupation;
        this.userSkillSet = builder.userSkillSet;
        this.userEventMap = builder.userEventMap;
    }

    public static class UserBuilder {

        private final String userId;
        private String userName = DEFAULT_USER_NAME;
        private String userOccupation = DEFAULT_USER_OCCUPATION;
        private SkillSet userSkillSet;
        private HashMap<String, String> userEventMap;

        public UserBuilder(String userId) {
            this.userId = userId;
            this.userSkillSet = new SkillSet();
            this.userEventMap = new HashMap<>();
        }

        public UserBuilder withUserName(String userName) {
            this.userName = userName;
            return this;
        }

        public UserBuilder withUserOccupation(String userOccupation) {
            this.userOccupation = userOccupation;
            return this;
        }

        public UserBuilder withUserEvent(String userEvent) {
            this.userEventMap.put(userEvent, Group.EMPTY_GROUP_ID);
            return this;
        }

        public UserBuilder withUserEventMap(HashMap<String, String> userEventMap) {
            this.userEventMap = userEventMap;
            return this;
        }

        public UserBuilder withUserSkillSet(SkillSet userSkillSet) {
            this.userSkillSet = userSkillSet;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserOccuption(String userOccupation) {
        this.userOccupation = userOccupation;
    }

    public String getUserOccupation() {
        return this.userOccupation;
    }

    public void addSkill(String skill) {
        this.userSkillSet.addSkill(skill);
    }

    public void removeSkill(String skill) {
        this.userSkillSet.removeSkill(skill);
    }

    public SkillSet getUserSkillSet() {
        return this.userSkillSet;
    }

    public void addEvent(String eventId) {
        this.userEventMap.put(eventId, Group.EMPTY_GROUP_ID);
    }

    public void removeEvent(String eventId) {
        this.userEventMap.remove(eventId);
    }

    public void addGroup(String groupId, String eventId) {
        this.userEventMap.replace(eventId, groupId);
    }

    public void removeGroup(String groupId, String eventId) {
        if (this.userEventMap.get(eventId) == groupId) {
            this.userEventMap.replace(eventId, null);
        }
    }

    public HashMap<String, String> getUserEventMap() {
        return this.userEventMap;
    }

    public Map<String, AttributeValue> eventMapToAttributeValue() {
        HashMap<String, AttributeValue> attributeValueMap = new HashMap<>();

        Set<String> keySet = this.userEventMap.keySet();
        for (String key : keySet) {
            attributeValueMap.put(key, new AttributeValue()
                .withS(userEventMap.get(key)));
        }

        return attributeValueMap;
    }

    public static HashMap<String, String> extractUserEventMap(Map<String, AttributeValue> attributeValueMap) {
        HashMap<String, String> userEventMap = new HashMap<>();

        Set<String> eventKeys = attributeValueMap.keySet();
        for (String key : eventKeys) {
            userEventMap.put(key, attributeValueMap.get(key).getS());
        }

        return userEventMap;
    }

}
