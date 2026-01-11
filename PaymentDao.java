package com.example.subscriber.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.subscriber.models.Payment;
import java.util.List;

@Dao
public interface PaymentDao {
    @Insert
    long insert(Payment payment);
    
    @Update
    void update(Payment payment);
    
    @Delete
    void delete(Payment payment);
    
    @Query("SELECT * FROM payments ORDER BY paymentDate DESC")
    List<Payment> getAllPayments();
    
    @Query("SELECT * FROM payments WHERE id = :id")
    Payment getPaymentById(int id);
    
    @Query("SELECT * FROM payments WHERE invoiceId = :invoiceId ORDER BY paymentDate DESC")
    List<Payment> getPaymentsByInvoice(int invoiceId);
    
    @Query("SELECT SUM(amount) FROM payments WHERE invoiceId = :invoiceId")
    double getTotalPaidAmount(int invoiceId);
    
    @Query("DELETE FROM payments WHERE id = :id")
    void deleteById(int id);
    
    @Query("SELECT COUNT(*) FROM payments")
    int getCount();
}
