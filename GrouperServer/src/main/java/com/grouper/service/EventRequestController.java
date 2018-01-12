package com.grouper.service;

import com.grouper.models.*;
import com.grouper.requestmodels.CreateEventRequest;
import com.grouper.requestmodels.DeleteEventRequest;
import com.grouper.requestmodels.UpdateEventRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.ArrayList;

@RestController
public class EventRequestController {

    /**
     * Get an event with the provided eventId from the object store. If the object is not currently in the cache, it
     * will be loaded from DynamoDB.
     *
     * <p> -- Request format -- </p>
     * <p>method: GET</p>
     * <p>url: box.grouper.site:8080/getEvent?eventId=00000000</p>
     *
     * @param eventId   string eventId
     * @return Message(status, description, field, value)
     *          status: {200, 400}
     *          description: {AWS_GET_SUCCESS, AWS_GET_FAILURE}
     *          field: {Event, eventId}
     *          value: {JSON Event object, offending eventId}
     */
    @RequestMapping(value="/getEvent", method= RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Message> getEvent(@RequestParam(value = "eventId", defaultValue = "00000000") String
                                                    eventId) {
        Event event = GrouperServiceApplication.eventObjectCache.getObject(eventId);

        if (event.getEventId() == Event.EMPTY_EVENT_ID) {
            return new ResponseEntity<Message>(new Message.MessageBuilder(Message.DEFAULT_FAILURE_STATUS)
                .withDescription(Message.AWS_GET_FAILURE)
                .withField("eventId")
                .withValue(eventId)
                .build(), HttpStatus.OK);
        } else {
            return new ResponseEntity<Message>(new Message.MessageBuilder(Message.DEFAULT_SUCCESS_STATUS)
                .withDescription(Message.AWS_GET_SUCCESS)
                .withField("Event")
                .withValue(event)
                .build(), HttpStatus.OK);
        }
    }

    /**
     * Create an event in DynamoDB with the provided parameters, and add it to the local object store.
     *
     * <p> -- Request format -- </p>
     * <p>method: POST</p>
     * <p>url: box.grouper.site:8080/createEvent</p>
     * <p>body: {eventName: String, eventDate: String, eventLocation: String, eventDescription: String}</p>
     *
     * @param request   CreateEventRequest request
     * @return Message(status, description, field, value)
     *          status: {200, 400}
     *          description: {AWS_PUT_SUCCESS, AWS_PUT_FAILURE}
     *          field: {Event, eventId}
     *          value: {JSON Event Object, offending eventId}
     */
    @RequestMapping(value = "/createEvent", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
        consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ResponseEntity<Message> createEvent(@RequestBody CreateEventRequest request) {

        String eventId = "E" + GrouperServiceApplication.hashids.encode(Instant.now().toEpochMilli());
        Event newEvent = new Event.EventBuilder(eventId)
            .withEventName(request.getEventName())
            .withEventDate(request.getEventDate())
            .withEventLocation(request.getEventLocation())
            .withEventDescription(request.getEventDescription())
            .build();

        Message message = GrouperServiceApplication.eventObjectCache.putObject(newEvent);

        return new ResponseEntity<Message>(message, HttpStatus.OK);
    }

    /**
     * Update an existing event with the given eventId, updates remotely (DynamoDB) and locally.
     *
     * <p> -- Request format -- </p>
     * <p>method: POST</p>
     * <p>url: box.grouper.site:8080/updateEventFields</p>
     * <p>body: {eventId: String, eventName: String, eventDate: String, eventLocation: String, eventDescription:
     * String}</p>
     *
     * @param request   UpdateEventRequest request
     * @return Message(status, description, field, value)
     *          status: {200, 400}
     *          description: {AWS_UPDATE_SUCCESS, AWS_UPDATE_FAILURE}
     *          field: {Event, eventId}
     *          value: {JSON Event Object, offending eventId}
     */
    @RequestMapping(value = "/updateEventFields", method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
        consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ResponseEntity<Message> updateEvent(@RequestBody UpdateEventRequest request) {

        Event currentEvent = GrouperServiceApplication.eventObjectCache.getObject(request.getEventId());

        Event updatedEvent = new Event.EventBuilder(request.getEventId())
            .withEventName(request.getEventName())
            .withEventDate(request.getEventDate())
            .withEventLocation(request.getEventLocation())
            .withEventDescription(request.getEventDescription())
            .withEventGroups(currentEvent.getEventGroups())
            .withEventUsers(currentEvent.getEventUsers())
            .build();

        Message message = GrouperServiceApplication.eventObjectCache.updateObject(updatedEvent);

        return new ResponseEntity<Message>(message, HttpStatus.OK);
    }

    /**
     * Delete an existing event with the given eventId, both remotely and locally. Deletes groups belonging to the
     * event in question, and removes enrolled users.
     *
     * If this operation results in failure, the id of the offending object will be included in the response payload.
     * This response is ugly, but this request is an abnormal one.
     *
     * This request should NOT be made via the Grouper app. Internal data tool only.
     * If an event host wants their event deleted, they will need to submit a request to Grouper admins.
     *
     * <p> -- Request format -- </p>
     * <p>method: DELETE</p>
     * <p>url: box.grouper.site:8080/deleteEvent</p>
     * <p>body: {eventId: String, eventGroups: [String], eventUsers: [String]}</p>
     *
     * @param request   DeleteEventRequest request
     * @return [Message(status, description, field, value)]
     *          status: {200, 400}
     *          description: {AWS_DELETE_SUCCESS, AWS_DELETE_FAILURE}
     *          field: {userId, groupId, eventId}
     *          value: {offending userId, offending groupId, offending eventId}
     */
    @RequestMapping(value = "/deleteEvent", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
        consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ResponseEntity<ArrayList<Message>> deleteGroup(@RequestBody DeleteEventRequest request) {

        // How do we treat our users if someone deletes an Event?
        // SHOULD someone be able to delete an event?
        // Realistically, deletion of events should never happen automatically. Hosts will need to contact Grouper
        // admins to have their event deleted.

        // This request should remove all users and groups from this event. Once groups have been removed, there will
        // be no way to access them, so they should probably just be deleted.

        ArrayList<Message> messages = new ArrayList<>();

        for (String userId : request.getEventUsers()) {
            User updatedUser = GrouperServiceApplication.userObjectCache.getObject(userId);
            updatedUser.removeEvent(request.getEventId());

            messages.add(GrouperServiceApplication.userObjectCache.updateObject(updatedUser));
        }

        for (String groupId : request.getEventGroups()) {
            messages.add(GrouperServiceApplication.groupObjectCache.deleteObject(groupId));
        }

        messages.add(GrouperServiceApplication.eventObjectCache.deleteObject(request.getEventId()));

        return new ResponseEntity<ArrayList<Message>>(messages, HttpStatus.OK);
    }

}
