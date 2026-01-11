package com.example.subscriber.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.io.Serializable;

@Entity(tableName = "messages")
public class Message implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public int id;
    
    public String content;
    public String type; // sms, whatsapp
    public String recipients; // JSON array of phone numbers
    public String status; // draft, sent, failed
    public String sentDate;
    public int sentBy; // user id
    public long createdAt;
    
    public Message() {
        this.createdAt = System.currentTimeMillis();
        this.status = "draft";
    }
    
    public Message(String content, String type, String recipients) {
        this.content = content;
        this.type = type;
        this.recipients = recipients;
        this.status = "draft";
        this.createdAt = System.currentTimeMillis();
    }
}
