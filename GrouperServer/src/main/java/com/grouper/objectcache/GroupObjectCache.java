package com.grouper.objectcache;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.dynamodbv2.model.*;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.grouper.models.Group;
import com.grouper.models.Message;
import com.grouper.models.SkillSet;
import com.grouper.service.GrouperServiceApplication;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class GroupObjectCache {

    private static final int MAXIMUM_SIZE = 100;

    private static final String GROUP_OBJECT_KEY = "Group";
    private static final String GROUP_ID_KEY = "groupId";
    private static final String GROUP_NAME_KEY = "groupName";
    private static final String GROUP_TYPE_KEY = "groupType";
    private static final String GROUP_DESCRIPTION_KEY = "groupDescription";
    private static final String GROUP_EVENT_KEY = "groupEvent";
    private static final String GROUP_OWNER_KEY = "groupOwner";
    private static final String GROUP_SKILLSET_KEY = "groupSkillSet";
    private static final String GROUP_USERS_KEY = "groupUsers";

    private static final String GROUP_PROJECTION_EXPRESSION = "groupId, groupName, groupType, groupDescription, " +
        "groupEvent, groupOwner, groupSkillSet, groupUsers";
    private static final String GROUP_UPDATE_EXPRESSION = "set groupName=:val1, set groupType=:val2, set " +
        "groupDescription=:val3, set groupEvent=:val4, set groupOwner=:val5, set groupSkillSet=:val6, set " +
        "groupUsers=:val7";
    private static final String GROUP_TABLE_NAME = "grouper-groups";

    private static LoadingCache<String, Group> groupObjectCache;

    public static void init() {

        CacheLoader<String, Group> loader;
        loader = new CacheLoader<String, Group>() {
            @Override
            public Group load(String s) throws Exception {
                try {
                    Map<String, AttributeValue> key = new HashMap<>();

                    key.put(GROUP_ID_KEY, new AttributeValue()
                        .withS(s));
                    GetItemRequest request = new GetItemRequest()
                        .withTableName(GROUP_TABLE_NAME)
                        .withKey(key)
                        .withProjectionExpression(GROUP_PROJECTION_EXPRESSION);

                    Map<String, AttributeValue> result = GrouperServiceApplication.dynamoClient.getItem(request)
                        .getItem();
                    if (result == null) {
                        throw new AmazonServiceException(Message.AWS_GET_FAILURE);
                    }

                    return new Group.GroupBuilder(result.get(GROUP_ID_KEY).getS())
                        .withGroupName(result.get(GROUP_NAME_KEY)
                            .getS())
                        .withGroupType(result.get(GROUP_TYPE_KEY)
                            .getS())
                        .withGroupDescription(result.get(GROUP_DESCRIPTION_KEY)
                            .getS())
                        .withGroupEvent(result.get(GROUP_EVENT_KEY)
                                .getS())
                        .withGroupOwner(result.get(GROUP_OWNER_KEY)
                            .getS())
                        .withGroupSkillSet(new SkillSet(SkillSet.extractSkillSet(result.get(GROUP_SKILLSET_KEY)
                            .getM())))
                        .withGroupUsers(result.get(GROUP_USERS_KEY)
                            .getSS())
                        .build();
                } catch (AmazonServiceException ase) {
                    return new Group.GroupBuilder(Group.EMPTY_GROUP_ID)
                        .build();
                }

            }
        };

        groupObjectCache = CacheBuilder.newBuilder()
            .maximumSize(MAXIMUM_SIZE)
            .build(loader);
    }

    public Group getObject(String groupId) {

        Group group = groupObjectCache.getUnchecked(groupId);

        int status;
        String description = new String();

        if (group.getGroupId() == Group.EMPTY_GROUP_ID) {
            status = Message.DEFAULT_FAILURE_STATUS;
            description = Message.AWS_GET_FAILURE;
            System.err.println(Date.from(Instant.now()).toString() + ": Amazon Service Exception ---- Get group " +
                "failed" +
                ".");
        } else {
            status = Message.DEFAULT_SUCCESS_STATUS;
            description = Message.AWS_GET_SUCCESS;
        }

        logResult(status, description, GROUP_ID_KEY, groupId);
        return group;
    }

    public Message updateObject(Group group) {
        int status = Message.DEFAULT_SUCCESS_STATUS;
        String description = Message.AWS_UPDATE_SUCCESS;

        try {
            Map<String, AttributeValue> key = new HashMap<>();
            Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();

            key.put(GROUP_ID_KEY, new AttributeValue()
                .withS(group.getGroupId()));
            expressionAttributeValues.put(":val1", new AttributeValue()
                .withS(group.getGroupName()));
            expressionAttributeValues.put(":val2", new AttributeValue()
                .withS(group.getGroupType()));
            expressionAttributeValues.put(":val3", new AttributeValue()
                .withS(group.getGroupDescription()));
            expressionAttributeValues.put(":val4", new AttributeValue()
                .withS(group.getGroupEvent()));
            expressionAttributeValues.put(":val5", new AttributeValue()
                .withS(group.getGroupOwner()));
            expressionAttributeValues.put(":val6", new AttributeValue()
                .withM(group.getGroupSkillSet()
                    .toAttributeValue()));
            expressionAttributeValues.put(":val7", new AttributeValue()
                .withSS(group.getGroupUsers()));

            UpdateItemRequest request = new UpdateItemRequest().withTableName(GROUP_TABLE_NAME).withKey(key)
                .withUpdateExpression(GROUP_UPDATE_EXPRESSION).withExpressionAttributeValues(expressionAttributeValues)
                .withReturnValues(ReturnValue.ALL_NEW);

            UpdateItemResult result = GrouperServiceApplication.dynamoClient.updateItem(request);
            groupObjectCache.put(group.getGroupId(), group);

        } catch (AmazonServiceException ase) {
            System.err.println(Date.from(Instant.now()).toString() + ": Amazon Service Exception ---- Group update " +
                "failed.");
            System.err.println(ase);
            status = Message.DEFAULT_FAILURE_STATUS;
            description = Message.AWS_UPDATE_FAILURE;
        }

        logResult(status, description, GROUP_OBJECT_KEY, group);

        return new Message.MessageBuilder(status)
            .withDescription(description)
            .withField(GROUP_ID_KEY)
            .withValue(group.getGroupId())
            .build();
    }

    public Message putObject(Group group) {
        int status = Message.DEFAULT_SUCCESS_STATUS;
        String description = Message.AWS_PUT_SUCCESS;

        try {
            Map<String, AttributeValue> newItem = new HashMap<>();

            newItem.put(GROUP_ID_KEY, new AttributeValue()
                .withS(group.getGroupId()));
            newItem.put(GROUP_NAME_KEY, new AttributeValue()
                .withS(group.getGroupName()));
            newItem.put(GROUP_TYPE_KEY, new AttributeValue()
                .withS(group.getGroupType()));
            newItem.put(GROUP_DESCRIPTION_KEY, new AttributeValue()
                .withS(group.getGroupDescription()));
            newItem.put(GROUP_EVENT_KEY, new AttributeValue()
                .withS(group.getGroupEvent()));
            newItem.put(GROUP_OWNER_KEY, new AttributeValue()
                .withS(group.getGroupOwner()));
            newItem.put(GROUP_SKILLSET_KEY, new AttributeValue()
                .withM(group.getGroupSkillSet()
                    .toAttributeValue()));
            newItem.put(GROUP_USERS_KEY, new AttributeValue()
                .withSS(group.getGroupUsers()));

            PutItemRequest request = new PutItemRequest()
                .withTableName(GROUP_TABLE_NAME)
                .withItem(newItem);

            GrouperServiceApplication.dynamoClient.putItem(request);
            groupObjectCache.put(group.getGroupId(), group);

        } catch (AmazonServiceException ase) {
            System.err.println(ase);
            System.err.println(Date.from(Instant.now()).toString() + ": Amazon Service Exception ---- Create group " +
                "failed.");
            status = Message.DEFAULT_FAILURE_STATUS;
            description = Message.AWS_PUT_FAILURE;
        }

        logResult(status, description, GROUP_OBJECT_KEY, group);

        return new Message.MessageBuilder(status)
            .withDescription(description)
            .withField(GROUP_ID_KEY)
            .withValue(group.getGroupId())
            .build();
    }

    public Message deleteObject(String groupId) {
        int status = Message.DEFAULT_SUCCESS_STATUS;
        String description = Message.AWS_DELETE_SUCCESS;

        Map<String, AttributeValue> key = new HashMap<>();
        key.put(GROUP_ID_KEY, new AttributeValue()
            .withS(groupId));

        DeleteItemRequest request = new DeleteItemRequest()
            .withTableName(GROUP_TABLE_NAME)
            .withKey(key);
        DeleteItemResult result = GrouperServiceApplication.dynamoClient.deleteItem(request);
        groupObjectCache.put(groupId, new Group.GroupBuilder(Group.EMPTY_GROUP_ID)
            .build());

        try {

        } catch (AmazonServiceException ase) {
            System.err.println(ase);
            System.err.println(Date.from(Instant.now()).toString() + ": Amazon Service Exception ---- Delete group " +
                "failed.");
            status = Message.DEFAULT_FAILURE_STATUS;
            description = Message.AWS_DELETE_FAILURE;
        }

        logResult(status, description, GROUP_ID_KEY, groupId);

        return new Message.MessageBuilder(status)
            .withDescription(description)
            .withField(GROUP_ID_KEY)
            .withValue(groupId)
            .build();
    }

    private void logResult(int status, String description, String field, Object value) {
        System.out.println(new Message.MessageBuilder(status)
            .withDescription(description)
            .withField(field)
            .withValue(value)
            .build()
            .toString());
    }

}
