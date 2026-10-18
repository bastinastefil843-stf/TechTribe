package com.techtribe.techtribe_backend.controller;

import com.techtribe.techtribe_backend.dto.AICodeReviewResponse;
import com.techtribe.techtribe_backend.model.CodeSnippet;
import com.techtribe.techtribe_backend.model.User;

import com.techtribe.techtribe_backend.repository.CodeSnippetRepository;
import com.techtribe.techtribe_backend.repository.ProjectMemberRepository;
import com.techtribe.techtribe_backend.repository.UserRepository;

import com.techtribe.techtribe_backend.service.AICodeReviewService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/ai/code-snippets")
public class AICodeReviewController {

    private final AICodeReviewService codeReviewService;
    private final CodeSnippetRepository codeSnippetRepository;
    private final ProjectMemberRepository memberRepository;
    private final UserRepository userRepository;

    public AICodeReviewController(
            AICodeReviewService codeReviewService,
            CodeSnippetRepository codeSnippetRepository,
            ProjectMemberRepository memberRepository,
            UserRepository userRepository) {

        this.codeReviewService = codeReviewService;
        this.codeSnippetRepository = codeSnippetRepository;
        this.memberRepository = memberRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/{snippetId}/review")
    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    public ResponseEntity<?> reviewCode(
            @PathVariable Long snippetId,
            Authentication authentication) {

        // STEP 1: Identify authenticated user

        User user = userRepository
                .findByEmail(authentication.getName())
                .orElse(null);

        if (user == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "error",
                            "User not found"
                    ));
        }

        // STEP 2: Retrieve code snippet

        CodeSnippet snippet = codeSnippetRepository
                .findById(snippetId)
                .orElse(null);

        if (snippet == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "error",
                            "Code snippet not found"
                    ));
        }

        // STEP 3: Retrieve associated project ID

        if (snippet.getProject() == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "error",
                            "Associated project not found"
                    ));
        }

        Long projectId = snippet.getProject().getId();

        // STEP 4: Check project ownership
        // Transaction keeps Hibernate session open here.

        boolean isOwner = snippet.getProject()
                .getOwner()
                .getId()
                .equals(user.getId());

        // STEP 5: Check accepted contributor membership

        boolean isContributor =
                memberRepository.existsByProjectIdAndUserId(
                        projectId,
                        user.getId()
                );

        // STEP 6: Block unauthorized developers

        if (!isOwner && !isContributor) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(Map.of(
                            "error",
                            "You are not a member of this project"
                    ));
        }

        // STEP 7: Generate AI code review

        AICodeReviewResponse review =
                codeReviewService.reviewCode(snippetId);

        // STEP 8: Return AI review

        return ResponseEntity.ok(review);
    }
}