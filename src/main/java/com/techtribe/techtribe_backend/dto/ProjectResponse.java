package com.techtribe.techtribe_backend.dto;

import com.techtribe.techtribe_backend.model.Project;

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

    public ProjectResponse(Project project) {

        this.id = project.getId();
        this.title = project.getTitle();
        this.description = project.getDescription();
        this.technologies = project.getTechnologies();
        this.status = project.getStatus();

        this.ownerId = project.getOwner().getId();
        this.ownerName = project.getOwner().getName();

        this.createdAt = project.getCreatedAt();
        this.updatedAt = project.getUpdatedAt();
    }

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
