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

    /**
     * Get a group with the provided groupId from the object store. If the object is not currently in the cache, it
     * will be loaded from DynamoDB.
     *
     * <p> -- Request format -- </p>
     * <p>method: GET</p>
     * <p>url: box.grouper.site:8080/getGroup?groupId=00000000</p>
     *
     * @param groupId   string groupId
     * @return Message(status, description, field, value)
     *          status: {200, 400}
     *          description: {AWS_GET_SUCCESS, AWS_GET_FAILURE}
     *          field: {Group, groupId}
     *          value: {JSON Group object, offending groupId}
     */
    @RequestMapping(value="/getGroup", method= RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Message> getGroup(@RequestParam(value = "groupId", defaultValue = "00000000") String
                                                    groupId) {

        Group group = GrouperServiceApplication.groupObjectCache.getObject(groupId);

        if (group.getGroupId() == Group.EMPTY_GROUP_ID) {
            return new ResponseEntity<Message>(new Message.MessageBuilder(Message.DEFAULT_FAILURE_STATUS)
                .withDescription(Message.AWS_GET_FAILURE)
                .withField("groupId")
                .withValue(groupId)
                .build(), HttpStatus.OK);
        } else {
            return new ResponseEntity<Message>(new Message.MessageBuilder(Message.DEFAULT_SUCCESS_STATUS)
                .withDescription(Message.AWS_GET_SUCCESS)
                .withField("Group")
                .withValue(group)
                .build(), HttpStatus.OK);
        }
    }

    /**
     * Create a group in DynamoDB with the provided parameters, and add it to the local object store.
     *
     * If any portions of this request fail, the offending id will be appending to the response payload.
     *
     * <p> -- Request format -- </p>
     * <p>method: POST</p>
     * <p>url: box.grouper.site:8080/createGroup</p>
     * <p>body: {groupName: String, groupType: String, groupDescription: String, groupEventId: String, groupOwnerId:
     *      String, groupSkills: Map(String: Boolean)}</p>
     *
     * @param request   CreateGroupRequest request
     * @return [Message(status, description, field, value)]
     *          status: {200, 400}
     *          description: {AWS_PUT_SUCCESS, AWS_PUT_FAILURE, AWS_UPDATE_SUCCESS, AWS_UPDATE_FAILURE}
     *          field: {Group, groupId}
     *          value: {JSON Group Object, offending groupId}
     */
    @RequestMapping(value = "/createGroup", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
        consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ResponseEntity<ArrayList<Message>> createGroup(@RequestBody CreateGroupRequest request) {

        ArrayList<Message> messages = new ArrayList<>();

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

        messages.add(GrouperServiceApplication.eventObjectCache.updateObject(updatedEvent));
        messages.add(GrouperServiceApplication.userObjectCache.updateObject(updatedUser));
        messages.add(GrouperServiceApplication.groupObjectCache.putObject(newGroup));

        return new ResponseEntity<ArrayList<Message>>(messages, HttpStatus.OK);
    }

    /**
     * Update an existing group with the given groupId, updates remotely (DynamoDB) and locally.
     *
     * <p> -- Request format -- </p>
     * <p>method: POST</p>
     * <p>url: box.grouper.site:8080/updateGroupFields</p>
     * <p>body: {groupId: String, groupName: String, groupType: String, groupDescription: String, groupEventId: String,
     *      groupOwnerId: String}</p>
     *
     * @param request   UpdateGroupRequest request
     * @return Message(status, description, field, value)
     *          status: {200, 400}
     *          description: {AWS_UPDATE_SUCCESS, AWS_UPDATE_FAILURE}
     *          field: {Group, groupId}
     *          value: {JSON Group Object, offending groupId}
     */
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

        Message message = GrouperServiceApplication.groupObjectCache.updateObject(updatedGroup);

        return new ResponseEntity<Message>(message, HttpStatus.OK);
    }

    /**
     * Delete an existing group with the given groupId, both remotely and locally. Deletes groups belonging to the
     * event in question, and removes enrolled users.
     *
     * If this operation results in failure, the id of the offending object will be included in the response payload.
     * This response is ugly, but this request should be an abnormal one.
     *
     * <p>-- Request format --</p>
     * <p>method: DELETE</p>
     * <p>url: box.grouper.site:8080/deleteGroup</p>
     * <p>body: {groupId: String, groupEventId: String, groupUsers: [String]}</p>
     *
     * @param request   DeleteGroupRequest request
     * @return [Message(status, description, field, value)]
     *          status: {200, 400}
     *          description: {AWS_DELETE_SUCCESS, AWS_DELETE_FAILURE, AWS_UPDATE_SUCCESS, AWS_UPDATE_FAILURE}
     *          field: {userId, groupId, eventId}
     *          value: {offending userId, offending groupId, offending eventId}
     */
    @RequestMapping(value = "/deleteGroup", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
        consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ResponseEntity<ArrayList<Message>> deleteGroup(@RequestBody DeleteGroupRequest request) {

        ArrayList<Message> messages = new ArrayList<>();

        Event updatedEvent = GrouperServiceApplication.eventObjectCache.getObject(request.getGroupEventId());
        updatedEvent.removeGroup(request.getGroupId());

        messages.add(GrouperServiceApplication.eventObjectCache.updateObject(updatedEvent));

        for (String userId : request.getGroupUsers()) {
            User updatedUser = GrouperServiceApplication.userObjectCache.getObject(userId);
            updatedUser.removeGroup(request.getGroupId(), request.getGroupEventId());

            messages.add(GrouperServiceApplication.userObjectCache.updateObject(updatedUser));
        }

        messages.add(GrouperServiceApplication.groupObjectCache.deleteObject(request.getGroupId()));

        return new ResponseEntity<ArrayList<Message>>(messages, HttpStatus.OK);
    }

}
