package com.techtribe.techtribe_backend.service;

import com.techtribe.techtribe_backend.model.Project;
import com.techtribe.techtribe_backend.model.User;

import com.techtribe.techtribe_backend.repository.ProjectRepository;
import com.techtribe.techtribe_backend.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProjectServiceSecurityTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    private ProjectService projectService;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);

        projectService = new ProjectService(
                projectRepository,
                userRepository
        );
    }

    // ==========================================
    // TEST 1: NONOWNER CANNOT DELETE PROJECT
    // ==========================================
    @Test
    void nonOwnerCannotDeleteProject() {

        String nonOwnerEmail = "nonowner@example.com";

        Long projectId = 1L;
        Long nonOwnerId = 99L;

        User nonOwner = new User();
        nonOwner.setId(nonOwnerId);

        when(userRepository.findByEmail(nonOwnerEmail))
                .thenReturn(Optional.of(nonOwner));

        // Repository returns no project because user is not the owner
        when(projectRepository.findByIdAndOwnerId(
                projectId,
                nonOwnerId
        )).thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> projectService.deleteProject(
                        nonOwnerEmail,
                        projectId
                )
        );

        // Verify that no project deletion occurred
        verify(projectRepository, never())
                .delete(any(Project.class));
    }

    // ==========================================
    // TEST 2: OWNER CAN DELETE PROJECT
    // ==========================================
    @Test
    void ownerCanDeleteProject() {

        String ownerEmail = "owner@example.com";

        Long projectId = 1L;
        Long ownerId = 8L;

        User owner = new User();
        owner.setId(ownerId);

        Project project = new Project();
        project.setId(projectId);
        project.setOwner(owner);

        when(userRepository.findByEmail(ownerEmail))
                .thenReturn(Optional.of(owner));

        when(projectRepository.findByIdAndOwnerId(
                projectId,
                ownerId
        )).thenReturn(Optional.of(project));

        projectService.deleteProject(
                ownerEmail,
                projectId
        );

        // Verify that deletion was requested exactly once
        verify(projectRepository, times(1))
                .delete(project);
    }
}