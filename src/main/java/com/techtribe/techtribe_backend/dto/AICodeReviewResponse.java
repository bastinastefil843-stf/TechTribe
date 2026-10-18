package com.techtribe.techtribe_backend.dto;

import java.util.List;

public class AICodeReviewResponse {

    private String summary;
    private int overallScore;
    private List<CodeReviewIssue> issues;
    private List<String> improvements;

    public AICodeReviewResponse() {
    }

    public AICodeReviewResponse(
            String summary,
            int overallScore,
            List<CodeReviewIssue> issues,
            List<String> improvements) {

        this.summary = summary;
        this.overallScore = overallScore;
        this.issues = issues;
        this.improvements = improvements;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public int getOverallScore() {
        return overallScore;
    }

    public void setOverallScore(int overallScore) {
        this.overallScore = overallScore;
    }

    public List<CodeReviewIssue> getIssues() {
        return issues;
    }

    public void setIssues(List<CodeReviewIssue> issues) {
        this.issues = issues;
    }

    public List<String> getImprovements() {
        return improvements;
    }

    public void setImprovements(List<String> improvements) {
        this.improvements = improvements;
    }

    public static class CodeReviewIssue {

        private String category;
        private String severity;
        private String description;
        private String suggestion;

        public CodeReviewIssue() {
        }

        public CodeReviewIssue(
                String category,
                String severity,
                String description,
                String suggestion) {

            this.category = category;
            this.severity = severity;
            this.description = description;
            this.suggestion = suggestion;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getSeverity() {
            return severity;
        }

        public void setSeverity(String severity) {
            this.severity = severity;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getSuggestion() {
            return suggestion;
        }

        public void setSuggestion(String suggestion) {
            this.suggestion = suggestion;
        }
    }
}