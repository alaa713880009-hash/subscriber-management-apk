package com.example.subscriber.utils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FormatUtil {
    
    private static final DecimalFormat currencyFormat = new DecimalFormat("#,##0.00");
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private static final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
    
    /**
     * تنسيق المبالغ المالية
     */
    public static String formatCurrency(double amount) {
        return currencyFormat.format(amount);
    }
    
    /**
     * تنسيق التاريخ
     */
    public static String formatDate(long timestamp) {
        return dateFormat.format(new Date(timestamp));
    }
    
    /**
     * تنسيق التاريخ والوقت
     */
    public static String formatDateTime(long timestamp) {
        return dateTimeFormat.format(new Date(timestamp));
    }
    
    /**
     * الحصول على تاريخ اليوم
     */
    public static String getTodayDate() {
        return dateFormat.format(new Date());
    }
    
    /**
     * تنسيق رقم الهاتف
     */
    public static String formatPhoneNumber(String phone) {
        if (phone == null || phone.isEmpty()) {
            return "";
        }
        // إزالة الأحرف غير الرقمية
        String cleaned = phone.replaceAll("[^0-9]", "");
        
        // إذا كان يبدأ بـ 0 وطوله 10 أرقام
        if (cleaned.startsWith("0") && cleaned.length() == 10) {
            return String.format("%s-%s-%s-%s", 
                cleaned.substring(0, 3),
                cleaned.substring(3, 5),
                cleaned.substring(5, 8),
                cleaned.substring(8));
        }
        
        return cleaned;
    }
    
    /**
     * التحقق من صحة رقم الهاتف
     */
    public static boolean isValidPhoneNumber(String phone) {
        if (phone == null || phone.isEmpty()) {
            return false;
        }
        String cleaned = phone.replaceAll("[^0-9]", "");
        return cleaned.length() >= 9 && cleaned.length() <= 13;
    }
    
    /**
     * تنسيق الاستهلاك
     */
    public static String formatConsumption(double consumption) {
        return String.format(Locale.US, "%.2f", consumption);
    }
    
    /**
     * التحقق من صحة البريد الإلكتروني
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    
    /**
     * حساب الفرق بين تاريخين بالأيام
     */
    public static long getDaysDifference(long startDate, long endDate) {
        return (endDate - startDate) / (1000 * 60 * 60 * 24);
    }
    
    /**
     * الحصول على اسم الشهر
     */
    public static String getMonthName(int month) {
        String[] months = {
            "يناير", "فبراير", "مارس", "أبريل", "مايو", "يونيو",
            "يوليو", "أغسطس", "سبتمبر", "أكتوبر", "نوفمبر", "ديسمبر"
        };
        if (month >= 1 && month <= 12) {
            return months[month - 1];
        }
        return "";
    }
    
    /**
     * تحويل النص إلى عنوان
     */
    public static String toTitleCase(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    }
}
