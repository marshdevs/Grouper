package com.grouper.requestmodels;

import com.grouper.models.Event;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Create Event Request
 *
 * CreateEventRequest should be a POST request of type (application/json;charset=UTF-8), with the required
 * parameters in the body.
 *
 * These parameters will be provided in the event creation view controller
 */

public class CreateEventRequest {
    /** String name of the event. {eventName: string}
     */
    private String eventName;
    /** String date of the event, in the format "EEEE, d MMM yyyy, h:mm a zzzz" {eventDate: string}
     */
    private Date eventDate;
    /** string location of the event {eventLocation: string} (TODO: use geo instead)
     */
    private String eventLocation;
    /** string description of the event {eventDescription: string}
     */
    private String eventDescription;

    CreateEventRequest() {
        this.eventName = Event.DEFAULT_EVENT_NAME;
        this.eventDate = Event.DEFAULT_EVENT_DATE;
        this.eventLocation = Event.DEFAULT_EVENT_LOCATION;
        this.eventDescription = Event.DEFAULT_EVENT_DESCRIPTION;
    }

    CreateEventRequest(String eventName, Date eventDate, String eventLocation, String eventDescription) {
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.eventLocation = eventLocation;
        this.eventDescription = eventDescription;
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
