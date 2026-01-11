package com.example.subscriber.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.io.Serializable;

@Entity(tableName = "subscribers")
public class Subscriber implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public int id;
    
    public String name;
    public String phone;
    public String address;
    public String meterNumber;
    public double tariff;
    public String accountNumber;
    public long createdAt;
    public long updatedAt;
    
    public Subscriber() {
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }
    
    public Subscriber(String name, String phone, String address, String meterNumber, double tariff) {
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.meterNumber = meterNumber;
        this.tariff = tariff;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }
    
    @Override
    public String toString() {
        return name + " - " + meterNumber;
    }
}
