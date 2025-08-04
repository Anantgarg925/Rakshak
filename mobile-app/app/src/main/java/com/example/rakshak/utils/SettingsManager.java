package com.example.rakshak.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingsManager {

    private static final String PREFS_NAME = "rakshak_settings";

    // Setting keys
    private static final String KEY_AUTO_START = "auto_start_enabled";
    private static final String KEY_HAPTIC_FEEDBACK = "haptic_feedback_enabled";
    private static final String KEY_VOICE_COMMANDS = "voice_commands_enabled";
    private static final String KEY_NOTIFICATIONS = "notifications_enabled";
    private static final String KEY_LOCATION_SHARING = "location_sharing_enabled";
    private static final String KEY_TEST_MODE = "test_mode_enabled";
    private static final String KEY_SENSITIVITY_LEVEL = "sensitivity_level";
    private static final String KEY_COUNTDOWN_DURATION = "countdown_duration";

    private SharedPreferences sharedPreferences;

    public SettingsManager(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    // Auto-start monitoring
    public boolean isAutoStartEnabled() {
        return sharedPreferences.getBoolean(KEY_AUTO_START, true);
    }

    public void setAutoStartEnabled(boolean enabled) {
        sharedPreferences.edit().putBoolean(KEY_AUTO_START, enabled).apply();
    }

    // Haptic feedback
    public boolean isHapticFeedbackEnabled() {
        return sharedPreferences.getBoolean(KEY_HAPTIC_FEEDBACK, true);
    }

    public void setHapticFeedbackEnabled(boolean enabled) {
        sharedPreferences.edit().putBoolean(KEY_HAPTIC_FEEDBACK, enabled).apply();
    }

    // Voice commands
    public boolean isVoiceCommandsEnabled() {
        return sharedPreferences.getBoolean(KEY_VOICE_COMMANDS, false);
    }

    public void setVoiceCommandsEnabled(boolean enabled) {
        sharedPreferences.edit().putBoolean(KEY_VOICE_COMMANDS, enabled).apply();
    }

    // Notifications
    public boolean isNotificationsEnabled() {
        return sharedPreferences.getBoolean(KEY_NOTIFICATIONS, true);
    }

    public void setNotificationsEnabled(boolean enabled) {
        sharedPreferences.edit().putBoolean(KEY_NOTIFICATIONS, enabled).apply();
    }

    // Location sharing
    public boolean isLocationSharingEnabled() {
        return sharedPreferences.getBoolean(KEY_LOCATION_SHARING, true);
    }

    public void setLocationSharingEnabled(boolean enabled) {
        sharedPreferences.edit().putBoolean(KEY_LOCATION_SHARING, enabled).apply();
    }

    // Test mode
    public boolean isTestModeEnabled() {
        return sharedPreferences.getBoolean(KEY_TEST_MODE, false);
    }

    public void setTestModeEnabled(boolean enabled) {
        sharedPreferences.edit().putBoolean(KEY_TEST_MODE, enabled).apply();
    }

    // Sensitivity level (1-5, 3 is default)
    public int getSensitivityLevel() {
        return sharedPreferences.getInt(KEY_SENSITIVITY_LEVEL, 3);
    }

    public void setSensitivityLevel(int level) {
        if (level >= 1 && level <= 5) {
            sharedPreferences.edit().putInt(KEY_SENSITIVITY_LEVEL, level).apply();
        }
    }

    // Countdown duration (in seconds, 15 is default)
    public int getCountdownDuration() {
        return sharedPreferences.getInt(KEY_COUNTDOWN_DURATION, 15);
    }

    public void setCountdownDuration(int duration) {
        if (duration >= 5 && duration <= 60) {
            sharedPreferences.edit().putInt(KEY_COUNTDOWN_DURATION, duration).apply();
        }
    }

    public void resetToDefaults() {
        sharedPreferences.edit().clear().apply();
    }

    public String getSettingsSummary() {
        return String.format(
                "Auto-start: %s\nHaptic: %s\nVoice: %s\nNotifications: %s\nLocation: %s\nTest mode: %s",
                isAutoStartEnabled() ? "ON" : "OFF",
                isHapticFeedbackEnabled() ? "ON" : "OFF",
                isVoiceCommandsEnabled() ? "ON" : "OFF",
                isNotificationsEnabled() ? "ON" : "OFF",
                isLocationSharingEnabled() ? "ON" : "OFF",
                isTestModeEnabled() ? "ON" : "OFF"
        );
    }
}