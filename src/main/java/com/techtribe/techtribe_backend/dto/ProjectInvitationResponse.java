package com.techtribe.techtribe_backend.dto;

import com.techtribe.techtribe_backend.model.ProjectInvitation;

import java.time.LocalDateTime;

public class ProjectInvitationResponse {

    private Long id;
    private Long projectId;
    private String projectName;
    private Long invitedUserId;
    private String invitedUserName;
    private String invitedUserEmail;
    private Long invitedById;
    private String invitedByName;
    private String status;
    private LocalDateTime createdAt;

    public ProjectInvitationResponse(ProjectInvitation invitation) {

        this.id = invitation.getId();

        this.projectId = invitation.getProject().getId();
        this.projectName = invitation.getProject().getTitle();

        this.invitedUserId = invitation.getInvitedUser().getId();
        this.invitedUserName = invitation.getInvitedUser().getName();
        this.invitedUserEmail = invitation.getInvitedUser().getEmail();

        this.invitedById = invitation.getInvitedBy().getId();
        this.invitedByName = invitation.getInvitedBy().getName();

        this.status = invitation.getStatus();
        this.createdAt = invitation.getCreatedAt();
    }

    public Long getId() {
        return id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public Long getInvitedUserId() {
        return invitedUserId;
    }

    public String getInvitedUserName() {
        return invitedUserName;
    }

    public String getInvitedUserEmail() {
        return invitedUserEmail;
    }

    public Long getInvitedById() {
        return invitedById;
    }

    public String getInvitedByName() {
        return invitedByName;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}