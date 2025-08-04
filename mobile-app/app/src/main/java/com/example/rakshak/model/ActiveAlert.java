package com.example.rakshak.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ActiveAlert {

    public enum Priority {
        LOW, MEDIUM, HIGH, CRITICAL
    }

    public enum AlertStatus {
        NEW, ACKNOWLEDGED, IN_PROGRESS, RESOLVED, FALSE_ALARM
    }

    private String id;
    private String userId;
    private String location;
    private double latitude;
    private double longitude;
    private long timestamp;
    private Priority priority;
    private AlertStatus status;
    private String description;
    private String responderName;
    private String deviceInfo;
    private float batteryLevel;
    private boolean locationAccurate;

    public ActiveAlert() {
        this.timestamp = System.currentTimeMillis();
        this.id = "alert_" + timestamp;
        this.status = AlertStatus.NEW;
        this.priority = Priority.HIGH;
    }

    public ActiveAlert(String userId, String location, double latitude, double longitude) {
        this();
        this.userId = userId;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Getters
    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getLocation() { return location; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public long getTimestamp() { return timestamp; }
    public Priority getPriority() { return priority; }
    public AlertStatus getStatus() { return status; }
    public String getDescription() { return description; }
    public String getResponderName() { return responderName; }
    public String getDeviceInfo() { return deviceInfo; }
    public float getBatteryLevel() { return batteryLevel; }
    public boolean isLocationAccurate() { return locationAccurate; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setLocation(String location) { this.location = location; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public void setPriority(Priority priority) { this.priority = priority; }
    public void setStatus(AlertStatus status) { this.status = status; }
    public void setDescription(String description) { this.description = description; }
    public void setResponderName(String responderName) { this.responderName = responderName; }
    public void setDeviceInfo(String deviceInfo) { this.deviceInfo = deviceInfo; }
    public void setBatteryLevel(float batteryLevel) { this.batteryLevel = batteryLevel; }
    public void setLocationAccurate(boolean locationAccurate) { this.locationAccurate = locationAccurate; }

    public String getTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    public String getFormattedTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    public String getPriorityText() {
        switch (priority) {
            case LOW: return "Low";
            case MEDIUM: return "Medium";
            case HIGH: return "High";
            case CRITICAL: return "Critical";
            default: return "Unknown";
        }
    }

    public String getStatusText() {
        switch (status) {
            case NEW: return "New";
            case ACKNOWLEDGED: return "Acknowledged";
            case IN_PROGRESS: return "In Progress";
            case RESOLVED: return "Resolved";
            case FALSE_ALARM: return "False Alarm";
            default: return "Unknown";
        }
    }

    public int getPriorityColor() {
        switch (priority) {
            case LOW: return android.R.color.holo_green_light;
            case MEDIUM: return android.R.color.holo_orange_light;
            case HIGH: return android.R.color.holo_red_light;
            case CRITICAL: return android.R.color.holo_red_dark;
            default: return android.R.color.black;
        }
    }

    public int getStatusColor() {
        switch (status) {
            case NEW: return android.R.color.holo_red_dark;
            case ACKNOWLEDGED: return android.R.color.holo_orange_dark;
            case IN_PROGRESS: return android.R.color.holo_blue_dark;
            case RESOLVED: return android.R.color.holo_green_dark;
            case FALSE_ALARM: return android.R.color.darker_gray;
            default: return android.R.color.black;
        }
    }

    public String getElapsedTime() {
        long elapsed = System.currentTimeMillis() - timestamp;
        long minutes = elapsed / (1000 * 60);

        if (minutes < 60) {
            return minutes + "m ago";
        } else {
            long hours = minutes / 60;
            return hours + "h ago";
        }
    }

    public boolean isCritical() {
        return priority == Priority.CRITICAL ||
                (System.currentTimeMillis() - timestamp) > (10 * 60 * 1000); // 10 minutes
    }
}