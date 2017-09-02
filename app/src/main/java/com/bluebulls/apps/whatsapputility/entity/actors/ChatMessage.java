package com.bluebulls.apps.whatsapputility.entity.actors;

/**
 * Created by dell on 8/20/2017.
 */

public class ChatMessage {

    private String messageText;
    private ChatUser messageUser;
    private int type;
    public ChatMessage(String messageText, ChatUser messageUser) {
        this.messageText = messageText;
        this.messageUser = messageUser;

    }

    public ChatMessage(String messageText, ChatUser messageUser, int type) {
        this.messageText = messageText;
        this.messageUser = messageUser;
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

    public ChatUser getMessageUser() {
return messageUser;
}

    public void setMessageUser(ChatUser messageUser) {
        this.messageUser = messageUser;
        }
}