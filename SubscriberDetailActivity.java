package com.example.subscriber.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.subscriber.R;
import com.example.subscriber.database.AppDatabase;
import com.example.subscriber.models.Subscriber;

public class SubscriberDetailActivity extends AppCompatActivity {
    
    private AppDatabase database;
    private EditText nameInput, phoneInput, addressInput, meterInput, tariffInput;
    private Button saveButton, deleteButton;
    private Subscriber currentSubscriber;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscriber_detail);
        
        database = AppDatabase.getInstance(this);
        
        initializeViews();
        loadSubscriberData();
        setupListeners();
    }
    
    private void initializeViews() {
        nameInput = findViewById(R.id.name_input);
        phoneInput = findViewById(R.id.phone_input);
        addressInput = findViewById(R.id.address_input);
        meterInput = findViewById(R.id.meter_input);
        tariffInput = findViewById(R.id.tariff_input);
        saveButton = findViewById(R.id.save_button);
        deleteButton = findViewById(R.id.delete_button);
    }
    
    private void loadSubscriberData() {
        Subscriber subscriber = (Subscriber) getIntent().getSerializableExtra("subscriber");
        if (subscriber != null) {
            currentSubscriber = subscriber;
            nameInput.setText(subscriber.name);
            phoneInput.setText(subscriber.phone);
            addressInput.setText(subscriber.address);
            meterInput.setText(subscriber.meterNumber);
            tariffInput.setText(String.valueOf(subscriber.tariff));
            deleteButton.setEnabled(true);
        } else {
            currentSubscriber = new Subscriber();
            deleteButton.setEnabled(false);
        }
    }
    
    private void setupListeners() {
        saveButton.setOnClickListener(v -> saveSubscriber());
        deleteButton.setOnClickListener(v -> deleteSubscriber());
    }
    
    private void saveSubscriber() {
        String name = nameInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();
        String address = addressInput.getText().toString().trim();
        String meter = meterInput.getText().toString().trim();
        String tariffStr = tariffInput.getText().toString().trim();
        
        if (name.isEmpty()) {
            Toast.makeText(this, "يرجى إدخال اسم المشترك", Toast.LENGTH_SHORT).show();
            return;
        }
        
        currentSubscriber.name = name;
        currentSubscriber.phone = phone;
        currentSubscriber.address = address;
        currentSubscriber.meterNumber = meter;
        currentSubscriber.tariff = tariffStr.isEmpty() ? 0 : Double.parseDouble(tariffStr);
        currentSubscriber.updatedAt = System.currentTimeMillis();
        
        if (currentSubscriber.id == 0) {
            database.subscriberDao().insert(currentSubscriber);
        } else {
            database.subscriberDao().update(currentSubscriber);
        }
        
        Toast.makeText(this, "تم حفظ المشترك بنجاح", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }
    
    private void deleteSubscriber() {
        if (currentSubscriber.id != 0) {
            database.subscriberDao().delete(currentSubscriber);
            Toast.makeText(this, "تم حذف المشترك بنجاح", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        }
    }
}
