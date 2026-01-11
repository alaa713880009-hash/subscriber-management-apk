package com.example.subscriber.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.subscriber.R;
import com.example.subscriber.adapters.SubscriberAdapter;
import com.example.subscriber.database.AppDatabase;
import com.example.subscriber.models.Subscriber;
import com.example.subscriber.services.MessageService;
import java.util.ArrayList;
import java.util.List;

public class SendMessageActivity extends AppCompatActivity {
    
    private AppDatabase database;
    private MessageService messageService;
    private EditText messageInput;
    private RadioGroup messageTypeGroup;
    private Button selectRecipientsBtn, previewBtn, sendBtn;
    private TextView selectedRecipientsText;
    private List<Subscriber> allSubscribers;
    private List<Subscriber> selectedSubscribers;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);
        
        database = AppDatabase.getInstance(this);
        messageService = new MessageService(this);
        
        initializeViews();
        loadSubscribers();
        setupListeners();
    }
    
    private void initializeViews() {
        messageInput = findViewById(R.id.message_input);
        messageTypeGroup = findViewById(R.id.message_type_group);
        selectRecipientsBtn = findViewById(R.id.select_recipients_btn);
        previewBtn = findViewById(R.id.preview_btn);
        sendBtn = findViewById(R.id.send_btn);
        selectedRecipientsText = findViewById(R.id.selected_recipients_text);
        
        allSubscribers = new ArrayList<>();
        selectedSubscribers = new ArrayList<>();
    }
    
    private void loadSubscribers() {
        allSubscribers.clear();
        allSubscribers.addAll(database.subscriberDao().getAllSubscribers());
    }
    
    private void setupListeners() {
        selectRecipientsBtn.setOnClickListener(v -> showRecipientsDialog());
        previewBtn.setOnClickListener(v -> showMessagePreview());
        sendBtn.setOnClickListener(v -> sendMessages());
    }
    
    private void showRecipientsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("اختر المستقبلين");
        
        // إنشاء قائمة بـ checkboxes
        boolean[] checkedItems = new boolean[allSubscribers.size()];
        String[] items = new String[allSubscribers.size()];
        
        for (int i = 0; i < allSubscribers.size(); i++) {
            Subscriber sub = allSubscribers.get(i);
            items[i] = sub.name + " - " + sub.phone;
            checkedItems[i] = selectedSubscribers.contains(sub);
        }
        
        builder.setMultiChoiceItems(items, checkedItems, (dialog, which, isChecked) -> {
            if (isChecked) {
                selectedSubscribers.add(allSubscribers.get(which));
            } else {
                selectedSubscribers.remove(allSubscribers.get(which));
            }
        });
        
        builder.setPositiveButton("تم", (dialog, which) -> {
            updateSelectedRecipientsText();
        });
        
        builder.setNegativeButton("إلغاء", null);
        builder.show();
    }
    
    private void updateSelectedRecipientsText() {
        if (selectedSubscribers.isEmpty()) {
            selectedRecipientsText.setText("لم يتم اختيار أي مستقبلين");
        } else {
            StringBuilder text = new StringBuilder("تم اختيار " + selectedSubscribers.size() + " مستقبل:\n");
            for (Subscriber sub : selectedSubscribers) {
                text.append("• ").append(sub.name).append("\n");
            }
            selectedRecipientsText.setText(text.toString());
        }
    }
    
    private void showMessagePreview() {
        String message = messageInput.getText().toString().trim();
        
        if (message.isEmpty()) {
            Toast.makeText(this, "يرجى إدخال الرسالة", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (selectedSubscribers.isEmpty()) {
            Toast.makeText(this, "يرجى اختيار المستقبلين", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // إنشاء نافذة معاينة
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("معاينة الرسالة");
        
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(16, 16, 16, 16);
        
        TextView previewText = new TextView(this);
        previewText.setText("الرسالة:\n\n" + message);
        previewText.setTextSize(14);
        layout.addView(previewText);
        
        TextView recipientsText = new TextView(this);
        StringBuilder recipients = new StringBuilder("\n\nالمستقبلون:\n");
        for (Subscriber sub : selectedSubscribers) {
            recipients.append("• ").append(sub.name).append(" (").append(sub.phone).append(")\n");
        }
        recipientsText.setText(recipients.toString());
        recipientsText.setTextSize(12);
        layout.addView(recipientsText);
        
        builder.setView(layout);
        builder.setPositiveButton("تأكيد الإرسال", (dialog, which) -> sendMessages());
        builder.setNegativeButton("تعديل", null);
        builder.show();
    }
    
    private void sendMessages() {
        String message = messageInput.getText().toString().trim();
        int selectedTypeId = messageTypeGroup.getCheckedRadioButtonId();
        
        if (message.isEmpty()) {
            Toast.makeText(this, "يرجى إدخال الرسالة", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (selectedSubscribers.isEmpty()) {
            Toast.makeText(this, "يرجى اختيار المستقبلين", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (selectedTypeId == -1) {
            Toast.makeText(this, "يرجى اختيار نوع الرسالة", Toast.LENGTH_SHORT).show();
            return;
        }
        
        RadioButton selectedType = findViewById(selectedTypeId);
        String messageType = selectedType.getText().toString();
        
        // إرسال الرسائل
        for (Subscriber subscriber : selectedSubscribers) {
            if (messageType.contains("نصية")) {
                messageService.sendSMS(subscriber.phone, message);
            } else if (messageType.contains("واتساب")) {
                messageService.sendWhatsApp(subscriber.phone, message);
            }
        }
        
        Toast.makeText(this, "تم إرسال الرسائل بنجاح", Toast.LENGTH_SHORT).show();
        messageInput.setText("");
        selectedSubscribers.clear();
        updateSelectedRecipientsText();
    }
}
