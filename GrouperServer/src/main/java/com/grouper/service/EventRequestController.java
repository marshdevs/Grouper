package com.grouper.service;

import com.grouper.models.Event;
import com.grouper.models.Group;
import com.grouper.models.Message;
import com.grouper.models.SkillSet;
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

    @RequestMapping(value="/getEvent", method= RequestMethod.GET)
    @ResponseBody
    public Message getEvent(@RequestParam(value = "eventId", defaultValue = "00000000") String eventId) {

        Event event = GrouperServiceApplication.eventObjectCache.getObject(eventId);

        return new Message.MessageBuilder(Message.DEFAULT_SUCCESS_STATUS)
            .withField("Event")
            .withValue(event)
            .build();
    }

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

        GrouperServiceApplication.eventObjectCache.putObject(newEvent);

        return new ResponseEntity<Message>(new Message.MessageBuilder(Message.DEFAULT_SUCCESS_STATUS)
            .withField("Event")
            .withValue(newEvent)
            .build(), HttpStatus.OK);
    }

    @RequestMapping(value = "/updateEvent", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
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

        GrouperServiceApplication.eventObjectCache.updateObject(updatedEvent);

        return new ResponseEntity<Message>(new Message.MessageBuilder(Message.DEFAULT_SUCCESS_STATUS)
            .withField("Event")
            .withValue(updatedEvent)
            .build(), HttpStatus.OK);
    }

    @RequestMapping(value = "/deleteEvent", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
        consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ResponseEntity<Message> deleteGroup(@RequestBody DeleteEventRequest request) {

        GrouperServiceApplication.eventObjectCache.deleteObject(request.getEventId());

        // How do we treat our users if someone deletes an Event?
        // SHOULD someone be able to delete an event?

        return new ResponseEntity<Message>(new Message.MessageBuilder(Message.DEFAULT_SUCCESS_STATUS)
            .withField("eventId")
            .withValue(request.getEventId())
            .build(), HttpStatus.OK);
    }

}
