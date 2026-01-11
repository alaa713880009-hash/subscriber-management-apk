package com.example.subscriber.models;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import java.io.Serializable;

@Entity(tableName = "payments",
        foreignKeys = @ForeignKey(entity = Invoice.class,
                parentColumns = "id",
                childColumns = "invoiceId",
                onDelete = ForeignKey.CASCADE))
public class Payment implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public int id;
    
    public int invoiceId;
    public double amount;
    public String paymentMethod; // cash, check, transfer, etc.
    public String paymentDate;
    public String notes;
    public long createdAt;
    
    public Payment() {
        this.createdAt = System.currentTimeMillis();
    }
    
    public Payment(int invoiceId, double amount, String paymentMethod, String paymentDate) {
        this.invoiceId = invoiceId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.paymentDate = paymentDate;
        this.createdAt = System.currentTimeMillis();
    }
}
