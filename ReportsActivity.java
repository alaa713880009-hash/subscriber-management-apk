package com.example.subscriber.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.subscriber.R;
import com.example.subscriber.database.AppDatabase;
import com.example.subscriber.services.BackupService;

public class ReportsActivity extends AppCompatActivity {
    
    private AppDatabase database;
    private BackupService backupService;
    private Button accountStatementBtn, exportPdfBtn, exportExcelBtn, backupBtn, restoreBtn;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);
        
        database = AppDatabase.getInstance(this);
        backupService = new BackupService(this);
        
        initializeViews();
        setupListeners();
    }
    
    private void initializeViews() {
        accountStatementBtn = findViewById(R.id.account_statement_btn);
        exportPdfBtn = findViewById(R.id.export_pdf_btn);
        exportExcelBtn = findViewById(R.id.export_excel_btn);
        backupBtn = findViewById(R.id.backup_btn);
        restoreBtn = findViewById(R.id.restore_btn);
    }
    
    private void setupListeners() {
        accountStatementBtn.setOnClickListener(v -> {
            Toast.makeText(this, "جاري إنشاء كشف الحساب...", Toast.LENGTH_SHORT).show();
        });
        
        exportPdfBtn.setOnClickListener(v -> {
            Toast.makeText(this, "جاري تصدير PDF...", Toast.LENGTH_SHORT).show();
        });
        
        exportExcelBtn.setOnClickListener(v -> {
            Toast.makeText(this, "جاري تصدير Excel...", Toast.LENGTH_SHORT).show();
        });
        
        backupBtn.setOnClickListener(v -> {
            try {
                String backupPath = backupService.createBackup();
                Toast.makeText(this, "تم إنشاء النسخة الاحتياطية: " + backupPath, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "خطأ: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        
        restoreBtn.setOnClickListener(v -> {
            Toast.makeText(this, "جاري استعادة البيانات...", Toast.LENGTH_SHORT).show();
        });
    }
}
