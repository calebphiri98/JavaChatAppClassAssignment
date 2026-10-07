package org.example.chatapp;

import java.time.LocalDateTime;

public class Message implements Comparable {
    LocalDateTime date;
    String text;

    public Message() {
    }

    public Message(LocalDateTime date, String text) {
        this.date = date;
        this.text = text;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return date.toString() + " " + this.getText();
    }

    @Override
    public int compareTo(Object o) {
        Message other = (Message) o;
        return this.date.compareTo(other.date);
    }
}
