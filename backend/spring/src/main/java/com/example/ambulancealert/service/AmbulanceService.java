package com.example.ambulancealert.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.ambulancealert.model.Ambulance;

@Service
public class AmbulanceService {

    @Autowired
    private AmbulanceSocketHandler socketHandler;

    // A thread-safe list of all available ambulances
    private final List<Ambulance> availableAmbulances = new CopyOnWriteArrayList<>(List.of(
            new Ambulance("AMB101", 28.7041, 77.1025, true),
            new Ambulance("AMB102", 28.5355, 77.3910, true),
            new Ambulance("AMB103", 28.4595, 77.0266, true)
    ));

    // A thread-safe map to track dispatched ambulances
    private final Map<String, Ambulance> dispatchedAmbulances = new ConcurrentHashMap<>();

    public Ambulance dispatchAmbulance() {
        for (Ambulance amb : availableAmbulances) {
            if (amb.isAvailable()) {
                amb.setAvailable(false);
                dispatchedAmbulances.put(amb.getId(), amb);
                System.out.println("Dispatched Ambulance: " + amb.getId());
                return amb;
            }
        }
        return null; // No ambulance available
    }

    // This method will run automatically every 5 seconds
    @Scheduled(fixedRate = 5000)
    public void simulateAmbulanceMovement() {
        if (dispatchedAmbulances.isEmpty()) {
            return; // No active ambulances to track
        }

        System.out.println("Simulating movement for dispatched ambulances...");

        for (Ambulance ambulance : dispatchedAmbulances.values()) {
            // Simulate movement by slightly changing coordinates
            ambulance.setLatitude(ambulance.getLatitude() + 0.0001);
            ambulance.setLongitude(ambulance.getLongitude() + 0.0001);

            // Create a JSON message with the new location
            String locationUpdate = String.format(
                    "{\"ambulanceId\":\"%s\", \"latitude\":%f, \"longitude\":%f, \"status\":\"in_transit\"}",
                    ambulance.getId(), ambulance.getLatitude(), ambulance.getLongitude()
            );

            // Broadcast the update to all connected clients
            socketHandler.broadcast(locationUpdate);
        }
    }
}