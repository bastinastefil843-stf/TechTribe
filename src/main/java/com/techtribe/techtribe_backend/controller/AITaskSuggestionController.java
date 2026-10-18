package com.techtribe.techtribe_backend.controller;

import com.techtribe.techtribe_backend.dto.AITaskSuggestionResponse;
import com.techtribe.techtribe_backend.model.Project;
import com.techtribe.techtribe_backend.model.User;

import com.techtribe.techtribe_backend.repository.ProjectRepository;
import com.techtribe.techtribe_backend.repository.ProjectMemberRepository;
import com.techtribe.techtribe_backend.repository.UserRepository;

import com.techtribe.techtribe_backend.service.AITaskSuggestionService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai/projects")
public class AITaskSuggestionController {

    private final AITaskSuggestionService taskSuggestionService;
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository memberRepository;
    private final UserRepository userRepository;

    public AITaskSuggestionController(
            AITaskSuggestionService taskSuggestionService,
            ProjectRepository projectRepository,
            ProjectMemberRepository memberRepository,
            UserRepository userRepository) {

        this.taskSuggestionService = taskSuggestionService;
        this.projectRepository = projectRepository;
        this.memberRepository = memberRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/{projectId}/task-suggestions")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getTaskSuggestions(
            @PathVariable Long projectId,
            Authentication authentication) {

        // STEP 1: Get authenticated user
        User user = userRepository
                .findByEmail(authentication.getName())
                .orElse(null);

        if (user == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "User not found"));
        }

        // STEP 2: Find project
        Project project = projectRepository
                .findById(projectId)
                .orElse(null);

        if (project == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Project not found"));
        }

        // STEP 3: Check project ownership
        boolean isOwner = project.getOwner()
                .getId()
                .equals(user.getId());

        // STEP 4: Check contributor membership
        boolean isContributor =
                memberRepository.existsByProjectIdAndUserId(
                        projectId,
                        user.getId()
                );

        // STEP 5: Deny access to nonmembers
        if (!isOwner && !isContributor) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(Map.of(
                            "error",
                            "You are not a member of this project"
                    ));
        }

        // STEP 6: Generate AI task suggestions
        List<AITaskSuggestionResponse> suggestions =
                taskSuggestionService.generateTaskSuggestions(projectId);

        // STEP 7: Return suggestions
        return ResponseEntity.ok(suggestions);
    }
}