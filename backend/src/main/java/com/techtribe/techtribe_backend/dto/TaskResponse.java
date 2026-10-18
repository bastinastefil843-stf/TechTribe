package com.techtribe.techtribe_backend.dto;

import com.techtribe.techtribe_backend.model.Task;

import org.hibernate.Hibernate;

import java.time.LocalDate;

public class TaskResponse {

    private Long id;
    private String title;
    private String description;
    private String status;
    private LocalDate deadline;
    private Long projectId;
    private Long assignedToId;
    private String assignedToName;

    public TaskResponse(Task task) {

        // STEP 1: Basic task information

        this.id = task.getId();
        this.title = task.getTitle();
        this.description = task.getDescription();
        this.status = task.getStatus();
        this.deadline = task.getDeadline();

        // STEP 2: Project information

        if (task.getProject() != null) {
            this.projectId = task.getProject().getId();
        }

        // STEP 3: Assigned developer information

        if (task.getAssignedTo() != null) {

            this.assignedToId = task.getAssignedTo().getId();

            // Avoid accessing an uninitialized Hibernate proxy
            // when the persistence context is already closed.

            if (Hibernate.isInitialized(task.getAssignedTo())) {
                this.assignedToName =
                        task.getAssignedTo().getName();
            }
        }
    }

    // =====================================
    // GETTERS
    // =====================================

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public Long getProjectId() {
        return projectId;
    }

    public Long getAssignedToId() {
        return assignedToId;
    }

    public String getAssignedToName() {
        return assignedToName;
    }
}