package com.techtribe.techtribe_backend.service;

import com.techtribe.techtribe_backend.model.Project;
import com.techtribe.techtribe_backend.model.Task;
import com.techtribe.techtribe_backend.model.User;

import com.techtribe.techtribe_backend.repository.ProjectMemberRepository;
import com.techtribe.techtribe_backend.repository.ProjectRepository;
import com.techtribe.techtribe_backend.repository.TaskRepository;
import com.techtribe.techtribe_backend.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository memberRepository;
    private final UserRepository userRepository;

    // ==========================================
    // CONSTRUCTOR
    // ==========================================

    public TaskService(
            TaskRepository taskRepository,
            ProjectRepository projectRepository,
            ProjectMemberRepository memberRepository,
            UserRepository userRepository) {

        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.memberRepository = memberRepository;
        this.userRepository = userRepository;
    }

    // ==========================================
    // 1. GET AUTHENTICATED USER
    // ==========================================

    private User getAuthenticatedUser(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found"
                        ));
    }

    // ==========================================
    // 2. VERIFY PROJECT OWNER
    // ==========================================

    private Project getOwnedProject(
            String email,
            Long projectId) {

        User user = getAuthenticatedUser(email);

        return projectRepository
                .findByIdAndOwnerId(
                        projectId,
                        user.getId()
                )
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Project not found or access denied"
                        ));
    }

    // ==========================================
    // 3. VERIFY PROJECT OWNER OR CONTRIBUTOR
    // ==========================================

    private Project getAccessibleProject(
            String email,
            Long projectId) {

        User user = getAuthenticatedUser(email);

        Project project = projectRepository
                .findById(projectId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Project not found or access denied"
                        ));

        boolean isOwner =
                project.getOwner()
                        .getId()
                        .equals(user.getId());

        boolean isContributor =
                memberRepository.existsByProjectIdAndUserId(
                        projectId,
                        user.getId()
                );

        if (!isOwner && !isContributor) {

            throw new IllegalArgumentException(
                    "Project not found or access denied"
            );
        }

        return project;
    }

    // ==========================================
    // 4. CREATE TASK
    // OWNER OR CONTRIBUTOR
    // ==========================================

    @Transactional
    public Task createTask(
            String email,
            Long projectId,
            Task taskData) {

        Project project =
                getAccessibleProject(email, projectId);

        Task task = new Task();

        task.setTitle(taskData.getTitle());
        task.setDescription(taskData.getDescription());
        task.setDeadline(taskData.getDeadline());

        // New tasks always start with TODO
        task.setStatus("TODO");

        task.setProject(project);

        return taskRepository.save(task);
    }

    // ==========================================
    // 5. VIEW ALL PROJECT TASKS
    // OWNER OR CONTRIBUTOR
    // ==========================================

    @Transactional(readOnly = true)
    public List<Task> getProjectTasks(
            String email,
            Long projectId) {

        getAccessibleProject(email, projectId);

        return taskRepository.findByProjectId(projectId);
    }

    // ==========================================
    // 6. VIEW INDIVIDUAL TASK
    // OWNER OR CONTRIBUTOR
    // ==========================================

    @Transactional(readOnly = true)
    public Task getTaskById(
            String email,
            Long projectId,
            Long taskId) {

        getAccessibleProject(email, projectId);

        return taskRepository
                .findByIdAndProjectId(
                        taskId,
                        projectId
                )
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Task not found"
                        ));
    }

    // ==========================================
    // 7. UPDATE TASK
    //
    // OWNER:
    // - UPDATE TITLE
    // - UPDATE DESCRIPTION
    // - UPDATE DEADLINE
    // - UPDATE STATUS
    //
    // ASSIGNED CONTRIBUTOR:
    // - UPDATE STATUS ONLY
    // ==========================================

    @Transactional
    public Task updateTask(
            String email,
            Long projectId,
            Long taskId,
            Task updatedData) {

        // STEP 1: GET AUTHENTICATED USER

        User user = getAuthenticatedUser(email);

        // STEP 2: VERIFY PROJECT ACCESS

        Project project =
                getAccessibleProject(email, projectId);

        // STEP 3: FIND TASK

        Task task = taskRepository
                .findByIdAndProjectId(
                        taskId,
                        projectId
                )
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Task not found"
                        ));

        // STEP 4: CHECK PROJECT OWNERSHIP

        boolean isOwner =
                project.getOwner()
                        .getId()
                        .equals(user.getId());

        // STEP 5: CHECK TASK ASSIGNMENT

        boolean isAssignedContributor =
                task.getAssignedTo() != null
                && task.getAssignedTo()
                        .getId()
                        .equals(user.getId());

        // ======================================
        // STEP 6: BLOCK UNAUTHORIZED USERS
        // ======================================

        if (!isOwner && !isAssignedContributor) {

            throw new IllegalStateException(
                    "You can only update tasks assigned to you"
            );
        }

        // ======================================
        // STEP 7: BLOCK UNAUTHORIZED FIELD UPDATES
        // ======================================

        if (!isOwner) {

            boolean attemptingOwnerOnlyUpdate =
                    updatedData.getTitle() != null
                    || updatedData.getDescription() != null
                    || updatedData.getDeadline() != null;

            if (attemptingOwnerOnlyUpdate) {

                throw new IllegalStateException(
                        "Only the project owner can update " +
                        "task title, description, or deadline"
                );
            }
        }

        // ======================================
        // STEP 8: VALIDATE TASK STATUS
        // ======================================

        if (updatedData.getStatus() != null) {

            List<String> allowedStatuses =
                    List.of(
                            "TODO",
                            "IN_PROGRESS",
                            "COMPLETED"
                    );

            if (!allowedStatuses.contains(
                    updatedData.getStatus())) {

                throw new IllegalArgumentException(
                        "Invalid task status"
                );
            }
        }

        // ======================================
        // STEP 9: OWNER UPDATES TASK DETAILS
        // ======================================

        if (isOwner) {

            // UPDATE TITLE

            if (updatedData.getTitle() != null) {

                String newTitle =
                        updatedData.getTitle();

                if (newTitle.isBlank()
                        || newTitle.length() > 150) {

                    throw new IllegalArgumentException(
                            "Task title must contain " +
                            "1 to 150 characters"
                    );
                }

                task.setTitle(newTitle);
            }

            // UPDATE DESCRIPTION

            if (updatedData.getDescription() != null) {

                task.setDescription(
                        updatedData.getDescription()
                );
            }

            // UPDATE DEADLINE

            if (updatedData.getDeadline() != null) {

                task.setDeadline(
                        updatedData.getDeadline()
                );
            }
        }

        // ======================================
        // STEP 10: UPDATE TASK STATUS
        //
        // OWNER OR ASSIGNED CONTRIBUTOR
        // ======================================

        if (updatedData.getStatus() != null) {

            task.setStatus(
                    updatedData.getStatus()
            );
        }

        // ======================================
        // STEP 11: SAVE TASK
        // ======================================

        return taskRepository.save(task);
    }

    // ==========================================
    // 8. ASSIGN TASK
    // PROJECT OWNER ONLY
    // ==========================================

    @Transactional
    public Task assignTask(
            String email,
            Long projectId,
            Long taskId,
            Long developerId) {

        // Verify project ownership

        Project project =
                getOwnedProject(email, projectId);

        // Find task

        Task task = taskRepository
                .findByIdAndProjectId(
                        taskId,
                        projectId
                )
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Task not found"
                        ));

        // Find developer

        User developer = userRepository
                .findById(developerId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Developer not found"
                        ));

        // Check whether developer owns project

        boolean isProjectOwner =
                project.getOwner()
                        .getId()
                        .equals(developerId);

        // Check project membership

        boolean isProjectMember =
                memberRepository.existsByProjectIdAndUserId(
                        projectId,
                        developerId
                );

        // Block assignment to nonmembers

        if (!isProjectOwner && !isProjectMember) {

            throw new IllegalArgumentException(
                    "Developer is not a project member"
            );
        }

        // Assign task

        task.setAssignedTo(developer);

        return taskRepository.save(task);
    }

    // ==========================================
    // 9. DELETE TASK
    // PROJECT OWNER ONLY
    // ==========================================

    @Transactional
    public void deleteTask(
            String email,
            Long projectId,
            Long taskId) {

        // Verify project ownership

        getOwnedProject(email, projectId);

        // Find task

        Task task = taskRepository
                .findByIdAndProjectId(
                        taskId,
                        projectId
                )
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Task not found"
                        ));

        // Delete task

        taskRepository.delete(task);
    }
}