package com.techtribe.techtribe_backend.controller;

import com.techtribe.techtribe_backend.dto.ProjectInvitationCreateRequest;
import com.techtribe.techtribe_backend.dto.ProjectInvitationResponse;
import com.techtribe.techtribe_backend.service.ProjectInvitationService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ProjectInvitationController {

    private final ProjectInvitationService invitationService;

    public ProjectInvitationController(
            ProjectInvitationService invitationService) {
        this.invitationService = invitationService;
    }

    // OWNER SENDS INVITATION

    @PostMapping("/projects/{projectId}/invitations")
    public ResponseEntity<?> sendInvitation(
            @PathVariable Long projectId,
            @Valid @RequestBody ProjectInvitationCreateRequest request,
            Authentication authentication) {

        try {
            ProjectInvitationResponse response =
                    invitationService.sendInvitation(
                            authentication.getName(),
                            projectId,
                            request.getEmail()
                    );

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());

        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(e.getMessage());
        }
    }

    // OWNER VIEWS SENT INVITATIONS

    @GetMapping("/projects/{projectId}/invitations")
    public ResponseEntity<?> getProjectInvitations(
            @PathVariable Long projectId,
            Authentication authentication) {

        try {
            List<ProjectInvitationResponse> invitations =
                    invitationService.getProjectInvitations(
                            authentication.getName(),
                            projectId
                    );

            return ResponseEntity.ok(invitations);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    // DEVELOPER VIEWS RECEIVED INVITATIONS

    @GetMapping("/invitations/my")
    public ResponseEntity<?> getMyInvitations(
            Authentication authentication) {

        try {
            List<ProjectInvitationResponse> invitations =
                    invitationService.getMyPendingInvitations(
                            authentication.getName()
                    );

            return ResponseEntity.ok(invitations);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    // DEVELOPER ACCEPTS INVITATION

    @PutMapping("/invitations/{invitationId}/accept")
    public ResponseEntity<?> acceptInvitation(
            @PathVariable Long invitationId,
            Authentication authentication) {

        try {
            ProjectInvitationResponse response =
                    invitationService.acceptInvitation(
                            authentication.getName(),
                            invitationId
                    );

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());

        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(e.getMessage());
        }
    }

    // DEVELOPER DECLINES INVITATION

    @PutMapping("/invitations/{invitationId}/decline")
    public ResponseEntity<?> declineInvitation(
            @PathVariable Long invitationId,
            Authentication authentication) {

        try {
            ProjectInvitationResponse response =
                    invitationService.declineInvitation(
                            authentication.getName(),
                            invitationId
                    );

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());

        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(e.getMessage());
        }
    }
}