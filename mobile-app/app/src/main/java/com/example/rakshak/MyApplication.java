package com.example.rakshak;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import androidx.appcompat.app.AppCompatActivity;

public class MyApplication extends Application {

    // Channel IDs
    public static final String SOS_ALERTS_CHANNEL_ID = "sos_alerts_channel";
    public static final String SAFETY_CIRCLE_ALERTS_CHANNEL_ID = "safety_circle_alerts_channel";
    public static final String LOCATION_SHARING_CHANNEL_ID = "location_sharing_channel";
    public static final String SAFE_ZONE_CHANNEL_ID = "safe_zone_channel";
    public static final String LOW_BATTERY_CHANNEL_ID = "low_battery_channel";
    public static final String ACCOUNT_SECURITY_CHANNEL_ID = "account_security_channel";
    public static final String FEATURE_DISCOVERY_CHANNEL_ID = "feature_discovery_channel";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannels();
    }

    private void createNotificationChannels() {
        // This check is necessary because Notification Channels were only added in API 26 (Oreo)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = getSystemService(NotificationManager.class);

            // 1. Critical: SOS & Emergency Alerts
            NotificationChannel sosChannel = new NotificationChannel(
                    SOS_ALERTS_CHANNEL_ID,
                    "SOS & Emergency Alerts",
                    NotificationManager.IMPORTANCE_HIGH
            );
            sosChannel.setDescription("Critical confirmations when your SOS has been sent and when contacts have been notified.");
            sosChannel.enableVibration(true);
            sosChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});

            // 2. Critical: Safety Circle Alerts
            NotificationChannel safetyCircleChannel = new NotificationChannel(
                    SAFETY_CIRCLE_ALERTS_CHANNEL_ID,
                    "Safety Circle Alerts",
                    NotificationManager.IMPORTANCE_HIGH
            );
            safetyCircleChannel.setDescription("Immediate notifications if someone in your trusted safety circle sends an emergency alert.");

            // 3. Informational: Live Location Sharing (Foreground Service)
            NotificationChannel locationChannel = new NotificationChannel(
                    LOCATION_SHARING_CHANNEL_ID,
                    "Live Location Sharing",
                    NotificationManager.IMPORTANCE_LOW // Low importance for persistent service notifications
            );
            locationChannel.setDescription("Shows a persistent notification when you are actively sharing your location.");

            // 4. Informational: Safe Zone Updates
            NotificationChannel safeZoneChannel = new NotificationChannel(
                    SAFE_ZONE_CHANNEL_ID,
                    "Safe Zone Updates",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            safeZoneChannel.setDescription("Get notified when a family member enters or leaves a designated Safe Zone.");

            // 5. Informational: Low Battery Warnings
            NotificationChannel lowBatteryChannel = new NotificationChannel(
                    LOW_BATTERY_CHANNEL_ID,
                    "Low Battery Warnings",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            lowBatteryChannel.setDescription("Receive a warning when a connected family member's phone battery is critically low.");

            // 6. General: Account Security
            NotificationChannel accountChannel = new NotificationChannel(
                    ACCOUNT_SECURITY_CHANNEL_ID,
                    "Account Security",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            accountChannel.setDescription("Important alerts about your account, such as new sign-ins.");


            // 7. General: Safety Tips & Features
            NotificationChannel featuresChannel = new NotificationChannel(
                    FEATURE_DISCOVERY_CHANNEL_ID,
                    "Safety Tips & Features",
                    NotificationManager.IMPORTANCE_LOW
            );
            featuresChannel.setDescription("Occasional tips on how to use Rakshak effectively and discover new features.");

            // Register all the channels with the system
            manager.createNotificationChannel(sosChannel);
            manager.createNotificationChannel(safetyCircleChannel);
            manager.createNotificationChannel(locationChannel);
            manager.createNotificationChannel(safeZoneChannel);
            manager.createNotificationChannel(lowBatteryChannel);
            manager.createNotificationChannel(accountChannel);
            manager.createNotificationChannel(featuresChannel);
        }
    }
}