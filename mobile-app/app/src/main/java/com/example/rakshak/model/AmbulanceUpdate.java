package com.example.rakshak.model;

public class AmbulanceUpdate {
    private String ambulanceId;
    private double latitude;
    private double longitude;
    private String status;

    // No-argument constructor for Gson
    public AmbulanceUpdate() {
    }

    // Getters
    public String getAmbulanceId() {
        return ambulanceId;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getStatus() {
        return status;
    }
}