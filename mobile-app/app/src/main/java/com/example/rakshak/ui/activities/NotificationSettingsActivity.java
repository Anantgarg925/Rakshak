package com.example.rakshak.ui.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rakshak.R;
import com.example.rakshak.ui.adapters.NotificationAdapter;
import com.example.rakshak.model.NotificationViewModel;

public class NotificationSettingsActivity extends AppCompatActivity {

    private NotificationViewModel notificationViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Notification History");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        RecyclerView recyclerView = findViewById(R.id.recycler_view_notifications);
        final NotificationAdapter adapter = new NotificationAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get a new or existing ViewModel from the ViewModelProvider.
        notificationViewModel = new ViewModelProvider(this).get(NotificationViewModel.class);

        // Add an observer on the LiveData returned by getAllNotifications.
        // The onChanged() method fires when the observed data changes and the activity is in the foreground.
        notificationViewModel.getAllNotifications().observe(this, adapter::submitList);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}