package com.techtribe.techtribe_backend.dto;

import jakarta.validation.constraints.Size;

public class ProjectJoinRequestCreateRequest {

    @Size(
        max = 500,
        message = "Join request message must not exceed 500 characters"
    )
    private String message;

    public ProjectJoinRequestCreateRequest() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}