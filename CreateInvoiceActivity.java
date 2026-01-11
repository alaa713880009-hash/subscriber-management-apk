package com.example.subscriber.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.subscriber.R;
import com.example.subscriber.database.AppDatabase;
import com.example.subscriber.models.Invoice;
import com.example.subscriber.models.Subscriber;
import com.example.subscriber.services.MessageService;
import com.example.subscriber.utils.FormatUtil;
import java.util.ArrayList;
import java.util.List;

public class CreateInvoiceActivity extends AppCompatActivity {
    
    private AppDatabase database;
    private MessageService messageService;
    private Spinner subscriberSpinner;
    private EditText previousReadingInput, currentReadingInput, periodInput, dueDateInput;
    private TextView consumptionText, amountText, tariffText;
    private Button calculateBtn, createBtn, selectMultipleBtn;
    private CheckBox sendMessageCheckbox;
    private List<Subscriber> allSubscribers;
    private List<Subscriber> selectedSubscribers;
    private Subscriber currentSubscriber;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_invoice);
        
        database = AppDatabase.getInstance(this);
        messageService = new MessageService(this);
        
        initializeViews();
        loadSubscribers();
        setupListeners();
    }
    
    private void initializeViews() {
        subscriberSpinner = findViewById(R.id.subscriber_spinner);
        previousReadingInput = findViewById(R.id.previous_reading_input);
        currentReadingInput = findViewById(R.id.current_reading_input);
        periodInput = findViewById(R.id.period_input);
        dueDateInput = findViewById(R.id.due_date_input);
        consumptionText = findViewById(R.id.consumption_text);
        amountText = findViewById(R.id.amount_text);
        tariffText = findViewById(R.id.tariff_text);
        calculateBtn = findViewById(R.id.calculate_btn);
        createBtn = findViewById(R.id.create_btn);
        selectMultipleBtn = findViewById(R.id.select_multiple_btn);
        sendMessageCheckbox = findViewById(R.id.send_message_checkbox);
        
        allSubscribers = new ArrayList<>();
        selectedSubscribers = new ArrayList<>();
    }
    
    private void loadSubscribers() {
        allSubscribers.clear();
        allSubscribers.addAll(database.subscriberDao().getAllSubscribers());
        
        if (allSubscribers.isEmpty()) {
            Toast.makeText(this, "لا توجد مشتركين. يرجى إضافة مشترك أولاً", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // تعيين المشترك الأول
        currentSubscriber = allSubscribers.get(0);
        updateSubscriberInfo();
    }
    
    private void setupListeners() {
        subscriberSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                currentSubscriber = allSubscribers.get(position);
                updateSubscriberInfo();
            }
            
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        });
        
        currentReadingInput.setOnTextChangedListener((s, start, before, count) -> {
            calculateConsumptionAndAmount();
        });
        
        previousReadingInput.setOnTextChangedListener((s, start, before, count) -> {
            calculateConsumptionAndAmount();
        });
        
        calculateBtn.setOnClickListener(v -> calculateConsumptionAndAmount());
        createBtn.setOnClickListener(v -> createInvoice());
        selectMultipleBtn.setOnClickListener(v -> showMultipleSubscribersDialog());
    }
    
    private void updateSubscriberInfo() {
        if (currentSubscriber != null) {
            tariffText.setText(String.format("التعرفة: %.2f ريال", currentSubscriber.tariff));
            consumptionText.setText("الاستهلاك: 0");
            amountText.setText("المبلغ: 0.00");
        }
    }
    
    private void calculateConsumptionAndAmount() {
        try {
            String prevStr = previousReadingInput.getText().toString().trim();
            String currStr = currentReadingInput.getText().toString().trim();
            
            if (prevStr.isEmpty() || currStr.isEmpty()) {
                return;
            }
            
            double previousReading = Double.parseDouble(prevStr);
            double currentReading = Double.parseDouble(currStr);
            
            // حساب الاستهلاك
            double consumption = currentReading >= previousReading ? 
                currentReading - previousReading : 0;
            
            // حساب المبلغ
            double amount = consumption * currentSubscriber.tariff;
            
            // عرض النتائج
            consumptionText.setText(String.format("الاستهلاك: %.2f", consumption));
            amountText.setText(String.format("المبلغ: %.2f ريال", amount));
            
        } catch (NumberFormatException e) {
            Toast.makeText(this, "يرجى إدخال أرقام صحيحة", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void createInvoice() {
        String period = periodInput.getText().toString().trim();
        String dueDate = dueDateInput.getText().toString().trim();
        String prevStr = previousReadingInput.getText().toString().trim();
        String currStr = currentReadingInput.getText().toString().trim();
        
        if (period.isEmpty() || dueDate.isEmpty() || prevStr.isEmpty() || currStr.isEmpty()) {
            Toast.makeText(this, "يرجى ملء جميع الحقول", Toast.LENGTH_SHORT).show();
            return;
        }
        
        try {
            double previousReading = Double.parseDouble(prevStr);
            double currentReading = Double.parseDouble(currStr);
            double consumption = currentReading >= previousReading ? 
                currentReading - previousReading : 0;
            double amount = consumption * currentSubscriber.tariff;
            
            // إنشاء الفاتورة
            Invoice invoice = new Invoice(
                currentSubscriber.id,
                period,
                previousReading,
                currentReading,
                amount,
                FormatUtil.getTodayDate(),
                dueDate
            );
            
            long invoiceId = database.invoiceDao().insert(invoice);
            
            Toast.makeText(this, "تم إنشاء الفاتورة بنجاح", Toast.LENGTH_SHORT).show();
            
            // إرسال رسالة إذا كان مفعلاً
            if (sendMessageCheckbox.isChecked()) {
                sendInvoiceMessage(currentSubscriber, amount, period);
            }
            
            // مسح النموذج
            clearForm();
            
        } catch (NumberFormatException e) {
            Toast.makeText(this, "خطأ في البيانات", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void sendInvoiceMessage(Subscriber subscriber, double amount, String period) {
        String message = String.format(
            "السلام عليكم ورحمة الله وبركاته\n\n" +
            "فاتورة جديدة لحسابك\n" +
            "الفترة: %s\n" +
            "المبلغ المستحق: %.2f ريال\n\n" +
            "يرجى تسديد المبلغ في الموعد المحدد\n\n" +
            "شكراً لك",
            period, amount
        );
        
        messageService.sendSMS(subscriber.phone, message);
    }
    
    private void showMultipleSubscribersDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("إنشاء فواتير لمشتركين متعددين");
        
        boolean[] checkedItems = new boolean[allSubscribers.size()];
        String[] items = new String[allSubscribers.size()];
        
        for (int i = 0; i < allSubscribers.size(); i++) {
            Subscriber sub = allSubscribers.get(i);
            items[i] = sub.name + " - " + sub.meterNumber;
            checkedItems[i] = selectedSubscribers.contains(sub);
        }
        
        builder.setMultiChoiceItems(items, checkedItems, (dialog, which, isChecked) -> {
            if (isChecked) {
                selectedSubscribers.add(allSubscribers.get(which));
            } else {
                selectedSubscribers.remove(allSubscribers.get(which));
            }
        });
        
        builder.setPositiveButton("إنشاء فواتير", (dialog, which) -> {
            createMultipleInvoices();
        });
        
        builder.setNegativeButton("إلغاء", null);
        builder.show();
    }
    
    private void createMultipleInvoices() {
        if (selectedSubscribers.isEmpty()) {
            Toast.makeText(this, "يرجى اختيار مشتركين", Toast.LENGTH_SHORT).show();
            return;
        }
        
        String period = periodInput.getText().toString().trim();
        String dueDate = dueDateInput.getText().toString().trim();
        
        if (period.isEmpty() || dueDate.isEmpty()) {
            Toast.makeText(this, "يرجى إدخال الفترة وتاريخ الاستحقاق", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // إنشاء نافذة لإدخال القراءات
        showBulkReadingDialog(period, dueDate);
    }
    
    private void showBulkReadingDialog(String period, String dueDate) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("إدخال القراءات");
        
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(16, 16, 16, 16);
        
        EditText prevReadingInput = new EditText(this);
        prevReadingInput.setHint("القراءة السابقة");
        prevReadingInput.setInputType(android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        layout.addView(prevReadingInput);
        
        EditText currReadingInput = new EditText(this);
        currReadingInput.setHint("القراءة الحالية");
        currReadingInput.setInputType(android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        layout.addView(currReadingInput);
        
        builder.setView(layout);
        
        builder.setPositiveButton("إنشاء", (dialog, which) -> {
            String prevStr = prevReadingInput.getText().toString().trim();
            String currStr = currReadingInput.getText().toString().trim();
            
            if (prevStr.isEmpty() || currStr.isEmpty()) {
                Toast.makeText(this, "يرجى إدخال القراءات", Toast.LENGTH_SHORT).show();
                return;
            }
            
            try {
                double previousReading = Double.parseDouble(prevStr);
                double currentReading = Double.parseDouble(currStr);
                
                int createdCount = 0;
                for (Subscriber subscriber : selectedSubscribers) {
                    double consumption = currentReading >= previousReading ? 
                        currentReading - previousReading : 0;
                    double amount = consumption * subscriber.tariff;
                    
                    Invoice invoice = new Invoice(
                        subscriber.id,
                        period,
                        previousReading,
                        currentReading,
                        amount,
                        FormatUtil.getTodayDate(),
                        dueDate
                    );
                    
                    database.invoiceDao().insert(invoice);
                    
                    // إرسال رسالة إذا كان مفعلاً
                    if (sendMessageCheckbox.isChecked()) {
                        sendInvoiceMessage(subscriber, amount, period);
                    }
                    
                    createdCount++;
                }
                
                Toast.makeText(this, "تم إنشاء " + createdCount + " فاتورة بنجاح", Toast.LENGTH_SHORT).show();
                clearForm();
                selectedSubscribers.clear();
                
            } catch (NumberFormatException e) {
                Toast.makeText(this, "خطأ في البيانات", Toast.LENGTH_SHORT).show();
            }
        });
        
        builder.setNegativeButton("إلغاء", null);
        builder.show();
    }
    
    private void clearForm() {
        previousReadingInput.setText("");
        currentReadingInput.setText("");
        periodInput.setText("");
        dueDateInput.setText("");
        consumptionText.setText("الاستهلاك: 0");
        amountText.setText("المبلغ: 0.00");
    }
}

// إضافة listener مخصص للتغييرات
class TextWatcherAdapter implements android.text.TextWatcher {
    private Runnable onTextChanged;
    
    public TextWatcherAdapter(Runnable onTextChanged) {
        this.onTextChanged = onTextChanged;
    }
    
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
    
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (onTextChanged != null) {
            onTextChanged.run();
        }
    }
    
    @Override
    public void afterTextChanged(android.text.Editable s) {}
}

// إضافة واجهة للـ listener
interface OnTextChangedListener {
    void onTextChanged(CharSequence s, int start, int before, int count);
}
