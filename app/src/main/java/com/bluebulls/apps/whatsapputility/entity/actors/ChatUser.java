package com.bluebulls.apps.whatsapputility.entity.actors;

/**
 * Created by ogil on 23/08/17.
 */

public class ChatUser {
    private String name;
    private String socket_id;

    public ChatUser() { }

    public ChatUser(String name , String socket_id){
        this.socket_id = socket_id;
        this.name  = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSocket_id() {
        return socket_id;
    }

    public void setSocket_id(String socket_id) {
        this.socket_id = socket_id;
    }
}
