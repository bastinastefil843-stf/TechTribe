package com.techtribe.techtribe_backend.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class TaskRequest {

    public interface OnCreate {}

    @NotBlank(
        groups = OnCreate.class,
        message = "Task title is required"
    )
    @Size(
        max = 150,
        message = "Task title must not exceed 150 characters"
    )
    private String title;

    @Size(
        max = 2000,
        message = "Task description must not exceed 2000 characters"
    )
    private String description;

    @Pattern(
        regexp = "^(TODO|IN_PROGRESS|COMPLETED)$",
        message = "Invalid task status"
    )
    private String status;

    private LocalDate deadline;

    public TaskRequest() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }
}