package com.example.ambulancealert.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ambulancealert.model.AccidentHistory;
import com.example.ambulancealert.model.User;

public interface AccidentHistoryRepository extends JpaRepository<AccidentHistory, UUID> {
    // Finds all history records for a user, ordered by most recent first
    List<AccidentHistory> findByUserOrderByTimestampDesc(User user);
}