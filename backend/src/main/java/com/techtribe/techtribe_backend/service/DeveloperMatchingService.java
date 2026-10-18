package com.techtribe.techtribe_backend.service;

import com.techtribe.techtribe_backend.dto.DeveloperMatchResponse;
import com.techtribe.techtribe_backend.model.DeveloperProfile;
import com.techtribe.techtribe_backend.model.Project;

import com.techtribe.techtribe_backend.repository.DeveloperProfileRepository;
import com.techtribe.techtribe_backend.repository.ProjectRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DeveloperMatchingService {

    private final DeveloperProfileRepository developerProfileRepository;
    private final ProjectRepository projectRepository;
    private final GeminiService geminiService;

    public DeveloperMatchingService(
            DeveloperProfileRepository developerProfileRepository,
            ProjectRepository projectRepository,
            GeminiService geminiService) {

        this.developerProfileRepository = developerProfileRepository;
        this.projectRepository = projectRepository;
        this.geminiService = geminiService;
    }

    @Transactional(readOnly = true)
    public List<DeveloperMatchResponse> findMatchingDevelopers(Long projectId) {

        // STEP 1: Find the project

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Project not found")
                );

        // STEP 2: Get the required technologies

        Set<String> requiredSkills = parseSkills(
                project.getTechnologies()
        );

        if (requiredSkills.isEmpty()) {
            throw new IllegalArgumentException(
                    "Project has no technologies specified"
            );
        }

        // STEP 3: Retrieve developer profiles

        List<DeveloperProfile> developers =
                developerProfileRepository.findAll();

        List<DeveloperMatchResponse> matches = new ArrayList<>();

        // STEP 4: Compare each developer's skills

        for (DeveloperProfile developer : developers) {

            if (developer.getUser() == null) {
                continue;
            }

            Set<String> developerSkills = parseSkills(
                    developer.getSkills()
            );

            Set<String> matchingSkills = new HashSet<>(developerSkills);
            matchingSkills.retainAll(requiredSkills);

            int matchScore = (int) Math.round(
                    (matchingSkills.size() * 100.0)
                            / requiredSkills.size()
            );

            // Skip developers without matching skills

            if (matchScore == 0) {
                continue;
            }

            // STEP 5: Generate an explanation using Gemini AI

            String prompt = """
                    You are an AI developer recruitment assistant
                    for the Tech Tribe collaboration platform.

                    Analyze the following project and developer.

                    Project:
                    %s

                    Project Description:
                    %s

                    Required Technologies:
                    %s

                    Developer Skills:
                    %s

                    Matching Skills:
                    %s

                    Match Score:
                    %d%%

                    Explain why this developer is suitable for
                    the project in 2 short sentences.

                    Base your explanation only on the supplied skills.
                    Do not invent experience or qualifications.
                    Do not change the match score.
                    Return plain text only.
                    """.formatted(
                    project.getTitle(),
                    project.getDescription(),
                    project.getTechnologies(),
                    developer.getSkills(),
                    String.join(", ", matchingSkills),
                    matchScore
            );

            String reason = geminiService.generateContent(prompt);

            // STEP 6: Create developer match response

            DeveloperMatchResponse response =
                    new DeveloperMatchResponse(
                            developer.getUser().getId(),
                            developer.getUser().getName(),
                            developer.getSkills(),
                            matchScore,
                            reason
                    );

            matches.add(response);
        }

        // STEP 7: Sort developers by highest match score

        matches.sort(
                Comparator.comparingInt(
                        DeveloperMatchResponse::getMatchScore
                ).reversed()
        );

        return matches;
    }

    // STEP 8: Convert comma-separated skills into a normalized set

    private Set<String> parseSkills(String skills) {

        if (skills == null || skills.isBlank()) {
            return new HashSet<>();
        }

        return Arrays.stream(skills.split(","))
                .map(String::trim)
                .map(String::toLowerCase)
                .filter(skill -> !skill.isEmpty())
                .collect(Collectors.toSet());
    }
}