package com.example.subscriber.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.subscriber.R;
import com.example.subscriber.adapters.InvoiceAdapter;
import com.example.subscriber.database.AppDatabase;
import com.example.subscriber.models.Invoice;
import java.util.ArrayList;
import java.util.List;

public class InvoiceListActivity extends AppCompatActivity {
    
    private AppDatabase database;
    private ListView invoiceListView;
    private Button addButton;
    private InvoiceAdapter adapter;
    private List<Invoice> invoices;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_list);
        
        database = AppDatabase.getInstance(this);
        
        initializeViews();
        loadInvoices();
        setupListeners();
    }
    
    private void initializeViews() {
        invoiceListView = findViewById(R.id.invoice_list);
        addButton = findViewById(R.id.add_button);
        
        invoices = new ArrayList<>();
        adapter = new InvoiceAdapter(this, invoices);
        invoiceListView.setAdapter(adapter);
    }
    
    private void loadInvoices() {
        invoices.clear();
        invoices.addAll(database.invoiceDao().getAllInvoices());
        adapter.notifyDataSetChanged();
    }
    
    private void setupListeners() {
        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, CreateInvoiceActivity.class);
            startActivityForResult(intent, 1002);
        });
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1002 && resultCode == RESULT_OK) {
            loadInvoices();
        }
    }
}
