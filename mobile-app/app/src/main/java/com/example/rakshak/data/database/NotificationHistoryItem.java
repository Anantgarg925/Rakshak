package com.example.rakshak.data.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "notification_history")
public class NotificationHistoryItem {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String title;
    public String message;
    public long timestamp;
}