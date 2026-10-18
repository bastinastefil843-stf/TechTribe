package com.techtribe.techtribe_backend.service;

import com.techtribe.techtribe_backend.model.DeveloperProfile;
import com.techtribe.techtribe_backend.model.User;

import com.techtribe.techtribe_backend.repository.DeveloperProfileRepository;
import com.techtribe.techtribe_backend.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class DeveloperProfileService {

    private final DeveloperProfileRepository profileRepository;
    private final UserRepository userRepository;

    public DeveloperProfileService(
            DeveloperProfileRepository profileRepository,
            UserRepository userRepository) {

        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
    }

    // ==========================================
    // 1. CREATE DEVELOPER PROFILE
    // ==========================================
    @Transactional
    public DeveloperProfile createProfile(
            String email,
            DeveloperProfile profileData) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        if (profileRepository.existsByUserId(user.getId())) {
            throw new IllegalStateException(
                    "Developer profile already exists"
            );
        }

        DeveloperProfile profile = new DeveloperProfile();

        profile.setUser(user);
        profile.setBio(profileData.getBio());
        profile.setSkills(profileData.getSkills());
        profile.setGithubUrl(profileData.getGithubUrl());
        profile.setLinkedinUrl(profileData.getLinkedinUrl());

        return profileRepository.save(profile);
    }

    // ==========================================
    // 2. GET LOGGED-IN DEVELOPER PROFILE
    // ==========================================
    @Transactional(readOnly = true)
    public Optional<DeveloperProfile> getMyProfile(String email) {

        return userRepository.findByEmail(email)
                .flatMap(user ->
                        profileRepository.findByUserId(user.getId())
                );
    }

    // ==========================================
    // 3. UPDATE LOGGED-IN DEVELOPER PROFILE
    // ==========================================
    @Transactional
    public DeveloperProfile updateProfile(
            String email,
            DeveloperProfile updatedData) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        DeveloperProfile profile = profileRepository
                .findByUserId(user.getId())
                .orElseThrow(() ->
                        new RuntimeException("Developer profile not found")
                );

        profile.setBio(updatedData.getBio());
        profile.setSkills(updatedData.getSkills());
        profile.setGithubUrl(updatedData.getGithubUrl());
        profile.setLinkedinUrl(updatedData.getLinkedinUrl());

        return profileRepository.save(profile);
    }
}