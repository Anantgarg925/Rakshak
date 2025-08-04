package com.example.rakshak.network;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;
import com.example.rakshak.MainActivity;
import com.example.rakshak.R;
import com.example.rakshak.ui.EmergencyAlertActivity;
import com.example.rakshak.utils.SensorManager;

public class EmergencyMonitoringService extends Service implements SensorManager.OnSensorListener {

    private static final String CHANNEL_ID = "emergency_monitoring";
    private static final int NOTIFICATION_ID = 1;

    private SensorManager sensorManager;
    private NotificationManager notificationManager;
    private boolean isMonitoring = false;

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        sensorManager = new SensorManager(this);
        sensorManager.setOnSensorListener(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForegroundService();
        startMonitoring();
        return START_STICKY; // Restart service if killed
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Emergency Monitoring",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Continuous monitoring for accident detection");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private void startForegroundService() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE
        );

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("🛡️ Rakshak Emergency System")
                .setContentText("Monitoring for accidents - System Active")
                .setSmallIcon(R.drawable.ic_shield_check)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();

        startForeground(NOTIFICATION_ID, notification);
    }

    private void startMonitoring() {
        if (!isMonitoring) {
            isMonitoring = true;
            sensorManager.startMonitoring();
            updateNotification("Monitoring active - All sensors operational");
        }
    }

    private void stopMonitoring() {
        if (isMonitoring) {
            isMonitoring = false;
            sensorManager.stopMonitoring();
            updateNotification("Monitoring stopped");
        }
    }

    private void updateNotification(String status) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE
        );

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("🛡️ Rakshak Emergency System")
                .setContentText(status)
                .setSmallIcon(R.drawable.ic_alert_triangle)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();

        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    @Override
    public void onSensorDataUpdated(java.util.List<com.example.rakshak.model.SensorData> sensorDataList) {
        // Handle real-time sensor data updates
        // This could be used for logging or remote monitoring
    }

    @Override
    public void onAccidentDetected() {
        // Handle accident detection
        handleAccidentDetection();
    }

    private void handleAccidentDetection() {
        // Create high-priority notification
        createAccidentNotification();

        // Launch emergency alert activity
        Intent alertIntent = new Intent(this, EmergencyAlertActivity.class);
        alertIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(alertIntent);
    }

    private void createAccidentNotification() {
        Intent alertIntent = new Intent(this, EmergencyAlertActivity.class);
        alertIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 1, alertIntent, PendingIntent.FLAG_IMMUTABLE
        );

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("🚨 ACCIDENT DETECTED")
                .setContentText("Emergency alert will be sent automatically")
                .setSmallIcon(R.drawable.ic_emergency_alert)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setAutoCancel(true)
                .setVibrate(new long[]{0, 1000, 500, 1000})
                .build();

        notificationManager.notify(2, notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopMonitoring();
    }
}