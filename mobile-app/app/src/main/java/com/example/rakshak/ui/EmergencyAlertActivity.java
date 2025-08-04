package com.example.rakshak.ui;

import static com.example.rakshak.R.id.btn_send_now;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import com.example.rakshak.R;
import com.example.rakshak.network.EmergencyService;
import com.example.rakshak.ui.activities.EmergencyTrackingActivity;
import com.example.rakshak.utils.LocationManager;
import com.example.rakshak.utils.ContactManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.example.rakshak.repository.AccidentRepository;

public class EmergencyAlertActivity extends AppCompatActivity {
    private static final int COUNTDOWN_SECONDS = 15;
    private static final String TAG = "EmergencyAlertActivity";
    private TextView tvCountdown, tvAlertTitle, tvDescription;
    private ImageView ivWarningIcon;
    private MaterialButton btnSendNow, btnCancel;
    private CircularProgressIndicator progressIndicator;

    private CountDownTimer countDownTimer;
    private EmergencyService emergencyService;
    private LocationManager locationManager;
    private ContactManager contactManager;
    private boolean isDetectionActive;
    private AccidentRepository accidentRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_alert);

        isDetectionActive = getIntent().getBooleanExtra("detection_active", false);

        initViews();
        initServices();
        accidentRepository=new AccidentRepository();
        
        setupClickListeners();
        startCountdown();
    }

    @SuppressLint("WrongViewCast")
    private void initViews() {
        tvCountdown = findViewById(R.id.tv_countdown);
        tvAlertTitle = findViewById(R.id.tv_alert_title);
        tvDescription = findViewById(R.id.tv_alert_description);
        ivWarningIcon = findViewById(R.id.iv_warning_icon);
        btnSendNow = findViewById(btn_send_now);
        btnCancel = findViewById(R.id.btn_cancel);
        progressIndicator = findViewById(R.id.progress_indicator);

        progressIndicator.setMax(COUNTDOWN_SECONDS);
        progressIndicator.setProgress(COUNTDOWN_SECONDS);
    }

    private void initServices() {
        emergencyService = new EmergencyService(this);
        locationManager = new LocationManager(this);
        contactManager = new ContactManager(this);
    }

    private void setupClickListeners() {
        btnSendNow.setOnClickListener(v -> {
            cancelCountdown();
            sendEmergencyAlert();
        });

        btnCancel.setOnClickListener(v -> {
            cancelCountdown();
            cancelAlert();
        });
    }

    private void startCountdown() {
        countDownTimer = new CountDownTimer(COUNTDOWN_SECONDS * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int secondsRemaining = (int) (millisUntilFinished / 1000);
                updateCountdownDisplay(secondsRemaining);
            }

            @Override
            public void onFinish() {
                sendEmergencyAlert();
            }
        };
        countDownTimer.start();
    }

    private void updateCountdownDisplay(int seconds) {
        tvCountdown.setText(String.valueOf(seconds));
        tvDescription.setText("Sending SOS alert in " + seconds + " seconds…");
        progressIndicator.setProgress(seconds);

        if (seconds <= 5) {
            ivWarningIcon.setImageResource(R.drawable.ic_alert_triangle);
            tvCountdown.setTextColor(getResources().getColor(R.color.emergency_red, null));
        }
    }

    // In EmergencyAlertActivity.java

    private void sendEmergencyAlert() {
        // Update UI to show alert is in progress
        tvAlertTitle.setText("🚨 Sending Emergency Alert...");
        tvDescription.setText("Getting your location and contacting services...");
        btnSendNow.setEnabled(false);
        btnCancel.setEnabled(false);

        // First, get the current location
        locationManager.getCurrentLocation(new LocationManager.LocationResultListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onLocationResult(Location location) {
                // This code runs AFTER the location has been found
                tvDescription.setText("Location found. Contacting server...");
                // Now, send the emergency alert using the location
                String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                accidentRepository.reportAccident(deviceId, latitude, longitude, new AccidentRepository.ApiResultCallback() {
                    @Override
                    public void onSuccess(String responseMessage) {
                        Log.i(TAG, "Successfully reported to server: " + responseMessage);
                        Intent intent = new Intent(EmergencyAlertActivity.this, EmergencyTrackingActivity.class);
                        startActivity(intent);
                        finish(); // Close this activity
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Log.e(TAG, "Failed to report to server: " + errorMessage);
                        tvAlertTitle.setText("❌ Alert Failed");
                        tvDescription.setText("Error: " + errorMessage + ". Please try again.");
                        btnSendNow.setEnabled(true);
                        btnCancel.setEnabled(true);
                    }
                });
            }

            @Override
            public void onLocationError(String error) {
                tvAlertTitle.setText("❌ Location Error");
                tvDescription.setText("Could not get your location. Please ensure GPS is enabled and try again.");
                btnSendNow.setEnabled(true);
                btnCancel.setEnabled(true);
            }
        });
    }

    private void cancelAlert() {
        tvAlertTitle.setText("✅ Emergency Alert Cancelled");
        tvDescription.setText("False alarm reported successfully");

        Intent resultIntent = new Intent();
        resultIntent.putExtra("alert_sent", false);
        setResult(RESULT_OK, resultIntent);

        tvCountdown.postDelayed(() -> finish(), 2000);
    }

    private void cancelCountdown() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    @SuppressLint("GestureBackNavigation")
    @Override
    public void onBackPressed() {
        // Prevent back button during emergency countdown
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelCountdown();
    }
}