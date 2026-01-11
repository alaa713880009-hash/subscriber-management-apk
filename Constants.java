package com.example.subscriber.utils;

public class Constants {
    // تفضيلات التطبيق
    public static final String PREF_NAME = "subscriber_management_prefs";
    public static final String PREF_CURRENT_USER = "current_user";
    public static final String PREF_CURRENT_USER_ID = "current_user_id";
    public static final String PREF_IS_LOGGED_IN = "is_logged_in";
    
    // الأدوار
    public static final String ROLE_ADMIN = "admin";
    public static final String ROLE_OPERATOR = "operator";
    public static final String ROLE_VIEWER = "viewer";
    
    // حالات الفواتير
    public static final String INVOICE_STATUS_PAID = "paid";
    public static final String INVOICE_STATUS_UNPAID = "unpaid";
    public static final String INVOICE_STATUS_PARTIAL = "partial";
    
    // أنواع الرسائل
    public static final String MESSAGE_TYPE_SMS = "sms";
    public static final String MESSAGE_TYPE_WHATSAPP = "whatsapp";
    
    // حالات الرسائل
    public static final String MESSAGE_STATUS_DRAFT = "draft";
    public static final String MESSAGE_STATUS_SENT = "sent";
    public static final String MESSAGE_STATUS_FAILED = "failed";
    
    // طرق الدفع
    public static final String PAYMENT_METHOD_CASH = "cash";
    public static final String PAYMENT_METHOD_CHECK = "check";
    public static final String PAYMENT_METHOD_TRANSFER = "transfer";
    public static final String PAYMENT_METHOD_CARD = "card";
    
    // أكواد الطلب
    public static final int REQUEST_CODE_SUBSCRIBER = 1001;
    public static final int REQUEST_CODE_INVOICE = 1002;
    public static final int REQUEST_CODE_PAYMENT = 1003;
    public static final int REQUEST_CODE_MESSAGE = 1004;
    public static final int REQUEST_CODE_USER = 1005;
    
    // أكواد الأذونات
    public static final int PERMISSION_SMS = 2001;
    public static final int PERMISSION_CONTACTS = 2002;
    public static final int PERMISSION_STORAGE = 2003;
}
