package com.bluebulls.apps.whatsapputility.entity.actors;

/**
 * Created by ogil on 03/08/17.
 */

public class Poll {

    public Poll(){}

    public Poll(String poll_id, String title, String options, String user, String ans){
        this.poll_id = poll_id;
        this.title = title;
        this.options = options;
        this.user = user;
        this.ans = ans;
    }

    private String poll_id;
    private String title;
    private String user;
    private String options;
    private String ans;

    public String getPoll_id() {
        return poll_id;
    }

    public void setPoll_id(String poll_id) {
        this.poll_id = poll_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public String getAns() {
        return ans;
    }

    public void setAns(String ans) {
        this.ans = ans;
    }
}