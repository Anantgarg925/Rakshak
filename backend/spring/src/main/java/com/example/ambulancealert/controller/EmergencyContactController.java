package com.example.ambulancealert.controller;

import com.example.ambulancealert.model.EmergencyContact;
import com.example.ambulancealert.model.User;
import com.example.ambulancealert.repository.EmergencyContactRepository;
import com.example.ambulancealert.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/contacts")
public class EmergencyContactController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmergencyContactRepository contactRepository;

    @GetMapping
    public ResponseEntity<?> getContacts(@RequestParam String deviceId) {
        User user = userRepository.findByDeviceId(deviceId);
        if (user == null) {
            return ResponseEntity.status(404).body(Map.of("error", "User not found"));
        }
        return ResponseEntity.ok(contactRepository.findByUser(user));
    }

    @PostMapping
    public ResponseEntity<?> addContact(@RequestParam String deviceId, @RequestBody EmergencyContact contact) {
        User user = userRepository.findByDeviceId(deviceId);
        if (user == null) {
            return ResponseEntity.status(404).body(Map.of("error", "User not found"));
        }
        contact.setUser(user);
        EmergencyContact savedContact = contactRepository.save(contact);
        return ResponseEntity.ok(savedContact);
    }

    @DeleteMapping("/{contactId}")
    public ResponseEntity<?> deleteContact(@PathVariable UUID contactId) {
        if (!contactRepository.existsById(contactId)) {
            return ResponseEntity.status(404).body(Map.of("error", "Contact not found"));
        }
        contactRepository.deleteById(contactId);
        return ResponseEntity.ok(Map.of("message", "Contact deleted successfully"));
    }
}