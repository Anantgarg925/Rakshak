package com.example.rakshak.model;

public class User {
    private String name;
    private String phoneNumber;
    private String emergencyContact;
    private String deviceId;
     private String bloodType;
    private String medicalConditions;
    private String allergies;

    public User() {}

    public User(String name, String phoneNumber, String emergencyContact, String deviceId) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.emergencyContact = emergencyContact;
        this.deviceId = deviceId;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getEmergencyContact() { return emergencyContact; }
    public void setEmergencyContact(String emergencyContact) { this.emergencyContact = emergencyContact; }
    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
     public String getBloodType() { return bloodType; }
    public void setBloodType(String bloodType) { this.bloodType = bloodType; }
    public String getMedicalConditions() { return medicalConditions; }
    public void setMedicalConditions(String medicalConditions) { this.medicalConditions = medicalConditions; }
    public String getAllergies() { return allergies; }
    public void setAllergies(String allergies) { this.allergies = allergies; }
}