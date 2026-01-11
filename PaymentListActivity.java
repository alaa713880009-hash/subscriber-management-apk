package com.example.subscriber.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.subscriber.R;
import com.example.subscriber.database.AppDatabase;
import com.example.subscriber.models.Payment;
import java.util.ArrayList;
import java.util.List;

public class PaymentListActivity extends AppCompatActivity {
    
    private AppDatabase database;
    private ListView paymentListView;
    private Button addButton;
    private List<Payment> payments;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_list);
        
        database = AppDatabase.getInstance(this);
        
        initializeViews();
        loadPayments();
        setupListeners();
    }
    
    private void initializeViews() {
        paymentListView = findViewById(R.id.payment_list);
        addButton = findViewById(R.id.add_button);
        payments = new ArrayList<>();
    }
    
    private void loadPayments() {
        payments.clear();
        payments.addAll(database.paymentDao().getAllPayments());
    }
    
    private void setupListeners() {
        addButton.setOnClickListener(v -> {
            Toast.makeText(this, "إضافة دفعة جديدة", Toast.LENGTH_SHORT).show();
        });
    }
}
