package com.techtribe.techtribe_backend.repository;

import com.techtribe.techtribe_backend.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {

    // Existing methods — DO NOT REMOVE

    List<Task> findByProjectId(Long projectId);

    Optional<Task> findByIdAndProjectId(Long taskId, Long projectId);

    List<Task> findByAssignedToId(Long userId);

    List<Task> findByProjectIdAndStatus(Long projectId, String status);

    // New methods for Module 7 — Project Progress Tracking

    // Count all tasks belonging to a project
    long countByProjectId(Long projectId);

    // Count tasks with a specific status
    long countByProjectIdAndStatus(Long projectId, String status);

    // Count tasks matching any of the provided statuses
    long countByProjectIdAndStatusIn(
            Long projectId,
            Collection<String> statuses
    );
}