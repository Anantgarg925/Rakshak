package com.example.rakshak.utils;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.rakshak.MainActivity;
import com.example.rakshak.MyApplication;
import com.example.rakshak.R;
import com.example.rakshak.data.database.AppDatabase;
import com.example.rakshak.data.database.NotificationHistoryItem;

public class NotificationHelper {

    private final Context context;

    public NotificationHelper(Context context) {
        this.context = context;
    }

    public void sendSosConfirmationNotification() {
        int notificationId = 101;
        String title = "SOS Alert Sent";
        String message = "Your emergency contacts have been notified.";

        // This intent will open the app when the notification is tapped.
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, MyApplication.SOS_ALERTS_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_alert_triangle) // Icon updated as per your code
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // --- START: PERMISSION CHECK ADDED ---
        // The check uses ActivityCompat for compatibility.
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // If permission is not granted, we log an error and stop.
            // The user must grant this permission from an Activity. This helper cannot request it.
            Log.e("NotificationHelper", "POST_NOTIFICATIONS permission not granted. Cannot send notification.");

            // Still save the alert to the in-app history even if the notification fails to send.
            saveNotificationToHistory(title, message);
            return;
        }
        // --- END: PERMISSION CHECK ADDED ---

        notificationManager.notify(notificationId, builder.build());
        saveNotificationToHistory(title, message);
    }

    private void saveNotificationToHistory(String title, String message) {
        NotificationHistoryItem item = new NotificationHistoryItem();
        item.title = title;
        item.message = message;
        item.timestamp = System.currentTimeMillis();

        AppDatabase.databaseWriteExecutor.execute(() -> {
            AppDatabase.getDatabase(context).notificationDao().insert(item);
        });
    }
}