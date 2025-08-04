package com.example.ambulancealert.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ambulancealert.model.EmergencyContact;
import com.example.ambulancealert.model.User;

public interface EmergencyContactRepository extends JpaRepository<EmergencyContact, UUID> {
    // Spring Data JPA automatically creates the query based on the method name
    List<EmergencyContact> findByUser(User user);
}