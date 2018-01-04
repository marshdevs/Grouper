package com.grouper.service;

import com.grouper.models.*;
import com.grouper.requestmodels.CreateGroupRequest;
import com.grouper.requestmodels.DeleteGroupRequest;
import com.grouper.requestmodels.UpdateGroupRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.ArrayList;

@RestController
public class GroupRequestController {

    @RequestMapping(value="/getGroup", method= RequestMethod.GET)
    @ResponseBody
    public Message getGroup(@RequestParam(value = "groupId", defaultValue = "00000000") String groupId) {

        Group group = GrouperServiceApplication.groupObjectCache.getObject(groupId);

        return new Message.MessageBuilder(Message.DEFAULT_SUCCESS_STATUS)
            .withField("Group")
            .withValue(group)
            .build();
    }

    @RequestMapping(value = "/createGroup", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
        consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ResponseEntity<Message> createGroup(@RequestBody CreateGroupRequest request) {

        String groupId = "G" + GrouperServiceApplication.hashids.encode(Instant.now().toEpochMilli());
        Group newGroup = new Group.GroupBuilder(groupId)
            .withGroupName(request.getGroupName())
            .withGroupType(request.getGroupType())
            .withGroupDescription(request.getGroupDescription())
            .withGroupEvent(request.getGroupEventId())
            .withGroupOwner(request.getGroupOwnerId())
            .withGroupSkillSet(new SkillSet(request.getGroupSkills()))
            .build();

        Event updatedEvent = GrouperServiceApplication.eventObjectCache.getObject(request.getGroupEventId());
        updatedEvent.addGroup(groupId);
        User updatedUser = GrouperServiceApplication.userObjectCache.getObject(request.getGroupOwnerId());
        updatedUser.addGroup(request.getGroupEventId(), request.getGroupOwnerId());

        GrouperServiceApplication.eventObjectCache.updateObject(updatedEvent);
        GrouperServiceApplication.userObjectCache.updateObject(updatedUser);
        GrouperServiceApplication.groupObjectCache.putObject(newGroup);

        return new ResponseEntity<Message>(new Message.MessageBuilder(Message.DEFAULT_SUCCESS_STATUS)
            .withField("Group")
            .withValue(newGroup)
            .build(), HttpStatus.OK);
    }

    @RequestMapping(value = "/updateGroupFields", method = RequestMethod.POST, produces = MediaType
        .APPLICATION_JSON_UTF8_VALUE,
        consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ResponseEntity<Message> updateGroupFields(@RequestBody UpdateGroupRequest request) {

        Group currentGroup = GrouperServiceApplication.groupObjectCache.getObject(request.getGroupId());

        Group updatedGroup = new Group.GroupBuilder(request.getGroupId())
            .withGroupName(request.getGroupName())
            .withGroupType(request.getGroupType())
            .withGroupDescription(request.getGroupDescription())
            .withGroupEvent(request.getGroupEventId())
            .withGroupOwner(request.getGroupOwnerId())
            .withGroupSkillSet(new SkillSet(request.getGroupSkills()))
            .withGroupUsers(currentGroup.getGroupUsers())
            .build();

        GrouperServiceApplication.groupObjectCache.updateObject(updatedGroup);

        return new ResponseEntity<Message>(new Message.MessageBuilder(Message.DEFAULT_SUCCESS_STATUS)
            .withField("Group")
            .withValue(updatedGroup)
            .build(), HttpStatus.OK);
    }

    @RequestMapping(value = "/deleteGroup", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
        consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ResponseEntity<Message> deleteGroup(@RequestBody DeleteGroupRequest request) {

        GrouperServiceApplication.groupObjectCache.deleteObject(request.getGroupId());

        Event updatedEvent = GrouperServiceApplication.eventObjectCache.getObject(request.getGroupEventId());
        updatedEvent.removeGroup(request.getGroupId());

        GrouperServiceApplication.eventObjectCache.updateObject(updatedEvent);

        for (String userId : request.getGroupUsers()) {
            User updatedUser = GrouperServiceApplication.userObjectCache.getObject(userId);
            updatedUser.removeGroup(request.getGroupId(), request.getGroupEventId());

            GrouperServiceApplication.userObjectCache.updateObject(updatedUser);
        }

        return new ResponseEntity<Message>(new Message.MessageBuilder(Message.DEFAULT_SUCCESS_STATUS)
            .withField("groupId")
            .withValue(request.getGroupId())
            .build(), HttpStatus.OK);
    }

}
