package com.example.subscriber.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.example.subscriber.R;
import com.example.subscriber.database.AppDatabase;
import com.example.subscriber.models.User;
import com.example.subscriber.utils.Constants;

public class MainActivity extends AppCompatActivity {
    
    private AppDatabase database;
    private SharedPreferences preferences;
    private User currentUser;
    private TextView welcomeText;
    private Button subscribersBtn, invoicesBtn, paymentsBtn, messagesBtn, reportsBtn, usersBtn, settingsBtn, logoutBtn;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        database = AppDatabase.getInstance(this);
        preferences = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        
        // التحقق من تسجيل الدخول
        if (!preferences.getBoolean(Constants.PREF_IS_LOGGED_IN, false)) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        
        // الحصول على بيانات المستخدم الحالي
        int userId = preferences.getInt(Constants.PREF_CURRENT_USER_ID, -1);
        currentUser = database.userDao().getUserById(userId);
        
        initializeViews();
        setupListeners();
        updateWelcomeMessage();
    }
    
    private void initializeViews() {
        welcomeText = findViewById(R.id.welcome_text);
        subscribersBtn = findViewById(R.id.subscribers_btn);
        invoicesBtn = findViewById(R.id.invoices_btn);
        paymentsBtn = findViewById(R.id.payments_btn);
        messagesBtn = findViewById(R.id.messages_btn);
        reportsBtn = findViewById(R.id.reports_btn);
        usersBtn = findViewById(R.id.users_btn);
        settingsBtn = findViewById(R.id.settings_btn);
        logoutBtn = findViewById(R.id.logout_btn);
    }
    
    private void setupListeners() {
        subscribersBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, SubscriberListActivity.class);
            startActivity(intent);
        });
        
        invoicesBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, InvoiceListActivity.class);
            startActivity(intent);
        });
        
        paymentsBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, PaymentListActivity.class);
            startActivity(intent);
        });
        
        messagesBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, SendMessageActivity.class);
            startActivity(intent);
        });
        
        reportsBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, ReportsActivity.class);
            startActivity(intent);
        });
        
        usersBtn.setOnClickListener(v -> {
            if (currentUser != null && currentUser.role.equals(Constants.ROLE_ADMIN)) {
                Intent intent = new Intent(this, UserManagementActivity.class);
                startActivity(intent);
            } else {
                android.widget.Toast.makeText(this, "ليس لديك صلاحية لإدارة المستخدمين", android.widget.Toast.LENGTH_SHORT).show();
            }
        });
        
        settingsBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        });
        
        logoutBtn.setOnClickListener(v -> performLogout());
    }
    
    private void updateWelcomeMessage() {
        if (currentUser != null) {
            welcomeText.setText("مرحباً " + currentUser.fullName);
        }
    }
    
    private void performLogout() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(Constants.PREF_IS_LOGGED_IN, false);
        editor.remove(Constants.PREF_CURRENT_USER_ID);
        editor.remove(Constants.PREF_CURRENT_USER);
        editor.apply();
        
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
