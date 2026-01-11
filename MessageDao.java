package com.example.subscriber.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.subscriber.models.Message;
import java.util.List;

@Dao
public interface MessageDao {
    @Insert
    long insert(Message message);
    
    @Update
    void update(Message message);
    
    @Delete
    void delete(Message message);
    
    @Query("SELECT * FROM messages ORDER BY createdAt DESC")
    List<Message> getAllMessages();
    
    @Query("SELECT * FROM messages WHERE id = :id")
    Message getMessageById(int id);
    
    @Query("SELECT * FROM messages WHERE type = :type ORDER BY createdAt DESC")
    List<Message> getMessagesByType(String type);
    
    @Query("SELECT * FROM messages WHERE status = :status ORDER BY createdAt DESC")
    List<Message> getMessagesByStatus(String status);
    
    @Query("DELETE FROM messages WHERE id = :id")
    void deleteById(int id);
    
    @Query("SELECT COUNT(*) FROM messages")
    int getCount();
}
