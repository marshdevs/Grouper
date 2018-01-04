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
import java.util.HashMap;
import java.util.Map;

@RestController
public class UserRequestController {

    @RequestMapping(value="/getUser", method=RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Message> getUser(@RequestParam(value = "userId", defaultValue = "00000000") String userId) {

        User user = GrouperServiceApplication.userObjectCache.getObject(userId);

        return new ResponseEntity<Message>(new Message.MessageBuilder(Message.DEFAULT_SUCCESS_STATUS)
            .withField("User")
            .withValue(user)
            .build(), HttpStatus.OK);
    }

    @RequestMapping(value = "/createUser", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
        consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ResponseEntity<Message> createUser(@RequestBody CreateUserRequest request) {

        String userId = "U" + GrouperServiceApplication.hashids.encode(Instant.now().toEpochMilli());
        User newUser = new User.UserBuilder(userId)
            .withUserName(request.getUserName())
            .withUserOccupation(request.getUserOccupation())
            .withUserSkillSet(new SkillSet(request.getUserSkills()))
            .withUserEvent(request.getUserEventId())
            .build();

        Event updatedEvent = GrouperServiceApplication.eventObjectCache.getObject(request.getUserEventId());
        updatedEvent.addUser(userId);

        GrouperServiceApplication.userObjectCache.putObject(newUser);
        GrouperServiceApplication.eventObjectCache.updateObject(updatedEvent);

        return new ResponseEntity<Message>(new Message.MessageBuilder(Message.DEFAULT_SUCCESS_STATUS)
            .withField("User")
            .withValue(newUser)
            .build(), HttpStatus.OK);
    }

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

        GrouperServiceApplication.userObjectCache.updateObject(updatedUser);

        return new ResponseEntity<Message>(new Message.MessageBuilder(Message.DEFAULT_SUCCESS_STATUS)
            .withField("User")
            .withValue(updatedUser)
            .build(), HttpStatus.OK);
    }

    @RequestMapping(value = "/deleteUser", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
        consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ResponseEntity<Message> deleteUser(@RequestBody DeleteUserRequest request) {

        GrouperServiceApplication.userObjectCache.deleteObject(request.getUserId());

        for (Map.Entry<String, String> entry : request.getUserEventMap().entrySet()) {
            Event updatedEvent = GrouperServiceApplication.eventObjectCache.getObject(entry.getKey());
            Group updatedGroup = GrouperServiceApplication.groupObjectCache.getObject(entry.getValue());

            updatedEvent.removeUser(request.getUserId());
            GrouperServiceApplication.eventObjectCache.updateObject(updatedEvent);
            updatedGroup.removeUser(request.getUserId());
            GrouperServiceApplication.groupObjectCache.updateObject(updatedGroup);
        }

        return new ResponseEntity<Message>(new Message.MessageBuilder(Message.DEFAULT_SUCCESS_STATUS)
            .withField("userId")
            .withValue(request.getUserId())
            .build(), HttpStatus.OK);
    }

    @RequestMapping(value = "/rel/addUserToEvent", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Message> addUserToEvent(
        @RequestParam(value = "userId", defaultValue = "00000000") String userId,
        @RequestParam(value = "eventId", defaultValue = "00000000") String eventId) {

        User updatedUser = GrouperServiceApplication.userObjectCache.getObject(userId);
        updatedUser.addEvent(eventId);

        Event updatedEvent = GrouperServiceApplication.eventObjectCache.getObject(eventId);
        updatedEvent.addUser(userId);

        GrouperServiceApplication.userObjectCache.updateObject(updatedUser);
        GrouperServiceApplication.eventObjectCache.updateObject(updatedEvent);

        return new ResponseEntity<Message>(new Message.MessageBuilder(Message.DEFAULT_SUCCESS_STATUS)
            .build(), HttpStatus.OK);
    }

    @RequestMapping(value = "/rel/removeUserFromEvent", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Message> removeUserFromEvent(
        @RequestParam(value = "userId", defaultValue = "00000000") String userId,
        @RequestParam(value = "eventId", defaultValue = "00000000") String eventId) {

        User updatedUser = GrouperServiceApplication.userObjectCache.getObject(userId);
        updatedUser.addEvent(eventId);

        Event updatedEvent = GrouperServiceApplication.eventObjectCache.getObject(eventId);
        updatedEvent.removeUser(userId);

        GrouperServiceApplication.userObjectCache.updateObject(updatedUser);
        GrouperServiceApplication.eventObjectCache.updateObject(updatedEvent);

        return new ResponseEntity<Message>(new Message.MessageBuilder(Message.DEFAULT_SUCCESS_STATUS)
            .build(), HttpStatus.OK);
    }

    @RequestMapping(value = "/rel/addUserToGroup", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Message> addUserToGroup(
        @RequestParam(value = "userId", defaultValue = "00000000") String userId,
        @RequestParam(value = "groupId", defaultValue = "00000000") String groupId){

        Group updatedGroup = GrouperServiceApplication.groupObjectCache.getObject(groupId);
        updatedGroup.addUser(userId);

        User updatedUser = GrouperServiceApplication.userObjectCache.getObject(userId);
        updatedUser.addGroup(groupId, updatedGroup.getGroupEvent());

        GrouperServiceApplication.groupObjectCache.updateObject(updatedGroup);
        GrouperServiceApplication.userObjectCache.updateObject(updatedUser);

        return new ResponseEntity<Message>(new Message.MessageBuilder(Message.DEFAULT_SUCCESS_STATUS)
            .build(), HttpStatus.OK);
    }

    @RequestMapping(value = "/rel/removeUserFromGroup", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Message> removeUserFromGroup(
        @RequestParam(value = "userId", defaultValue = "00000000") String userId,
        @RequestParam(value = "groupId", defaultValue = "00000000") String groupId){

        Group updatedGroup = GrouperServiceApplication.groupObjectCache.getObject(groupId);
        updatedGroup.removeUser(userId);

        User updatedUser = GrouperServiceApplication.userObjectCache.getObject(userId);
        updatedUser.removeGroup(groupId, updatedGroup.getGroupEvent());

        GrouperServiceApplication.groupObjectCache.updateObject(updatedGroup);
        GrouperServiceApplication.userObjectCache.updateObject(updatedUser);

        return new ResponseEntity<Message>(new Message.MessageBuilder(Message.DEFAULT_SUCCESS_STATUS)
            .build(), HttpStatus.OK);
    }

}
