package com.example.rakshak.model;

public class AccidentReport {
    private String deviceId;
    private double latitude;
    private double longitude;
    private String severity;
    private String timestamp; // Send timestamp as an ISO-8601 formatted string

    public AccidentReport() {
    }

    public AccidentReport(String deviceId, double latitude, double longitude, String severity, String timestamp) {
        this.deviceId = deviceId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.severity = severity;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}