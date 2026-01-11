package com.example.subscriber.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.subscriber.models.Subscriber;
import java.util.List;

@Dao
public interface SubscriberDao {
    @Insert
    long insert(Subscriber subscriber);
    
    @Update
    void update(Subscriber subscriber);
    
    @Delete
    void delete(Subscriber subscriber);
    
    @Query("SELECT * FROM subscribers ORDER BY name ASC")
    List<Subscriber> getAllSubscribers();
    
    @Query("SELECT * FROM subscribers WHERE id = :id")
    Subscriber getSubscriberById(int id);
    
    @Query("SELECT * FROM subscribers WHERE name LIKE :query OR phone LIKE :query OR meterNumber LIKE :query ORDER BY name ASC")
    List<Subscriber> searchSubscribers(String query);
    
    @Query("DELETE FROM subscribers WHERE id = :id")
    void deleteById(int id);
    
    @Query("SELECT COUNT(*) FROM subscribers")
    int getCount();
}
