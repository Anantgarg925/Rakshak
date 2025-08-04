package com.example.ambulancealert.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.ambulancealert.model.User;
import com.example.ambulancealert.repository.UserRepository;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerUser(@RequestBody User user) {
        if (userRepository.findByDeviceId(user.getDeviceId()) != null) {
            return ResponseEntity.status(409).body(Map.of("error", "Device already registered."));
        }
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("message", "User registered successfully."));
    }

    @GetMapping
    public ResponseEntity<?> getUser(@RequestParam String deviceId) {
        User user = userRepository.findByDeviceId(deviceId);
        if (user == null) {
            return ResponseEntity.status(404).body(Map.of("error", "User not found"));
        }
        return ResponseEntity.ok(user);
    }

    @PutMapping("/update")
    public ResponseEntity<Map<String, String>> updateUser(@RequestBody User updatedUser) {
        User existingUser = userRepository.findByDeviceId(updatedUser.getDeviceId());
        if (existingUser == null) {
            return ResponseEntity.status(404).body(Map.of("error", "User not found. Cannot update."));
        }
        existingUser.setName(updatedUser.getName());
        existingUser.setPhoneNumber(updatedUser.getPhoneNumber());
        existingUser.setEmergencyContact(updatedUser.getEmergencyContact());

        existingUser.setBloodType(updatedUser.getBloodType());
        existingUser.setMedicalConditions(updatedUser.getMedicalConditions());
        existingUser.setAllergies(updatedUser.getAllergies());
        userRepository.save(existingUser);
        return ResponseEntity.ok(Map.of("message", "User updated successfully."));
    }
}