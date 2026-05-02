package com.JVM.eCart.constants;

public class EmailConstants {

    private EmailConstants() {}

    public static final String ADMIN_EMAIL = "vishal.kaushik@tothenew.com";

    // Base URLs
    public static final String BASE_URL = "http://localhost:8080";
    public static final String ACTIVATE_PATH = "/auth/activate?token=";
    public static final String RESET_PASSWORD_PATH = "/auth/reset-password?token=";

    // Subjects
    public static final String SUBJECT_ACTIVATE_ACCOUNT = "Activate Your Account";
    public static final String SUBJECT_SELLER_REGISTRATION = "Seller Registration Received";
    public static final String SUBJECT_ACCOUNT_LOCKED = "Account Locked";
    public static final String SUBJECT_RESET_PASSWORD = "Reset Password";
    public static final String SUBJECT_PASSWORD_UPDATED = "Password Updated";
    public static final String SUBJECT_PRODUCT_ADDED = "Product Added: ";
    public static final String SUBJECT_ACCOUNT_ACTIVATED = "Account Activated";
    public static final String SUBJECT_ACCOUNT_DEACTIVATED = "Account Deactivated";

    // Messages
    public static final String MSG_ACTIVATE_ACCOUNT = "Click the link to activate your account:\n";
    public static final String MSG_SELLER_REGISTRATION = "Thank you for registering as a seller. Your application is under review. We will notify you once it's approved.";
    public static final String MSG_ACCOUNT_LOCKED = "Your account has been locked due to multiple failed attempts";
    public static final String MSG_RESET_PASSWORD = "Click the link to reset your Password:\n";
    public static final String MSG_PASSWORD_UPDATED = "Your password has been updated successfully";

}
