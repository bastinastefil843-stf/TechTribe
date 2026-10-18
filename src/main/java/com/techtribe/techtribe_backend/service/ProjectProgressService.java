package com.techtribe.techtribe_backend.service;

import com.techtribe.techtribe_backend.dto.ProjectProgressResponse;
import com.techtribe.techtribe_backend.model.Project;
import com.techtribe.techtribe_backend.model.User;

import com.techtribe.techtribe_backend.repository.TaskRepository;
import com.techtribe.techtribe_backend.repository.ProjectRepository;
import com.techtribe.techtribe_backend.repository.ProjectMemberRepository;
import com.techtribe.techtribe_backend.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProjectProgressService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository memberRepository;
    private final UserRepository userRepository;

    // CONSTRUCTOR

    public ProjectProgressService(
            TaskRepository taskRepository,
            ProjectRepository projectRepository,
            ProjectMemberRepository memberRepository,
            UserRepository userRepository) {

        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.memberRepository = memberRepository;
        this.userRepository = userRepository;
    }

    // GET LOGGED-IN USER

    private User getAuthenticatedUser(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));
    }

    // VERIFY PROJECT ACCESS

    private Project getAccessibleProject(
            String email,
            Long projectId) {

        User user = getAuthenticatedUser(email);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Project not found"));

        boolean isOwner = project.getOwner().getId()
                .equals(user.getId());

        boolean isContributor =
                memberRepository.existsByProjectIdAndUserId(
                        projectId,
                        user.getId()
                );

        if (!isOwner && !isContributor) {
            throw new SecurityException(
                    "You are not a member of this project");
        }

        return project;
    }

    // CALCULATE PROJECT PROGRESS

    @Transactional(readOnly = true)
    public ProjectProgressResponse getProjectProgress(
            String email,
            Long projectId) {

        // Step 1: Verify project access

        Project project = getAccessibleProject(email, projectId);

        // Step 2: Count total tasks

        long totalTasks =
                taskRepository.countByProjectId(projectId);

        // Step 3: Count completed tasks

        long completedTasks =
                taskRepository.countByProjectIdAndStatus(
                        projectId,
                        "COMPLETED"
                );

        // Step 4: Count in-progress tasks

        long inProgressTasks =
                taskRepository.countByProjectIdAndStatus(
                        projectId,
                        "IN_PROGRESS"
                );

        // Step 5: Count pending tasks

        long pendingTasks =
                taskRepository.countByProjectIdAndStatus(
                        projectId,
                        "TODO"
                );

        // Step 6: Calculate completion percentage

        double completionPercentage = 0.0;

        if (totalTasks > 0) {
            completionPercentage =
                    ((double) completedTasks / totalTasks) * 100;
        }

        // Step 7: Round to two decimal places

        completionPercentage =
                Math.round(completionPercentage * 100.0) / 100.0;

        // Step 8: Return project progress response

        return new ProjectProgressResponse(
                project.getId(),
                project.getTitle(),
                totalTasks,
                completedTasks,
                inProgressTasks,
                pendingTasks,
                completionPercentage
        );
    }
}