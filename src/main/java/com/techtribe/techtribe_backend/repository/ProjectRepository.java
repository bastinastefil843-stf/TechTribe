package com.techtribe.techtribe_backend.repository;

import com.techtribe.techtribe_backend.model.Project;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    // Find all projects created by a particular developer
    List<Project> findByOwnerId(Long ownerId);

    // Find a project by ID only if it belongs to the specified developer
    Optional<Project> findByIdAndOwnerId(Long projectId, Long ownerId);

    // Find projects by their status
    List<Project> findByStatus(String status);

    // Find projects created by a developer, newest first
    List<Project> findByOwnerIdOrderByCreatedAtDesc(Long ownerId);
}
