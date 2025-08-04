package com.example.rakshak.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EmergencyRecord {

    public String getType() {
        return this.getAlertTypeText();
    }

    public void setResponder(String s) {
    }

    public enum AlertType {
        ACCIDENT_DETECTED,
        MANUAL_SOS,
        TEST_ALERT,
        FALSE_ALARM
    }

    public enum Status {
        PENDING,
        SENT,
        RESPONDED,
        RESOLVED,
        CANCELLED
    }

    private String id;
    private AlertType alertType;
    private Status status;
    private long timestamp;
    private String location;
    private double latitude;
    private double longitude;
    private String responseTime;
    private String notes;
    private boolean wasResolved;

    // --- ADDED/RENAMED FIELDS ---
    private String alertId;      // ADDED: For linking to the original alert
    private String responderId;  // RENAMED: from 'responder' for clarity

    public EmergencyRecord() {
        this.timestamp = System.currentTimeMillis();
        this.id = String.valueOf(timestamp);
    }

    public EmergencyRecord(AlertType alertType, String location, double latitude, double longitude) {
        this();
        this.alertType = alertType;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.status = Status.PENDING;
    }

    // --- GETTERS ---
    public String getId() { return id; }
    public AlertType getAlertType() { return alertType; }
    public Status getStatus() { return status; }
    public long getTimestamp() { return timestamp; }
    public String getLocation() { return location; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public String getResponseTime() { return responseTime; }
    public String getNotes() { return notes; }

    public boolean isWasResolved() { return wasResolved; }

    // --- ADDED/RENAMED GETTERS ---
    public String getAlertId() { return alertId; }
    public String getResponderId() { return responderId; } // RENAMED from getResponder()

    // --- SETTERS ---
    public void setId(String id) { this.id = id; }
    public void setAlertType(AlertType alertType) { this.alertType = alertType; }
    public void setStatus(Status status) { this.status = status; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public void setLocation(String location) { this.location = location; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    public void setResponseTime(String responseTime) { this.responseTime = responseTime; }
    public void setNotes(String notes) { this.notes = notes; }
    public void setWasResolved(boolean wasResolved) { this.wasResolved = wasResolved; }

    // --- ADDED/RENAMED SETTERS ---
    public void setAlertId(String alertId) { this.alertId = alertId; }
    public void setResponderId(String responderId) { this.responderId = responderId; } // RENAMED from setResponder()

    // --- HELPER METHODS ---
    public String getFormattedTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    public String getTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    public String getAlertTypeText() {
        if (alertType == null) return "Unknown";
        switch (alertType) {
            case ACCIDENT_DETECTED:
                return "Accident Detected";
            case MANUAL_SOS:
                return "Manual SOS";
            case TEST_ALERT:
                return "Test Alert";
            case FALSE_ALARM:
                return "False Alarm";
            default:
                return "Unknown";
        }
    }

    public String getStatusText() {
        if (status == null) return "Unknown";
        switch (status) {
            case PENDING:
                return "Pending";
            case SENT:
                return "Sent";
            case RESPONDED:
                return "Responded";
            case RESOLVED:
                return "Resolved";
            case CANCELLED:
                return "Cancelled";
            default:
                return "Unknown";
        }
    }

    public int getStatusColor() {
        if (status == null) return android.R.color.black;
        switch (status) {
            case PENDING:
                return android.R.color.holo_orange_dark;
            case SENT:
                return android.R.color.holo_blue_dark;
            case RESPONDED:
                return android.R.color.holo_green_dark;
            case RESOLVED:
                return android.R.color.holo_green_light;
            case CANCELLED:
                return android.R.color.darker_gray;
            default:
                return android.R.color.black;
        }
    }

    public String getEmoji() {
        if (alertType == null) return "📋";
        switch (alertType) {
            case ACCIDENT_DETECTED:
                return "🚨";
            case MANUAL_SOS:
                return "🆘";
            case TEST_ALERT:
                return "🧪";
            case FALSE_ALARM:
                return "❌";
            default:
                return "📋";
        }
    }
}