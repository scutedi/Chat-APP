package com.example.guiex1.domain;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.List;

public class Message extends Entity<Long> {
    private Utilizator from;
    private List<Utilizator> to;
    private String message;
    private LocalDateTime data;
    private Message reply;

    public Message() {}

    public Message(Utilizator from, List<Utilizator> to, String message , LocalDateTime data , Message reply) {
        this.from = from;
        this.to = to;
        this.message = message;
        this.data = data;
        this.reply = reply;
    }

    public Utilizator getFrom() {
        return from;
    }

    public List<Utilizator> getTo() {
        return to;
    }

    public void setTo(List<Utilizator> to) {
        this.to = to;
    }

    public void addTo(Utilizator u){
        this.to.add(u);
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getData() {
        return data;
    }

    public Message getReply() {
        return reply;
    }

    public void setReply(Message msg) {
        reply = msg;
    }

    @Override
    public String toString() {
        String baseMessage = "";
        if (reply != null) {
            baseMessage = "Răspuns la: " + reply.getFrom().getFirstName() + ": " + reply.getMessage() + "\n" + reply.getData().getHour() + ":" + reply.getData().getMinute() + "\n\n";
        }
        return baseMessage + from.getUsername() + ": " + message + " " + "\n" + data.getHour() + ":" + data.getMinute();
    }

    public LocalDateTime getDate() {
        return data;
    }

}
