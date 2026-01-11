package com.example.subscriber.services;

import android.content.Context;
import android.os.Environment;
import com.example.subscriber.database.AppDatabase;
import com.example.subscriber.models.Invoice;
import com.example.subscriber.models.Message;
import com.example.subscriber.models.Payment;
import com.example.subscriber.models.Subscriber;
import com.example.subscriber.models.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class BackupService {
    
    private Context context;
    private AppDatabase database;
    private Gson gson;
    
    public BackupService(Context context) {
        this.context = context;
        this.database = AppDatabase.getInstance(context);
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }
    
    /**
     * إنشاء نسخة احتياطية من البيانات
     */
    public String createBackup() throws IOException {
        // إنشاء مجلد النسخ الاحتياطية
        File backupDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), "SubscriberManagement/Backups");
        
        if (!backupDir.exists()) {
            backupDir.mkdirs();
        }
        
        // إنشاء اسم الملف بناءً على التاريخ والوقت
        String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault()).format(new Date());
        File backupFile = new File(backupDir, "backup_" + timestamp + ".json");
        
        // جمع البيانات
        Map<String, Object> backupData = new HashMap<>();
        backupData.put("timestamp", System.currentTimeMillis());
        backupData.put("subscribers", database.subscriberDao().getAllSubscribers());
        backupData.put("invoices", database.invoiceDao().getAllInvoices());
        backupData.put("payments", database.paymentDao().getAllPayments());
        backupData.put("users", database.userDao().getAllUsers());
        backupData.put("messages", database.messageDao().getAllMessages());
        
        // كتابة البيانات إلى الملف
        try (FileWriter writer = new FileWriter(backupFile)) {
            gson.toJson(backupData, writer);
        }
        
        return backupFile.getAbsolutePath();
    }
    
    /**
     * استعادة البيانات من نسخة احتياطية
     */
    public boolean restoreBackup(String filePath) {
        try {
            // قراءة ملف النسخة الاحتياطية
            File backupFile = new File(filePath);
            String json = readFile(backupFile);
            
            // تحويل JSON إلى خريطة
            Map<String, Object> backupData = gson.fromJson(json, Map.class);
            
            // استعادة البيانات
            if (backupData.containsKey("subscribers")) {
                List<Subscriber> subscribers = gson.fromJson(
                    gson.toJson(backupData.get("subscribers")), 
                    new com.google.gson.reflect.TypeToken<List<Subscriber>>(){}.getType()
                );
                for (Subscriber subscriber : subscribers) {
                    database.subscriberDao().insert(subscriber);
                }
            }
            
            if (backupData.containsKey("invoices")) {
                List<Invoice> invoices = gson.fromJson(
                    gson.toJson(backupData.get("invoices")), 
                    new com.google.gson.reflect.TypeToken<List<Invoice>>(){}.getType()
                );
                for (Invoice invoice : invoices) {
                    database.invoiceDao().insert(invoice);
                }
            }
            
            if (backupData.containsKey("payments")) {
                List<Payment> payments = gson.fromJson(
                    gson.toJson(backupData.get("payments")), 
                    new com.google.gson.reflect.TypeToken<List<Payment>>(){}.getType()
                );
                for (Payment payment : payments) {
                    database.paymentDao().insert(payment);
                }
            }
            
            if (backupData.containsKey("users")) {
                List<User> users = gson.fromJson(
                    gson.toJson(backupData.get("users")), 
                    new com.google.gson.reflect.TypeToken<List<User>>(){}.getType()
                );
                for (User user : users) {
                    database.userDao().insert(user);
                }
            }
            
            if (backupData.containsKey("messages")) {
                List<Message> messages = gson.fromJson(
                    gson.toJson(backupData.get("messages")), 
                    new com.google.gson.reflect.TypeToken<List<Message>>(){}.getType()
                );
                for (Message message : messages) {
                    database.messageDao().insert(message);
                }
            }
            
            return true;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * قراءة محتوى الملف
     */
    private String readFile(File file) throws IOException {
        StringBuilder content = new StringBuilder();
        try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
        }
        return content.toString();
    }
    
    /**
     * الحصول على قائمة النسخ الاحتياطية
     */
    public List<File> getBackupsList() {
        File backupDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), "SubscriberManagement/Backups");
        
        List<File> backups = new java.util.ArrayList<>();
        if (backupDir.exists() && backupDir.isDirectory()) {
            File[] files = backupDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.getName().endsWith(".json")) {
                        backups.add(file);
                    }
                }
            }
        }
        return backups;
    }
}
