package com.example.rakshak.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import com.example.rakshak.R;
import com.example.rakshak.ui.activities.EmergencyContactsActivity;
import com.example.rakshak.ui.activities.NotificationSettingsActivity;
import com.example.rakshak.utils.SettingsManager;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingsFragment extends Fragment {

    private SwitchMaterial switchAutoStart, switchHapticFeedback, switchVoiceCommands, 
                          switchNotifications, switchLocationSharing, switchTestMode;
    private MaterialCardView cardEmergencyContacts, cardNotificationSettings, 
                           cardLocationSettings, cardAppInfo;
    private TextView tvAutoStartDesc, tvHapticDesc, tvVoiceDesc, tvNotificationDesc,
                   tvLocationDesc, tvTestModeDesc;
    
    private SettingsManager settingsManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        
        initializeViews(view);
        setupSettingsManager();
        loadCurrentSettings();
        setupClickListeners();
        
        return view;
    }

    @SuppressLint("WrongViewCast")
    private void initializeViews(View view) {
        switchAutoStart = view.findViewById(R.id.switch_auto_start);
        switchHapticFeedback = view.findViewById(R.id.switch_haptic_feedback);
        switchVoiceCommands = view.findViewById(R.id.switch_voice_commands);
        switchNotifications = view.findViewById(R.id.switch_notifications);
        switchLocationSharing = view.findViewById(R.id.switch_location_sharing);
        switchTestMode = view.findViewById(R.id.switch_test_mode);
        
        cardEmergencyContacts = view.findViewById(R.id.card_emergency_contacts);
        cardNotificationSettings = view.findViewById(R.id.card_notification_settings);
        cardLocationSettings = view.findViewById(R.id.card_location_settings);
        cardAppInfo = view.findViewById(R.id.card_app_info);
        
        tvAutoStartDesc = view.findViewById(R.id.tv_auto_start_desc);
        tvHapticDesc = view.findViewById(R.id.tv_haptic_desc);
        tvVoiceDesc = view.findViewById(R.id.tv_voice_desc);
        tvNotificationDesc = view.findViewById(R.id.tv_notification_desc);
        tvLocationDesc = view.findViewById(R.id.tv_location_desc);
        tvTestModeDesc = view.findViewById(R.id.tv_test_mode_desc);
    }

    private void setupSettingsManager() {
        settingsManager = new SettingsManager(requireContext());
    }

    private void loadCurrentSettings() {
        switchAutoStart.setChecked(settingsManager.isAutoStartEnabled());
        switchHapticFeedback.setChecked(settingsManager.isHapticFeedbackEnabled());
        switchVoiceCommands.setChecked(settingsManager.isVoiceCommandsEnabled());
        switchNotifications.setChecked(settingsManager.isNotificationsEnabled());
        switchLocationSharing.setChecked(settingsManager.isLocationSharingEnabled());
        switchTestMode.setChecked(settingsManager.isTestModeEnabled());
        
        updateDescriptions();
    }

    private void updateDescriptions() {
        // Update descriptions based on current settings
        tvAutoStartDesc.setText(switchAutoStart.isChecked() ? 
            "Monitoring will start automatically when app opens" : 
            "Manual start required for monitoring");
            
        tvHapticDesc.setText(switchHapticFeedback.isChecked() ? 
            "Vibration feedback enabled for alerts" : 
            "Vibration feedback disabled");
            
        tvVoiceDesc.setText(switchVoiceCommands.isChecked() ? 
            "Voice commands enabled for emergency situations" : 
            "Voice commands disabled");
            
        tvNotificationDesc.setText(switchNotifications.isChecked() ? 
            "Push notifications enabled" : 
            "Push notifications disabled");
            
        tvLocationDesc.setText(switchLocationSharing.isChecked() ? 
            "Location sharing enabled with emergency contacts" : 
            "Location sharing disabled");
            
        tvTestModeDesc.setText(switchTestMode.isChecked() ? 
            "Test mode active - simulated accidents only" : 
            "Live monitoring mode active");
    }

    private void setupClickListeners() {
        switchAutoStart.setOnCheckedChangeListener((buttonView, isChecked) -> {
            settingsManager.setAutoStartEnabled(isChecked);
            updateDescriptions();
            showToast(isChecked ? "🔄 Auto-start enabled" : "⏸️ Auto-start disabled");
        });

        switchHapticFeedback.setOnCheckedChangeListener((buttonView, isChecked) -> {
            settingsManager.setHapticFeedbackEnabled(isChecked);
            updateDescriptions();
            if (isChecked) {
                // Test haptic feedback
                buttonView.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY);
            }
            showToast(isChecked ? "📳 Haptic feedback enabled" : "🔇 Haptic feedback disabled");
        });

        switchVoiceCommands.setOnCheckedChangeListener((buttonView, isChecked) -> {
            settingsManager.setVoiceCommandsEnabled(isChecked);
            updateDescriptions();
            showToast(isChecked ? "🎤 Voice commands enabled" : "🤫 Voice commands disabled");
        });

        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            settingsManager.setNotificationsEnabled(isChecked);
            updateDescriptions();
            showToast(isChecked ? "🔔 Notifications enabled" : "🔕 Notifications disabled");
        });

        switchLocationSharing.setOnCheckedChangeListener((buttonView, isChecked) -> {
            settingsManager.setLocationSharingEnabled(isChecked);
            updateDescriptions();
            showToast(isChecked ? "📍 Location sharing enabled" : "🚫 Location sharing disabled");
        });

        switchTestMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            settingsManager.setTestModeEnabled(isChecked);
            updateDescriptions();
            showToast(isChecked ? "🧪 Test mode enabled" : "🚨 Live mode enabled");
        });

        cardEmergencyContacts.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), EmergencyContactsActivity.class);
            startActivity(intent);
        });

        cardNotificationSettings.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), NotificationSettingsActivity.class);
            startActivity(intent);
        });

        cardLocationSettings.setOnClickListener(v -> {
            // Open location settings
            showToast("🗺️ Location settings");
        });

        cardAppInfo.setOnClickListener(v -> {
            // Show app info dialog
            showAppInfoDialog();
        });
    }

    private void showAppInfoDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(requireContext());
        builder.setTitle("Rakshak - Smart Emergency Alert System")
               .setMessage("Version: 1.0.0\nBuild: 2025.01.14\n\nDeveloped with ❤️ for emergency response\n\nFeatures:\n• Real-time accident detection\n• Automatic emergency alerts\n• GPS location tracking\n• Emergency contact management\n• Offline mode support")
               .setPositiveButton("OK", null)
               .show();
    }

    private void showToast(String message) {
        if (getContext() != null) {
            android.widget.Toast.makeText(getContext(), message, android.widget.Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCurrentSettings();
    }
}