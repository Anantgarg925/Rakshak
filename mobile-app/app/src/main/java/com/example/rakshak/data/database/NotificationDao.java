package com.example.rakshak.data.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface NotificationDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(NotificationHistoryItem notification);

    @Query("SELECT * FROM notification_history ORDER BY timestamp DESC")
    LiveData<List<NotificationHistoryItem>> getAllNotifications();
}