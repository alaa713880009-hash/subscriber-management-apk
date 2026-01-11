package com.example.subscriber.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.subscriber.R;
import com.example.subscriber.adapters.UserAdapter;
import com.example.subscriber.database.AppDatabase;
import com.example.subscriber.models.User;
import com.example.subscriber.utils.Constants;
import com.example.subscriber.utils.EncryptionUtil;
import java.util.ArrayList;
import java.util.List;

public class UserManagementActivity extends AppCompatActivity {
    
    private AppDatabase database;
    private ListView userListView;
    private EditText usernameInput, fullNameInput, passwordInput;
    private Spinner roleSpinner;
    private Button addUserBtn, updateUserBtn, deleteUserBtn;
    private UserAdapter adapter;
    private List<User> users;
    private User selectedUser;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_management);
        
        database = AppDatabase.getInstance(this);
        
        initializeViews();
        loadUsers();
        setupListeners();
    }
    
    private void initializeViews() {
        userListView = findViewById(R.id.user_list);
        usernameInput = findViewById(R.id.username_input);
        fullNameInput = findViewById(R.id.full_name_input);
        passwordInput = findViewById(R.id.password_input);
        roleSpinner = findViewById(R.id.role_spinner);
        addUserBtn = findViewById(R.id.add_user_btn);
        updateUserBtn = findViewById(R.id.update_user_btn);
        deleteUserBtn = findViewById(R.id.delete_user_btn);
        
        users = new ArrayList<>();
        adapter = new UserAdapter(this, users);
        userListView.setAdapter(adapter);
        
        updateUserBtn.setEnabled(false);
        deleteUserBtn.setEnabled(false);
    }
    
    private void loadUsers() {
        users.clear();
        users.addAll(database.userDao().getAllUsers());
        adapter.notifyDataSetChanged();
    }
    
    private void setupListeners() {
        addUserBtn.setOnClickListener(v -> addUser());
        updateUserBtn.setOnClickListener(v -> updateUser());
        deleteUserBtn.setOnClickListener(v -> deleteUser());
        
        userListView.setOnItemClickListener((parent, view, position, id) -> {
            selectedUser = users.get(position);
            usernameInput.setText(selectedUser.username);
            fullNameInput.setText(selectedUser.fullName);
            passwordInput.setText("");
            roleSpinner.setSelection(getRoleIndex(selectedUser.role));
            
            addUserBtn.setEnabled(false);
            updateUserBtn.setEnabled(true);
            deleteUserBtn.setEnabled(true);
        });
    }
    
    private void addUser() {
        String username = usernameInput.getText().toString().trim();
        String fullName = fullNameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String role = roleSpinner.getSelectedItem().toString();
        
        if (username.isEmpty() || fullName.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "يرجى ملء جميع الحقول", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // التحقق من عدم وجود اسم مستخدم مكرر
        if (database.userDao().getUserByUsername(username) != null) {
            Toast.makeText(this, "اسم المستخدم موجود بالفعل", Toast.LENGTH_SHORT).show();
            return;
        }
        
        User newUser = new User(
            username,
            EncryptionUtil.hashPassword(password),
            fullName,
            role
        );
        
        database.userDao().insert(newUser);
        Toast.makeText(this, "تم إضافة المستخدم بنجاح", Toast.LENGTH_SHORT).show();
        clearForm();
        loadUsers();
    }
    
    private void updateUser() {
        if (selectedUser == null) {
            Toast.makeText(this, "يرجى اختيار مستخدم", Toast.LENGTH_SHORT).show();
            return;
        }
        
        String fullName = fullNameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String role = roleSpinner.getSelectedItem().toString();
        
        if (fullName.isEmpty()) {
            Toast.makeText(this, "يرجى إدخال الاسم الكامل", Toast.LENGTH_SHORT).show();
            return;
        }
        
        selectedUser.fullName = fullName;
        selectedUser.role = role;
        
        if (!password.isEmpty()) {
            selectedUser.password = EncryptionUtil.hashPassword(password);
        }
        
        selectedUser.updatedAt = System.currentTimeMillis();
        database.userDao().update(selectedUser);
        
        Toast.makeText(this, "تم تحديث المستخدم بنجاح", Toast.LENGTH_SHORT).show();
        clearForm();
        loadUsers();
    }
    
    private void deleteUser() {
        if (selectedUser == null) {
            Toast.makeText(this, "يرجى اختيار مستخدم", Toast.LENGTH_SHORT).show();
            return;
        }
        
        database.userDao().delete(selectedUser);
        Toast.makeText(this, "تم حذف المستخدم بنجاح", Toast.LENGTH_SHORT).show();
        clearForm();
        loadUsers();
    }
    
    private void clearForm() {
        usernameInput.setText("");
        fullNameInput.setText("");
        passwordInput.setText("");
        roleSpinner.setSelection(0);
        selectedUser = null;
        
        addUserBtn.setEnabled(true);
        updateUserBtn.setEnabled(false);
        deleteUserBtn.setEnabled(false);
    }
    
    private int getRoleIndex(String role) {
        if (role.equals(Constants.ROLE_OPERATOR)) {
            return 1;
        } else if (role.equals(Constants.ROLE_VIEWER)) {
            return 2;
        }
        return 0; // admin
    }
}
