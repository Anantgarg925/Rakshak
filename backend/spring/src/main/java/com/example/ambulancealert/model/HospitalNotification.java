package com.example.ambulancealert.model;

public class HospitalNotification {
    private String ambulanceId;
    private String deviceId;
    private String status; // e.g., "In Transit"
    private int etaMinutes;

    public HospitalNotification(String ambulanceId, String deviceId, String status, int etaMinutes) {
        this.ambulanceId = ambulanceId;
        this.deviceId = deviceId;
        this.status = status;
        this.etaMinutes = etaMinutes;
    }

    // Getters & Setters
    public String getAmbulanceId() { return ambulanceId; }
    public String getDeviceId() { return deviceId; }
    public String getStatus() { return status; }
    public int getEtaMinutes() { return etaMinutes; }
}
