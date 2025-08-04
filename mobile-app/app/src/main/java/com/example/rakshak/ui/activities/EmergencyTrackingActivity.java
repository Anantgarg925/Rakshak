package com.example.rakshak.ui.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import androidx.appcompat.app.AppCompatActivity;

import com.example.rakshak.R;
import com.example.rakshak.model.AmbulanceUpdate;
import com.example.rakshak.network.WebSocketManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.Locale;

public class EmergencyTrackingActivity extends AppCompatActivity implements WebSocketManager.MessageListener, OnMapReadyCallback {

    private WebSocketManager webSocketManager;
    private TextView connectionStatusTextView;
    private TextView updateTextView;
    private Gson gson;

    // Map variables
    private GoogleMap googleMap;
    private Marker ambulanceMarker;

    private LatLng lastKnownAmbulanceLocation=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_tracking);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Live Emergency Tracking");
        }

        connectionStatusTextView = findViewById(R.id.connection_status_text_view);
        updateTextView = findViewById(R.id.update_text_view);

        webSocketManager = new WebSocketManager();
        gson = new Gson();

        // Initialize the map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.googleMap = map;
        Log.d("WebSocket", "Map is ready");
        // Set a default starting location, e.g., Delhi, India
        LatLng initialLocation = new LatLng(28.7041, 77.1025);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLocation, 12f));

        if(lastKnownAmbulanceLocation!=null){
            Log.d("WebSocket","Map ready,drawing stored marker location.");
            updateMapMarker(lastKnownAmbulanceLocation,"Ambulance");

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        webSocketManager.connect(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        webSocketManager.disconnect();
    }

    @Override
    public void onMessageReceived(String message) {
        Log.d("WebSocket", "WebSocket message received: " + message);
        runOnUiThread(() -> {
            try {
                AmbulanceUpdate update = gson.fromJson(message, AmbulanceUpdate.class);

                // Update the text status card
                String statusText = String.format(Locale.US,
                        "Ambulance %s is %s",
                        update.getAmbulanceId(),
                        update.getStatus().replace("_", " ")
                );
                updateTextView.setText(statusText);

                // Update the ambulance's position on the map
                lastKnownAmbulanceLocation = new LatLng(update.getLatitude(), update.getLongitude());
                updateMapMarker(lastKnownAmbulanceLocation, update.getAmbulanceId());

            } catch (JsonSyntaxException e) {
                // You can show an error or just log it
                Log.e("WebSocket", "Received malformed data: " + message);
            }
        });
    }

    private void updateMapMarker(LatLng location, String title) {
        if (googleMap == null){
            Log.d("WebSocket","Map not ready yet,skipping marker update.");
            return;
        }

        if (ambulanceMarker == null) {

            MarkerOptions markerOptions = new MarkerOptions()
                    .position(location)
                    .title(title);
            ambulanceMarker = googleMap.addMarker(markerOptions);
            Log.d("WebSocket","Ambulance marker created at:"+ location);
        } else {
            // Move the existing marker to the new location
            ambulanceMarker.setPosition(location);
            Log.d("WebSocket","Ambulance marker moved to:"+ location);

        }
        // Animate the camera to follow the marker
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15f));
    }

    @Override
    public void onConnectionChange(boolean isConnected) {
        runOnUiThread(() -> {
            if (isConnected) {
                connectionStatusTextView.setText("Status: Connected (Real-time)");
                connectionStatusTextView.setTextColor(getColor(R.color.success_green));
            } else {
                connectionStatusTextView.setText("Status: Disconnected");
                connectionStatusTextView.setTextColor(getColor(R.color.emergency_red));
            }
        });
    }
}