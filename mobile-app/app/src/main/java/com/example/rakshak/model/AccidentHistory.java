package com.example.rakshak.model;

// This class represents a single history item received from the backend.
public class AccidentHistory {
    private String id;
    private String timestamp; // Received as a string like "2025-07-28T12:30:00"
    private double latitude;
    private double longitude;
    private String ambulanceId;

    // Getters
    public String getId() { return id; }
    public String getTimestamp() { return timestamp; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public String getAmbulanceId() { return ambulanceId; }
}