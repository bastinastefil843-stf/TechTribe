package com.techtribe.techtribe_backend.dto;

import com.techtribe.techtribe_backend.model.ProjectMember;
import java.time.LocalDateTime;

public class ProjectMemberResponse {

    private Long id;
    private Long projectId;
    private Long developerId;
    private String developerName;
    private String role;
    private LocalDateTime joinedAt;

    public ProjectMemberResponse(ProjectMember member) {

        this.id = member.getId();
        this.projectId = member.getProject().getId();
        this.developerId = member.getUser().getId();
        this.developerName = member.getUser().getName();
        this.role = member.getRole();
        this.joinedAt = member.getJoinedAt();
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

    public String getRole() {
        return role;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }
}