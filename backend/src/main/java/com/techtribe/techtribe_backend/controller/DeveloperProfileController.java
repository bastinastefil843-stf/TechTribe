package com.techtribe.techtribe_backend.controller;

import com.techtribe.techtribe_backend.dto.DeveloperProfileRequest;
import com.techtribe.techtribe_backend.dto.DeveloperProfileResponse;
import com.techtribe.techtribe_backend.model.DeveloperProfile;
import com.techtribe.techtribe_backend.service.DeveloperProfileService;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profiles")
public class DeveloperProfileController {

    private final DeveloperProfileService profileService;

    public DeveloperProfileController(
            DeveloperProfileService profileService) {

        this.profileService = profileService;
    }

    // ==========================================
    // 1. CREATE DEVELOPER PROFILE
    // ==========================================
    @PostMapping
    public ResponseEntity<?> createProfile(
            @Valid @RequestBody DeveloperProfileRequest request,
            Authentication authentication) {

        String email = authentication.getName();

        DeveloperProfile profileData = new DeveloperProfile();

        profileData.setBio(request.getBio());
        profileData.setSkills(request.getSkills());
        profileData.setGithubUrl(request.getGithubUrl());
        profileData.setLinkedinUrl(request.getLinkedinUrl());

        try {

            DeveloperProfile savedProfile =
                    profileService.createProfile(email, profileData);

            return ResponseEntity.status(201)
                    .body(new DeveloperProfileResponse(savedProfile));

        } catch (IllegalStateException e) {

            return ResponseEntity.status(409)
                    .body(e.getMessage());
        }
    }

    // ==========================================
    // 2. VIEW MY PROFILE
    // ==========================================
    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile(
            Authentication authentication) {

        String email = authentication.getName();

        return profileService.getMyProfile(email)
                .map(profile -> ResponseEntity.ok(
                        (Object) new DeveloperProfileResponse(profile)
                ))
                .orElseGet(() ->
                        ResponseEntity.status(404)
                                .body("Developer profile not found")
                );
    }

    // ==========================================
    // 3. UPDATE MY PROFILE
    // ==========================================
    @PutMapping("/me")
    public ResponseEntity<?> updateProfile(
            @Valid @RequestBody DeveloperProfileRequest request,
            Authentication authentication) {

        String email = authentication.getName();

        DeveloperProfile updatedData = new DeveloperProfile();

        updatedData.setBio(request.getBio());
        updatedData.setSkills(request.getSkills());
        updatedData.setGithubUrl(request.getGithubUrl());
        updatedData.setLinkedinUrl(request.getLinkedinUrl());

        try {

            DeveloperProfile updatedProfile =
                    profileService.updateProfile(email, updatedData);

            return ResponseEntity.ok(
                    new DeveloperProfileResponse(updatedProfile)
            );

        } catch (RuntimeException e) {

            return ResponseEntity.status(404)
                    .body("Developer profile not found");
        }
    }
}
