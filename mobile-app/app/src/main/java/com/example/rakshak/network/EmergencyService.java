package com.example.rakshak.network;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import com.example.rakshak.model.EmergencyContact;
import com.example.rakshak.utils.LocationManager;
import com.example.rakshak.utils.ContactManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EmergencyService {

    // FIX 3: Added 'final' keyword for best practices
    private final Context context;
    private final LocationManager locationManager;
    private final ContactManager contactManager;
    private final Handler handler = new Handler(Looper.getMainLooper());


    public interface EmergencyCallback {
        void onSuccess();
        void onError(String error);
    }

    public EmergencyService(Context context) {
        this.context = context;
        this.locationManager = new LocationManager(context);
        this.contactManager = new ContactManager(context);
    }

    public void sendEmergencyAlert(EmergencyCallback callback) {
        locationManager.getCurrentLocation(new LocationManager.LocationResultListener() {
            @Override
            public void onLocationResult(Location location) {
                // Start background thread for sending alerts
                new Thread(() -> {
                    try {
                        List<EmergencyContact> contacts = contactManager.getEmergencyContacts();
                        String message = prepareEmergencyMessage(location);
                        sendSMSAlerts(contacts, message);

                        // Call emergency services
                        callEmergencyServices();

                        // Send to nearest hospital
                        notifyNearestHospital(location);

                        // Success callback on the main thread
                        handler.post(callback::onSuccess);

                    } catch (Exception e) {
                        handler.post(() -> callback.onError(e.getMessage()));
                    }
                }).start();
            }

            // FIX 1: Implemented the missing onLocationError method
            @Override
            public void onLocationError(String error) {
                // Propagate the error back to the activity
                handler.post(() -> callback.onError(error));
            }
        });
    }

    private String prepareEmergencyMessage(Location location) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String timestamp = sdf.format(new Date());

        String locationText = "Unknown location";
        if (location != null) {
            locationText = String.format(Locale.US, "Lat: %.6f, Lng: %.6f",
                    location.getLatitude(), location.getLongitude());
        }

        return String.format(
                "🚨 EMERGENCY ALERT 🚨\n" +
                        "Rakshak has detected a possible accident.\n\n" +
                        "Time: %s\n" +
                        "Location: %s\n" +
                        "Please respond immediately.",
                timestamp, locationText
        );
    }

    private void sendSMSAlerts(List<EmergencyContact> contacts, String message) {
        // This method requires SMS permissions to be granted
        // For now, we will just log this action
        System.out.println("--- SIMULATING SENDING SMS ---");
        System.out.println("Message: " + message);
        for (EmergencyContact contact : contacts) {
            System.out.println("To: " + contact.getPhoneNumber());
        }
        System.out.println("-----------------------------");
    }

    private void callEmergencyServices() {
        // FIX 2: Changed to ACTION_DIAL for safety. This opens the dialer,
        // but lets the user initiate the call.
        try {
            Intent dialIntent = new Intent(Intent.ACTION_DIAL);
            dialIntent.setData(Uri.parse("tel:108")); // Emergency services number
            dialIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(dialIntent);
        } catch (Exception e) {
            // This can happen on devices without a dialer (like tablets)
            e.printStackTrace();
        }
    }

    private void notifyNearestHospital(Location location) {
        // This would typically send a notification to the nearest hospital
        // For now, we'll simulate this
        if (location != null) {
            // Find nearest hospital API call would go here
            // For simulation, we'll just log the action
            System.out.println("Notifying nearest hospital at coordinates: " +
                    location.getLatitude() + ", " + location.getLongitude());
        }
    }

    public void sendTestAlert(EmergencyCallback callback) {
        // Send a test alert with clearly marked test message
        new Thread(() -> {
            try {
                List<EmergencyContact> contacts = contactManager.getEmergencyContacts();
                String testMessage = "🧪 TEST ALERT 🧪\n" +
                        "This is a test message from Rakshak Emergency System.\n" +
                        "No action required. System is functioning normally.\n\n" +
                        "Time: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

                sendSMSAlerts(contacts, testMessage);
                handler.post(callback::onSuccess);

            } catch (Exception e) {
                handler.post(() -> callback.onError(e.getMessage()));
            }
        }).start();
    }
}