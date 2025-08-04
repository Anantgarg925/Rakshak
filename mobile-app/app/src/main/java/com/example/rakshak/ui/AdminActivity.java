package com.example.rakshak.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.rakshak.R;
import com.example.rakshak.model.ActiveAlert;
import com.example.rakshak.model.EmergencyRecord;
import com.example.rakshak.ui.adapters.ActiveAlertsAdapter;
import com.example.rakshak.ui.adapters.EmergencyResponseAdapter;
import com.example.rakshak.utils.AdminDataManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.tabs.TabLayout;
import java.util.List;

public class AdminActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private TextView tvActiveAlerts, tvTotalToday, tvResponseTime, tvSystemStatus;
    private RecyclerView rvActiveAlerts, rvEmergencyResponse;
    private MaterialButton btnRefresh, btnSettings, btnExportData;
    private MaterialCardView cardStats;

    private ActiveAlertsAdapter activeAlertsAdapter;
    private EmergencyResponseAdapter emergencyResponseAdapter;
    private AdminDataManager adminDataManager;

    private int currentTab = 0; // 0: Active Alerts, 1: Emergency Response

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        initializeViews();
        setupToolbar();
        setupTabs();
        setupAdminData();
        setupRecyclerViews();
        setupClickListeners();
        loadAdminData();
    }

    @SuppressLint("WrongViewCast")
    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        tabLayout = findViewById(R.id.tab_layout);
        tvActiveAlerts = findViewById(R.id.tv_active_alerts);
        tvTotalToday = findViewById(R.id.tv_total_today);
        tvResponseTime = findViewById(R.id.tv_response_time);
        tvSystemStatus = findViewById(R.id.tv_system_status);
        rvActiveAlerts = findViewById(R.id.rv_active_alerts);
        rvEmergencyResponse = findViewById(R.id.rv_emergency_response);
        btnRefresh = findViewById(R.id.btn_refresh);
        btnSettings = findViewById(R.id.btn_settings);
        btnExportData = findViewById(R.id.btn_export_data);
        cardStats = findViewById(R.id.card_stats);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("🛠️ Admin Control Panel");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("Active Alerts"));
        tabLayout.addTab(tabLayout.newTab().setText("Emergency Response"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentTab = tab.getPosition();
                updateTabContent();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupAdminData() {
        adminDataManager = new AdminDataManager(this);
    }

    private void setupRecyclerViews() {
        // Active Alerts RecyclerView
        activeAlertsAdapter = new ActiveAlertsAdapter(this::onAlertClick);
        rvActiveAlerts.setLayoutManager(new LinearLayoutManager(this));
        rvActiveAlerts.setAdapter(activeAlertsAdapter);

        // Emergency Response RecyclerView
        emergencyResponseAdapter = new EmergencyResponseAdapter(this::onResponseClick);
        rvEmergencyResponse.setLayoutManager(new LinearLayoutManager(this));
        rvEmergencyResponse.setAdapter(emergencyResponseAdapter);
    }

    private void setupClickListeners() {
        btnRefresh.setOnClickListener(v -> {
            showToast("🔄 Refreshing data...");
            loadAdminData();
        });

        btnSettings.setOnClickListener(v -> {
            showToast("⚙️ Admin settings");
            // Open admin settings
        });

        btnExportData.setOnClickListener(v -> {
            showToast("📤 Exporting data...");
            exportEmergencyData();
        });
    }

    private void loadAdminData() {
        // Load statistics
        updateStatistics();

        // Load data based on current tab
        updateTabContent();
    }

    private void updateStatistics() {
        int activeAlerts = adminDataManager.getActiveAlertsCount();
        int totalToday = adminDataManager.getTodayAlertsCount();
        String responseTime = adminDataManager.getAverageResponseTime();
        boolean systemStatus = adminDataManager.getSystemStatus();

        tvActiveAlerts.setText(String.valueOf(activeAlerts));
        tvTotalToday.setText(String.valueOf(totalToday));
        tvResponseTime.setText(responseTime);
        tvSystemStatus.setText(systemStatus ? "🟢 Operational" : "🔴 Issues Detected");
        tvSystemStatus.setTextColor(getResources().getColor(
            systemStatus ? R.color.success_green : R.color.emergency_red, null));
    }

    private void updateTabContent() {
        switch (currentTab) {
            case 0: // Active Alerts
                rvActiveAlerts.setVisibility(RecyclerView.VISIBLE);
                rvEmergencyResponse.setVisibility(RecyclerView.GONE);
                loadActiveAlerts();
                break;
            case 1: // Emergency Response
                rvActiveAlerts.setVisibility(RecyclerView.GONE);
                rvEmergencyResponse.setVisibility(RecyclerView.VISIBLE);
                loadEmergencyResponse();
                break;
        }
    }

    private void loadActiveAlerts() {
        List<ActiveAlert> alerts = adminDataManager.getActiveAlerts();
        activeAlertsAdapter.updateAlerts(alerts);
    }

    private void loadEmergencyResponse() {
        List<EmergencyRecord> responses = adminDataManager.getEmergencyResponses();
        emergencyResponseAdapter.updateResponses(responses);
    }

    private void onAlertClick(ActiveAlert alert) {
        // Show alert details and response options
        showAlertResponseDialog(alert);
    }

    private void onResponseClick(EmergencyRecord response) {
        // Show response details
        showResponseDetailsDialog(response);
    }

    private void showAlertResponseDialog(ActiveAlert alert) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Emergency Alert Response")
               .setMessage("Alert ID: " + alert.getId() + "\n" +
                          "Location: " + alert.getLocation() + "\n" +
                          "Time: " + alert.getTime() + "\n" +
                          "Status: " + alert.getStatus() + "\n\n" +
                          "Choose response action:")
               .setPositiveButton("Dispatch Help", (dialog, which) -> {
                   dispatchHelp(alert);
               })
               .setNegativeButton("Mark as False Alarm", (dialog, which) -> {
                   markAsFalseAlarm(alert);
               })
               .setNeutralButton("View Details", (dialog, which) -> {
                   viewAlertDetails(alert);
               })
               .show();
    }

    private void showResponseDetailsDialog(EmergencyRecord response) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Emergency Response Details")
               .setMessage("Response ID: " + response.getId() + "\n" +
                          "Location: " + response.getLocation() + "\n" +
                          "Time: " + response.getTime() + "\n" +
                          "Status: " + response.getStatus() + "\n" +
                          "Response Time: " + response.getResponseTime() + "\n" +
                          "Responder: " + response.getResponderId())
               .setPositiveButton("OK", null)
               .show();
    }

    private void dispatchHelp(ActiveAlert alert) {
        adminDataManager.dispatchEmergencyHelp(alert.getId());
        showToast("🚑 Emergency help dispatched");
        loadAdminData();
    }

    private void markAsFalseAlarm(ActiveAlert alert) {
        adminDataManager.markAsFalseAlarm(alert.getId());
        showToast("✅ Marked as false alarm");
        loadAdminData();
    }

    private void viewAlertDetails(ActiveAlert alert) {
        // Open detailed view
        showToast("📋 Viewing alert details");
    }

    private void exportEmergencyData() {
        adminDataManager.exportEmergencyData(new AdminDataManager.ExportCallback() {
            @Override
            public void onSuccess(String filePath) {
                runOnUiThread(() -> {
                    showToast("✅ Data exported to: " + filePath);
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    showToast("❌ Export failed: " + error);
                });
            }
        },this );
    }

    private void showToast(String message) {
        android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when returning to activity
        loadAdminData();
    }
}