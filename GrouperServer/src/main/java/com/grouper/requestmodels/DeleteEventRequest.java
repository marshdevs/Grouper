package com.grouper.requestmodels;

import com.grouper.models.Event;

import java.util.ArrayList;

/**
 * Delete Event Request
 *
 * DeleteEventRequest should be a DELETE request of type (application/json;charset=UTF-8), with the required
 * parameters in the body.
 *
 * This request realistically should NEVER be sent by the client, unless an event was accidentally created.
 * Instead, this request will likely be sent internally, with the internal data tool on grouper.site
 */
public class DeleteEventRequest {

    /** String eventId to be deleted {eventId: string}
     */
    private String eventId;
    /** [String] list of groupIds belonging to this event {eventGroups: [String]}
     */
    private ArrayList<String> eventGroups;
    /** [String] list of userIds belonging to this event {eventUsers: [String]}
     */
    private ArrayList<String> eventUsers;

    DeleteEventRequest() {
        this.eventId = Event.EMPTY_EVENT_ID;
        this.eventGroups = new ArrayList<>();
        this.eventUsers = new ArrayList<>();
    }

    DeleteEventRequest(String eventId, ArrayList<String> eventGroups, ArrayList<String> eventUsers) {
        this.eventId = eventId;
        this.eventGroups = eventGroups;
        this.eventUsers = eventUsers;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventGroups(ArrayList<String> eventGroups) {
        this.eventGroups = eventGroups;
    }

    public ArrayList<String> getEventGroups() {
        return eventGroups;
    }

    public void setEventUsers(ArrayList<String> eventUsers) {
        this.eventUsers = eventUsers;
    }

    public ArrayList<String> getEventUsers() {
        return eventUsers;
    }

}
