package com.example.rakshak.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthManager {

    private static final String PREFS_NAME = "rakshak_auth";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_IS_ADMIN = "is_admin";
    private static final String KEY_LOGIN_TIME = "login_time";
    private static final String ADMIN_CODE = "RAKSHAK2025"; // In real app, this would be encrypted/secured

    private SharedPreferences sharedPreferences;

    public AuthManager(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public boolean isLoggedIn() {
        return FirebaseAuth.getInstance().getCurrentUser()!=null;
    }

    public void login(String userId, boolean isAdmin) {
        sharedPreferences.edit()
                .putBoolean(KEY_IS_LOGGED_IN, true)
                .putString(KEY_USER_ID, userId)
                .putBoolean(KEY_IS_ADMIN, isAdmin)
                .putLong(KEY_LOGIN_TIME, System.currentTimeMillis())
                .apply();
    }

    public void logout() {
       FirebaseAuth.getInstance().signOut();
    }

    public String getUserId() {
        return sharedPreferences.getString(KEY_USER_ID, null);
    }

    public boolean isAdmin() {
        return sharedPreferences.getBoolean(KEY_IS_ADMIN, false);
    }

    public long getLoginTime() {
        return sharedPreferences.getLong(KEY_LOGIN_TIME, 0);
    }

    public boolean verifyAdminCode(String code) {
        return ADMIN_CODE.equals(code);
    }

    public void grantAdminAccess() {
        if (isLoggedIn()) {
            sharedPreferences.edit()
                    .putBoolean(KEY_IS_ADMIN, true)
                    .apply();
        }
    }

    public boolean authenticateUser(String email, String password) {
        // In a real app, this would validate against a backend service
        // For demo purposes, we'll accept any non-empty credentials
        if (email != null && !email.trim().isEmpty() &&
                password != null && !password.trim().isEmpty()) {

            String userId = "user_" + System.currentTimeMillis();
            boolean isAdmin = email.contains("admin") || email.contains("hospital");
            login(userId, isAdmin);
            return true;
        }
        return false;
    }

    public boolean registerUser(String name, String email, String password, String phone) {
        // In a real app, this would register with a backend service
        // For demo purposes, we'll create a local user
        if (name != null && !name.trim().isEmpty() &&
                email != null && !email.trim().isEmpty() &&
                password != null && !password.trim().isEmpty()) {

            String userId = "user_" + System.currentTimeMillis();
            login(userId, false);

            // Save user details
            UserManager userManager = new UserManager((Context) sharedPreferences.getAll());
            userManager.saveUserDetails(name, email, phone);

            return true;
        }
        return false;
    }
}