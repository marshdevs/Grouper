package com.grouper.requestmodels;

import com.grouper.models.Event;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class UpdateEventRequest {

    private String eventId;
    private String eventName;
    private Date eventDate;
    private String eventLocation;
    private String eventDescription;

    UpdateEventRequest() {
        this.eventId = Event.EMPTY_EVENT_ID;
        this.eventName = Event.DEFAULT_EVENT_NAME;
        this.eventDate = Event.DEFAULT_EVENT_DATE;
        this.eventLocation = Event.DEFAULT_EVENT_LOCATION;
        this.eventDescription = Event.DEFAULT_EVENT_DESCRIPTION;
    }

    UpdateEventRequest(String eventId, String eventName, Date eventDate, String eventLocation, String
        eventDescription, ArrayList<String> eventGroups, ArrayList<String> eventUsers) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.eventLocation = eventLocation;
        this.eventDescription = eventDescription;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventDate(String eventDate) {
        SimpleDateFormat format = new SimpleDateFormat(Event.EVENT_DATE_FORMAT, Locale.ENGLISH);

        try {
            this.eventDate = format.parse(eventDate);
        } catch (java.text.ParseException pe) {
            System.err.println(pe);
            this.eventDate = Event.DEFAULT_EVENT_DATE;
        }
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public String getEventDescription() {
        return eventDescription;
    }

}
