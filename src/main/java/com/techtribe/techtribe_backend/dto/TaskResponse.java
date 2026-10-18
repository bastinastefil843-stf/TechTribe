
package com.techtribe.techtribe_backend.dto;

import com.techtribe.techtribe_backend.model.Task;
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

        this.id = task.getId();
        this.title = task.getTitle();
        this.description = task.getDescription();
        this.status = task.getStatus();
        this.deadline = task.getDeadline();

        this.projectId = task.getProject().getId();

        if (task.getAssignedTo() != null) {
            this.assignedToId = task.getAssignedTo().getId();
            this.assignedToName = task.getAssignedTo().getName();
        }
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