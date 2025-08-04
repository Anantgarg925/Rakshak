package com.example.rakshak.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.rakshak.model.ActiveAlert;
import com.example.rakshak.model.EmergencyRecord;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects; // Import Objects for null-safe equals
import java.util.Random;

public class AdminDataManager {

    private static final String PREFS_NAME = "rakshak_admin";
    private static final String KEY_ACTIVE_ALERTS = "active_alerts";
    private static final String KEY_SYSTEM_STATUS = "system_status";

    // FIX 2: Make fields final as they are initialized once
    private final SharedPreferences sharedPreferences;
    private final Gson gson;
    private final EmergencyDataManager emergencyDataManager;

    public interface ExportCallback {
        void onSuccess(String filePath);
        void onError(String error);
    }

    public AdminDataManager(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.gson = new Gson();
        this.emergencyDataManager = new EmergencyDataManager(context);
    }

    public List<ActiveAlert> getActiveAlerts() {
        String alertsJson = sharedPreferences.getString(KEY_ACTIVE_ALERTS, null);
        if (alertsJson != null) {
            Type listType = new TypeToken<List<ActiveAlert>>(){}.getType();
            List<ActiveAlert> alerts = gson.fromJson(alertsJson, listType);
            return alerts != null ? alerts : generateSampleAlerts();
        }
        return generateSampleAlerts();
    }

    private List<ActiveAlert> generateSampleAlerts() {
        List<ActiveAlert> sampleAlerts = new ArrayList<>();
        Random random = new Random();
        int alertCount = 3 + random.nextInt(3);

        for (int i = 0; i < alertCount; i++) {
            ActiveAlert alert = new ActiveAlert();
            alert.setUserId("user_" + (i + 1));
            alert.setLocation("Location " + (i + 1) + ", Delhi");
            alert.setLatitude(28.7041 + (random.nextDouble() - 0.5) * 0.1);
            alert.setLongitude(77.1025 + (random.nextDouble() - 0.5) * 0.1);
            alert.setTimestamp(System.currentTimeMillis() - (i * 5 * 60 * 1000));
            alert.setDescription("Automatic accident detection triggered");
            alert.setDeviceInfo("Rakshak Mobile v1.0");
            alert.setBatteryLevel(70 + random.nextInt(30));
            alert.setLocationAccurate(true);

            ActiveAlert.Priority[] priorities = ActiveAlert.Priority.values();
            alert.setPriority(priorities[random.nextInt(priorities.length)]);

            ActiveAlert.AlertStatus[] statuses = {
                    ActiveAlert.AlertStatus.NEW,
                    ActiveAlert.AlertStatus.ACKNOWLEDGED,
                    ActiveAlert.AlertStatus.IN_PROGRESS
            };
            alert.setStatus(statuses[random.nextInt(statuses.length)]);
            sampleAlerts.add(alert);
        }
        return sampleAlerts;
    }

    public void saveActiveAlerts(List<ActiveAlert> alerts) {
        String alertsJson = gson.toJson(alerts);
        sharedPreferences.edit()
                .putString(KEY_ACTIVE_ALERTS, alertsJson)
                .apply();
    }

    public int getActiveAlertsCount() {
        List<ActiveAlert> alerts = getActiveAlerts();
        int count = 0;
        for (ActiveAlert alert : alerts) {
            // FIX 1: Use !Objects.equals() for safe enum comparison
            if (!Objects.equals(alert.getStatus(), ActiveAlert.AlertStatus.RESOLVED) &&
                    !Objects.equals(alert.getStatus(), ActiveAlert.AlertStatus.FALSE_ALARM)) {
                count++;
            }
        }
        return count;
    }

    public int getTodayAlertsCount() {
        return emergencyDataManager.getTodayAlertsCount();
    }

    public String getAverageResponseTime() {
        Random random = new Random();
        int minutes = 2 + random.nextInt(8);
        return minutes + " min";
    }

    public boolean getSystemStatus() {
        return sharedPreferences.getBoolean(KEY_SYSTEM_STATUS, true);
    }

    public void setSystemStatus(boolean status) {
        sharedPreferences.edit().putBoolean(KEY_SYSTEM_STATUS, status).apply();
    }

    public List<EmergencyRecord> getEmergencyResponses() {
        List<EmergencyRecord> allRecords = emergencyDataManager.getAllEmergencyRecords();
        List<EmergencyRecord> responses = new ArrayList<>();
        for (EmergencyRecord record : allRecords) {
            if (!Objects.equals(record.getStatus(), EmergencyRecord.Status.PENDING)) {
                responses.add(record);
            }
        }
        if (responses.isEmpty()) {
            responses = generateSampleResponses();
        }
        return responses;
    }

    private List<EmergencyRecord> generateSampleResponses() {
        List<EmergencyRecord> sampleResponses = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 5; i++) {
            EmergencyRecord record = new EmergencyRecord();
            record.setId("response_" + i);
            record.setAlertId("alert_" + i); // Assuming an alertId exists
            record.setAlertType(EmergencyRecord.AlertType.ACCIDENT_DETECTED);
            record.setLocation("Response Location " + (i + 1));
            record.setLatitude(28.7041 + (random.nextDouble() - 0.5) * 0.1);
            record.setLongitude(77.1025 + (random.nextDouble() - 0.5) * 0.1);
            record.setTimestamp(System.currentTimeMillis() - (i * 60 * 60 * 1000));
            record.setStatus(EmergencyRecord.Status.RESPONDED);
            record.setResponseTime((2 + random.nextInt(8)) + " min");
            record.setResponderId("Emergency Team " + (i + 1));
            record.setNotes("Emergency response completed successfully");
            sampleResponses.add(record);
        }
        return sampleResponses;
    }

    public void dispatchEmergencyHelp(String alertId) {
        List<ActiveAlert> alerts = getActiveAlerts();
        for (ActiveAlert alert : alerts) {
            if (Objects.equals(alert.getId(), alertId)) {
                alert.setStatus(ActiveAlert.AlertStatus.IN_PROGRESS);
                alert.setResponderName("Emergency Team Alpha");
                break;
            }
        }
        saveActiveAlerts(alerts);
        EmergencyRecord record = new EmergencyRecord();
        record.setAlertId(alertId);
        record.setAlertType(EmergencyRecord.AlertType.ACCIDENT_DETECTED);
        record.setStatus(EmergencyRecord.Status.RESPONDED);
        record.setLocation("Emergency dispatch location");
        record.setResponseTime("< 1 min");
        record.setResponderId("Emergency Team Alpha");
        emergencyDataManager.saveEmergencyRecord(record);
    }

    public void markAsFalseAlarm(String alertId) {
        List<ActiveAlert> alerts = getActiveAlerts();
        for (ActiveAlert alert : alerts) {
            if (Objects.equals(alert.getId(), alertId)) {
                alert.setStatus(ActiveAlert.AlertStatus.FALSE_ALARM);
                break;
            }
        }
        saveActiveAlerts(alerts);
        EmergencyRecord record = new EmergencyRecord();
        record.setAlertId(alertId);
        record.setAlertType(EmergencyRecord.AlertType.FALSE_ALARM);
        record.setStatus(EmergencyRecord.Status.CANCELLED);
        record.setLocation("False alarm location");
        record.setNotes("Marked as false alarm by admin");
        emergencyDataManager.saveEmergencyRecord(record);
    }

    public void exportEmergencyData(ExportCallback callback, Context context) {
        new Thread(() -> {
            try {
                Thread.sleep(2000);
                String filePath = context.getExternalFilesDir(null).getAbsolutePath() + "/rakshak_emergency_data.csv";
                callback.onSuccess(filePath);
            } catch (Exception e) {
                callback.onError("Export failed: " + e.getMessage());
            }
        }).start();
    }

    public void clearAllData() {
        sharedPreferences.edit().clear().apply();
        emergencyDataManager.clearAllRecords();
    }
}