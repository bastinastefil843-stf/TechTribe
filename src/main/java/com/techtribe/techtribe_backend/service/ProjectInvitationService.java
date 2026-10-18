package com.techtribe.techtribe_backend.service;

import com.techtribe.techtribe_backend.dto.ProjectInvitationResponse;
import com.techtribe.techtribe_backend.model.Project;
import com.techtribe.techtribe_backend.model.ProjectInvitation;
import com.techtribe.techtribe_backend.model.ProjectMember;
import com.techtribe.techtribe_backend.model.User;

import com.techtribe.techtribe_backend.repository.ProjectInvitationRepository;
import com.techtribe.techtribe_backend.repository.ProjectMemberRepository;
import com.techtribe.techtribe_backend.repository.ProjectRepository;
import com.techtribe.techtribe_backend.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProjectInvitationService {

    private final ProjectInvitationRepository invitationRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository memberRepository;
    private final UserRepository userRepository;

    public ProjectInvitationService(
            ProjectInvitationRepository invitationRepository,
            ProjectRepository projectRepository,
            ProjectMemberRepository memberRepository,
            UserRepository userRepository) {

        this.invitationRepository = invitationRepository;
        this.projectRepository = projectRepository;
        this.memberRepository = memberRepository;
        this.userRepository = userRepository;
    }

    // GET LOGGED-IN USER

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));
    }

    // VERIFY PROJECT OWNER

    private Project getOwnedProject(String email, Long projectId) {
        User owner = getUser(email);

        return projectRepository
                .findByIdAndOwnerId(projectId, owner.getId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Project not found or access denied"));
    }

    // OWNER SENDS INVITATION

    @Transactional
    public ProjectInvitationResponse sendInvitation(
            String ownerEmail,
            Long projectId,
            String developerEmail) {

        Project project = getOwnedProject(ownerEmail, projectId);
        User owner = getUser(ownerEmail);

        if (developerEmail == null || developerEmail.isBlank()) {
            throw new IllegalStateException(
                    "Developer email is required");
        }

        User developer = getUser(developerEmail.trim());

        // Owner cannot invite themselves

        if (owner.getId().equals(developer.getId())) {
            throw new IllegalStateException(
                    "Project owner cannot invite themselves");
        }

        // Prevent inviting existing contributors

        if (memberRepository.existsByProjectIdAndUserId(
                projectId, developer.getId())) {

            throw new IllegalStateException(
                    "Developer is already a project member");
        }

        // Prevent duplicate invitations

        if (invitationRepository.existsByProjectIdAndInvitedUserId(
                projectId, developer.getId())) {

            throw new IllegalStateException(
                    "An invitation already exists for this developer");
        }

        ProjectInvitation invitation = new ProjectInvitation();

        invitation.setProject(project);
        invitation.setInvitedUser(developer);
        invitation.setInvitedBy(owner);
        invitation.setStatus("PENDING");

        return new ProjectInvitationResponse(
                invitationRepository.save(invitation));
    }

    // DEVELOPER VIEWS PENDING INVITATIONS

    @Transactional(readOnly = true)
    public List<ProjectInvitationResponse> getMyPendingInvitations(
            String email) {

        User developer = getUser(email);

        return invitationRepository
                .findByInvitedUserIdAndStatus(
                        developer.getId(), "PENDING")
                .stream()
                .map(ProjectInvitationResponse::new)
                .toList();
    }

    // OWNER VIEWS INVITATIONS SENT FOR THEIR PROJECT

    @Transactional(readOnly = true)
    public List<ProjectInvitationResponse> getProjectInvitations(
            String ownerEmail,
            Long projectId) {

        getOwnedProject(ownerEmail, projectId);

        return invitationRepository.findByProjectId(projectId)
                .stream()
                .map(ProjectInvitationResponse::new)
                .toList();
    }

    // DEVELOPER ACCEPTS INVITATION

    @Transactional
    public ProjectInvitationResponse acceptInvitation(
            String email,
            Long invitationId) {

        User developer = getUser(email);

        ProjectInvitation invitation = invitationRepository
                .findById(invitationId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Invitation not found"));

        // Only invited developer can accept

        if (!invitation.getInvitedUser().getId()
                .equals(developer.getId())) {

            throw new IllegalArgumentException(
                    "You cannot accept another developer's invitation");
        }

        if (!"PENDING".equals(invitation.getStatus())) {
            throw new IllegalStateException(
                    "Invitation has already been processed");
        }

        Long projectId = invitation.getProject().getId();

        if (memberRepository.existsByProjectIdAndUserId(
                projectId, developer.getId())) {

            throw new IllegalStateException(
                    "You are already a project member");
        }

        // Create contributor membership

        ProjectMember member = new ProjectMember();

        member.setProject(invitation.getProject());
        member.setUser(developer);
        member.setRole("CONTRIBUTOR");

        memberRepository.save(member);

        invitation.setStatus("ACCEPTED");

        return new ProjectInvitationResponse(
                invitationRepository.save(invitation));
    }

    // DEVELOPER DECLINES INVITATION

    @Transactional
    public ProjectInvitationResponse declineInvitation(
            String email,
            Long invitationId) {

        User developer = getUser(email);

        ProjectInvitation invitation = invitationRepository
                .findById(invitationId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Invitation not found"));

        if (!invitation.getInvitedUser().getId()
                .equals(developer.getId())) {

            throw new IllegalArgumentException(
                    "You cannot decline another developer's invitation");
        }

        if (!"PENDING".equals(invitation.getStatus())) {
            throw new IllegalStateException(
                    "Invitation has already been processed");
        }

        invitation.setStatus("DECLINED");

        return new ProjectInvitationResponse(
                invitationRepository.save(invitation));
    }
}
