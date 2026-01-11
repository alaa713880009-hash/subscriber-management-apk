package com.example.subscriber.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.example.subscriber.R;
import com.example.subscriber.models.Subscriber;
import java.util.List;

public class SubscriberAdapter extends ArrayAdapter<Subscriber> {
    
    private Context context;
    private List<Subscriber> subscribers;
    
    public SubscriberAdapter(Context context, List<Subscriber> subscribers) {
        super(context, 0, subscribers);
        this.context = context;
        this.subscribers = subscribers;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_subscriber, parent, false);
        }
        
        Subscriber subscriber = subscribers.get(position);
        
        TextView nameText = convertView.findViewById(R.id.subscriber_name);
        TextView phoneText = convertView.findViewById(R.id.subscriber_phone);
        TextView meterText = convertView.findViewById(R.id.subscriber_meter);
        TextView addressText = convertView.findViewById(R.id.subscriber_address);
        
        nameText.setText(subscriber.name);
        phoneText.setText(subscriber.phone);
        meterText.setText(subscriber.meterNumber);
        addressText.setText(subscriber.address);
        
        return convertView;
    }
}
