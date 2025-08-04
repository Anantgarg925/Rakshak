package com.example.rakshak.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.rakshak.MainActivity;
import com.example.rakshak.R;
import com.example.rakshak.utils.PermissionManager;
import com.example.rakshak.utils.LocationManager;
import com.example.rakshak.network.EmergencyMonitoringService;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 4000; // 4 seconds
    private static final int STEP_DURATION = SPLASH_DURATION / 4; // 1 second per step
    
    private TextView tvStatus, tvProgress;
    private ProgressBar progressBar;
    private ImageView ivIcon;
    private Handler handler;
    
    private PermissionManager permissionManager;
    private LocationManager locationManager;
    
    private int currentStep = 0;
    private int currentProgress = 0;
    
    private final String[] loadingSteps = {
        "🛡️ Initializing Security System",
        "📡 Connecting to Sensors", 
        "📍 Acquiring GPS Location",
        "📱 Starting Emergency Monitoring"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        
        // Initialize views
        initViews();
        
        // Initialize managers
        permissionManager = new PermissionManager(this);
        locationManager = new LocationManager(this);
        handler = new Handler(Looper.getMainLooper());
        
        // Start initialization sequence
        startInitialization();
    }
    
    private void initViews() {
        tvStatus = findViewById(R.id.tv_status);
        tvProgress = findViewById(R.id.tv_progress);
        progressBar = findViewById(R.id.progress_bar);
        ivIcon = findViewById(R.id.iv_icon);
        
        // Set initial values
        tvStatus.setText(loadingSteps[0]);
        tvProgress.setText("0% Complete");
        progressBar.setProgress(0);
    }
    
    private void startInitialization() {
        // Update progress every 50ms for smooth animation
        Runnable progressUpdater = new Runnable() {
            @Override
            public void run() {
                currentProgress += 2; // 2% every 50ms = 100% in 2.5s
                
                if (currentProgress <= 100) {
                    updateProgress(currentProgress);
                    handler.postDelayed(this, 50);
                } else {
                    finishSplash();
                }
            }
        };
        
        // Update steps every STEP_DURATION
        Runnable stepUpdater = new Runnable() {
            @Override
            public void run() {
                if (currentStep < loadingSteps.length - 1) {
                    currentStep++;
                    updateStep(currentStep);
                    
                    // Perform actual initialization for this step
                    performStepInitialization(currentStep);
                    
                    handler.postDelayed(this, STEP_DURATION);
                }
            }
        };
        
        // Start both updaters
        handler.post(progressUpdater);
        handler.postDelayed(stepUpdater, STEP_DURATION);
        
        // Start with first step initialization
        performStepInitialization(0);
    }
    
    private void updateProgress(int progress) {
        progressBar.setProgress(progress);
        tvProgress.setText(progress + "% Complete");
    }
    
    private void updateStep(int step) {
        if (step < loadingSteps.length) {
            tvStatus.setText(loadingSteps[step]);
            
            // Add visual feedback for step completion
            switch (step) {
                case 1:
                    ivIcon.setImageResource(R.drawable.ic_sensors);
                    break;
                case 2:
                    ivIcon.setImageResource(R.drawable.ic_gps);
                    break;
                case 3:
                    ivIcon.setImageResource(R.drawable.ic_monitoring);
                    break;
            }
        }
    }
    
    private void performStepInitialization(int step) {
        switch (step) {
            case 0: // Initialize Security System
                initializeSecuritySystem();
                break;
            case 1: // Connect to Sensors
                initializeSensors();
                break;
            case 2: // Acquire GPS Location
                initializeLocation();
                break;
            case 3: // Start Emergency Monitoring
                startEmergencyMonitoring();
                break;
        }
    }
    
    private void initializeSecuritySystem() {
        // Initialize core security components
        new Thread(() -> {
            try {
                // Simulate security system initialization
                Thread.sleep(500);
                
                runOnUiThread(() -> {
                    // Update UI if needed
                    tvStatus.setText("✅ " + loadingSteps[0]);
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    private void initializeSensors() {
        new Thread(() -> {
            try {
                // Initialize sensor connections
                Thread.sleep(500);
                
                runOnUiThread(() -> {
                    tvStatus.setText("✅ " + loadingSteps[1]);
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    private void initializeLocation() {
        new Thread(() -> {
            try {
                // Initialize GPS and location services
                if (permissionManager.hasLocationPermission()) {
                                    }
                Thread.sleep(500);
                
                runOnUiThread(() -> {
                    tvStatus.setText("✅ " + loadingSteps[2]);
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    private void startEmergencyMonitoring() {
        new Thread(() -> {
            try {
                // Start emergency monitoring service
                Intent serviceIntent = new Intent(SplashActivity.this, EmergencyMonitoringService.class);

                Thread.sleep(500);
                
                runOnUiThread(() -> {
                    tvStatus.setText("✅ " + loadingSteps[3]);
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    private void finishSplash() {
        // Small delay before transitioning
        handler.postDelayed(() -> {
            Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mainIntent);
            
            // Add transition animation
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            
            finish();
        }, 500);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }
    

}