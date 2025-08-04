package com.example.rakshak.model;

import java.util.Objects;
import java.util.Locale; // For String.format in example toString

public class SensorData {

    public void setActive(boolean isDetectionActive) {
    }

    // If you need distinct types beyond just the name, use this enum.
    // If name itself is the primary type, you might not need this explicitly stored
    // if other logic is purely based on the name string.
    public enum SensorType {
        ACCELEROMETER,
        GYROSCOPE,
        GPS,
        CONNECTIVITY,
        DEVICE_STATUS, // Added from adapter logic
        UNKNOWN
    }

    // Status enum as you had it
    public enum Status {
        ACTIVE("Active"),
        INACTIVE("Inactive"),
        ALERT("Alert"),
        POOR("Poor"),       // From original code
        SEARCHING("Searching"); // Added from adapter logic

        private final String displayName;

        Status(String displayName) {
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }

    private String name; // Name of the sensor (e.g., "Accelerometer")
    private String description; // Optional detailed description
    private String unit;        // e.g., "m/s²", "°", "%"
    private Object value;       // Can be Float, Double, Integer, String etc.
    private Status status;
    private SensorType sensorType; // Using the enum for type

    // GPS specific fields - consider a subclass or composition if this grows more
    private double latitude;
    private double longitude;
    private float accuracy;

    
    // Constructor for basic sensors
    public SensorData(String name, Object value, String unit, Status status, SensorType sensorType) {
        this.name = name;
        this.value = value;
        this.unit = unit;
        this.status = status;
        this.sensorType = (sensorType != null) ? sensorType : SensorType.UNKNOWN;
        this.description = ""; // Default description
    }

    // Constructor including description
    public SensorData(String name, String description, Object value, String unit, Status status, SensorType sensorType) {
        this(name, value, unit, status, sensorType); // Call other constructor
        this.description = description;
    }

    // Constructor for GPS type data
    public SensorData(String name, double latitude, double longitude, float accuracy, Status status) {
        this.name = name;
        this.value = String.format(Locale.US, "Lat: %.4f, Lon: %.4f", latitude, longitude); // Store GPS as a string value for simplicity
        this.unit = "coords";
        this.status = status;
        this.sensorType = SensorType.GPS;
        this.latitude = latitude;
        this.longitude = longitude;
        this.accuracy = accuracy;
        this.description = "GPS Coordinates";
    }


    // Getters
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getUnit() { return unit; }
    public Object getValue() { return value; } // Consumer needs to know how to interpret this
    public Enum<Status> getStatus() { return status; }
    public SensorType getSensorType() { return sensorType; }

    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public float getAccuracy() { return accuracy; }


    // Setters
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setUnit(String unit) { this.unit = unit; }
    public void setValue(Object value) { this.value = value; }
    public void setStatus(Status status) { this.status = (status != null) ? status : Status.INACTIVE; }
    public void setSensorType(SensorType sensorType) { this.sensorType = (sensorType != null) ? sensorType : SensorType.UNKNOWN; }

    public void setGpsData(double latitude, double longitude, float accuracy) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.accuracy = accuracy;
        // Optionally update the main 'value' field as well if this sensor is primarily GPS
        this.value = String.format(Locale.US, "Lat: %.4f, Lon: %.4f", latitude, longitude);
        this.sensorType = SensorType.GPS; // Ensure type is set
    }


    // --- Helper methods for UI (similar to what was in SensorAdapter or useful here) ---

    public String getFormattedValue() {
        if (value instanceof Float) {
            return String.format(Locale.getDefault(), "%.2f", (Float) value);
        } else if (value instanceof Double) {
            return String.format(Locale.getDefault(), "%.2f", (Double) value);
        } else if (value instanceof Integer) {
            return String.format(Locale.getDefault(), "%d", (Integer) value);
        } else if (value != null) {
            return value.toString();
        }
        return "N/A";
    }

    // This method returns the display name from the enum
    public String getStatusText() {
        return (status != null) ? status.toString() : Status.INACTIVE.toString();
    }

    public int getStatusColor() {
        if (status == null) return android.R.color.darker_gray; // Default
        switch (status) {
            case ACTIVE:
                return android.R.color.holo_green_dark; // Or your R.color.success_green
            case ALERT:
                return android.R.color.holo_red_dark;   // Or your R.color.emergency_red
            case POOR:
                return android.R.color.holo_orange_dark;
            case SEARCHING:
                return android.R.color.holo_blue_light; // Or your R.color.warning_yellow
            case INACTIVE:
            default:
                return android.R.color.darker_gray;
        }
    }


    // --- equals() and hashCode() ---

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SensorData that = (SensorData) o;
        return Double.compare(that.latitude, latitude) == 0 &&
                Double.compare(that.longitude, longitude) == 0 &&
                Float.compare(that.accuracy, accuracy) == 0 &&
                Objects.equals(name, that.name) &&
                Objects.equals(description, that.description) &&
                Objects.equals(unit, that.unit) &&
                Objects.equals(value, that.value) && // Objects.equals handles null for value
                status == that.status &&
                sensorType == that.sensorType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, unit, value, status, sensorType, latitude, longitude, accuracy);
    }

    // Example toString() for debugging
    @Override
    public String toString() {
        return "SensorData{" +
                "name='" + name + '\'' +
                ", value=" + getFormattedValue() +
                ", unit='" + unit + '\'' +
                ", status=" + status +
                ", type=" + sensorType +
                '}';
    }
}
