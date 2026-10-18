package com.techtribe.techtribe_backend.service;

import com.techtribe.techtribe_backend.dto.DeveloperProfileResponse;
import com.techtribe.techtribe_backend.model.User;
import com.techtribe.techtribe_backend.repository.DeveloperProfileRepository;
import com.techtribe.techtribe_backend.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class DeveloperDirectoryService {

    private final DeveloperProfileRepository profileRepository;
    private final UserRepository userRepository;

    public DeveloperDirectoryService(
            DeveloperProfileRepository profileRepository,
            UserRepository userRepository) {

        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
    }

    private User getCurrentUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Authenticated user not found"
                        )
                );
    }

    // List developers
    @Transactional(readOnly = true)
    public List<DeveloperProfileResponse> getDevelopers(
            String email,
            String keyword) {

        User currentUser = getCurrentUser(email);

        List<com.techtribe.techtribe_backend.model.DeveloperProfile>
                profiles;

        if (keyword == null || keyword.isBlank()) {

            profiles = profileRepository.findDirectoryProfiles(
                    currentUser.getId()
            );

        } else {

            profiles = profileRepository.searchDevelopers(
                    currentUser.getId(),
                    keyword.trim()
            );
        }

        return profiles.stream()
                .map(DeveloperProfileResponse::new)
                .toList();
    }

    // View another developer's profile
    @Transactional(readOnly = true)
    public Optional<DeveloperProfileResponse> getPublicProfile(
            Long userId) {

        return profileRepository.findPublicProfile(userId)
                .map(DeveloperProfileResponse::new);
    }
}