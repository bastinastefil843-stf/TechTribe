package com.techtribe.techtribe_backend.controller;

import com.techtribe.techtribe_backend.dto.DeveloperProfileResponse;
import com.techtribe.techtribe_backend.service.DeveloperDirectoryService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/developers")
@PreAuthorize("isAuthenticated()")
public class DeveloperDirectoryController {

    private final DeveloperDirectoryService directoryService;

    public DeveloperDirectoryController(
            DeveloperDirectoryService directoryService) {

        this.directoryService = directoryService;
    }

    // GET /api/developers
    // GET /api/developers?search=java

    @GetMapping
    public ResponseEntity<List<DeveloperProfileResponse>>
            getDevelopers(
                    @RequestParam(
                            required = false
                    ) String search,
                    Authentication authentication) {

        List<DeveloperProfileResponse> developers =
                directoryService.getDevelopers(
                        authentication.getName(),
                        search
                );

        return ResponseEntity.ok(developers);
    }

    // GET /api/developers/{userId}

    @GetMapping("/{userId}")
    public ResponseEntity<DeveloperProfileResponse>
            getDeveloperProfile(
                    @PathVariable Long userId) {

        return directoryService.getPublicProfile(userId)
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.notFound().build()
                );
    }
}