package com.example.rakshak.model;

public class Ambulance {
    private String id;
    private double latitude;
    private double longitude;
    private boolean available;

    public Ambulance() {
    }

    public Ambulance(String id, double latitude, double longitude, boolean available) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.available = available;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}