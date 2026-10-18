package com.techtribe.techtribe_backend.controller;

import com.techtribe.techtribe_backend.dto.DeveloperConnectionResponse;
import com.techtribe.techtribe_backend.service.DeveloperConnectionService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/connections")
@PreAuthorize("isAuthenticated()")
public class DeveloperConnectionController {

    private final DeveloperConnectionService connectionService;

    public DeveloperConnectionController(
            DeveloperConnectionService connectionService) {

        this.connectionService = connectionService;
    }

    @PostMapping("/request/{developerId}")
    public ResponseEntity<?> sendRequest(
            @PathVariable Long developerId,
            Authentication authentication) {

        try {
            return ResponseEntity.status(201).body(
                    connectionService.sendRequest(
                            authentication.getName(),
                            developerId
                    )
            );
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409)
                    .body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/incoming")
    public List<DeveloperConnectionResponse> incoming(
            Authentication authentication) {

        return connectionService.getIncomingRequests(
                authentication.getName()
        );
    }

    @GetMapping("/outgoing")
    public List<DeveloperConnectionResponse> outgoing(
            Authentication authentication) {

        return connectionService.getOutgoingRequests(
                authentication.getName()
        );
    }

    @GetMapping("/my")
    public List<DeveloperConnectionResponse> myConnections(
            Authentication authentication) {

        return connectionService.getMyConnections(
                authentication.getName()
        );
    }

    @GetMapping("/status/{developerId}")
    public ResponseEntity<?> status(
            @PathVariable Long developerId,
            Authentication authentication) {

        return connectionService.getConnectionStatus(
                        authentication.getName(),
                        developerId
                )
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.noContent().build()
                );
    }

    @PutMapping("/{connectionId}/accept")
    public ResponseEntity<?> accept(
            @PathVariable Long connectionId,
            Authentication authentication) {

        try {
            return ResponseEntity.ok(
                    connectionService.acceptRequest(
                            authentication.getName(),
                            connectionId
                    )
            );
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409)
                    .body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{connectionId}/reject")
    public ResponseEntity<?> reject(
            @PathVariable Long connectionId,
            Authentication authentication) {

        try {
            return ResponseEntity.ok(
                    connectionService.rejectRequest(
                            authentication.getName(),
                            connectionId
                    )
            );
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409)
                    .body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{connectionId}")
    public ResponseEntity<?> remove(
            @PathVariable Long connectionId,
            Authentication authentication) {

        try {
            connectionService.removeConnection(
                    authentication.getName(),
                    connectionId
            );

            return ResponseEntity.noContent().build();

        } catch (IllegalStateException e) {
            return ResponseEntity.status(409)
                    .body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
}