package com.grouper.objectcache;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.dynamodbv2.model.*;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.CacheStats;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.grouper.models.Message;
import com.grouper.models.SkillSet;
import com.grouper.models.User;
import com.grouper.service.GrouperServiceApplication;

import javax.validation.constraints.Null;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;

public class UserObjectCache {

    public static final int MAXIMUM_CACHE_SIZE = 1000;

    private static final String USER_OBJECT_KEY = "User";
    private static final String USER_ID_KEY = "userId";
    private static final String USER_NAME_KEY = "userName";
    private static final String USER_OCCUPATION_KEY = "userOccupation";
    private static final String USER_SKILLSET_KEY = "userSkillSet";
    private static final String USER_EVENTMAP_KEY = "userEventMap";

    private static final String USER_TABLE_NAME = "grouper-users";
    private static final String USER_PROJECTION_EXPRESSION = "userId, userName, userOccupation, userSkillSet," +
        "userEventMap";
    private static final String USER_UPDATE_EXPRESSION = "set userName=:val1, set userOccupation=:val2, set " +
        "userSkillSet=:val3, set userEventMap=:val4";

    private static LoadingCache<String, User> userObjectCache;

    public static void init() {

        CacheLoader<String, User> loader;
        loader = new CacheLoader<String, User>() {
            @Override
            public User load(String s) throws Exception {
                try {
                    Map<String, AttributeValue> key = new HashMap<>();

                    key.put(USER_ID_KEY, new AttributeValue()
                        .withS(s));
                    GetItemRequest request = new GetItemRequest()
                        .withTableName(USER_TABLE_NAME)
                        .withKey(key)
                        .withProjectionExpression(USER_PROJECTION_EXPRESSION);

                    Map<String, AttributeValue> result = GrouperServiceApplication.dynamoClient.getItem(request)
                        .getItem();
                    if (result == null) {
                        throw new AmazonServiceException(Message.AWS_GET_FAILURE);
                    }

                    return new User.UserBuilder(result.get(USER_ID_KEY).getS())
                        .withUserName(result.get(USER_NAME_KEY)
                            .getS())
                        .withUserOccupation(result.get(USER_OCCUPATION_KEY)
                            .getS())
                        .withUserSkillSet(new SkillSet(SkillSet.extractSkillSet(result.get(USER_SKILLSET_KEY)
                            .getM())))
                        .withUserEventMap(User.extractUserEventMap(result.get(USER_EVENTMAP_KEY)
                            .getM()))
                        .build();
                } catch (AmazonServiceException ase) {
                    System.err.println(ase);
                    return new User.UserBuilder(User.EMPTY_USER_ID)
                        .build();
                }

            }
        };

        userObjectCache = CacheBuilder.newBuilder()
            .maximumSize(MAXIMUM_CACHE_SIZE)
            .build(loader);
    }

    public User getObject(String userId) {
        User user = userObjectCache.getUnchecked(userId);
        int status = Message.DEFAULT_SUCCESS_STATUS;
        String description = Message.AWS_GET_SUCCESS;

        if (user.getUserId() == User.EMPTY_USER_ID) {
            status = Message.DEFAULT_FAILURE_STATUS;
            description = Message.AWS_GET_FAILURE;
            System.err.println(Date.from(Instant.now()).toString() + ": Amazon Service Exception ---- Get user failed" +
                ".");
        }

        logResult(status, description, USER_ID_KEY, userId);
        return user;
    }

    public void updateObject(User user) {
        int status = Message.DEFAULT_SUCCESS_STATUS;
        String description = Message.AWS_UPDATE_SUCCESS;

        try {
            Map<String, AttributeValue> key = new HashMap<>();
            Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();

            key.put(USER_ID_KEY, new AttributeValue()
                .withS(user.getUserId()));
            expressionAttributeValues.put(":val1", new AttributeValue()
                .withS(user.getUserName()));
            expressionAttributeValues.put(":val2", new AttributeValue()
                .withS(user.getUserOccupation()));
            expressionAttributeValues.put(":val3", new AttributeValue()
                .withM(user.getUserSkillSet()
                    .toAttributeValue()));
            expressionAttributeValues.put(":val4", new AttributeValue()
                .withM(user.eventMapToAttributeValue()));

            UpdateItemRequest request = new UpdateItemRequest().withTableName(USER_TABLE_NAME).withKey(key)
                .withUpdateExpression(USER_UPDATE_EXPRESSION).withExpressionAttributeValues(expressionAttributeValues)
                .withReturnValues(ReturnValue.ALL_NEW);

            UpdateItemResult result = GrouperServiceApplication.dynamoClient.updateItem(request);
            userObjectCache.put(user.getUserId(), user);

        } catch (AmazonServiceException ase) {
            System.err.println(Date.from(Instant.now()).toString() + ": Amazon Service Exception ---- User update " +
                "failed.");
            System.err.println(ase);
            status = Message.DEFAULT_FAILURE_STATUS;
            description = Message.AWS_UPDATE_FAILURE;
        }

        logResult(status, description, USER_OBJECT_KEY, user);
    }

    public void putObject(User user) {
        int status = Message.DEFAULT_SUCCESS_STATUS;
        String description = Message.AWS_PUT_SUCCESS;

        try {
            Map<String, AttributeValue> newItem = new HashMap<>();

            newItem.put(USER_ID_KEY, new AttributeValue()
                .withS(user.getUserId()));
            newItem.put(USER_NAME_KEY, new AttributeValue()
                .withS(user.getUserName()));
            newItem.put(USER_OCCUPATION_KEY, new AttributeValue()
                .withS(user.getUserOccupation()));
            newItem.put(USER_SKILLSET_KEY, new AttributeValue()
                .withM(user.getUserSkillSet()
                    .toAttributeValue()));
            newItem.put(USER_EVENTMAP_KEY, new AttributeValue()
                .withM(user.eventMapToAttributeValue()));

            PutItemRequest request = new PutItemRequest()
                .withTableName(USER_TABLE_NAME)
                .withItem(newItem);

            GrouperServiceApplication.dynamoClient.putItem(request);
            userObjectCache.put(user.getUserId(), user);

        } catch (AmazonServiceException ase) {
            System.err.println(ase);
            System.err.println(Date.from(Instant.now()).toString() + ": Amazon Service Exception ---- Create user " +
                "failed.");
            status = Message.DEFAULT_FAILURE_STATUS;
            description = Message.AWS_PUT_FAILURE;
        }

        logResult(status, description, USER_OBJECT_KEY, user);
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
