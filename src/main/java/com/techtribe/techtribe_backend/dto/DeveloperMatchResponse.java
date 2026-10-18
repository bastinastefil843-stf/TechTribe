package com.techtribe.techtribe_backend.dto;

public class DeveloperMatchResponse {

    private Long developerId;
    private String developerName;
    private String skills;
    private int matchScore;
    private String reason;

    public DeveloperMatchResponse() {
    }

    public DeveloperMatchResponse(
            Long developerId,
            String developerName,
            String skills,
            int matchScore,
            String reason) {

        this.developerId = developerId;
        this.developerName = developerName;
        this.skills = skills;
        this.matchScore = matchScore;
        this.reason = reason;
    }

    public Long getDeveloperId() {
        return developerId;
    }

    public void setDeveloperId(Long developerId) {
        this.developerId = developerId;
    }

    public String getDeveloperName() {
        return developerName;
    }

    public void setDeveloperName(String developerName) {
        this.developerName = developerName;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public int getMatchScore() {
        return matchScore;
    }

    public void setMatchScore(int matchScore) {
        this.matchScore = matchScore;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}