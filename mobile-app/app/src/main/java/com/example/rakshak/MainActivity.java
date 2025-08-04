package com.example.rakshak;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.example.rakshak.model.EmergencyRecord;
import com.example.rakshak.model.SensorData;
import com.example.rakshak.ui.activities.EmergencyTrackingActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.example.rakshak.ui.fragments.HomeFragment;
import com.example.rakshak.ui.fragments.HistoryFragment;
import com.example.rakshak.ui.fragments.SettingsFragment;
import com.example.rakshak.ui.fragments.ProfileFragment;
import com.example.rakshak.ui.EmergencyAlertActivity;
import com.example.rakshak.utils.PermissionManager;
import com.example.rakshak.utils.SensorManager;
import com.example.rakshak.repository.AccidentRepository;// Assuming SensorManager is in this package

import java.util.List;

// FIX 1: MainActivity now implements the required listener interfaces
public class MainActivity extends AppCompatActivity implements
        BottomNavigationView.OnNavigationItemSelectedListener,
        SensorManager.OnSensorListener{   // Listener for history item clicks

    private BottomNavigationView bottomNavigation;
    private FloatingActionButton sosButton;
    private FragmentManager fragmentManager;
    private SensorManager sensorManager;
    private boolean isDetectionActive = false;

    private AccidentRepository accidentRepository;
    // Fragment instances
    private HomeFragment homeFragment;
    private HistoryFragment historyFragment;
    private SettingsFragment settingsFragment;
    private ProfileFragment profileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        initializeServices();
        setupBottomNavigation();
        setupSOSButton();
        checkPermissions();
        accidentRepository=new AccidentRepository();

        if (savedInstanceState == null) {
            loadFragment(getHomeFragment());
            bottomNavigation.setSelectedItemId(R.id.nav_home);
        }
    }

    private void initializeViews() {
        bottomNavigation = findViewById(R.id.bottom_navigation);
        sosButton = findViewById(R.id.sos_button);
        fragmentManager = getSupportFragmentManager();
    }

    private void initializeServices() {
        sensorManager = new SensorManager(this);
        // FIX 2: Set the listener to 'this' since MainActivity now implements it
        sensorManager.setOnSensorListener(this);
    }

    private void setupBottomNavigation() {
        bottomNavigation.setOnNavigationItemSelectedListener(this);
    }

    private void setupSOSButton() {
        sosButton.setOnClickListener(v -> {
            Snackbar.make(v, "Hold for 3 seconds to activate SOS", Snackbar.LENGTH_SHORT).show();
        });

        sosButton.setOnLongClickListener(v -> {
            handleEmergencyAlert("sos_manual");
            return true;
        });
    }

    private void checkPermissions() {
        PermissionManager permissionManager = new PermissionManager(this);
        if (!permissionManager.hasAllPermissions()) {
            permissionManager.requestPermissions();
        } else {
            startAutomaticDetection();
        }
    }

    private void startAutomaticDetection() {
        if (!isDetectionActive) {
            isDetectionActive = true;
            sensorManager.startMonitoring();
            showSuccessMessage("Automatic monitoring started");
        }
    }

    public void toggleDetection() {
        if (isDetectionActive) {
            isDetectionActive = false;
            sensorManager.stopMonitoring();
            showInfoMessage("Accident detection stopped");
        } else {
            isDetectionActive = true;
            sensorManager.startMonitoring();
            showSuccessMessage("Accident detection activated");
        }
        if (homeFragment != null && homeFragment.isVisible()) {
            homeFragment.updateDetectionStatus(isDetectionActive);
        }
    }

    private void handleEmergencyAlert(String alertType) {
        
        Intent intent = new Intent(this, EmergencyAlertActivity.class);
        intent.putExtra("alert_type", alertType);
        startActivity(intent);

        if ("accident_detected".equals(alertType)) {
            showErrorMessage("Accident Detected! Protocol activated.");
        } else {
            showErrorMessage("Emergency SOS Activated!");
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment = null;
        int itemId = item.getItemId();
        if (itemId == R.id.nav_home) {
            selectedFragment = getHomeFragment();
        } else if (itemId == R.id.nav_history) {
            selectedFragment = getHistoryFragment();
        } else if (itemId == R.id.nav_settings) {
            selectedFragment = getSettingsFragment();
        } else if (itemId == R.id.nav_profile) {
            selectedFragment = getProfileFragment();
        }
        if (selectedFragment != null) {
            loadFragment(selectedFragment);
            return true;
        }
        return false;
    }

    private void loadFragment(Fragment fragment) {
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private HomeFragment getHomeFragment() {
        if (homeFragment == null) {
            homeFragment = HomeFragment.newInstance(isDetectionActive);
        }
        return homeFragment;
    }

    private HistoryFragment getHistoryFragment() {
        if (historyFragment == null) {
            // FIX 3: Pass 'this' to the HistoryFragment constructor
            historyFragment = new HistoryFragment();
        }
        return historyFragment;
    }

    private SettingsFragment getSettingsFragment() {
        if (settingsFragment == null) {
            settingsFragment = new SettingsFragment();
        }
        return settingsFragment;
    }

    private ProfileFragment getProfileFragment() {
        if (profileFragment == null) {
            profileFragment = new ProfileFragment();
        }
        return profileFragment;
    }

    // Utility methods for showing messages
    private void showSuccessMessage(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
                .setBackgroundTint(getColor(R.color.success_green)).show();
    }

    private void showErrorMessage(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
                .setBackgroundTint(getColor(R.color.emergency_red)).show();
    }

    private void showInfoMessage(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
                .setBackgroundTint(getColor(R.color.emergency_blue)).show();
    }

    public boolean isDetectionActive() {
        return isDetectionActive;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sensorManager != null) {
            sensorManager.stopMonitoring();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager permissionManager = new PermissionManager(this);
        if (permissionManager.hasAllPermissions()) {
            startAutomaticDetection();
        }
    }

    @Override
    public void onAccidentDetected(){
        handleEmergencyAlert("accident_detected");
    }

    @Override
    public void onSensorDataUpdated(List<SensorData> sensorDataList) {
        if (homeFragment!=null && homeFragment.isVisible()){
            homeFragment.updateSensorData(sensorDataList);
        }
    }




}