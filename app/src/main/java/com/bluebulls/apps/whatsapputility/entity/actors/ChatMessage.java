package com.bluebulls.apps.whatsapputility.entity.actors;

/**
 * Created by dell on 8/20/2017.
 */

public class ChatMessage {

    private String messageText;
    private String messageUser;
    private boolean isMine = false;

    public ChatMessage(String messageText, String messageUser, boolean isMine) {
        this.messageText = messageText;
        this.messageUser = messageUser;
        this.isMine = isMine;
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