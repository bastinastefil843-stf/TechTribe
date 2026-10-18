package com.techtribe.techtribe_backend.service;

import com.techtribe.techtribe_backend.dto.ProjectMemberResponse;

import com.techtribe.techtribe_backend.model.Project;
import com.techtribe.techtribe_backend.model.ProjectMember;
import com.techtribe.techtribe_backend.model.User;

import com.techtribe.techtribe_backend.repository.ProjectRepository;
import com.techtribe.techtribe_backend.repository.ProjectMemberRepository;
import com.techtribe.techtribe_backend.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProjectMemberService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository memberRepository;
    private final UserRepository userRepository;

    public ProjectMemberService(
            ProjectRepository projectRepository,
            ProjectMemberRepository memberRepository,
            UserRepository userRepository) {

        this.projectRepository = projectRepository;
        this.memberRepository = memberRepository;
        this.userRepository = userRepository;
    }

    // =====================================
    // GET PROJECT MEMBERS
    // =====================================

    @Transactional(readOnly = true)
    public List<ProjectMemberResponse> getProjectMembers(
            String email,
            Long projectId) {

        // STEP 1: GET AUTHENTICATED USER

        User currentUser = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found"
                        ));

        // STEP 2: FIND PROJECT

        Project project = projectRepository
                .findById(projectId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Project not found"
                        ));

        // STEP 3: VERIFY PROJECT ACCESS

        boolean isOwner =
                project.getOwner()
                        .getId()
                        .equals(currentUser.getId());

        boolean isMember =
                memberRepository.existsByProjectIdAndUserId(
                        projectId,
                        currentUser.getId()
                );

        if (!isOwner && !isMember) {
            throw new SecurityException(
                    "You do not have access to this project"
            );
        }

        // STEP 4: GET PROJECT MEMBERS

        List<ProjectMember> members =
                memberRepository.findByProjectId(projectId);

        // STEP 5: CONVERT TO EXISTING DTO

        return members.stream()
                .map(ProjectMemberResponse::new)
                .toList();
    }
}