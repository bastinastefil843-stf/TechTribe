package com.techtribe.techtribe_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class ProjectInvitationCreateRequest {

    @NotBlank(message = "Developer email is required")
    @Email(message = "Enter a valid email address")
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}