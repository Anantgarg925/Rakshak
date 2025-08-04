package com.example.ambulancealert.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.ambulancealert.model.AccidentReport;
import com.example.ambulancealert.model.Ambulance;

@Service
public class AccidentService {

    private final List<Ambulance> ambulances = List.of(
        new Ambulance("AMB101", 28.7041, 77.1025, true),
        new Ambulance("AMB102", 28.5355, 77.3910, true),
        new Ambulance("AMB103", 28.4595, 77.0266, true)
    );

    public Ambulance dispatchAmbulance(AccidentReport report) {
        // Select the first available ambulance for now
        for (Ambulance amb : ambulances) {
            if (amb.isAvailable()) {
                amb.setAvailable(false); // mark as dispatched
                return amb;
            }
        }
        return null; // no ambulance available
    }
}
