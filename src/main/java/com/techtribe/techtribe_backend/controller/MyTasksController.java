package com.techtribe.techtribe_backend.controller;

import com.techtribe.techtribe_backend.dto.TaskResponse;
import com.techtribe.techtribe_backend.model.Task;
import com.techtribe.techtribe_backend.model.User;

import com.techtribe.techtribe_backend.repository.TaskRepository;
import com.techtribe.techtribe_backend.repository.UserRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RestController
@RequestMapping("/api/my-tasks")
public class MyTasksController {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public MyTasksController(
            TaskRepository taskRepository,
            UserRepository userRepository) {

        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    // ==========================================
    // GET TASKS ASSIGNED TO LOGGED-IN USER
    // ==========================================

    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<?> getMyTasks(
            Authentication authentication) {

        if (authentication == null ||
                !authentication.isAuthenticated()) {

            return ResponseEntity.status(401)
                    .body("Unauthorized");
        }

        User user = userRepository
                .findByEmail(authentication.getName())
                .orElse(null);

        if (user == null) {
            return ResponseEntity.status(404)
                    .body("User not found");
        }

        List<Task> tasks = taskRepository
                .findByAssignedToId(user.getId());

        List<TaskResponse> response = tasks
                .stream()
                .map(TaskResponse::new)
                .toList();

        return ResponseEntity.ok(response);
    }
}