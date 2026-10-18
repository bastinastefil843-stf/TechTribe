package com.techtribe.techtribe_backend.repository;

import com.techtribe.techtribe_backend.model.ProjectInvitation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ProjectInvitationRepository
        extends JpaRepository<ProjectInvitation, Long> {

    // Check whether an invitation already exists
    boolean existsByProjectIdAndInvitedUserId(
            Long projectId,
            Long invitedUserId
    );

    // Find an invitation for a specific developer
    Optional<ProjectInvitation> findByProjectIdAndInvitedUserId(
            Long projectId,
            Long invitedUserId
    );

    // Get all pending invitations received by a developer
    List<ProjectInvitation> findByInvitedUserIdAndStatus(
            Long invitedUserId,
            String status
    );

    // Get invitations sent for a particular project
    List<ProjectInvitation> findByProjectId(
            Long projectId
    );
}