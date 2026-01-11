package com.example.subscriber.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.subscriber.R;
import com.example.subscriber.database.AppDatabase;
import com.example.subscriber.models.Message;
import com.example.subscriber.models.Subscriber;
import com.example.subscriber.services.MessageService;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;

public class AdvancedMessageActivity extends AppCompatActivity {
    
    private AppDatabase database;
    private MessageService messageService;
    private EditText messageInput;
    private RadioGroup messageTypeGroup;
    private Button selectRecipientsBtn, previewBtn, sendBtn, templateBtn;
    private TextView selectedRecipientsText, characterCountText;
    private CheckBox saveMessageCheckbox;
    private List<Subscriber> allSubscribers;
    private List<Subscriber> selectedSubscribers;
    private Gson gson;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_message);
        
        database = AppDatabase.getInstance(this);
        messageService = new MessageService(this);
        gson = new Gson();
        
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
        templateBtn = findViewById(R.id.template_btn);
        selectedRecipientsText = findViewById(R.id.selected_recipients_text);
        characterCountText = findViewById(R.id.character_count_text);
        saveMessageCheckbox = findViewById(R.id.save_message_checkbox);
        
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
        templateBtn.setOnClickListener(v -> showTemplatesDialog());
        
        messageInput.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateCharacterCount();
            }
            
            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });
    }
    
    private void updateCharacterCount() {
        int count = messageInput.getText().length();
        characterCountText.setText("عدد الأحرف: " + count);
    }
    
    private void showRecipientsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("اختر المستقبلين");
        
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
        
        builder.setNeutralButton("تحديد الكل", (dialog, which) -> {
            selectedSubscribers.clear();
            selectedSubscribers.addAll(allSubscribers);
            updateSelectedRecipientsText();
        });
        
        builder.show();
    }
    
    private void updateSelectedRecipientsText() {
        if (selectedSubscribers.isEmpty()) {
            selectedRecipientsText.setText("لم يتم اختيار أي مستقبلين");
        } else {
            StringBuilder text = new StringBuilder("تم اختيار " + selectedSubscribers.size() + " مستقبل:\n");
            for (int i = 0; i < Math.min(selectedSubscribers.size(), 3); i++) {
                Subscriber sub = selectedSubscribers.get(i);
                text.append("• ").append(sub.name).append("\n");
            }
            if (selectedSubscribers.size() > 3) {
                text.append("... و").append(selectedSubscribers.size() - 3).append(" آخرين");
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
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("معاينة الرسالة");
        
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(16, 16, 16, 16);
        
        TextView previewText = new TextView(this);
        previewText.setText("الرسالة:\n\n" + message);
        previewText.setTextSize(14);
        previewText.setPadding(0, 0, 0, 16);
        layout.addView(previewText);
        
        TextView recipientsText = new TextView(this);
        StringBuilder recipients = new StringBuilder("المستقبلون (" + selectedSubscribers.size() + "):\n");
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
    
    private void showTemplatesDialog() {
        String[] templates = {
            "فاتورة جديدة: الفترة: {period} - المبلغ: {amount} ريال",
            "تذكير بالدفع: يرجى تسديد المبلغ المستحق",
            "شكراً لك على الدفع: تم استقبال دفعتك",
            "تحديث الحساب: رصيدك الحالي {balance}",
            "عطل في الخدمة: سيتم الإصلاح قريباً"
        };
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("اختر قالب رسالة");
        builder.setItems(templates, (dialog, which) -> {
            messageInput.setText(templates[which]);
        });
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
        
        // حفظ الرسالة إذا كان مفعلاً
        if (saveMessageCheckbox.isChecked()) {
            saveMessage(message, messageType);
        }
        
        // إرسال الرسائل
        int sentCount = 0;
        for (Subscriber subscriber : selectedSubscribers) {
            try {
                if (messageType.contains("نصية")) {
                    messageService.sendSMS(subscriber.phone, message);
                } else if (messageType.contains("واتساب")) {
                    messageService.sendWhatsApp(subscriber.phone, message);
                }
                sentCount++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        Toast.makeText(this, "تم إرسال " + sentCount + " رسالة بنجاح", Toast.LENGTH_SHORT).show();
        messageInput.setText("");
        selectedSubscribers.clear();
        updateSelectedRecipientsText();
    }
    
    private void saveMessage(String content, String type) {
        Message message = new Message(
            content,
            type.contains("نصية") ? "sms" : "whatsapp",
            gson.toJson(selectedSubscribers)
        );
        message.status = "sent";
        message.sentDate = System.currentTimeMillis() + "";
        
        database.messageDao().insert(message);
    }
}
