package com.techtribe.techtribe_backend.controller;

import com.techtribe.techtribe_backend.model.CodeSnippet;
import com.techtribe.techtribe_backend.model.Project;
import com.techtribe.techtribe_backend.model.User;

import com.techtribe.techtribe_backend.repository.CodeSnippetRepository;
import com.techtribe.techtribe_backend.repository.ProjectMemberRepository;
import com.techtribe.techtribe_backend.repository.UserRepository;

import com.techtribe.techtribe_backend.service.AICodeExplanationService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai/code-snippets")
public class AICodeExplanationController {

    private final AICodeExplanationService explanationService;
    private final CodeSnippetRepository codeSnippetRepository;
    private final ProjectMemberRepository memberRepository;
    private final UserRepository userRepository;

    public AICodeExplanationController(
            AICodeExplanationService explanationService,
            CodeSnippetRepository codeSnippetRepository,
            ProjectMemberRepository memberRepository,
            UserRepository userRepository) {

        this.explanationService = explanationService;
        this.codeSnippetRepository = codeSnippetRepository;
        this.memberRepository = memberRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/{snippetId}/explain")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> explainCode(
            @PathVariable Long snippetId,
            Authentication authentication) {

        User user = userRepository
                .findByEmail(authentication.getName())
                .orElse(null);

        if (user == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "User not found"));
        }

        CodeSnippet snippet = codeSnippetRepository
                .findById(snippetId)
                .orElse(null);

        if (snippet == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Code snippet not found"));
        }

        Project project = snippet.getProject();

        boolean isOwner = project.getOwner()
                .getId()
                .equals(user.getId());

        boolean isContributor =
                memberRepository.existsByProjectIdAndUserId(
                        project.getId(),
                        user.getId()
                );

        if (!isOwner && !isContributor) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(Map.of(
                            "error",
                            "You are not a member of this project"
                    ));
        }

        String explanation =
                explanationService.explainCode(snippetId);

        return ResponseEntity.ok(
                Map.of("explanation", explanation)
        );
    }
}