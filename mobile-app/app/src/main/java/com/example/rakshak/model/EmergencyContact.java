package com.example.rakshak.model;

public class EmergencyContact {

    private String id;
    private String name;
    private String phoneNumber;
    private String relationship;
    private boolean isPrimary;
    private String email;

    public EmergencyContact() {}

    public EmergencyContact(String id, String name, String phoneNumber, String relationship, boolean isPrimary) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.relationship = relationship;
        this.isPrimary = isPrimary;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getRelationship() { return relationship; }
    public boolean isPrimary() { return isPrimary; }
    public String getEmail() { return email; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setRelationship(String relationship) { this.relationship = relationship; }
    public void setPrimary(boolean primary) { isPrimary = primary; }
    public void setEmail(String email) { this.email = email; }

    public String getFormattedName() {
        return name + " (" + relationship + ")";
    }

    public String getContactInfo() {
        return name + "\n" + phoneNumber + "\n" + relationship;
    }
}