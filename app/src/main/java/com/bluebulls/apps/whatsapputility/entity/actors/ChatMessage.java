package com.bluebulls.apps.whatsapputility.entity.actors;

/**
 * Created by dell on 8/20/2017.
 */

public class ChatMessage {

    private String messageText;
    private String messageUser;
    private boolean isMine;
    private int type;
    public ChatMessage(String messageText, String messageUser, boolean isMine) {
        this.messageText = messageText;
        this.messageUser = messageUser;
        this.isMine = isMine;
    }

    public ChatMessage(String messageText, String messageUser, boolean isMine, int type) {
        this.messageText = messageText;
        this.messageUser = messageUser;
        this.isMine = isMine;
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public ChatMessage(){}

    public String getMessageText() {
return messageText;
}

    public void setMessageText(String messageText) {
this.messageText = messageText;
}

    public String getMessageUser() {
return messageUser;
}

    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
        }

    public boolean isMine() {
        return isMine;
    }

    public void setMine(boolean mine) {
        isMine = mine;
    }
}