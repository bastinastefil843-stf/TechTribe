package com.techtribe.techtribe_backend.controller;

import com.techtribe.techtribe_backend.dto.ProjectRequest;
import com.techtribe.techtribe_backend.dto.ProjectResponse;

import com.techtribe.techtribe_backend.model.Project;
import com.techtribe.techtribe_backend.service.ProjectService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    // ==========================================
    // CONSTRUCTOR
    // ==========================================

    public ProjectController(
            ProjectService projectService) {

        this.projectService = projectService;
    }

    // ==========================================
    // 1. CREATE PROJECT
    // ==========================================

    @PostMapping
    public ResponseEntity<?> createProject(
            @Valid @RequestBody ProjectRequest request,
            Authentication authentication) {

        Project projectData = new Project();

        projectData.setTitle(request.getTitle());
        projectData.setDescription(request.getDescription());
        projectData.setTechnologies(request.getTechnologies());

        Project savedProject =
                projectService.createProject(
                        authentication.getName(),
                        projectData
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ProjectResponse(savedProject));
    }

    // ==========================================
    // 2. VIEW MY PROJECTS
    // ==========================================

    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getMyProjects(
            Authentication authentication) {

        List<ProjectResponse> projects =
                projectService.getMyProjects(
                        authentication.getName()
                )
                .stream()
                .map(ProjectResponse::new)
                .toList();

        return ResponseEntity.ok(projects);
    }

    // ==========================================
    // 3. EXPLORE PROJECTS
    // NEW FEATURE
    // ==========================================

    @GetMapping("/explore")
    public ResponseEntity<List<ProjectResponse>> exploreProjects(
            Authentication authentication) {

        List<ProjectResponse> projects =
                projectService.exploreProjects(
                        authentication.getName()
                )
                .stream()
                .map(ProjectResponse::new)
                .toList();

        return ResponseEntity.ok(projects);
    }

    // ==========================================
    // 4. VIEW EXPLORE PROJECT DETAILS
    // NEW FEATURE
    // ==========================================

    @GetMapping("/explore/{id}")
    public ResponseEntity<?> getExploreProjectById(
            @PathVariable Long id,
            Authentication authentication) {

        try {

            Project project =
                    projectService.getExploreProjectById(
                            authentication.getName(),
                            id
                    );

            return ResponseEntity.ok(
                    new ProjectResponse(project)
            );

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Project not found");
        }
    }

    // ==========================================
    // 5. VIEW SINGLE OWNED PROJECT
    // ==========================================

    @GetMapping("/{id}")
    public ResponseEntity<?> getProjectById(
            @PathVariable Long id,
            Authentication authentication) {

        try {

            Project project =
                    projectService.getMyProjectById(
                            authentication.getName(),
                            id
                    );

            return ResponseEntity.ok(
                    new ProjectResponse(project)
            );

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Project not found");
        }
    }

    // ==========================================
    // 6. UPDATE PROJECT
    // PROJECT OWNER ONLY
    // ==========================================

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProject(
            @PathVariable Long id,
            @Valid @RequestBody ProjectRequest request,
            Authentication authentication) {

        Project updatedData = new Project();

        updatedData.setTitle(request.getTitle());
        updatedData.setDescription(request.getDescription());
        updatedData.setTechnologies(request.getTechnologies());
        updatedData.setStatus(request.getStatus());

        try {

            Project updatedProject =
                    projectService.updateProject(
                            authentication.getName(),
                            id,
                            updatedData
                    );

            return ResponseEntity.ok(
                    new ProjectResponse(updatedProject)
            );

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Project not found or access denied");
        }
    }

    // ==========================================
    // 7. DELETE PROJECT
    // PROJECT OWNER ONLY
    // ==========================================

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProject(
            @PathVariable Long id,
            Authentication authentication) {

        try {

            projectService.deleteProject(
                    authentication.getName(),
                    id
            );

            return ResponseEntity.noContent().build();

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Project not found or access denied");
        }
    }
}