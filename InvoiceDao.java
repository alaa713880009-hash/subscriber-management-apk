package com.example.subscriber.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.subscriber.models.Invoice;
import java.util.List;

@Dao
public interface InvoiceDao {
    @Insert
    long insert(Invoice invoice);
    
    @Update
    void update(Invoice invoice);
    
    @Delete
    void delete(Invoice invoice);
    
    @Query("SELECT * FROM invoices ORDER BY issueDate DESC")
    List<Invoice> getAllInvoices();
    
    @Query("SELECT * FROM invoices WHERE id = :id")
    Invoice getInvoiceById(int id);
    
    @Query("SELECT * FROM invoices WHERE subscriberId = :subscriberId ORDER BY issueDate DESC")
    List<Invoice> getInvoicesBySubscriber(int subscriberId);
    
    @Query("SELECT * FROM invoices WHERE status = :status ORDER BY issueDate DESC")
    List<Invoice> getInvoicesByStatus(String status);
    
    @Query("DELETE FROM invoices WHERE id = :id")
    void deleteById(int id);
    
    @Query("SELECT COUNT(*) FROM invoices")
    int getCount();
    
    @Query("SELECT SUM(amount) FROM invoices WHERE subscriberId = :subscriberId")
    double getTotalAmountBySubscriber(int subscriberId);
}
