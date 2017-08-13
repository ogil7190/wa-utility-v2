package com.bluebulls.apps.whatsapputility.entity.actors;

/**
 * Created by ogil on 03/08/17.
 */

public class Event {

    public Event(){}

    public Event(String event_id, String topic, String description, String participants, String user, String time){
        this.event_id = event_id;
        this.topic = topic;
        this.description = description;
        this.user = user;
        this.participants = participants;
        this.time = time;
    }

    private String event_id;
    private String topic;
    private String user;
    private String participants;
    private String description;
    private String time;

    public String getEvent_id() {
        return event_id;
    }

    public void setEvent_id(String event_id) {
        this.event_id = event_id;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getParticipants() {
        return participants;
    }

    public void setParticipants(String participants) {
        this.participants = participants;
    }

    public String getDescriptioin() {
        return description;
    }

    public void setDescriptioin(String descriptioin) {
        this.description = descriptioin;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
