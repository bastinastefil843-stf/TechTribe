package com.techtribe.techtribe_backend.repository;

import com.techtribe.techtribe_backend.model.Project;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    // Find projects belonging to a developer
    @EntityGraph(attributePaths = {"owner"})
    List<Project> findByOwnerId(Long ownerId);

    // Find a project belonging to a particular developer
    @EntityGraph(attributePaths = {"owner"})
    Optional<Project> findByIdAndOwnerId(
            Long projectId,
            Long ownerId
    );

    // Find projects by status
    @EntityGraph(attributePaths = {"owner"})
    List<Project> findByStatus(String status);

    // Find developer projects, newest first
    @EntityGraph(attributePaths = {"owner"})
    List<Project> findByOwnerIdOrderByCreatedAtDesc(
            Long ownerId
    );

    // Explore all projects with their owners loaded
    @Override
    @EntityGraph(attributePaths = {"owner"})
    List<Project> findAll();

    // Find a project by ID with its owner loaded
    @Override
    @EntityGraph(attributePaths = {"owner"})
    Optional<Project> findById(Long id);
}