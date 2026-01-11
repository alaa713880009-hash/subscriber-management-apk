package com.example.subscriber.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.subscriber.models.User;
import java.util.List;

@Dao
public interface UserDao {
    @Insert
    long insert(User user);
    
    @Update
    void update(User user);
    
    @Delete
    void delete(User user);
    
    @Query("SELECT * FROM users WHERE isActive = 1 ORDER BY fullName ASC")
    List<User> getAllUsers();
    
    @Query("SELECT * FROM users WHERE id = :id")
    User getUserById(int id);
    
    @Query("SELECT * FROM users WHERE username = :username")
    User getUserByUsername(String username);
    
    @Query("SELECT * FROM users WHERE username = :username AND password = :password")
    User authenticate(String username, String password);
    
    @Query("DELETE FROM users WHERE id = :id")
    void deleteById(int id);
    
    @Query("SELECT COUNT(*) FROM users WHERE isActive = 1")
    int getCount();
}
