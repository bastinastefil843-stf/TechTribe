package com.techtribe.techtribe_backend.controller;

import com.techtribe.techtribe_backend.dto.ProjectProgressResponse;
import com.techtribe.techtribe_backend.service.ProjectProgressService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projects/{projectId}/progress")
public class ProjectProgressController {

    private final ProjectProgressService progressService;

    public ProjectProgressController(
            ProjectProgressService progressService) {

        this.progressService = progressService;
    }

    // GET PROJECT PROGRESS

    @GetMapping
    public ResponseEntity<?> getProjectProgress(
            @PathVariable Long projectId,
            Authentication authentication) {

        try {

            String email = authentication.getName();

            ProjectProgressResponse response =
                    progressService.getProjectProgress(
                            email,
                            projectId
                    );

            return ResponseEntity.ok(response);

        } catch (SecurityException e) {

            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(e.getMessage());

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }
}