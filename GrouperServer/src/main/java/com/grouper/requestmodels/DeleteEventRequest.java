package com.grouper.requestmodels;

import com.grouper.models.Event;

import java.util.ArrayList;

public class DeleteEventRequest {

    private String eventId;
    private ArrayList<String> eventGroups;
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
