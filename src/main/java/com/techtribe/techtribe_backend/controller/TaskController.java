package com.techtribe.techtribe_backend.controller;

import com.techtribe.techtribe_backend.dto.TaskRequest;
import com.techtribe.techtribe_backend.dto.TaskResponse;
import com.techtribe.techtribe_backend.model.Task;
import com.techtribe.techtribe_backend.service.TaskService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.groups.Default;

import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    // =====================================
    // 1. CREATE TASK
    // =====================================

    @PostMapping
    public ResponseEntity<?> createTask(
            @PathVariable Long projectId,
            @Validated({Default.class, TaskRequest.OnCreate.class})
            @RequestBody TaskRequest request,
            Authentication authentication) {

        Task taskData = toTask(request);
        taskData.setStatus(request.getStatus());

        try {
            Task task = taskService.createTask(
                    authentication.getName(),
                    projectId,
                    taskData
            );

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new TaskResponse(task));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    // =====================================
    // 2. VIEW ALL PROJECT TASKS
    // =====================================

    @GetMapping
    public ResponseEntity<?> getProjectTasks(
            @PathVariable Long projectId,
            Authentication authentication) {

        try {
            List<TaskResponse> tasks = taskService.getProjectTasks(
                    authentication.getName(),
                    projectId
            )
                    .stream()
                    .map(TaskResponse::new)
                    .toList();

            return ResponseEntity.ok(tasks);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    // =====================================
    // 3. VIEW SINGLE TASK
    // =====================================

    @GetMapping("/{taskId}")
    public ResponseEntity<?> getTask(
            @PathVariable Long projectId,
            @PathVariable Long taskId,
            Authentication authentication) {

        try {
            Task task = taskService.getTaskById(
                    authentication.getName(),
                    projectId,
                    taskId
            );

            return ResponseEntity.ok(new TaskResponse(task));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    // =====================================
    // 4. UPDATE TASK
    // OWNER: ALL DETAILS
    // ASSIGNED CONTRIBUTOR: STATUS ONLY
    // =====================================

    @PutMapping("/{taskId}")
    public ResponseEntity<?> updateTask(
            @PathVariable Long projectId,
            @PathVariable Long taskId,
            @Validated(Default.class)
            @RequestBody TaskRequest request,
            Authentication authentication) {

        // A supplied title must not be blank.
        // Missing title is allowed for status-only updates.
        if (request.getTitle() != null &&
                request.getTitle().isBlank()) {

            return ResponseEntity.badRequest()
                    .body("Task title cannot be blank");
        }

        Task updatedData = toTask(request);
        updatedData.setStatus(request.getStatus());

        try {
            Task task = taskService.updateTask(
                    authentication.getName(),
                    projectId,
                    taskId,
                    updatedData
            );

            return ResponseEntity.ok(new TaskResponse(task));

        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(e.getMessage());

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    // =====================================
    // 5. ASSIGN TASK - OWNER ONLY
    // =====================================

    @PutMapping("/{taskId}/assign/{developerId}")
    public ResponseEntity<?> assignTask(
            @PathVariable Long projectId,
            @PathVariable Long taskId,
            @PathVariable Long developerId,
            Authentication authentication) {

        try {
            Task task = taskService.assignTask(
                    authentication.getName(),
                    projectId,
                    taskId,
                    developerId
            );

            return ResponseEntity.ok(new TaskResponse(task));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    // =====================================
    // 6. DELETE TASK - OWNER ONLY
    // =====================================

    @DeleteMapping("/{taskId}")
    public ResponseEntity<?> deleteTask(
            @PathVariable Long projectId,
            @PathVariable Long taskId,
            Authentication authentication) {

        try {
            taskService.deleteTask(
                    authentication.getName(),
                    projectId,
                    taskId
            );

            return ResponseEntity.noContent().build();

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    // =====================================
    // 7. CONVERT REQUEST INTO TASK ENTITY
    // =====================================

    private Task toTask(TaskRequest request) {

        Task task = new Task();

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setDeadline(request.getDeadline());

        return task;
    }
}