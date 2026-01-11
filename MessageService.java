package com.example.subscriber.services;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telephony.SmsManager;
import android.widget.Toast;

public class MessageService {
    
    private Context context;
    
    public MessageService(Context context) {
        this.context = context;
    }
    
    /**
     * إرسال رسالة نصية
     */
    public void sendSMS(String phoneNumber, String message) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            
            // تقسيم الرسالة إذا كانت طويلة جداً
            ArrayList<String> parts = smsManager.divideMessage(message);
            ArrayList<PendingIntent> sentIntents = new ArrayList<>();
            ArrayList<PendingIntent> deliveryIntents = new ArrayList<>();
            
            for (int i = 0; i < parts.size(); i++) {
                sentIntents.add(PendingIntent.getBroadcast(context, 0, new Intent("SMS_SENT"), 0));
                deliveryIntents.add(PendingIntent.getBroadcast(context, 0, new Intent("SMS_DELIVERED"), 0));
            }
            
            smsManager.sendMultipartTextMessage(phoneNumber, null, parts, sentIntents, deliveryIntents);
            Toast.makeText(context, "تم إرسال الرسالة بنجاح", Toast.LENGTH_SHORT).show();
            
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "خطأ في إرسال الرسالة: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * فتح تطبيق الواتساب لإرسال رسالة
     */
    public void sendWhatsApp(String phoneNumber, String message) {
        try {
            // إزالة الأحرف غير الرقمية من رقم الهاتف
            String cleanPhone = phoneNumber.replaceAll("[^0-9]", "");
            
            // إضافة رمز الدولة إذا لم يكن موجوداً
            if (!cleanPhone.startsWith("+")) {
                if (cleanPhone.startsWith("0")) {
                    cleanPhone = "+966" + cleanPhone.substring(1);
                } else if (!cleanPhone.startsWith("966")) {
                    cleanPhone = "+966" + cleanPhone;
                } else {
                    cleanPhone = "+" + cleanPhone;
                }
            }
            
            String url = "https://api.whatsapp.com/send?phone=" + cleanPhone + "&text=" + Uri.encode(message);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            context.startActivity(intent);
            
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "خطأ: تأكد من تثبيت تطبيق الواتساب", Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * فتح تطبيق الرسائل النصية
     */
    public void openSMSApp(String phoneNumber, String message) {
        try {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("smsto:" + phoneNumber));
            intent.putExtra("sms_body", message);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "خطأ في فتح تطبيق الرسائل", Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * إرسال رسائل متعددة
     */
    public void sendMultipleMessages(String[] phoneNumbers, String message, String type) {
        for (String phone : phoneNumbers) {
            if (type.equals("sms")) {
                sendSMS(phone, message);
            } else if (type.equals("whatsapp")) {
                sendWhatsApp(phone, message);
            }
        }
    }
}
