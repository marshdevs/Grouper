package com.grouper.models;

import com.grouper.objectcache.GroupObjectCache;
import com.grouper.objectcache.UserObjectCache;
import com.grouper.service.GrouperServiceApplication;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Event {

    public static final String EMPTY_EVENT_ID = "00000000";
    public static final String EVENT_DATE_FORMAT = "EEEE, d MMM yyyy, h:mm a zzzz";

    public static final String DEFAULT_EVENT_NAME = "NO_EVENT_NAME";
    public static final Date DEFAULT_EVENT_DATE = Date.from(Instant.now());
    public static final String DEFAULT_EVENT_LOCATION = "NO_EVENT_LOCATION";
    public static final String DEFAULT_EVENT_DESCRIPTION = "NO_EVENT_DESCRIPTION";

    private final String eventId;
    private String eventName;
    private Date eventDate;
    private String eventLocation; // TODO - change to GEO
    private String eventDescription;
    private ArrayList<String> eventGroups;
    private ArrayList<String> eventUsers;

    private Event(EventBuilder builder) {
        this.eventId = builder.eventId;
        this.eventName = builder.eventName;
        this.eventDate = builder.eventDate;
        this.eventLocation = builder.eventLocation;
        this.eventDescription = builder.eventDescription;
        this.eventGroups = builder.eventGroups;
        this.eventUsers = builder.eventUsers;
    }

    public static class EventBuilder {

        private final String eventId;
        private String eventName = DEFAULT_EVENT_NAME;
        private Date eventDate = DEFAULT_EVENT_DATE;
        private String eventLocation = DEFAULT_EVENT_LOCATION; // TODO - change to GEO
        private String eventDescription = DEFAULT_EVENT_DESCRIPTION;
        private ArrayList<String> eventGroups;
        private ArrayList<String> eventUsers;

        public EventBuilder(String eventId) {
            this.eventId = eventId;

            this.eventGroups = new ArrayList<>();
            this.eventUsers = new ArrayList<>();
        }

        public EventBuilder withEventName(String eventName) {
            this.eventName = eventName;
            return this;
        }

        public EventBuilder withEventDate(Date eventDate) {
            this.eventDate = eventDate;
            return this;
        }

        // TODO - change to GEO
        public EventBuilder withEventLocation(String eventLocation) {
            this.eventLocation = eventLocation;
            return this;
        }

        public EventBuilder withEventDescription(String eventDescription) {
            this.eventDescription = eventDescription;
            return this;
        }

        public EventBuilder withEventGroups(List<String> stringList) {
            ArrayList<String> eventGroups = new ArrayList<>();

            for (String groupId : stringList) {
                eventGroups.add(groupId);
            }

            this.eventGroups = eventGroups;
            return this;
        }

        public EventBuilder withEventUsers(List<String> stringList) {
            ArrayList<String> eventUsers = new ArrayList<>();

            for (String userId : stringList) {
                eventUsers.add(userId);
            }

            this.eventUsers = eventUsers;
            return this;
        }

        public Event build() {
            return new Event(this);
        }
    }

    public String getEventId() {
        return this.eventId;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventName() {
        return this.eventName;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public Date getEventDate() {
        return this.eventDate;
    }

    // TODO - change to GEO
    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    // TODO - change to GEO
    public String getEventLocation() {
        return this.eventLocation;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public String getEventDescription() {
        return this.eventDescription;
    }

    public ArrayList<String> getEventGroups() {
        return this.eventGroups;
    }

    public void addGroup(String groupId) {
        this.eventGroups.add(groupId);
    }

    public void removeGroup(String groupId) {
        this.eventGroups.remove(groupId);
    }

    public ArrayList<String> getEventUsers() {
        return this.eventUsers;
    }

    public void addUser(String userId) {
        this.eventGroups.add(userId);
    }

    public void removeUser(String userId) {
        this.eventGroups.remove(userId);
    }

}
