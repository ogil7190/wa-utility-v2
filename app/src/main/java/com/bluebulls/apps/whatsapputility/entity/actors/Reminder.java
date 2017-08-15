package com.bluebulls.apps.whatsapputility.entity.actors;

/**
 * Created by ogil on 15/08/17.
 */

public class Reminder {

    private int rem_id;
    private String reminder_title;
    private String reminder_desc;
    private String date_time;

    public Reminder() {
    }

    public Reminder(int rem_id, String reminder_title, String reminder_desc, String date_time) {
        this.rem_id = rem_id;
        this.reminder_title = reminder_title;
        this.reminder_desc = reminder_desc;
        this.date_time = date_time;
    }

    public int getRem_id() {
        return rem_id;
    }

    public void setRem_id(int rem_id) {
        this.rem_id = rem_id;
    }

    public String getReminder_title() {
        return reminder_title;
    }

    public void setReminder_title(String reminder_title) {
        this.reminder_title = reminder_title;
    }

    public String getReminder_desc() {
        return reminder_desc;
    }

    public void setReminder_desc(String reminder_desc) {
        this.reminder_desc = reminder_desc;
    }

    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }
}
