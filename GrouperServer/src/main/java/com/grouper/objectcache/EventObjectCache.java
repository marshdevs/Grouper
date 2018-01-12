package com.grouper.objectcache;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.dynamodbv2.model.*;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.grouper.models.Event;
import com.grouper.models.Message;
import com.grouper.service.GrouperServiceApplication;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EventObjectCache {

    private static int MAXIMUM_SIZE = 10;

    private static final String EVENT_OBJECT_KEY = "Event";
    private static final String EVENT_ID_KEY = "eventId";
    private static final String EVENT_NAME_KEY = "eventName";
    private static final String EVENT_DATE_KEY = "eventDate";
    private static final String EVENT_LOCATION_KEY = "eventLocation";
    private static final String EVENT_DESCRIPTION_KEY = "eventDescription";
    private static final String EVENT_GROUPS_KEY = "eventGroups";
    private static final String EVENT_USERS_KEY = "eventUsers";

    private static final String EVENT_PROJECTION_EXPRESSION = "eventId, eventName, eventDate, eventLocation, " +
        "eventDescription, eventGroups, eventUsers";
    private static final String EVENT_UPDATE_EXPRESSION = "set eventName=:val1, set eventDate=:val2, set " +
        "eventLocation=:val3, set eventDescription=:val4, set eventGroups=:val5, set eventUsers=:val6";
    private static final String EVENT_TABLE_NAME = "grouper-events";

    private static LoadingCache<String, Event> eventObjectCache;

    public static void init() {

        CacheLoader<String, Event> loader;
        loader = new CacheLoader<String, Event>() {
            @Override
            public Event load(String s) throws Exception {
                try {
                    Map<String, AttributeValue> key = new HashMap<>();

                    key.put(EVENT_ID_KEY, new AttributeValue()
                        .withS(s));
                    GetItemRequest request = new GetItemRequest()
                        .withTableName(EVENT_TABLE_NAME)
                        .withKey(key)
                        .withProjectionExpression(EVENT_PROJECTION_EXPRESSION);

                    Map<String, AttributeValue> result = GrouperServiceApplication.dynamoClient.getItem(request)
                        .getItem();
                    if (result == null) {
                        throw new AmazonServiceException(Message.AWS_GET_FAILURE);
                    }

                    return new Event.EventBuilder(result.get(EVENT_ID_KEY).getS())
                        .withEventName(result.get(EVENT_NAME_KEY).getS())
                        .withEventDate(new SimpleDateFormat(Event.EVENT_DATE_FORMAT, Locale.ENGLISH)
                            .parse(result.get(EVENT_DATE_KEY)
                                .getS()))
                        .withEventLocation(result.get(EVENT_LOCATION_KEY)
                            .getS())
                        .withEventDescription(result.get(EVENT_DESCRIPTION_KEY)
                            .getS())
                        .withEventGroups(result.get(EVENT_GROUPS_KEY)
                            .getSS())
                        .withEventUsers(result.get(EVENT_USERS_KEY)
                            .getSS())
                        .build();
                } catch (AmazonServiceException ase) {
                    return new Event.EventBuilder(Event.EMPTY_EVENT_ID)
                        .build();
                }

            }
        };

        eventObjectCache = CacheBuilder.newBuilder()
            .maximumSize(MAXIMUM_SIZE)
            .build(loader);
    }

    public Event getObject(String eventId) {

        Event event = eventObjectCache.getUnchecked(eventId);

        int status;
        String description = new String();

        if (event.getEventId() == Event.EMPTY_EVENT_ID) {
            status = Message.DEFAULT_FAILURE_STATUS;
            description = Message.AWS_GET_FAILURE;
            System.err.println(Date.from(Instant.now()).toString() + ": Amazon Service Exception ---- Get event " +
                "failed" +
                ".");
        } else {
            status = Message.DEFAULT_SUCCESS_STATUS;
            description = Message.AWS_GET_SUCCESS;
        }

        logResult(status, description, EVENT_ID_KEY, eventId);
        return event;
    }

    public Message updateObject(Event event) {
        int status = Message.DEFAULT_SUCCESS_STATUS;
        String description = Message.AWS_UPDATE_SUCCESS;

        try {
            Map<String, AttributeValue> key = new HashMap<>();
            Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();

            key.put(EVENT_ID_KEY, new AttributeValue()
                .withS(event.getEventId()));
            expressionAttributeValues.put(":val1", new AttributeValue()
                .withS(event.getEventName()));
            expressionAttributeValues.put(":val2", new AttributeValue()
                .withS(new SimpleDateFormat(Event.EVENT_DATE_FORMAT)
                    .format(event.getEventDate())));
            expressionAttributeValues.put(":val3", new AttributeValue()
                .withS(event.getEventLocation()));
            expressionAttributeValues.put(":val4", new AttributeValue()
                .withS(event.getEventDescription()));
            expressionAttributeValues.put(":val5", new AttributeValue()
                .withSS(event.getEventGroups()));
            expressionAttributeValues.put(":val6", new AttributeValue()
                .withSS(event.getEventUsers()));

            UpdateItemRequest request = new UpdateItemRequest().withTableName(EVENT_TABLE_NAME).withKey(key)
                .withUpdateExpression(EVENT_UPDATE_EXPRESSION).withExpressionAttributeValues(expressionAttributeValues)
                .withReturnValues(ReturnValue.ALL_NEW);

            UpdateItemResult result = GrouperServiceApplication.dynamoClient.updateItem(request);
            eventObjectCache.put(event.getEventId(), event);

        } catch (AmazonServiceException ase) {
            System.err.println(Date.from(Instant.now()).toString() + ": Amazon Service Exception ---- Event update " +
                "failed.");
            System.err.println(ase);
            status = Message.DEFAULT_FAILURE_STATUS;
            description = Message.AWS_UPDATE_FAILURE;
        }

        logResult(status, description, EVENT_OBJECT_KEY, event);

        return new Message.MessageBuilder(status)
            .withDescription(description)
            .withField(EVENT_ID_KEY)
            .withValue(event.getEventId())
            .build();
    }

    public Message putObject(Event event) {
        int status = Message.DEFAULT_SUCCESS_STATUS;
        String description = Message.AWS_PUT_SUCCESS;

        try {
            Map<String, AttributeValue> newItem = new HashMap<>();

            newItem.put(EVENT_ID_KEY, new AttributeValue()
                .withS(event.getEventId()));
            newItem.put(EVENT_NAME_KEY, new AttributeValue()
                .withS(event.getEventName()));
            newItem.put(EVENT_DATE_KEY, new AttributeValue()
                .withS(new SimpleDateFormat(Event.EVENT_DATE_FORMAT)
                    .format(event.getEventDate())));
            newItem.put(EVENT_LOCATION_KEY, new AttributeValue()
                .withS(event.getEventLocation()));
            newItem.put(EVENT_DESCRIPTION_KEY, new AttributeValue()
                .withS(event.getEventDescription()));
            newItem.put(EVENT_GROUPS_KEY, new AttributeValue()
                .withSS(event.getEventGroups()));
            newItem.put(EVENT_USERS_KEY, new AttributeValue()
                .withSS(event.getEventUsers()));

            PutItemRequest request = new PutItemRequest()
                .withTableName(EVENT_TABLE_NAME)
                .withItem(newItem);

            GrouperServiceApplication.dynamoClient.putItem(request);
            eventObjectCache.put(event.getEventId(), event);

        } catch (AmazonServiceException ase) {
            System.err.println(ase);
            System.err.println(Date.from(Instant.now()).toString() + ": Amazon Service Exception ---- Create event " +
                "failed.");
            status = Message.DEFAULT_FAILURE_STATUS;
            description = Message.AWS_PUT_FAILURE;
        }

        logResult(status, description, EVENT_OBJECT_KEY, event);

        return new Message.MessageBuilder(status)
            .withDescription(description)
            .withField(EVENT_ID_KEY)
            .withValue(event.getEventId())
            .build();
    }

    public Message deleteObject(String eventId) {
        int status = Message.DEFAULT_SUCCESS_STATUS;
        String description = Message.AWS_DELETE_SUCCESS;

        Map<String, AttributeValue> key = new HashMap<>();
        key.put(EVENT_ID_KEY, new AttributeValue()
            .withS(eventId));

        DeleteItemRequest request = new DeleteItemRequest()
            .withTableName(EVENT_TABLE_NAME)
            .withKey(key);
        DeleteItemResult result = GrouperServiceApplication.dynamoClient.deleteItem(request);
        eventObjectCache.put(eventId, new Event.EventBuilder(Event.EMPTY_EVENT_ID)
            .build());

        try {

        } catch (AmazonServiceException ase) {
            System.err.println(ase);
            System.err.println(Date.from(Instant.now()).toString() + ": Amazon Service Exception ---- Delete event " +
                "failed.");
            status = Message.DEFAULT_FAILURE_STATUS;
            description = Message.AWS_DELETE_FAILURE;
        }

        logResult(status, description, EVENT_ID_KEY, eventId);

        return new Message.MessageBuilder(status)
            .withDescription(description)
            .withField(EVENT_ID_KEY)
            .withValue(eventId)
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
