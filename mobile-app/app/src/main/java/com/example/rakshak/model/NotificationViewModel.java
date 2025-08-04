package com.example.rakshak.model;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.rakshak.data.database.AppDatabase;
import com.example.rakshak.data.database.NotificationDao;
import com.example.rakshak.data.database.NotificationHistoryItem;
import java.util.List;

public class NotificationViewModel extends AndroidViewModel {

    private final NotificationDao notificationDao;
    private final LiveData<List<NotificationHistoryItem>> allNotifications;

    public NotificationViewModel(Application application) {
        super(application);
        AppDatabase db = AppDatabase.getDatabase(application);
        notificationDao = db.notificationDao();
        allNotifications = notificationDao.getAllNotifications();
    }

    public LiveData<List<NotificationHistoryItem>> getAllNotifications() {
        return allNotifications;
    }
}