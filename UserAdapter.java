package com.example.subscriber.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.example.subscriber.R;
import com.example.subscriber.models.User;
import java.util.List;

public class UserAdapter extends ArrayAdapter<User> {
    
    private Context context;
    private List<User> users;
    
    public UserAdapter(Context context, List<User> users) {
        super(context, 0, users);
        this.context = context;
        this.users = users;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        }
        
        User user = users.get(position);
        
        TextView usernameText = convertView.findViewById(R.id.username_text);
        TextView fullNameText = convertView.findViewById(R.id.full_name_text);
        TextView roleText = convertView.findViewById(R.id.role_text);
        
        usernameText.setText(user.username);
        fullNameText.setText(user.fullName);
        roleText.setText(user.role);
        
        return convertView;
    }
}
