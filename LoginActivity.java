package com.example.subscriber.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.subscriber.R;
import com.example.subscriber.database.AppDatabase;
import com.example.subscriber.models.User;
import com.example.subscriber.utils.Constants;
import com.example.subscriber.utils.EncryptionUtil;

public class LoginActivity extends AppCompatActivity {
    
    private EditText usernameInput;
    private EditText passwordInput;
    private Button loginButton;
    private AppDatabase database;
    private SharedPreferences preferences;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        database = AppDatabase.getInstance(this);
        preferences = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        
        // التحقق من تسجيل الدخول السابق
        if (preferences.getBoolean(Constants.PREF_IS_LOGGED_IN, false)) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }
        
        initializeViews();
        setupListeners();
        initializeDefaultUser();
    }
    
    private void initializeViews() {
        usernameInput = findViewById(R.id.username_input);
        passwordInput = findViewById(R.id.password_input);
        loginButton = findViewById(R.id.login_button);
    }
    
    private void setupListeners() {
        loginButton.setOnClickListener(v -> performLogin());
    }
    
    private void performLogin() {
        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "يرجى إدخال اسم المستخدم وكلمة المرور", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // تشفير كلمة المرور
        String hashedPassword = EncryptionUtil.hashPassword(password);
        
        // البحث عن المستخدم
        User user = database.userDao().getUserByUsername(username);
        
        if (user != null && user.password.equals(hashedPassword)) {
            // تسجيل الدخول بنجاح
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(Constants.PREF_IS_LOGGED_IN, true);
            editor.putInt(Constants.PREF_CURRENT_USER_ID, user.id);
            editor.putString(Constants.PREF_CURRENT_USER, username);
            editor.apply();
            
            Toast.makeText(this, "تم تسجيل الدخول بنجاح", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            Toast.makeText(this, "بيانات الدخول غير صحيحة", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void initializeDefaultUser() {
        // إنشاء مستخدم افتراضي إذا لم يكن هناك أي مستخدمين
        if (database.userDao().getCount() == 0) {
            User adminUser = new User(
                "admin",
                EncryptionUtil.hashPassword("admin123"),
                "المدير",
                Constants.ROLE_ADMIN
            );
            database.userDao().insert(adminUser);
        }
    }
}
