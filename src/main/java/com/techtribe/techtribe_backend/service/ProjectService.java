package com.techtribe.techtribe_backend.service;

import com.techtribe.techtribe_backend.model.Project;
import com.techtribe.techtribe_backend.model.User;

import com.techtribe.techtribe_backend.repository.ProjectRepository;
import com.techtribe.techtribe_backend.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    // ==========================================
    // CONSTRUCTOR
    // ==========================================

    public ProjectService(
            ProjectRepository projectRepository,
            UserRepository userRepository) {

        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    // ==========================================
    // 1. CREATE PROJECT
    // ==========================================

    @Transactional
    public Project createProject(
            String email,
            Project projectData) {

        User owner = findUserByEmail(email);

        Project project = new Project();

        project.setTitle(projectData.getTitle());
        project.setDescription(projectData.getDescription());
        project.setTechnologies(projectData.getTechnologies());

        // New projects start with PLANNING status
        project.setStatus("PLANNING");

        // Assign authenticated user as owner
        project.setOwner(owner);

        return projectRepository.save(project);
    }

    // ==========================================
    // 2. GET MY PROJECTS
    // ==========================================

    @Transactional(readOnly = true)
    public List<Project> getMyProjects(String email) {

        User user = findUserByEmail(email);

        return projectRepository
                .findByOwnerIdOrderByCreatedAtDesc(
                        user.getId()
                );
    }

    // ==========================================
    // 3. GET SINGLE OWNED PROJECT
    // ==========================================

    @Transactional(readOnly = true)
    public Project getMyProjectById(
            String email,
            Long projectId) {

        User user = findUserByEmail(email);

        return projectRepository
                .findByIdAndOwnerId(
                        projectId,
                        user.getId()
                )
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Project not found"
                        )
                );
    }

    // ==========================================
    // 4. UPDATE PROJECT
    // PROJECT OWNER ONLY
    // ==========================================

    @Transactional
    public Project updateProject(
            String email,
            Long projectId,
            Project updatedData) {

        User user = findUserByEmail(email);

        Project project = projectRepository
                .findByIdAndOwnerId(
                        projectId,
                        user.getId()
                )
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Project not found or access denied"
                        )
                );

        // Update project information
        project.setTitle(updatedData.getTitle());
        project.setDescription(updatedData.getDescription());
        project.setTechnologies(updatedData.getTechnologies());

        // Update status if provided
        if (updatedData.getStatus() != null) {
            project.setStatus(
                    updatedData.getStatus()
            );
        }

        return projectRepository.save(project);
    }

    // ==========================================
    // 5. DELETE PROJECT
    // PROJECT OWNER ONLY
    // ==========================================

    @Transactional
    public void deleteProject(
            String email,
            Long projectId) {

        User user = findUserByEmail(email);

        Project project = projectRepository
                .findByIdAndOwnerId(
                        projectId,
                        user.getId()
                )
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Project not found or access denied"
                        )
                );

        projectRepository.delete(project);
    }

    // ==========================================
    // 6. EXPLORE ALL PROJECTS
    // NEW FEATURE
    // ==========================================

    @Transactional(readOnly = true)
    public List<Project> exploreProjects(String email) {

        // Verify that the requesting user exists
        findUserByEmail(email);

        // Return projects available for discovery
        return projectRepository.findAll();
    }

    // ==========================================
    // 7. VIEW EXPLORE PROJECT DETAILS
    // NEW FEATURE
    // ==========================================

    @Transactional(readOnly = true)
    public Project getExploreProjectById(
            String email,
            Long projectId) {

        // Verify authenticated user
        findUserByEmail(email);

        return projectRepository
                .findById(projectId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Project not found"
                        )
                );
    }

    // ==========================================
    // 8. HELPER METHOD
    // ==========================================

    private User findUserByEmail(String email) {

        return userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found"
                        )
                );
    }
}