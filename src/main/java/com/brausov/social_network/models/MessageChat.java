package com.brausov.social_network.models;

import javax.persistence.*;
import java.sql.Time;

@Entity
public class MessageChat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne
    private User from;

    @OneToOne
    private User recipient;
    private String message;
    private Time time;

    public MessageChat() {
    }

    public MessageChat(User from, User recipient, String message, Time time) {
        this.from = from;
        this.recipient = recipient;
        this.message = message;
        this.time = time;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getFrom() {
        return from;
    }

    public void setFrom(User from) {
        this.from = from;
    }

    public User getRecipient() {
        return recipient;
    }

    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\": \"" + id + "\"" +
                ", \"from\": \"" + from.getLastName() + " " + from.getFirstName() + "\"" +
                ", \"recipient\": \"" + recipient.getLastName() + " " + recipient.getFirstName() + "\"" +
                ", \"message\": \"" + message + '\"' +
                ", \"hours\": \"" + time.getHours() + "\"" +
                ", \"minutes\": \"" + time.getMinutes() + "\"" +
                ", \"seconds\": \"" + time.getSeconds() + "\"" +
                '}';
    }
}
