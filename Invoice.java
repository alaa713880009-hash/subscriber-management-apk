package com.example.subscriber.models;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import java.io.Serializable;

@Entity(tableName = "invoices",
        foreignKeys = @ForeignKey(entity = Subscriber.class,
                parentColumns = "id",
                childColumns = "subscriberId",
                onDelete = ForeignKey.CASCADE))
public class Invoice implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public int id;
    
    public int subscriberId;
    public String period;
    public double previousReading;
    public double currentReading;
    public double consumption;
    public double amount;
    public String issueDate;
    public String dueDate;
    public String status; // paid, unpaid, partial
    public String notes;
    public long createdAt;
    public long updatedAt;
    
    public Invoice() {
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
        this.status = "unpaid";
    }
    
    public Invoice(int subscriberId, String period, double previousReading, 
                   double currentReading, double amount, String issueDate, String dueDate) {
        this.subscriberId = subscriberId;
        this.period = period;
        this.previousReading = previousReading;
        this.currentReading = currentReading;
        this.consumption = currentReading - previousReading;
        this.amount = amount;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.status = "unpaid";
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }
}
