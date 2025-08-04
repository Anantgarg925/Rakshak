package com.example.ambulancealert.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ambulancealert.model.HospitalNotification;

@RestController
@RequestMapping("/api/hospital")
@CrossOrigin(origins = "http://localhost:4200")
public class HospitalController {

    @PostMapping("/notify")
    public ResponseEntity<String> notifyHospital(@RequestBody HospitalNotification notification) {
        System.out.println("🏥 Hospital notified:");
        System.out.println("🚑 Ambulance: " + notification.getAmbulanceId());
        System.out.println("📱 Device ID: " + notification.getDeviceId());
        System.out.println("⏱️ ETA: " + notification.getEtaMinutes() + " mins");
        return ResponseEntity.ok("Hospital alerted successfully.");
    }
}
