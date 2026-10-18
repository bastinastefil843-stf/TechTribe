
package com.techtribe.techtribe_backend.dto;

import com.techtribe.techtribe_backend.model.DeveloperProfile;

public class DeveloperProfileResponse {

    private Long id;
    private Long userId;
    private String name;
    private String bio;
    private String skills;
    private String githubUrl;
    private String linkedinUrl;

    public DeveloperProfileResponse(DeveloperProfile profile) {

        this.id = profile.getId();
        this.userId = profile.getUser().getId();
        this.name = profile.getUser().getName();
        this.bio = profile.getBio();
        this.skills = profile.getSkills();
        this.githubUrl = profile.getGithubUrl();
        this.linkedinUrl = profile.getLinkedinUrl();
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getBio() {
        return bio;
    }

    public String getSkills() {
        return skills;
    }

    public String getGithubUrl() {
        return githubUrl;
    }

    public String getLinkedinUrl() {
        return linkedinUrl;
    }
}