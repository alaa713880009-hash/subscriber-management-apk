package com.example.subscriber.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class EncryptionUtil {
    
    /**
     * تشفير كلمة المرور باستخدام SHA-256
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return password;
        }
    }
    
    /**
     * التحقق من كلمة المرور
     */
    public static boolean verifyPassword(String password, String hash) {
        return hashPassword(password).equals(hash);
    }
    
    /**
     * توليد رقم عشوائي
     */
    public static String generateRandomId() {
        return System.currentTimeMillis() + "_" + (int)(Math.random() * 10000);
    }
}
