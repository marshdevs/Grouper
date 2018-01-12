package com.grouper.service;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.grouper.models.*;
import com.grouper.objectcache.GroupObjectCache;
import com.grouper.objectcache.UserObjectCache;
import com.grouper.requestmodels.CreateUserRequest;

import com.grouper.requestmodels.DeleteUserRequest;
import com.grouper.requestmodels.UpdateUserRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
public class UserRequestController {

    /**
     * Get a user with the provided userId from the object store. If the object is not currently in the cache, it
     * will be loaded from DynamoDB.
     *
     * <p> -- Request format -- </p>
     * <p>method: GET</p>
     * <p>url: box.grouper.site:8080/getUser?userId=00000000</p>
     *
     * @param userId   string userId
     * @return Message(status, description, field, value)
     *          status: {200, 400}
     *          description: {AWS_GET_SUCCESS, AWS_GET_FAILURE}
     *          field: {User, userId}
     *          value: {JSON User object, offending userId}
     */
    @RequestMapping(value="/getUser", method=RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Message> getUser(@RequestParam(value = "userId", defaultValue = "00000000") String userId) {

        User user = GrouperServiceApplication.userObjectCache.getObject(userId);

        if (user.getUserId() == User.EMPTY_USER_ID) {
            return new ResponseEntity<Message>(new Message.MessageBuilder(Message.DEFAULT_FAILURE_STATUS)
                .withDescription(Message.AWS_GET_FAILURE)
                .withField("userId")
                .withValue(userId)
                .build(), HttpStatus.OK);

        } else {
            return new ResponseEntity<Message>(new Message.MessageBuilder(Message.DEFAULT_SUCCESS_STATUS)
                .withDescription(Message.AWS_GET_SUCCESS)
                .withField("User")
                .withValue(user)
                .build(), HttpStatus.OK);
        }
    }

    /**
     * Create a user in DynamoDB with the provided parameters, and add it to the local object store.
     *
     * <p> -- Request format -- </p>
     * <p>method: POST</p>
     * <p>url: box.grouper.site:8080/createUser</p>
     * <p>body: {userName: String, userOccupation: String, userSkills: Map(String, Boolean)}</p>
     *
     * @param request   CreateUserRequest request
     * @return Message(status, description, field, value)
     *          status: {200, 400}
     *          description: {AWS_PUT_SUCCESS, AWS_PUT_FAILURE}
     *          field: {User, userId}
     *          value: {JSON User Object, offending userId}
     */
    @RequestMapping(value = "/createUser", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
        consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ResponseEntity<Message> createUser(@RequestBody CreateUserRequest request) {

        String userId = "U" + GrouperServiceApplication.hashids.encode(Instant.now().toEpochMilli());
        User newUser = new User.UserBuilder(userId)
            .withUserName(request.getUserName())
            .withUserOccupation(request.getUserOccupation())
            .withUserSkillSet(new SkillSet(request.getUserSkills()))
            .build();

        Message message = GrouperServiceApplication.userObjectCache.putObject(newUser);

        return new ResponseEntity<Message>(message, HttpStatus.OK);
    }

    /**
     * Update an existing user with the given userId, updates remotely (DynamoDB) and locally.
     *
     * <p> -- Request format -- </p>
     * <p>method: POST</p>
     * <p>url: box.grouper.site:8080/updateUserFields</p>
     * <p>body: {userId: String, userName: String, userOccupation: String, userSkills: Map(String, Boolean)}</p>
     *
     * @param request   UpdateUserRequest request
     * @return Message(status, description, field, value)
     *          status: {200, 400}
     *          description: {AWS_UPDATE_SUCCESS, AWS_UPDATE_FAILURE}
     *          field: {User, userId}
     *          value: {JSON User Object, offending userId}
     */
    @RequestMapping(value = "/updateUserFields", method = RequestMethod.POST, produces = MediaType
        .APPLICATION_JSON_UTF8_VALUE,
        consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ResponseEntity<Message> updateUserFields(@RequestBody UpdateUserRequest request) {

        User currentUser = GrouperServiceApplication.userObjectCache.getObject(request.getUserId());

        User updatedUser = new User.UserBuilder(request.getUserId())
            .withUserName(request.getUserName())
            .withUserOccupation(request.getUserOccupation())
            .withUserSkillSet(new SkillSet(request.getUserSkills()))
            .withUserEventMap(currentUser.getUserEventMap())
            .build();

        Message message = GrouperServiceApplication.userObjectCache.updateObject(updatedUser);

        return new ResponseEntity<Message>(message, HttpStatus.OK);
    }


    /**
     * Delete an existing user with the given userId, both remotely and locally. Removes user from all events and
     * groups they are enrolled in.
     *
     * If this operation results in failure, the id of the offending object will be included in the response payload.
     * This response is ugly, but this request should be an abnormal one.
     *
     * <p> -- Request format -- </p>
     * <p>method: DELETE</p>
     * url: box.grouper.site:8080/deleteUser
     * body: {userId: String, userEventMap: Map(String: String)}
     *
     * @param request   DeleteUserRequest request
     * @return [Message(status, description, field, value)]
     *          status: {200, 400}
     *          description: {AWS_DELETE_SUCCESS, AWS_DELETE_FAILURE, AWS_UPDATE_SUCCESS, AWS_UPDATE_FAILURE}
     *          field: {userId, groupId, eventId}
     *          value: {offending userId, offending groupId, offending eventId}
     */
    @RequestMapping(value = "/deleteUser", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
        consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ResponseEntity<ArrayList<Message>> deleteUser(@RequestBody DeleteUserRequest request) {

        ArrayList<Message> messages = new ArrayList<>();

        for (Map.Entry<String, String> entry : request.getUserEventMap().entrySet()) {
            Event updatedEvent = GrouperServiceApplication.eventObjectCache.getObject(entry.getKey());
            Group updatedGroup = GrouperServiceApplication.groupObjectCache.getObject(entry.getValue());

            updatedEvent.removeUser(request.getUserId());
            messages.add(GrouperServiceApplication.eventObjectCache.updateObject(updatedEvent));

            updatedGroup.removeUser(request.getUserId());
            messages.add(GrouperServiceApplication.groupObjectCache.updateObject(updatedGroup));
        }

        messages.add(GrouperServiceApplication.userObjectCache.deleteObject(request.getUserId()));

        return new ResponseEntity<ArrayList<Message>>(messages, HttpStatus.OK);
    }

    /**
     * Adds user with the given userId to event with the given eventId
     *
     * If this operation results in failure, the id of the offending object will be included in the response payload.
     *
     * <p> -- Request format -- </p>
     * <p>method: GET</p>
     * <p>url: box.grouper.site:8080/addUserToEvent?userId=00000000&amp;eventId=00000000</p>
     *
     * @param userId   string userId
     * @param eventId  string eventId
     * @return [Message(status, description, field, value)]
     *          status: {200, 400}
     *          description: {AWS_UPDATE_SUCCESS, AWS_UPDATE_FAILURE}
     *          field: {userId, eventId}
     *          value: {offending userId, offending eventId}
     */
    @RequestMapping(value = "/rel/addUserToEvent", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<ArrayList<Message>> addUserToEvent(
        @RequestParam(value = "userId", defaultValue = "00000000") String userId,
        @RequestParam(value = "eventId", defaultValue = "00000000") String eventId) {

        ArrayList<Message> messages = new ArrayList<>();

        User updatedUser = GrouperServiceApplication.userObjectCache.getObject(userId);
        updatedUser.addEvent(eventId);

        Event updatedEvent = GrouperServiceApplication.eventObjectCache.getObject(eventId);
        updatedEvent.addUser(userId);

        messages.add(GrouperServiceApplication.userObjectCache.updateObject(updatedUser));
        messages.add(GrouperServiceApplication.eventObjectCache.updateObject(updatedEvent));

        return new ResponseEntity<ArrayList<Message>>(messages, HttpStatus.OK);
    }

    /**
     * Removes user with the given userId from event with the given eventId
     *
     * If this operation results in failure, the id of the offending object will be included in the response payload.
     *
     * <p>-- Request format --</p>
     * <p>method: GET</p>
     * <p>url: box.grouper.site:8080/removeUserFromEvent?userId=00000000&amp;eventId=00000000</p>
     *
     * @param userId   string userId
     * @param eventId  string eventId
     * @return [Message(status, description, field, value)]
     *          status: {200, 400}
     *          description: {AWS_UPDATE_SUCCESS, AWS_UPDATE_FAILURE}
     *          field: {userId, eventId}
     *          value: {offending userId, offending eventId}
     */
    @RequestMapping(value = "/rel/removeUserFromEvent", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<ArrayList<Message>> removeUserFromEvent(
        @RequestParam(value = "userId", defaultValue = "00000000") String userId,
        @RequestParam(value = "eventId", defaultValue = "00000000") String eventId) {

        ArrayList<Message> messages = new ArrayList<>();

        User updatedUser = GrouperServiceApplication.userObjectCache.getObject(userId);
        updatedUser.addEvent(eventId);

        Event updatedEvent = GrouperServiceApplication.eventObjectCache.getObject(eventId);
        updatedEvent.removeUser(userId);

        messages.add(GrouperServiceApplication.userObjectCache.updateObject(updatedUser));
        messages.add(GrouperServiceApplication.eventObjectCache.updateObject(updatedEvent));

        return new ResponseEntity<ArrayList<Message>>(messages, HttpStatus.OK);
    }

    /**
     * Adds user with the given userId to group with the given groupId. User must be enrolled in this group's
     * event already.
     *
     * If this operation results in failure, the id of the offending object will be included in the response payload.
     *
     * <p> -- Request format -- </p>
     * <p>method: GET</p>
     * <p>url: box.grouper.site:8080/addUserToGroup?userId=00000000&amp;groupId=00000000</p>
     *
     * @param userId   string userId
     * @param groupId  string groupId
     * @return [Message(status, description, field, value)]
     *          status: {200, 400}
     *          description: {AWS_UPDATE_SUCCESS, AWS_UPDATE_FAILURE}
     *          field: {userId, groupId}
     *          value: {offending userId, offending groupId}
     */
    @RequestMapping(value = "/rel/addUserToGroup", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<ArrayList<Message>> addUserToGroup(
        @RequestParam(value = "userId", defaultValue = "00000000") String userId,
        @RequestParam(value = "groupId", defaultValue = "00000000") String groupId){

        ArrayList<Message> messages = new ArrayList<>();

        Group updatedGroup = GrouperServiceApplication.groupObjectCache.getObject(groupId);
        updatedGroup.addUser(userId);

        User updatedUser = GrouperServiceApplication.userObjectCache.getObject(userId);
        updatedUser.addGroup(groupId, updatedGroup.getGroupEvent());

        messages.add(GrouperServiceApplication.groupObjectCache.updateObject(updatedGroup));
        messages.add(GrouperServiceApplication.userObjectCache.updateObject(updatedUser));

        return new ResponseEntity<ArrayList<Message>>(messages, HttpStatus.OK);
    }

    /**
     * Removes user with the given userId from group with the given groupId
     *
     * If this operation results in failure, the id of the offending object will be included in the response payload.
     *
     * <p>-- Request format --</p>
     * <p>method: GET</p>
     * <p>url: box.grouper.site:8080/removeUserFromGroup?userId=00000000&amp;groupId=00000000</p>
     *
     * @param userId   string userId
     * @param groupId  string groupId
     * @return [Message(status, description, field, value)]
     *          status: {200, 400}
     *          description: {AWS_UPDATE_SUCCESS, AWS_UPDATE_FAILURE}
     *          field: {userId, groupId}
     *          value: {offending userId, offending groupId}
     */
    @RequestMapping(value = "/rel/removeUserFromGroup", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<ArrayList<Message>> removeUserFromGroup(
        @RequestParam(value = "userId", defaultValue = "00000000") String userId,
        @RequestParam(value = "groupId", defaultValue = "00000000") String groupId){

        ArrayList<Message> messages = new ArrayList<>();

        Group updatedGroup = GrouperServiceApplication.groupObjectCache.getObject(groupId);
        updatedGroup.removeUser(userId);

        User updatedUser = GrouperServiceApplication.userObjectCache.getObject(userId);
        updatedUser.removeGroup(groupId, updatedGroup.getGroupEvent());

        messages.add(GrouperServiceApplication.groupObjectCache.updateObject(updatedGroup));
        messages.add(GrouperServiceApplication.userObjectCache.updateObject(updatedUser));

        return new ResponseEntity<ArrayList<Message>>(messages, HttpStatus.OK);
    }

}
