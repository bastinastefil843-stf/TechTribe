package com.techtribe.techtribe_backend.dto;

import com.techtribe.techtribe_backend.model.ProjectJoinRequest;
import com.techtribe.techtribe_backend.model.DeveloperProfile;

import java.time.LocalDateTime;

public class ProjectJoinRequestResponse {

    private Long id;
    private Long projectId;
    private Long developerId;
    private String developerName;

    private String bio;
    private String skills;
    private String githubUrl;
    private String linkedinUrl;

    private String message;
    private String status;
    private LocalDateTime requestedAt;

    public ProjectJoinRequestResponse(
            ProjectJoinRequest request,
            DeveloperProfile profile) {

        this.id = request.getId();
        this.projectId = request.getProject().getId();
        this.developerId = request.getUser().getId();
        this.developerName = request.getUser().getName();

        this.message = request.getMessage();
        this.status = request.getStatus();
        this.requestedAt = request.getRequestedAt();

        if (profile != null) {
            this.bio = profile.getBio();
            this.skills = profile.getSkills();
            this.githubUrl = profile.getGithubUrl();
            this.linkedinUrl = profile.getLinkedinUrl();
        }
    }

    public Long getId() {
        return id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public Long getDeveloperId() {
        return developerId;
    }

    public String getDeveloperName() {
        return developerName;
    }

    public String getBio() {
        return bio;
    }

    public String getSkills() {
        return skills;
    }

    public String getGithubUrl() {
        return githubUrl;
    }

    public String getLinkedinUrl() {
        return linkedinUrl;
    }

    public String getMessage() {
        return message;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }
}