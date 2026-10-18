package com.techtribe.techtribe_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CodeSnippetRequest {

    // =====================================
    // 1. SNIPPET TITLE
    // Required, maximum 150 characters
    // =====================================

    @NotBlank(message = "Snippet title is required")
    @Size(
        max = 150,
        message = "Title must not exceed 150 characters"
    )
    private String title;

    // =====================================
    // 2. PROGRAMMING LANGUAGE
    // Required, maximum 50 characters
    // =====================================

    @NotBlank(message = "Programming language is required")
    @Size(
        max = 50,
        message = "Language must not exceed 50 characters"
    )
    private String language;

    // =====================================
    // 3. DESCRIPTION
    // Optional, maximum 2000 characters
    // =====================================

    @Size(
        max = 2000,
        message = "Description must not exceed 2000 characters"
    )
    private String description;

    // =====================================
    // 4. SOURCE CODE
    // Required, maximum 100000 characters
    // =====================================

    @NotBlank(message = "Code cannot be empty")
    @Size(
        max = 100000,
        message = "Code must not exceed 100000 characters"
    )
    private String code;

    // =====================================
    // 5. DEFAULT CONSTRUCTOR
    // =====================================

    public CodeSnippetRequest() {
    }

    // =====================================
    // 6. GETTERS AND SETTERS
    // =====================================

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}