package com.techtribe.techtribe_backend.dto;

public class ProjectProgressResponse {

    private Long projectId;
    private String projectName;

    private long totalTasks;
    private long completedTasks;
    private long inProgressTasks;
    private long pendingTasks;

    private double completionPercentage;

    public ProjectProgressResponse(
            Long projectId,
            String projectName,
            long totalTasks,
            long completedTasks,
            long inProgressTasks,
            long pendingTasks,
            double completionPercentage) {

        this.projectId = projectId;
        this.projectName = projectName;
        this.totalTasks = totalTasks;
        this.completedTasks = completedTasks;
        this.inProgressTasks = inProgressTasks;
        this.pendingTasks = pendingTasks;
        this.completionPercentage = completionPercentage;
    }

    public Long getProjectId() {
        return projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public long getTotalTasks() {
        return totalTasks;
    }

    public long getCompletedTasks() {
        return completedTasks;
    }

    public long getInProgressTasks() {
        return inProgressTasks;
    }

    public long getPendingTasks() {
        return pendingTasks;
    }

    public double getCompletionPercentage() {
        return completionPercentage;
    }
}