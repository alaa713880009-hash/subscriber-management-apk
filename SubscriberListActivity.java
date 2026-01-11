package com.example.subscriber.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.subscriber.R;
import com.example.subscriber.adapters.SubscriberAdapter;
import com.example.subscriber.database.AppDatabase;
import com.example.subscriber.models.Subscriber;
import java.util.ArrayList;
import java.util.List;

public class SubscriberListActivity extends AppCompatActivity {
    
    private AppDatabase database;
    private ListView subscriberListView;
    private EditText searchInput;
    private Button addButton;
    private SubscriberAdapter adapter;
    private List<Subscriber> subscribers;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscriber_list);
        
        database = AppDatabase.getInstance(this);
        
        initializeViews();
        loadSubscribers();
        setupListeners();
    }
    
    private void initializeViews() {
        subscriberListView = findViewById(R.id.subscriber_list);
        searchInput = findViewById(R.id.search_input);
        addButton = findViewById(R.id.add_button);
        
        subscribers = new ArrayList<>();
        adapter = new SubscriberAdapter(this, subscribers);
        subscriberListView.setAdapter(adapter);
    }
    
    private void loadSubscribers() {
        subscribers.clear();
        subscribers.addAll(database.subscriberDao().getAllSubscribers());
        adapter.notifyDataSetChanged();
    }
    
    private void setupListeners() {
        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, SubscriberDetailActivity.class);
            startActivityForResult(intent, 1001);
        });
        
        searchInput.setOnTextChangedListener((s, start, before, count) -> {
            if (s.toString().isEmpty()) {
                loadSubscribers();
            } else {
                searchSubscribers(s.toString());
            }
        });
        
        subscriberListView.setOnItemClickListener((parent, view, position, id) -> {
            Subscriber subscriber = subscribers.get(position);
            Intent intent = new Intent(this, SubscriberDetailActivity.class);
            intent.putExtra("subscriber", subscriber);
            startActivityForResult(intent, 1001);
        });
    }
    
    private void searchSubscribers(String query) {
        subscribers.clear();
        String searchQuery = "%" + query + "%";
        subscribers.addAll(database.subscriberDao().searchSubscribers(searchQuery));
        adapter.notifyDataSetChanged();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            loadSubscribers();
        }
    }
}
