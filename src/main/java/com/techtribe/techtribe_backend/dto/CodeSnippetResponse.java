package com.techtribe.techtribe_backend.dto;

import com.techtribe.techtribe_backend.model.CodeSnippet;

import java.time.LocalDateTime;

public class CodeSnippetResponse {

    private Long id;
    private Long projectId;

    private Long authorId;
    private String authorName;

    private String title;
    private String language;
    private String description;
    private String code;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CodeSnippetResponse(CodeSnippet snippet) {

        this.id = snippet.getId();

        this.projectId = snippet.getProject().getId();

        this.authorId = snippet.getAuthor().getId();
        this.authorName = snippet.getAuthor().getName();

        this.title = snippet.getTitle();
        this.language = snippet.getLanguage();
        this.description = snippet.getDescription();
        this.code = snippet.getCode();

        this.createdAt = snippet.getCreatedAt();
        this.updatedAt = snippet.getUpdatedAt();
    }

    public Long getId() {
        return id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getTitle() {
        return title;
    }

    public String getLanguage() {
        return language;
    }

    public String getDescription() {
        return description;
    }

    public String getCode() {
        return code;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}