package com.example.ambulancealert.model;

import java.time.LocalDateTime;

public class AccidentReport {
    private String deviceId;
    private double latitude;
    private double longitude;
    private String severity;
    private LocalDateTime timestamp;

    // Getters & Setters
    public String getDeviceId(){
        return deviceId;
    }
    public double getLatitude(){
        return latitude;
    }
    public double getLongitude(){
        return longitude;
    }

    public void setseverity(String severity){
        this.severity=severity;

    }
    public void setTimestamp(LocalDateTime timestamp){
        this.timestamp=timestamp;
    }
}
