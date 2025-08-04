package com.example.ambulancealert.model;

public class Ambulance {
    private String id;
    private double latitude;
    private double longitude;
    private boolean available;

    public Ambulance(String id, double latitude, double longitude, boolean available) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.available = available;
    }

    // Getters & Setters
    public String getId() { return id; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
     public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
