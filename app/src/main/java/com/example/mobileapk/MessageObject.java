package com.example.mobileapk;

import org.bson.types.ObjectId;

import io.realm.RealmObject;

public class MessageObject {

    private ObjectId id = new ObjectId();
    private String send_from, send_to, message_content;

    public MessageObject() {
    }

    public MessageObject(String send_from, String send_to, String message_content) {
        this.send_from = send_from;
        this.send_to = send_to;
        this.message_content = message_content;
    }

    public String getSend_from() {
        return send_from;
    }

    public String getSend_to() {
        return send_to;
    }

    public String getMessage_content() {
        return message_content;
    }

    public void setSend_from(String send_from) {
        this.send_from = send_from;
    }

    public void setSend_to(String send_to) {
        this.send_to = send_to;
    }

    public void setMessage_content(String message_content) {
        this.message_content = message_content;
    }

    @Override
    public String toString() {
        return "MessageObject{" +
                "id=" + id +
                ", send_from='" + send_from + '\'' +
                ", send_to='" + send_to + '\'' +
                ", message_content='" + message_content + '\'' +
                '}';
    }
}
