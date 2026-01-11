package com.example.subscriber.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.subscriber.R;

public class SettingsActivity extends AppCompatActivity {
    
    private TextView developerInfo;
    private Button contactButton;
    private Button backupButton;
    private Button restoreButton;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        
        initializeViews();
        setupListeners();
        displayDeveloperInfo();
    }
    
    private void initializeViews() {
        developerInfo = findViewById(R.id.developer_info);
        contactButton = findViewById(R.id.contact_button);
        backupButton = findViewById(R.id.backup_button);
        restoreButton = findViewById(R.id.restore_button);
    }
    
    private void setupListeners() {
        contactButton.setOnClickListener(v -> contactDeveloper());
        backupButton.setOnClickListener(v -> createBackup());
        restoreButton.setOnClickListener(v -> restoreBackup());
    }
    
    private void displayDeveloperInfo() {
        String developerName = getString(R.string.developer_name);
        String developerPhone = getString(R.string.developer_phone);
        String info = String.format(
            getString(R.string.developed_by),
            developerName,
            developerPhone
        );
        developerInfo.setText(info);
    }
    
    private void contactDeveloper() {
        String phone = getString(R.string.developer_phone);
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phone));
        startActivity(intent);
    }
    
    private void createBackup() {
        // سيتم تطبيق هذه الوظيفة لاحقاً
        android.widget.Toast.makeText(this, "جاري إنشاء النسخة الاحتياطية...", android.widget.Toast.LENGTH_SHORT).show();
    }
    
    private void restoreBackup() {
        // سيتم تطبيق هذه الوظيفة لاحقاً
        android.widget.Toast.makeText(this, "جاري استعادة البيانات...", android.widget.Toast.LENGTH_SHORT).show();
    }
}
