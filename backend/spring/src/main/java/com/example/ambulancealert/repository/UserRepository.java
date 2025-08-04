package com.example.ambulancealert.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ambulancealert.model.User;

public interface UserRepository extends JpaRepository<User, UUID> {
    // This method tells Spring Data JPA to create a query that
    // finds a User entity by its deviceId field.
    User findByDeviceId(String deviceId);
}