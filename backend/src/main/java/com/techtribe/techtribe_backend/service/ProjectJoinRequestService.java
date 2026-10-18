package com.techtribe.techtribe_backend.service;

import com.techtribe.techtribe_backend.dto.ProjectJoinRequestResponse;

import com.techtribe.techtribe_backend.model.DeveloperProfile;
import com.techtribe.techtribe_backend.model.Project;
import com.techtribe.techtribe_backend.model.ProjectJoinRequest;
import com.techtribe.techtribe_backend.model.ProjectMember;
import com.techtribe.techtribe_backend.model.User;

import com.techtribe.techtribe_backend.repository.DeveloperProfileRepository;
import com.techtribe.techtribe_backend.repository.ProjectRepository;
import com.techtribe.techtribe_backend.repository.ProjectJoinRequestRepository;
import com.techtribe.techtribe_backend.repository.ProjectMemberRepository;
import com.techtribe.techtribe_backend.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProjectJoinRequestService {

    private final ProjectRepository projectRepository;
    private final ProjectJoinRequestRepository joinRequestRepository;
    private final ProjectMemberRepository memberRepository;
    private final UserRepository userRepository;
    private final DeveloperProfileRepository developerProfileRepository;

    // CONSTRUCTOR

    public ProjectJoinRequestService(
            ProjectRepository projectRepository,
            ProjectJoinRequestRepository joinRequestRepository,
            ProjectMemberRepository memberRepository,
            UserRepository userRepository,
            DeveloperProfileRepository developerProfileRepository) {

        this.projectRepository = projectRepository;
        this.joinRequestRepository = joinRequestRepository;
        this.memberRepository = memberRepository;
        this.userRepository = userRepository;
        this.developerProfileRepository = developerProfileRepository;
    }

    // GET CURRENT USER

    private User getUser(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));
    }

    // VERIFY PROJECT OWNERSHIP

    private Project getOwnedProject(String email, Long projectId) {

        User owner = getUser(email);

        return projectRepository
                .findByIdAndOwnerId(projectId, owner.getId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Project not found or access denied"));
    }

    // DEVELOPER REQUESTS TO JOIN PROJECT

    @Transactional
    public ProjectJoinRequest requestToJoin(
            String email,
            Long projectId,
            String message) {

        User developer = getUser(email);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Project not found"));

        // Prevent owner from joining own project

        if (project.getOwner().getId().equals(developer.getId())) {
            throw new IllegalStateException(
                    "Project owner cannot request to join their own project");
        }

        // Check existing membership

        if (memberRepository.existsByProjectIdAndUserId(
                projectId, developer.getId())) {

            throw new IllegalStateException(
                    "You are already a project member");
        }

        // Prevent duplicate join requests

        if (joinRequestRepository.existsByProjectIdAndUserId(
                projectId, developer.getId())) {

            throw new IllegalStateException(
                    "A join request already exists for this project");
        }

        // Validate message

        if (message != null && message.length() > 500) {
            throw new IllegalStateException(
                    "Join request message cannot exceed 500 characters");
        }

        // Create request

        ProjectJoinRequest request = new ProjectJoinRequest();

        request.setProject(project);
        request.setUser(developer);
        request.setStatus("PENDING");
        request.setMessage(message);

        return joinRequestRepository.save(request);
    }

    // OWNER VIEWS PENDING REQUESTS

    @Transactional(readOnly = true)
    public List<ProjectJoinRequest> getPendingRequests(
            String email,
            Long projectId) {

        getOwnedProject(email, projectId);

        return joinRequestRepository
                .findByProjectIdAndStatus(projectId, "PENDING");
    }

    // GET JOIN REQUEST WITH DEVELOPER PROFILE

    @Transactional(readOnly = true)
    public ProjectJoinRequestResponse getRequestResponse(
            String email,
            Long projectId,
            Long requestId) {

        ProjectJoinRequest request = joinRequestRepository
                .findById(requestId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Join request not found"));

        if (!request.getProject().getId().equals(projectId)) {
            throw new IllegalArgumentException(
                    "Join request does not belong to this project");
        }

        User currentUser = getUser(email);

        boolean isOwner = request.getProject()
                .getOwner().getId().equals(currentUser.getId());

        boolean isRequester = request.getUser()
                .getId().equals(currentUser.getId());

        if (!isOwner && !isRequester) {
            throw new IllegalArgumentException(
                    "You do not have permission to view this request");
        }

        DeveloperProfile profile = developerProfileRepository
                .findByUserId(request.getUser().getId())
                .orElse(null);

        return new ProjectJoinRequestResponse(request, profile);
    }

    // OWNER VIEWS PENDING REQUESTS WITH PROFILES

    @Transactional(readOnly = true)
    public List<ProjectJoinRequestResponse> getPendingRequestResponses(
            String email,
            Long projectId) {

        getOwnedProject(email, projectId);

        return joinRequestRepository
                .findByProjectIdAndStatus(projectId, "PENDING")
                .stream()
                .map(request -> {

                    DeveloperProfile profile = developerProfileRepository
                            .findByUserId(request.getUser().getId())
                            .orElse(null);

                    return new ProjectJoinRequestResponse(
                            request, profile);
                })
                .toList();
    }

    // OWNER APPROVES REQUEST

    @Transactional
    public ProjectJoinRequest approveRequest(
            String email,
            Long projectId,
            Long requestId) {

        Project project = getOwnedProject(email, projectId);

        ProjectJoinRequest request = joinRequestRepository
                .findById(requestId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Join request not found"));

        if (!request.getProject().getId().equals(projectId)) {
            throw new IllegalArgumentException(
                    "Join request does not belong to this project");
        }

        if (!request.getStatus().equals("PENDING")) {
            throw new IllegalStateException(
                    "Join request has already been processed");
        }

        User developer = request.getUser();

        if (memberRepository.existsByProjectIdAndUserId(
                projectId, developer.getId())) {

            throw new IllegalStateException(
                    "Developer is already a project member");
        }

        // Add developer as contributor

        ProjectMember member = new ProjectMember();

        member.setProject(project);
        member.setUser(developer);
        member.setRole("CONTRIBUTOR");

        memberRepository.save(member);

        // Approve request

        request.setStatus("APPROVED");

        return joinRequestRepository.save(request);
    }

    // OWNER REJECTS REQUEST

    @Transactional
    public ProjectJoinRequest rejectRequest(
            String email,
            Long projectId,
            Long requestId) {

        getOwnedProject(email, projectId);

        ProjectJoinRequest request = joinRequestRepository
                .findById(requestId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Join request not found"));

        if (!request.getProject().getId().equals(projectId)) {
            throw new IllegalArgumentException(
                    "Join request does not belong to this project");
        }

        if (!request.getStatus().equals("PENDING")) {
            throw new IllegalStateException(
                    "Join request has already been processed");
        }

        request.setStatus("REJECTED");

        return joinRequestRepository.save(request);
    }
}