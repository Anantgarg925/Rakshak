package com.example.rakshak.ui.fragments;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import com.example.rakshak.MainActivity;
import com.example.rakshak.R;
import com.example.rakshak.ui.adapters.SensorAdapter;
import com.example.rakshak.utils.SensorManager;
import com.example.rakshak.model.SensorData;
import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HomeFragment extends Fragment {
    
    private TextView statusTitle;
    private TextView statusSubtitle;
    private MaterialCardView statusCard;
    private Button toggleButton;
    private Switch detectionSwitch;
    private RecyclerView sensorRecyclerView;
    private SensorAdapter sensorAdapter;
    private TextView currentTime;
    private TextView alertsToday;
    private MaterialCardView emergencyCountdownCard;
    private Button cancelAlertButton;
    private boolean isDetectionActive;
    
    private SensorManager sensorManager;
    private List<SensorData> sensorDataList;
    
    public static HomeFragment newInstance(boolean isDetectionActive) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putBoolean("detection_active", isDetectionActive);
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isDetectionActive = getArguments().getBoolean("detection_active", false);
        }
        
        sensorManager = new SensorManager(getContext());
        sensorDataList = new ArrayList<>();
        initializeMockSensorData();
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        
        initializeViews(view);
        setupRecyclerView();
        updateUI();
        updateCurrentTime();
        
        return view;
    }
    
    private void initializeViews(View view) {
        statusTitle = view.findViewById(R.id.status_title);
        statusSubtitle = view.findViewById(R.id.status_subtitle);
        statusCard = view.findViewById(R.id.status_card);
        toggleButton = view.findViewById(R.id.toggle_button);
        sensorRecyclerView = view.findViewById(R.id.sensor_recycler_view);
        
        // Optional views (may not exist in all layouts)
        detectionSwitch = view.findViewById(R.id.switch_detection);
        currentTime = view.findViewById(R.id.tv_current_time);
        alertsToday = view.findViewById(R.id.tv_alerts_today);
        emergencyCountdownCard = view.findViewById(R.id.card_emergency_countdown);
        cancelAlertButton = view.findViewById(R.id.btn_cancel);
        
        toggleButton.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).toggleDetection();
            }
        });
        
        if (detectionSwitch != null) {
            detectionSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).toggleDetection();
                }
            });
        }
        
        if (cancelAlertButton != null) {
            cancelAlertButton.setOnClickListener(v -> {
                if (emergencyCountdownCard != null) {
                    emergencyCountdownCard.setVisibility(View.GONE);
                }
            });
        }
    }
    
    private void setupRecyclerView() {
        sensorAdapter = new SensorAdapter(sensorDataList);
        sensorRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        sensorRecyclerView.setAdapter(sensorAdapter);
    }
    
    private void initializeMockSensorData() {
        // New, correct way using your better constructor
sensorDataList.add(new SensorData("Accelerometer", 9.81f, "m/s²", SensorData.Status.ACTIVE, SensorData.SensorType.ACCELEROMETER));
sensorDataList.add(new SensorData("Gyroscope", 0.02f, "rad/s", SensorData.Status.ACTIVE, SensorData.SensorType.GYROSCOPE));
sensorDataList.add(new SensorData("GPS Location", "Acquired", "coords", SensorData.Status.ACTIVE, SensorData.SensorType.GPS));
sensorDataList.add(new SensorData("Network Signal", 85, "%", SensorData.Status.ACTIVE, SensorData.SensorType.CONNECTIVITY));
sensorDataList.add(new SensorData("Battery Level", 78, "%", SensorData.Status.ACTIVE, SensorData.SensorType.DEVICE_STATUS));
    }
    
    public void updateDetectionStatus(boolean isActive) {
        this.isDetectionActive = isActive;
        updateUI();
    }

    public void updateSensorData(List<SensorData> newSensorDataList) {
        if(sensorAdapter !=null &&newSensorDataList != null){
            sensorAdapter.updateSensorData(newSensorDataList);
        }
    }
    private void updateUI() {
        if (statusTitle != null && statusSubtitle != null && statusCard != null && toggleButton != null) {
            if (isDetectionActive) {
                statusTitle.setText("🟢 System Active");
                statusSubtitle.setText("Real-time monitoring enabled • All sensors operational");
                statusCard.setCardBackgroundColor(getResources().getColor(R.color.success_green_light, null));
                toggleButton.setText("Stop Detection");
                toggleButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.emergency_red, null)));


                if (detectionSwitch != null) {
                    detectionSwitch.setChecked(true);
                }
            } else {
                statusTitle.setText("🔴 System Inactive");
                statusSubtitle.setText("Emergency monitoring is currently disabled");
                statusCard.setCardBackgroundColor(getResources().getColor(R.color.emergency_gray_light, null));
                toggleButton.setText("Start Detection");
                toggleButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.success_green, null)));


                if (detectionSwitch != null) {
                    detectionSwitch.setChecked(false);
                }
            }
        }
        
        // Update sensor data status
        for (SensorData sensor : sensorDataList) {
            sensor.setActive(isDetectionActive);
        }
        
        if (sensorAdapter != null) {
            sensorAdapter.notifyDataSetChanged();
        }
        
        // Update alerts today
        if (alertsToday != null) {
            alertsToday.setText("3 alerts today");
        }
    }
    
    private void updateCurrentTime() {
        if (currentTime != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            String time = sdf.format(new Date());
            currentTime.setText(time);
        }
    }
    
    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() instanceof MainActivity) {
            updateDetectionStatus(((MainActivity) getActivity()).isDetectionActive());
        }
        updateCurrentTime();
    }
}