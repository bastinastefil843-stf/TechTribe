package com.techtribe.techtribe_backend.controller;

import com.techtribe.techtribe_backend.dto.ProjectJoinRequestCreateRequest;
import com.techtribe.techtribe_backend.dto.ProjectJoinRequestResponse;
import com.techtribe.techtribe_backend.model.ProjectJoinRequest;
import com.techtribe.techtribe_backend.service.ProjectJoinRequestService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/join-requests")
public class ProjectJoinRequestController {

    private final ProjectJoinRequestService joinRequestService;

    public ProjectJoinRequestController(
            ProjectJoinRequestService joinRequestService) {
        this.joinRequestService = joinRequestService;
    }

    // DEVELOPER REQUESTS TO JOIN

    @PostMapping
    public ResponseEntity<?> requestToJoin(
            @PathVariable Long projectId,
            @Valid @RequestBody ProjectJoinRequestCreateRequest requestBody,
            Authentication authentication) {

        try {
            ProjectJoinRequest request =
                    joinRequestService.requestToJoin(
                            authentication.getName(),
                            projectId,
                            requestBody.getMessage()
                    );

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(joinRequestService.getRequestResponse(
                            authentication.getName(),
                            projectId,
                            request.getId()
                    ));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());

        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(e.getMessage());
        }
    }

    // OWNER VIEWS PENDING REQUESTS WITH PROFILES

    @GetMapping
    public ResponseEntity<?> getPendingRequests(
            @PathVariable Long projectId,
            Authentication authentication) {

        try {
            List<ProjectJoinRequestResponse> requests =
                    joinRequestService.getPendingRequestResponses(
                            authentication.getName(),
                            projectId
                    );

            return ResponseEntity.ok(requests);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    // OWNER APPROVES REQUEST

    @PutMapping("/{requestId}/approve")
    public ResponseEntity<?> approveRequest(
            @PathVariable Long projectId,
            @PathVariable Long requestId,
            Authentication authentication) {

        try {
            ProjectJoinRequest request =
                    joinRequestService.approveRequest(
                            authentication.getName(),
                            projectId,
                            requestId
                    );

            return ResponseEntity.ok(
                    joinRequestService.getRequestResponse(
                            authentication.getName(),
                            projectId,
                            request.getId()
                    )
            );

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());

        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(e.getMessage());
        }
    }

    // OWNER REJECTS REQUEST

    @PutMapping("/{requestId}/reject")
    public ResponseEntity<?> rejectRequest(
            @PathVariable Long projectId,
            @PathVariable Long requestId,
            Authentication authentication) {

        try {
            ProjectJoinRequest request =
                    joinRequestService.rejectRequest(
                            authentication.getName(),
                            projectId,
                            requestId
                    );

            return ResponseEntity.ok(
                    joinRequestService.getRequestResponse(
                            authentication.getName(),
                            projectId,
                            request.getId()
                    )
            );

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());

        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(e.getMessage());
        }
    }
}