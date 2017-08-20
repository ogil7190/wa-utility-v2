package com.bluebulls.apps.whatsapputility.entity.actors;

/**
 * Created by dell on 8/20/2017.
 */

public class ChatMessage {

    private String messageText;
    private String messageUser;

        public ChatMessage(String messageText, String messageUser) {
            this.messageText = messageText;
            this.messageUser = messageUser;
        }

        public ChatMessage(){
}

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

}