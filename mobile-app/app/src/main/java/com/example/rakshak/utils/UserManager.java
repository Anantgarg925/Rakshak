package com.example.rakshak.utils;

import android.content.Context;
import android.content.SharedPreferences;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UserManager {

    private static final String PREFS_NAME = "rakshak_user";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USER_PHONE = "user_phone";
    private static final String KEY_MEMBER_SINCE = "member_since";
    private static final String KEY_IS_VERIFIED = "is_verified";
    private static final String KEY_PROFILE_PHOTO_URI = "profile_photo_uri";
    private static final String KEY_EMERGENCY_INFO = "emergency_info";

    private SharedPreferences sharedPreferences;

    public UserManager(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void saveUserDetails(String name, String email, String phone) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM yyyy", Locale.getDefault());
        String memberSince = sdf.format(new Date());

        sharedPreferences.edit()
                .putString(KEY_USER_NAME, name)
                .putString(KEY_USER_EMAIL, email)
                .putString(KEY_USER_PHONE, phone)
                .putString(KEY_MEMBER_SINCE, memberSince)
                .putBoolean(KEY_IS_VERIFIED, false)
                .apply();
    }

    public String getUserName() {
        return sharedPreferences.getString(KEY_USER_NAME, "Anonymous User");
    }

    public void setUserName(String name) {
        sharedPreferences.edit().putString(KEY_USER_NAME, name).apply();
    }

    public String getUserEmail() {
        return sharedPreferences.getString(KEY_USER_EMAIL, null);
    }

    public void setUserEmail(String email) {
        sharedPreferences.edit().putString(KEY_USER_EMAIL, email).apply();
    }

    public String getUserPhone() {
        return sharedPreferences.getString(KEY_USER_PHONE, null);
    }

    public void setUserPhone(String phone) {
        sharedPreferences.edit().putString(KEY_USER_PHONE, phone).apply();
    }

    public String getMemberSince() {
        return sharedPreferences.getString(KEY_MEMBER_SINCE, "Unknown");
    }

    public boolean isVerified() {
        return sharedPreferences.getBoolean(KEY_IS_VERIFIED, false);
    }

    public void setVerified(boolean verified) {
        sharedPreferences.edit().putBoolean(KEY_IS_VERIFIED, verified).apply();
    }

    public String getProfilePhotoUri() {
        return sharedPreferences.getString(KEY_PROFILE_PHOTO_URI, null);
    }

    public void setProfilePhotoUri(String uri) {
        sharedPreferences.edit().putString(KEY_PROFILE_PHOTO_URI, uri).apply();
    }

    public String getEmergencyInfo() {
        return sharedPreferences.getString(KEY_EMERGENCY_INFO, null);
    }

    public void setEmergencyInfo(String info) {
        sharedPreferences.edit().putString(KEY_EMERGENCY_INFO, info).apply();
    }

    public void clearUserData() {
        sharedPreferences.edit().clear().apply();
    }

    public boolean hasProfileData() {
        return getUserName() != null && !getUserName().equals("Anonymous User");
    }

    public String getUserInitials() {
        String name = getUserName();
        if (name != null && !name.equals("Anonymous User")) {
            String[] parts = name.split(" ");
            StringBuilder initials = new StringBuilder();
            for (String part : parts) {
                if (!part.isEmpty()) {
                    initials.append(part.charAt(0));
                }
            }
            return initials.toString().toUpperCase();
        }
        return "AU";
    }

    public String getContactInfo() {
        StringBuilder info = new StringBuilder();

        String name = getUserName();
        if (name != null) {
            info.append("Name: ").append(name).append("\n");
        }

        String phone = getUserPhone();
        if (phone != null) {
            info.append("Phone: ").append(phone).append("\n");
        }

        String email = getUserEmail();
        if (email != null) {
            info.append("Email: ").append(email).append("\n");
        }

        return info.toString();
    }
}