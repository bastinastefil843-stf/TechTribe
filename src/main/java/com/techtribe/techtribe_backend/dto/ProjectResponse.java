package com.techtribe.techtribe_backend.dto;

import com.techtribe.techtribe_backend.model.Project;
import com.techtribe.techtribe_backend.model.User;

import java.time.LocalDateTime;

public class ProjectResponse {

    private Long id;
    private String title;
    private String description;
    private String technologies;
    private String status;
    private Long ownerId;
    private String ownerName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ==========================================
    // CONSTRUCTOR
    // ==========================================

    public ProjectResponse(Project project) {

        this.id = project.getId();
        this.title = project.getTitle();
        this.description = project.getDescription();
        this.technologies = project.getTechnologies();
        this.status = project.getStatus();

        // Safely retrieve project owner
        User owner = project.getOwner();

        if (owner != null) {
            this.ownerId = owner.getId();
            this.ownerName = owner.getName();
        } else {
            this.ownerId = null;
            this.ownerName = "Unknown";
        }

        this.createdAt = project.getCreatedAt();
        this.updatedAt = project.getUpdatedAt();
    }

    // ==========================================
    // GETTERS
    // ==========================================

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getTechnologies() {
        return technologies;
    }

    public String getStatus() {
        return status;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}