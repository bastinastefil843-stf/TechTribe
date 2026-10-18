package com.techtribe.techtribe_backend.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class DeveloperProfileRequest {

    @Size(max = 2000, message = "Bio must not exceed 2000 characters")
    private String bio;

    @Size(max = 1000, message = "Skills must not exceed 1000 characters")
    private String skills;

    @Size(max = 255, message = "GitHub URL must not exceed 255 characters")
    @Pattern(
        regexp = "^(|https://(www\\.)?github\\.com/[A-Za-z0-9-]+/?)$",
        message = "Enter a valid GitHub profile URL"
    )
    private String githubUrl;

    @Size(max = 255, message = "LinkedIn URL must not exceed 255 characters")
    @Pattern(
        regexp = "^(|https://(www\\.)?linkedin\\.com/(in|company)/[A-Za-z0-9_-]+/?)$",
        message = "Enter a valid LinkedIn profile URL"
    )
    private String linkedinUrl;

    public DeveloperProfileRequest() {
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public String getGithubUrl() {
        return githubUrl;
    }

    public void setGithubUrl(String githubUrl) {
        this.githubUrl = githubUrl;
    }

    public String getLinkedinUrl() {
        return linkedinUrl;
    }

    public void setLinkedinUrl(String linkedinUrl) {
        this.linkedinUrl = linkedinUrl;
    }
}